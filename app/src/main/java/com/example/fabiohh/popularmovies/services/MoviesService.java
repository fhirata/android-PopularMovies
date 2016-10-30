package com.example.fabiohh.popularmovies.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.fabiohh.popularmovies.MoviesFragment;
import com.example.fabiohh.popularmovies.R;
import com.example.fabiohh.popularmovies.adapters.ReviewAdapter;
import com.example.fabiohh.popularmovies.adapters.TrailerAdapter;
import com.example.fabiohh.popularmovies.db.MovieContract;
import com.example.fabiohh.popularmovies.models.IMovieInfo;
import com.example.fabiohh.popularmovies.models.IMovieReview;
import com.example.fabiohh.popularmovies.models.IMovieTrailer;
import com.example.fabiohh.popularmovies.models.MovieReview;
import com.example.fabiohh.popularmovies.models.MovieTrailer;

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
import java.util.List;
import java.util.Vector;

import static com.example.fabiohh.popularmovies.MoviesFragment.MOVIE_FETCH_MODE_POPULAR;
import static com.example.fabiohh.popularmovies.MoviesFragment.MOVIE_FETCH_MODE_REVIEWS;
import static com.example.fabiohh.popularmovies.MoviesFragment.MOVIE_FETCH_MODE_TOPRATED;
import static com.example.fabiohh.popularmovies.MoviesFragment.MOVIE_FETCH_MODE_TRAILERS;

/**
 * Created by fabiohh on 9/5/16.
 */
public class MoviesService extends AsyncTask<String, Void, String> implements IMovieInfo, IMovieReview, IMovieTrailer {
    private final String MOVIE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    private final String MOVIE_THUMBNAIL_URL = "http://img.youtube.com/vi/%1s/%1s.jpg";
    private final String MOVIE_YOUTUBE_URL = "https://www.youtube.com/watch?v=%1s";

    private String LOG_TAG = MoviesService.class.getSimpleName();
    private String KEY_PARAM = "api_key";
    private Context context;
    private String apiUrl;
    private String apiType;
    private MoviesFragment movieFragment;
    private RecyclerView.Adapter mRecyclerAdapter;

    static final String MOVIE_DOMAIN = "http://api.themoviedb.org";
    static final String MOVIE_DOMAIN_URI = "/3";
    static final String MOVIE_HOST = MOVIE_DOMAIN + MOVIE_DOMAIN_URI;

    static final String MOVIE_DISCOVER_API_URL = MOVIE_HOST + "/discover/movie";
    static final String MOVIE_TOP_RATED_API_URL = MOVIE_HOST + "/movie/top_rated";
    static final String MOVIE_REVIEWS_API_URL = MOVIE_HOST + "/movie/%1$s/reviews";
    static final String MOVIE_TRAILERS_API_URL = MOVIE_HOST + "/movie/%1$s/videos";

    public MoviesService(Context context, String apiType, long movieId, RecyclerView.Adapter adapter) {
        if (apiType.equals(MOVIE_FETCH_MODE_REVIEWS)) {
            this.apiUrl = String.format(MOVIE_REVIEWS_API_URL, movieId);
        } else if (apiType.equals(MOVIE_FETCH_MODE_TRAILERS)) {
            this.apiUrl = String.format(MOVIE_TRAILERS_API_URL, movieId);
        }
        this.context = context;
        this.apiType = apiType;
        this.mRecyclerAdapter = adapter;
    }

    public MoviesService(Context context, String apiType, MoviesFragment parentFragment) {

        if (apiType.equals(MOVIE_FETCH_MODE_TOPRATED)) {
            this.apiUrl = MOVIE_TOP_RATED_API_URL;
        } else if (apiType.equals(MOVIE_FETCH_MODE_POPULAR)) {
            this.apiUrl = MOVIE_DISCOVER_API_URL;
        } else {
            Log.e(LOG_TAG, "Error: incorrect fetch type");
        }
        this.context = context;
        this.apiType = apiType;
        this.movieFragment = parentFragment;
    }

