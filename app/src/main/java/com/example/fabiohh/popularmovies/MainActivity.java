package com.example.fabiohh.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            TitlesFragment titlesFragment = new TitlesFragment();
            titlesFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(R.id.container, titlesFragment).commit();
        }
    }
}