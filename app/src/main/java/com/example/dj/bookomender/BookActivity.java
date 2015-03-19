package com.example.dj.bookomender;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by DJ on 3/16/2015.
 */
public class BookActivity extends ActionBarActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}