    @Override
    protected void onPostExecute(String jsonString) {
        super.onPostExecute(jsonString);
        try {
            if (apiType.equals(MOVIE_FETCH_MODE_TOPRATED) || apiType.equals(MOVIE_FETCH_MODE_POPULAR)) {
                // Save Contents into database
                saveMovieFromJson(jsonString);
                if (movieFragment != null) {
                    movieFragment.refreshList();
                }
                return;
            }

            if (apiType.equals(MOVIE_FETCH_MODE_REVIEWS)) {
                List<MovieReview> movieReviewsList = getReviewsDataFromJson(jsonString);
                if (mRecyclerAdapter != null) {
                    ((ReviewAdapter) mRecyclerAdapter).setData(movieReviewsList);
                }
            } else if (apiType.equals(MOVIE_FETCH_MODE_TRAILERS)) {
                List<MovieTrailer> movieTrailersList = getTrailersDataFromJson(jsonString);
                if (mRecyclerAdapter != null) {
                    ((TrailerAdapter) mRecyclerAdapter).setData(movieTrailersList);
                }
            }
        } catch (JSONException jsonException) {
            Log.e(LOG_TAG, jsonException.getMessage(), jsonException);
            jsonException.printStackTrace();
        }
    }

    private List<MovieReview> getReviewsDataFromJson(String jsonString) throws JSONException {
        if (jsonString == null) {
            return null;
        }

        JSONObject reviewsJSONObject = new JSONObject(jsonString);
        JSONArray reviewsArray = reviewsJSONObject.getJSONArray(REVIEW_RESULTS);

        ArrayList<MovieReview> reviewList = new ArrayList<>();

        for (int i = 0; i < reviewsArray.length(); i++) {
            JSONObject movieObject = reviewsArray.getJSONObject(i);

            String id = movieObject.getString(REVIEW_ID);
            String author = movieObject.getString(REVIEW_AUTHOR);
            String content = movieObject.getString(REVIEW_CONTENT);
            String url = movieObject.getString(REVIEW_URL);

            reviewList.add(new MovieReview(id, author, content, url));
        }
        return reviewList;
    }

    private List<MovieTrailer> getTrailersDataFromJson(String jsonString) throws JSONException {
        if (jsonString == null) {
            return null;
        }

        JSONObject trailersJSONObject = new JSONObject(jsonString);
        JSONArray trailerArray = trailersJSONObject.getJSONArray(TRAILER_RESULTS);

        ArrayList<MovieTrailer> trailerList = new ArrayList<>();

        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject movieObject = trailerArray.getJSONObject(i);

            String id = movieObject.getString(TRAILER_ID);
            String thumbnailUrl = buildThumbnailUrl(movieObject.getString(TRAILER_KEY), i);
            String videoUrl = buildYouTubeUrl(movieObject.getString(TRAILER_KEY));
            String name = movieObject.getString(TRAILER_NAME);
            String site = movieObject.getString(TRAILER_SITE);
            String size = movieObject.getString(TRAILER_SIZE);
            String type = movieObject.getString(TRAILER_TYPE);

            trailerList.add(new MovieTrailer(id, thumbnailUrl, videoUrl, name, site, size, type));
        }
        return trailerList;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String resultJsonStr = null;
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
                resultJsonStr = null;
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
                resultJsonStr = null;
            }
            resultJsonStr = buffer.toString();

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

        return resultJsonStr;
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
            long movieId = movieObject.getInt(MOVIE_ID);
            String imgUrl = buildImageURL(movieObject.getString(MOVIE_IMAGE));
            String description = movieObject.getString(MOVIE_DESC);
            String voteAverage = movieObject.getString(MOVIE_VOTE_AVG);
            String voteCount = movieObject.getString(MOVIE_VOTE_COUNT);
            String releaseYear = movieObject.getString(MOVIE_YEAR);
            String backDrop = buildImageURL(movieObject.getString(MOVIE_BACKDROP));

            ContentValues contentValues = new ContentValues();

            contentValues.put(MovieContract.MovieEntry.COLUMN_NAME, title);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseYear);
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_BITMAP, "blob");
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, imgUrl);
            contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, voteCount);
            contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
            contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, description);
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
                    new String[]{String.valueOf(movieId)},
                    null);

            if (cursor.getCount() == 0) {
                context.getContentResolver().insert(uri, contentValues);
            }
        }
        // TODO: bulk insert the values, checking for duplicates
//        ContentValues[] values = new ContentValues[movieItemInfoResult.size()];
//        movieItemInfoResult.toArray(values);
//        context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, null);
    }

    /**
     * http://stackoverflow.com/questions/2068344/how-do-i-get-a-youtube-video-thumbnail-from-the-youtube-api
     */
    private String buildThumbnailUrl(String key, int i) {
        return String.format(MOVIE_THUMBNAIL_URL, key, i%4);
    }

    private String buildYouTubeUrl(String key) {
        return String.format(MOVIE_YOUTUBE_URL, key);
    }
    private String buildImageURL(String imageUrl) {
        return MOVIE_IMAGE_URL + imageUrl;
    }
}