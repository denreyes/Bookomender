package com.example.dj.bookomender;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

/**
 * Created by DJ on 3/10/2015.
 */
public class ResultActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            ResultFragment resultFragment = new ResultFragment();
            Bundle bundle = new Bundle();
            bundle.putString("SEARCH",getIntent().getStringExtra("SEARCH"));
            resultFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, resultFragment)
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
}
