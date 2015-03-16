package com.example.dj.bookomender;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by DJ on 3/16/2015.
 */
public class SaveFragment extends Fragment{
    private static final int RESULT_LOADER = 0;

    ListView listResult;

    public SaveFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);

        listResult = (ListView)rootView.findViewById(R.id.listview_search_result);
        Cursor cursor = new BookDBHelper(getActivity()).getReadableDatabase().
                query(BookContract.BookEntry.TABLE_NAME, null, null, null, null, null, null);
        listResult.setAdapter(new CursorAdapter(getActivity(),cursor,false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.list_item_result, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ImageView imgBook = (ImageView)view.findViewById(R.id.imgBook);
                TextView txtTitle = (TextView)view.findViewById(R.id.txtTitle);
//                TextView txtDescription = (TextView)view.findViewById(R.id.txtDescription);
                TextView txtAuthor = (TextView)view.findViewById(R.id.txtAuthor);
                TextView txtRating = (TextView)view.findViewById(R.id.txtRating);

                txtTitle.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE)));
//                txtDescription.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_DESC)));
                txtAuthor.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR)));
                txtRating.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RATING)));
                Picasso.with(context)
                        .load(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_IMG)))
                        .resize(50, 81)
                        .centerCrop()
                        .into(imgBook);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}