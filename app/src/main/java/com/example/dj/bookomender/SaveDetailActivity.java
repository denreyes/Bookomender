package com.example.dj.bookomender;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class SaveDetailActivity extends ActionBarActivity {
    PlaceholderFragment placeholderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String id = getIntent().getStringExtra("ID");
        Bundle bundle = new Bundle();
        bundle.putString("ID",id);
        placeholderFragment = new PlaceholderFragment();
        placeholderFragment.setArguments(bundle);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, placeholderFragment)
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
                placeholderFragment.delete();
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        TextView txtTitle,txtAuthor,txtRating,txtDescription,txtSRating;
        ImageView imgBook;
        ScrollView bookView;
        Cursor cursor;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_book, container, false);

            txtTitle = (TextView) rootView.findViewById(R.id.txtTitle);
            txtAuthor = (TextView) rootView.findViewById(R.id.txtAuthor);
            txtRating = (TextView) rootView.findViewById(R.id.txtRating);
            txtSRating = (TextView) rootView.findViewById(R.id.txtSRating);
            txtDescription = (TextView) rootView.findViewById(R.id.txtDescription);
            imgBook = (ImageView) rootView.findViewById(R.id.imgBook);

            bookView = (ScrollView)rootView.findViewById(R.id.bookView);

            queryList();
            return rootView;
        }

        public void delete(){
            new BookDBHelper(getActivity()).getReadableDatabase().delete(BookContract.BookEntry.TABLE_NAME,
                    "_id = " + getArguments().getString("ID"), null);
        }

        public void queryList(){
            cursor = new BookDBHelper(getActivity()).getReadableDatabase().
                    query(BookContract.BookEntry.TABLE_NAME, null, "_id = " + getArguments().getString("ID") , null, null, null, null);
            if(cursor.moveToFirst()){
                bookView.setVisibility(View.VISIBLE);

                String rate = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RATING));
                String x="★★★★★";
                int aveRating = (int) Math.round(Double.parseDouble(rate));
                final SpannableStringBuilder sb = new SpannableStringBuilder(x);
                final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.dark_teal));
                sb.setSpan(fcs, 0, aveRating, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                txtSRating.setText(sb);
                txtRating.setText(rate);
                txtTitle.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE)));
                txtAuthor.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR)));
                txtDescription.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_DESC)));

                String img = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_IMG));
                if(img.equals("noimage")){
                    Picasso.with(getActivity())
                            .load(R.drawable.unknown_g)
                            .resize(80, 131)
                            .centerCrop()
                            .into(imgBook);
                }
                else {
                    Picasso.with(getActivity())
                            .load(img)
                            .resize(80, 131)
                            .centerCrop()
                            .into(imgBook);
                }
            }
        }
    }
}
