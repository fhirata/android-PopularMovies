package com.example.fabiohh.popularmovies;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.example.fabiohh.popularmovies.models.MovieItem;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            return;
        }

        if (findViewById(R.id.container) != null) {
            // During initial setup, plug in the details fragment.
            MoviesFragment moviesFragment = new MoviesFragment();

            moviesFragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction()
                    .add(R.id.container, moviesFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.movies, menu);

        return true;
    }

    public void onMovieSelected(int position, MovieItem movieItem) {
        // The user selected a movie item

        // Capture the detail fragment from the activity layout
        DetailFragment detailFragmentLandscape = (DetailFragment)
                getFragmentManager().findFragmentById(R.id.detail_fragment_land);

        if (detailFragmentLandscape != null) {
            // DetailFragment frag is available, so we're in two-pane layout.

            // Call a method in the ArticleFragment to update its content
            detailFragmentLandscape.updateMovieDetail(movieItem, detailFragmentLandscape.getView());
        }
    }

    public void handleNoAPIResponse() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_api_response)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        builder.show();
    }
}