package com.example.dj.bookomender;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by DJ on 3/16/2015.
 */
public class BookActivity extends ActionBarActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            BookFragment bookFragment = new BookFragment();
            Bundle bundle = new Bundle();
            bundle.putString("ISBN",getIntent().getStringExtra("ISBN"));
            bookFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, bookFragment)
                    .commit();
        }
    }
}