package com.example.fabiohh.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.example.fabiohh.popularmovies.models.MovieItem;
import com.example.fabiohh.popularmovies.services.MoviesService;

import java.util.ArrayList;

/**
 * Created by fabiohh on 8/23/16.
 */
public class MoviesFragment extends Fragment implements Preference.OnPreferenceChangeListener {
    static final String MOVIE_DISCOVER_API_URL = "http://api.themoviedb.org/3/discover/movie";
    static final String MOVIE_TOP_RATED_API_URL = "http://api.themoviedb.org/3/movie/top_rated";

    ImageAdapter mImageAdapter;
    LayoutInflater mInflater;
    int mApiMode;

    static final int MOVIE_ITEM_POSITION_CODE = 6767;

    // Preferences
    static final int MOVIE_API_MODE_POPULAR = 0;
    static final int MOVIE_API_MODE_TOPRATED = 1;
    static final String MOVIE_API_PREFERENCE = "api_mode";
    String movieApiUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.grid_movies);

        mInflater = inflater;
        mImageAdapter = new ImageAdapter(getActivity(), new ArrayList<MovieItem>());
        gridview.setAdapter(mImageAdapter);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use last user's preference if available
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (preferences.contains(MOVIE_API_PREFERENCE)) {
            mApiMode = preferences.getInt(MOVIE_API_PREFERENCE, MOVIE_API_MODE_TOPRATED);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateData();
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
        updateData();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().putInt(MOVIE_API_PREFERENCE, mApiMode).apply();

        return super.onOptionsItemSelected(menuItem);
    }

    public boolean networkConnected() {
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();


        return netInfo != null && netInfo.isAvailable() && netInfo.isConnectedOrConnecting();
    }

    private void updateData() {

        if (mApiMode == MOVIE_API_MODE_TOPRATED) {
            getActivity().setTitle(getString(R.string.app_name) + ": " + getString(R.string.top_rated));
        } else {
            getActivity().setTitle(getString(R.string.app_name) + ": " + getString(R.string.popular));
        }

        movieApiUrl = (mApiMode == MOVIE_API_MODE_TOPRATED) ?
                MOVIE_TOP_RATED_API_URL : MOVIE_DISCOVER_API_URL;

        if (networkConnected()) {
            MoviesService moviesService = new MoviesService(getActivity().getApplicationContext(), movieApiUrl, mImageAdapter);
            moviesService.execute();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection.  Please check your connection and try again.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        preference.setSummary(stringValue);
        return true;
    }
}

