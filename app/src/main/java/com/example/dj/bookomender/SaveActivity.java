package com.example.dj.bookomender;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by DJ on 3/16/2015.
 */
public class SaveActivity extends ActionBarActivity{
    SaveFragment saveFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bravo);

        if (savedInstanceState == null) {
            saveFragment = new SaveFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, saveFragment)
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
            case R.id.action_delete:
                saveFragment.delete();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        if(findViewById(R.id.container_x)!=null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_delete, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void orienChange(String id){
        SaveDetailFragment saveDetailFragment = new SaveDetailFragment();
        Bundle b_bundle = new Bundle();
        b_bundle.putString("ID", id);
        saveDetailFragment.setArguments(b_bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_x, saveDetailFragment)
                .commit();
    }
}