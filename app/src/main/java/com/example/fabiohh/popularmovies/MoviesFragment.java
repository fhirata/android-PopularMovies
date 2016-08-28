package com.example.fabiohh.popularmovies;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.fabiohh.popularmovies.models.IMovieInfo;
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
public class MoviesFragment extends Fragment implements Preference.OnPreferenceChangeListener {

    static final String MOVIE_DISCOVER_API_URL = "http://api.themoviedb.org/3/discover/movie";
    static final String MOVIE_TOP_RATED_API_URL = "http://api.themoviedb.org/3/movie/top_rated";
    static final String MOVIE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    ImageAdapter mImageAdapter;
    LayoutInflater mInflater;
    int mApiMode;

    static final int MOVIE_ITEM_POSITION_CODE = 6767;

    // Preferences
    static final int MOVIE_API_MODE_TOPRATED = 0;
    static final int MOVIE_API_MODE_POPULAR = 1;
    static final String MOVIE_API_PREFERENCE = "api_mode";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.grid_movies);

        FetchMovieTask moviesTitleTask = new FetchMovieTask();
        moviesTitleTask.execute();

        mInflater = inflater;
        mImageAdapter = new ImageAdapter(getActivity(), new ArrayList<MovieItem>());
        gridview.setAdapter(mImageAdapter);

        // Use last user's preference if available
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (preferences.contains(MOVIE_API_PREFERENCE)) {
            mApiMode = preferences.getInt(MOVIE_API_PREFERENCE, MOVIE_API_MODE_TOPRATED);
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.menu_popular:
                mApiMode = MOVIE_API_MODE_POPULAR;
                break;
            case R.id.menu_toprated:
                mApiMode = MOVIE_API_MODE_TOPRATED;
                break;
        }
        FetchMovieTask moviesTitleTask = new FetchMovieTask();
        moviesTitleTask.execute();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().putInt(MOVIE_API_PREFERENCE, mApiMode).apply();

        return super.onOptionsItemSelected(menuItem);
    }

    private class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieItem>> implements IMovieInfo {

        private String LOG_TAG = FetchMovieTask.class.getSimpleName();
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

            String movie_api_url = (mApiMode == MOVIE_API_MODE_TOPRATED) ?
                    MOVIE_TOP_RATED_API_URL : MOVIE_DISCOVER_API_URL;

            Uri uri = Uri.parse(movie_api_url).buildUpon()
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

            JSONObject moviesJSONObject = new JSONObject(movieJsonStr);
            JSONArray moviesArray = moviesJSONObject.getJSONArray(MOVIE_RESULTS);

            ArrayList<MovieItem> movieItemInfoResult = new ArrayList<>();

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);

                String title = movieObject.getString(MOVIE_TITLE);
                String imgUrl = buildImageURL(movieObject.getString(MOVIE_IMAGE));
                String description = movieObject.getString(MOVIE_DESC);
                String voteAverage = movieObject.getString(MOVIE_VOTE_AVG);
                String voteCount = movieObject.getString(MOVIE_VOTE_COUNT);
                String releaseYear = movieObject.getString(MOVIE_YEAR);

                movieItemInfoResult.add(new MovieItem(title, description, voteAverage, voteCount, imgUrl, releaseYear));
            }
            return movieItemInfoResult;
        }

        private String buildImageURL(String imageUrl) {
            return MOVIE_IMAGE_URL + imageUrl;
        }
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        preference.setSummary(stringValue);
        return true;
    }
}

