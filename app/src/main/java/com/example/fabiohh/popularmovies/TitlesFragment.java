package com.example.fabiohh.popularmovies;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.fabiohh.popularmovies.models.MovieItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by fabiohh on 8/23/16.
 */
@TargetApi(11)
public class TitlesFragment extends Fragment {

    static final String MOVIE_API_URL   = "http://api.themoviedb.org/3/discover/movie";
    static final String MOVIE_IMAGE_URL = "http://image.tmdb.org/t/p/w500";

    ImageAdapter mImageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.grid_movies);

        FetchMovieTitleTask moviesTitleTask = new FetchMovieTitleTask();
        moviesTitleTask.execute();

        mImageAdapter = new ImageAdapter(getActivity(), new ArrayList<MovieItem>());
        gridview.setAdapter(mImageAdapter);

        return rootView;
    }


    private class FetchMovieTitleTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {

        private String LOG_TAG = FetchMovieTitleTask.class.getSimpleName();
        private String KEY_PARAM = "api_key";

        @Override
        protected void onPostExecute(ArrayList<MovieItem> movieItems) {

            if (movieItems != null) {
                mImageAdapter.setData(movieItems);
            }

            super.onPostExecute(movieItems);
        }

        @Override
        protected ArrayList<MovieItem> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;
            String key = getString(R.string.movies_api_key);

            if (key.equals("")) {
                throw new RuntimeException("Missing API Key. See README file.");
            }

            Uri uri = Uri.parse(MOVIE_API_URL).buildUpon()
                    .appendQueryParameter(KEY_PARAM, key).build();

            Log.v("URI", "URI is " + uri.toString());
            try {
                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();

                Log.w("Content", movieJsonStr);

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException jsonException) {
                Log.e(LOG_TAG, jsonException.getMessage(), jsonException);
                jsonException.printStackTrace();
            }
            return null;
        }

        private ArrayList<MovieItem> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MOVIE_RESULTS    = "results";
            final String MOVIE_TITLE      = "title";
            final String MOVIE_IMAGE      = "poster_path";
            final String MOVIE_DESC       = "overview";
            final String MOVIE_VOTE_COUNT = "vote_count";
            final String MOVIE_VOTE_AVG   = "vote_average";

            JSONObject moviesJSONObject = new JSONObject(movieJsonStr);
            JSONArray moviesArray = moviesJSONObject.getJSONArray(MOVIE_RESULTS);

            ArrayList<MovieItem> movieItemInfoResult = new ArrayList<>();

            for(int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);

                String title       = movieObject.getString(MOVIE_TITLE);
                String imgUrl      = buildImageURL(movieObject.getString(MOVIE_IMAGE));
                String description = movieObject.getString(MOVIE_DESC);
                String voteAverage = movieObject.getString(MOVIE_VOTE_AVG);
                String voteCount   = movieObject.getString(MOVIE_VOTE_COUNT);

                movieItemInfoResult.add(new MovieItem(title, description, voteAverage, voteCount, imgUrl));
            }
            return movieItemInfoResult;
        }

        private String buildImageURL(String imageUrl) {
            return MOVIE_IMAGE_URL + imageUrl;
        }
    }


}

