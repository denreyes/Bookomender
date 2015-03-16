package com.example.dj.bookomender;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by DJ on 3/16/2015.
 */
public class SaveActivity extends ActionBarActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            SaveFragment saveFragment = new SaveFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, saveFragment)
                    .commit();
        }
    }
}