package com.example.fabiohh.popularmovies.services;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.fabiohh.popularmovies.ImageAdapter;
import com.example.fabiohh.popularmovies.R;
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
 * Created by fabiohh on 9/5/16.
 */
public class MoviesService extends AsyncTask<String, Void, ArrayList<MovieItem>> implements IMovieInfo {
    static final String MOVIE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";

    private String LOG_TAG = MoviesService.class.getSimpleName();
    private String KEY_PARAM = "api_key";
    private Context context;
    private String apiUrl;
    private ImageAdapter mImageAdapter;


    public MoviesService(Context context, String apiUrl, ImageAdapter imageAdapter) {
        this.context = context;
        this.mImageAdapter = imageAdapter;
        this.apiUrl = apiUrl;

    }
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

            Log.w("Content: ", movieJsonStr);

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
            if (movieJsonStr != null) {
                return getMovieDataFromJson(movieJsonStr);
            } else {
                Log.e(LOG_TAG, "Movie data is empty.");
            }
        } catch (JSONException jsonException) {
            Log.e(LOG_TAG, jsonException.getMessage(), jsonException);
            jsonException.printStackTrace();
            Toast.makeText(context.getApplicationContext(), "Unable to Read movie information.  Message: " + jsonException.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private ArrayList<MovieItem> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        if (movieJsonStr == null) {
            return null;
        }

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
            String backDrop = buildImageURL(movieObject.getString(MOVIE_BACKDROP));

            movieItemInfoResult.add(new MovieItem(title, description, voteAverage, voteCount, imgUrl, releaseYear, backDrop));
        }
        return movieItemInfoResult;
    }

    private String buildImageURL(String imageUrl) {
        return MOVIE_IMAGE_URL + imageUrl;
    }
}