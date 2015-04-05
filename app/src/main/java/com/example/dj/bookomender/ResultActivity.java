package com.example.dj.bookomender;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by DJ on 3/10/2015.
 */
public class ResultActivity extends ActionBarActivity {
    ResultFragment resultFragment;
    String isbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bravo);

        isbn = getIntent().getStringExtra("ISBN");

        resultFragment = new ResultFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, resultFragment)
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ActionBar actionBar = ((ActionBarActivity) this).getSupportActionBar();
        actionBar.setTitle(getIntent().getStringExtra("BOOK_TITLE"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        if(findViewById(R.id.container_x)!=null)
            inflater.inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_info:
                resultFragment.onItemClick(
                        isbn);
                break;
            case R.id.action_add:
                resultFragment.addBook();
                break;
        }
        return true;
    }
}
