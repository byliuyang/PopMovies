package com.example.android.popmovies;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.example.android.popmovies.Models.Movie;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoviePostersFragment extends Fragment {

    private final String LOG_TAG = MoviePostersFragment.class.getSimpleName();
    ArrayAdapter<Movie> mPostersAdapter;

    public MoviePostersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_posters, container, false);

        mPostersAdapter = new ImageArrayAdapter<>(
                getActivity(),
                R.layout.grid_item_movie,
                R.id.image_view_movie_poster,
                new ArrayList<Movie>()
        );

        mPostersAdapter.setNotifyOnChange(false);

        GridView posterGridView = (GridView) rootView.findViewById(R.id.grid_view_movies);
        posterGridView.setAdapter(mPostersAdapter);

        posterGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
                Movie movie = (Movie) parent.getAdapter().getItem(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, Long.toString(movie.getId()));
                startActivity(detailIntent);

            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchPosterTask fetchPosterTask = new FetchPosterTask();
        fetchPosterTask.execute();
    }

    public class FetchPosterTask extends AsyncTask<String, Void, Movie[]> {

        private final String BASE_URI = "http://api.themoviedb.org/3";
        private final String PATH_DISCOVER = "discover";
        private final String PATH_MOVIE = "movie";
        private final String PARAM_SORT_BY = "sort_by";
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
        protected Movie[] doInBackground(String... params) {

            String sortBy = "popularity.desc";
            Movie[] movies = null;

            try {
                Uri builtUri = Uri.parse(BASE_URI).buildUpon()
                        .appendPath(PATH_DISCOVER)
                        .appendPath(PATH_MOVIE)
                        .appendQueryParameter(PARAM_SORT_BY, sortBy)
                        .appendQueryParameter(PARAM_API_KEY, API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());


                String popMovieStr = Utilities.getJsonStrFromServer(getActivity(), url);
                movies = Utilities.getMovieDataFromPosterJsonStr(getActivity(), popMovieStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "IO error", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Json error", e);
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Parse error", e);
            } finally {
                return movies;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                mPostersAdapter.clear();
                for (Movie movie : movies) {
                    mPostersAdapter.add(movie);
                }
                mPostersAdapter.notifyDataSetChanged();
            }
        }
    }
}