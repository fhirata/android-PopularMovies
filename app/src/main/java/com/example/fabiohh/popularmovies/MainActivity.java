package com.example.fabiohh.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            MoviesFragment moviesFragment = new MoviesFragment();
            moviesFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction()
                    .add(R.id.container, moviesFragment)
                    .commit();
        }
    }

}