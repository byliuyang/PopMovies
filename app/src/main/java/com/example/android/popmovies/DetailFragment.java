package com.example.android.popmovies;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popmovies.Models.Movie;
import com.example.android.popmovies.Models.Trailer;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {
    TrailerArrayAdapter mTrailerAdapter;
    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mTrailerAdapter = new TrailerArrayAdapter(
                getActivity(),
                R.layout.list_item_trailers,
                R.id.trailers_list_view,
                new ArrayList<Trailer>()
        );

        mTrailerAdapter.setNotifyOnChange(false);

        AdapterView trailerListView = (AdapterView) rootView.findViewById(R.id.trailers_list_view);
        trailerListView.setAdapter(mTrailerAdapter);

        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * Callback method to be invoked when an item in this AdapterView has
             * been clicked.
             * <p/>
             * Implementers can call getItemAtPosition(position) if they need
             * to access the data associated with the selected item.
             *
             * @param parent   The AdapterView where the click happened.
             * @param view     The view within the AdapterView that was clicked (this
             *                 will be a view provided by the adapter)
             * @param position The position of the view in the adapter.
             * @param id       The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trailer trailer = (Trailer)parent.getAdapter().getItem(position);
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getKey()));
                youtubeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(youtubeIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle extras = getActivity().getIntent().getExtras();
        String movieId = extras.getString(Intent.EXTRA_TEXT);

        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute(movieId);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie> {

        private final String BASE_URI = "http://api.themoviedb.org/3";
        private final String PATH_MOVIE = "movie";
        private final String PARAM_API_KEY = "api_key";
        private final String API_KEY = "a6c28527ad7139aa9aca11d64d7ac6f2";

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Movie doInBackground(String... params) {

            String movieId = params[0];
            Movie movie = null;

            try {
                Uri builtUri = Uri.parse(BASE_URI).buildUpon()
                        .appendPath(PATH_MOVIE)
                        .appendPath(movieId)
                        .appendQueryParameter(PARAM_API_KEY, API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                String popMovieStr = Utilities.getJsonStrFromServer(getActivity(), url);
                movie = Utilities.getMovieDataFromMovieJsonStr(getActivity(), popMovieStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "IO error", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Json error", e);
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Parse error", e);
            } finally {
               return movie;
            }
        }

        @Override
        protected void onPostExecute(Movie movie) {

            Activity activity = getActivity();
            getActivity().setTitle(movie.getTitle());

            Bitmap backdropBitmap = BitmapFactory.decodeFile(movie.getBackdropPath().toString());

            ImageView backdropImageView = (ImageView) activity.findViewById(R.id.backdrop_image_view);
            backdropImageView.setImageBitmap(backdropBitmap);

            Bitmap posterBitmap = BitmapFactory.decodeFile(movie.getPosterPath().toString());

            ImageView posterImageView = (ImageView) activity.findViewById(R.id.detail_poster_image_view);
            posterImageView.setImageBitmap(posterBitmap);

            TextView titleTextView = (TextView) activity.findViewById(R.id.detail_movie_title_text_view);
            titleTextView.setText(movie.getTitle());

            TextView releaseYearTextView = (TextView) activity.findViewById(R.id.movie_release_year_text_view);
            releaseYearTextView.setText(movie.getReleaseYear());

            RatingBar movieRatingBar = (RatingBar) activity.findViewById(R.id.movie_rating_bar);
            movieRatingBar.setRating((float) movie.getVoteAverage());

            TextView overviewTextView = (TextView) activity.findViewById(R.id.overview_text_view);
            overviewTextView.setText(movie.getOverview());

            Trailer[] trailers = movie.getTrailers();
            if(trailers.length > 0) {
                mTrailerAdapter.clear();
                for (int i = 0 ; i<trailers.length; i++) {
                    mTrailerAdapter.add(trailers[i]);
                }
                mTrailerAdapter.notifyDataSetChanged();
            }
        }
    }
}
