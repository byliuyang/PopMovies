package com.example.android.popmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.android.popmovies.Models.Movie;
import com.example.android.popmovies.Models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by harryliu on 10/26/15.
 */
public class Utilities {
    private static final String LOG_TAG = Utilities.class.getSimpleName();

    public static Movie[] getMovieDataFromPosterJsonStr(Context context, String popMovieStr) throws JSONException, ParseException, IOException {
        final String JSON_RESULTS = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_POSTER_PATH = "poster_path";

        JSONObject popMovieJson = new JSONObject(popMovieStr);
        JSONArray movieArray = popMovieJson.getJSONArray(JSON_RESULTS);

        int numMovies = movieArray.length();
        Movie[] movies = new Movie[numMovies];

        for (int num = 0; num < numMovies; num++) {

            JSONObject movieObject = movieArray.getJSONObject(num);
            Movie movie = new Movie();
            movie.setId(
                    movieObject.getLong(MOVIE_ID));

            String encodedPosterFileStr = movieObject.getString(MOVIE_POSTER_PATH).toString();
            String posterFileStrWithSlash = Uri.decode(encodedPosterFileStr);
            String posterFileStr = posterFileStrWithSlash.substring(1);

            File posterLocalDir = context.getDir(Long.toString(movie.getId()), Context.MODE_PRIVATE);
            File posterLocalFile = new File(posterLocalDir, posterFileStr);
            if (!posterLocalFile.exists()) {
                Bitmap bitmap = null;
                // Load image from server
                Uri posterNetworkUri = Uri.parse(Movie.BASE_URI).buildUpon()
                        .appendPath(Movie.POSTER_WIDTH)
                        .appendPath(posterFileStr)
                        .build();

                String posterUriStr = Uri.decode(posterNetworkUri.toString());
                URL posterUrl = new URL(posterUriStr);
                bitmap = BitmapFactory.decodeStream(posterUrl.openConnection().getInputStream());

                // Save file to local storage

                FileOutputStream fileOutputStream = new FileOutputStream(posterLocalFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();
            }

            Uri posterLocalUri = Uri.parse(posterLocalFile.getPath());

            movie.setPosterPath(posterLocalUri);
            movies[num] = movie;
        }
        return movies;
    }

    public static String getReleaseYear(String releaseDate) throws ParseException {
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-mm-dd");
        Date date = parseFormat.parse(releaseDate);

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy");
        String releaseYear = outputFormat.format(date);
        return releaseYear;
    }

    public static Movie getMovieDataFromMovieJsonStr(Context context, String popMovieStr) throws JSONException, ParseException, IOException {
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "title";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_BACK_DROP_PATH = "backdrop_path";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_VOTE_AVERAGE = "vote_average";


        JSONObject movieObject = new JSONObject(popMovieStr);

        Long movieId = movieObject.getLong(MOVIE_ID);

        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setTitle(
                movieObject.getString(MOVIE_TITLE));
        movie.setOverview(
                movieObject.getString(MOVIE_OVERVIEW));

        String encodedPosterFileStr = movieObject.getString(MOVIE_POSTER_PATH).toString();
        String posterFileStrWithSlash = Uri.decode(encodedPosterFileStr);
        String posterFileStr = posterFileStrWithSlash.substring(1);

        String encodedBackdropFileStr = movieObject.getString(MOVIE_BACK_DROP_PATH).toString();
        String backdropFileStrWithSlash = Uri.decode(encodedBackdropFileStr);
        String backdropFileStr = backdropFileStrWithSlash.substring(1);

        movie.setReleaseYear(
                getReleaseYear(movieObject.getString(MOVIE_RELEASE_DATE)));
        movie.setVoteAverage(movieObject.getDouble(MOVIE_VOTE_AVERAGE));

        File movieLocalDir = context.getDir(Long.toString(movie.getId()), Context.MODE_PRIVATE);

        Uri backdropLocalUri = generateImageUri(movieLocalDir.toString(), backdropFileStr, Movie.BACKDROP_WIDTH);
        Uri posterLocalUri = generateImageUri(movieLocalDir.toString(), posterFileStr, Movie.POSTER_WIDTH);

        movie.setBackdropPath(backdropLocalUri);
        movie.setPosterPath(posterLocalUri);

        // Get trailers of movie with id = movieId
        Trailer[] trailers = getTrailersFromServer(context, Long.toString(movieId));
        movie.setTrailers(trailers);
        return movie;
    }

    public static Uri generateImageUri(String dir, String imageFileStr, String imageWidth) throws IOException {

        File imageLocalFile = new File(dir, imageFileStr);
        if (!imageLocalFile.exists())

        {
            Bitmap bitmap = null;
            // Load image from server
            Uri posterNetworkUri = Uri.parse(Movie.BASE_URI).buildUpon()
                    .appendPath(imageWidth)
                    .appendPath(imageFileStr)
                    .build();

            String imageUriStr = Uri.decode(posterNetworkUri.toString());
            URL posterUrl = new URL(imageUriStr);
            bitmap = BitmapFactory.decodeStream(posterUrl.openConnection().getInputStream());

            // Save file to local storage
            FileOutputStream fileOutputStream = new FileOutputStream(imageLocalFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        }
        return Uri.parse(imageLocalFile.getPath());
    }

    private static Trailer[] getTrailersFromServer(Context context, String movieId) throws MalformedURLException, JSONException {

        final String BASE_URI = "http://api.themoviedb.org/3";
        final String PATH_MOVIE = "movie";
        final String PATH_VIDEOS = "videos";
        final String PARAM_API_KEY = "api_key";
        final String API_KEY = "a6c28527ad7139aa9aca11d64d7ac6f2";

        Uri builtUri = Uri.parse(BASE_URI).buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(movieId)
                .appendPath(PATH_VIDEOS)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = new URL(builtUri.toString());
        String trailersStr = Utilities.getJsonStrFromServer(context, url);
        Trailer[] trailers = Utilities.getTrailersDataFromMovieJsonStr(trailersStr);

        return trailers;
    }

    private static Trailer[] getTrailersDataFromMovieJsonStr(String trailersStr) throws JSONException{
        final String JSON_RESULTS = "results";
        final String VIDEO_NAME = "name";
        final String VIDEO_KEY = "key";
        final String VIDEO_SITE = "site";
        final String VIDEO_TYPE = "type";
        final String VIDEO_LANGUAGE = "iso_639_1";

        JSONObject videoJson = new JSONObject(trailersStr);
        JSONArray videosArray = videoJson.getJSONArray(JSON_RESULTS);

        int numVideos = videosArray.length();
        Trailer[] trailers = new Trailer[numVideos];
        for (int num=0; num< numVideos; num++) {
            JSONObject videoObject = videosArray.getJSONObject(num);
            Trailer trailer = new Trailer(
                    videoObject.getString(VIDEO_KEY),
                    videoObject.getString(VIDEO_LANGUAGE),
                    videoObject.getString(VIDEO_NAME),
                    videoObject.getString(VIDEO_SITE)
            );

            trailers[num] = trailer;

        }

        return trailers;
    }

    public static String getJsonStrFromServer(Context context, URL url) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

        try {

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (buffer == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            jsonStr = buffer.toString();


        } catch (IOException e) {
            Log.e(LOG_TAG, "IO error", e);
        } finally {
            httpURLConnection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Close stream error", e);
                }
            }


        }

        return jsonStr;
    }
}
