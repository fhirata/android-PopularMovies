package com.example.fabiohh.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.example.fabiohh.popularmovies.adapters.MovieAdapter;
import com.example.fabiohh.popularmovies.db.MovieContract;
import com.example.fabiohh.popularmovies.services.MoviesService;

/**
 * Created by fabiohh on 8/23/16.
 * reference for Loader:
 * http://stackoverflow.com/questions/25208402/getloadermanager-initloader-doesnt-accept-this-as-argument-though-the-cla
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, Preference.OnPreferenceChangeListener {
    MovieAdapter movieAdapter;
    LayoutInflater mInflater;
    String mFetchMode;

    private static final int MOVIE_LOADER = 1;

    public static final int MOVIE_ITEM_POSITION_CODE = 6767;

    // API Modes
    public static final String MOVIE_FETCH_MODE_POPULAR = "popular";
    public static final String MOVIE_FETCH_MODE_TOPRATED = "toprated";
    public static final String MOVIE_FETCH_MODE_FAVORITE = "favorite";

    // Detail Fragment API Modes
    public static final String MOVIE_FETCH_MODE_REVIEWS = "reviews";
    public static final String MOVIE_FETCH_MODE_TRAILERS = "trailers";

    static final String MOVIE_API_PREFERENCE = "api_mode";
    String movieApiUrl;


    private static final String[] MOVIES_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            MovieContract.FavoriteEntry.TABLE_NAME + "." + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_BITMAP
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_POSTER_URL = 2;
    public static final int COL_FAVORITE = 3;
    public static final int COL_POSTER_BITMAP = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.grid_movies);

        mInflater = inflater;
        movieAdapter = new MovieAdapter(getActivity(), null, 0);
        gridview.setAdapter(movieAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use last user's preference if available
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mFetchMode = preferences.getString(MOVIE_API_PREFERENCE, MOVIE_FETCH_MODE_TOPRATED);

        setHasOptionsMenu(true);
    }


    @Override
    public void onResume() {
        super.onResume();

//        updateData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.menu_popular:
                mFetchMode = MOVIE_FETCH_MODE_POPULAR;
                break;
            case R.id.menu_toprated:
                mFetchMode = MOVIE_FETCH_MODE_TOPRATED;
                break;
            case R.id.menu_favorite:
                mFetchMode = MOVIE_FETCH_MODE_FAVORITE;
                break;
        }
        updateData();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().putString(MOVIE_API_PREFERENCE, mFetchMode).apply();

        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);

        return super.onOptionsItemSelected(menuItem);
    }

    public boolean networkConnected() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();


        return netInfo != null && netInfo.isAvailable() && netInfo.isConnectedOrConnecting();
    }

    private void updateData() {

        if (mFetchMode.equals(MOVIE_FETCH_MODE_TOPRATED)) {
            getActivity().setTitle(getString(R.string.app_name) + ": " + getString(R.string.top_rated));
        } else if (mFetchMode.equals(MOVIE_FETCH_MODE_POPULAR)){
            getActivity().setTitle(getString(R.string.app_name) + ": " + getString(R.string.popular));
        } else {
            String favorite = getString(R.string.app_name);
            getActivity().setTitle(favorite + ": " + getString(R.string.favorite));
        }

        if (mFetchMode.equals(MOVIE_FETCH_MODE_TOPRATED) || mFetchMode.equals(MOVIE_FETCH_MODE_POPULAR)) {
            MoviesService moviesService = new MoviesService(getActivity(), mFetchMode);
            moviesService.execute();
        }

        if (!networkConnected()) {
            Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection.  Please check your connection and try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
        preference.setSummary(stringValue);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieContract.MovieEntry.buildMovieUri(mFetchMode);

        return new CursorLoader(getActivity(),
                uri,
                MOVIES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }
}

