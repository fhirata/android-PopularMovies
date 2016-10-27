package com.example.fabiohh.popularmovies.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.fabiohh.popularmovies.R;
import com.example.fabiohh.popularmovies.db.MovieContract;
import com.example.fabiohh.popularmovies.models.IMovieInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import static com.example.fabiohh.popularmovies.MoviesFragment.MOVIE_FETCH_MODE_POPULAR;
import static com.example.fabiohh.popularmovies.MoviesFragment.MOVIE_FETCH_MODE_TOPRATED;

/**
 * Created by fabiohh on 9/5/16.
 */
public class MoviesService extends AsyncTask<String, Void, Void> implements IMovieInfo {
    private final String MOVIE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";

    private String LOG_TAG = MoviesService.class.getSimpleName();
    private String KEY_PARAM = "api_key";
    private Context context;
    private String apiUrl;
    private String apiType;

    static final String MOVIE_DISCOVER_API_URL = "http://api.themoviedb.org/3/discover/movie";
    static final String MOVIE_TOP_RATED_API_URL = "http://api.themoviedb.org/3/movie/top_rated";

    public MoviesService(Context context, String apiType) {

        if (apiType.equals(MOVIE_FETCH_MODE_TOPRATED)) {
            this.apiUrl = MOVIE_TOP_RATED_API_URL;
        } else if (apiType.equals(MOVIE_FETCH_MODE_POPULAR)) {
            this.apiUrl = MOVIE_DISCOVER_API_URL;
        } else {
            Log.e(LOG_TAG, "Error: incorrect fetch type");
        }
        this.context = context;
        this.apiType = apiType;
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;
        String key = context.getString(R.string.movies_api_key);

        if (key.equals("")) {
            throw new RuntimeException("Missing API Key. See README file.");
        }

        Uri uri = Uri.parse(apiUrl).buildUpon()
                .appendQueryParameter(KEY_PARAM, key).build();

        Log.v("URI", "URI is " + uri.toString());
        try {
            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
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
                buffer.append(line)
                        .append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                movieJsonStr = null;
            }
            movieJsonStr = buffer.toString();

            //Log.w("Content: ", movieJsonStr);

        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
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
            // Save Contents into database
            saveMovieFromJson(movieJsonStr);
            //return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException jsonException) {
            Log.e(LOG_TAG, jsonException.getMessage(), jsonException);
            jsonException.printStackTrace();
        }
        return null;
    }

    private void saveMovieFromJson(String movieJsonStr)
            throws JSONException {

        if (movieJsonStr == null) {
            return;
        }

        JSONObject moviesJSONObject = new JSONObject(movieJsonStr);
        JSONArray moviesArray = moviesJSONObject.getJSONArray(MOVIE_RESULTS);

        Vector<ContentValues> movieItemInfoResult = new Vector<>();

        Cursor cursor = null;

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieObject = moviesArray.getJSONObject(i);

            String title = movieObject.getString(MOVIE_TITLE);
            int movieId = movieObject.getInt(MOVIE_ID);
            String imgUrl = buildImageURL(movieObject.getString(MOVIE_IMAGE));
            String description = movieObject.getString(MOVIE_DESC);
            String voteAverage = movieObject.getString(MOVIE_VOTE_AVG);
            String voteCount = movieObject.getString(MOVIE_VOTE_COUNT);
            String releaseYear = movieObject.getString(MOVIE_YEAR);
            String backDrop = buildImageURL(movieObject.getString(MOVIE_BACKDROP));

            ContentValues contentValues = new ContentValues();

            contentValues.put(MovieContract.MovieEntry.COLUMN_NAME, title);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseYear);
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_BITMAP, "blob");
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, imgUrl);
            contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, voteCount);
            contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
            contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, description);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, backDrop);
            contentValues.put(MovieContract.MovieEntry.COLUMN_TYPE, apiType);

            // TODO: bulk insert the values, checking for duplicates
//            movieItemInfoResult.add(contentValues);

            // TODO: create MovieContentManager to hide query statement
            // Check if movie item exists
            Uri uri = MovieContract.MovieEntry.buildMovieItemUri(movieId);
            cursor = context.getContentResolver().query(uri,
                    null,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{Long.toString(movieId)},
                    null);

            if (cursor.getCount() == 0) {
                context.getContentResolver().insert(uri, contentValues);
            }
        }
        // TODO: bulk insert the values, checking for duplicates
//        ContentValues[] values = new ContentValues[movieItemInfoResult.size()];
//        movieItemInfoResult.toArray(values);
//        context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, values);
    }

    private String buildImageURL(String imageUrl) {
        return MOVIE_IMAGE_URL + imageUrl;
    }
}