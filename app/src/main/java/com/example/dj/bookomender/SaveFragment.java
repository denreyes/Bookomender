package com.example.dj.bookomender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by DJ on 3/16/2015.
 */
public class SaveFragment extends Fragment{

    ListView listResult;

    public SaveFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);

        listResult = (ListView)rootView.findViewById(R.id.listview_search_result);
        final Cursor cursor = new BookDBHelper(getActivity()).getReadableDatabase().
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
                TextView txtAuthor = (TextView)view.findViewById(R.id.txtAuthor);
                TextView txtRating = (TextView)view.findViewById(R.id.txtRating);

                txtTitle.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE)));
                txtAuthor.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR)));

                String x="★★★★★";
                int aveRating = (int) Math.round(Double.parseDouble(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RATING))));

                final SpannableStringBuilder sb = new SpannableStringBuilder(x);
                final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.teal));
                sb.setSpan(fcs, 0, aveRating, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                txtRating.setText(sb);
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
        });
        listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String isbn = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_ID));
                Intent i = new Intent(getActivity(),BookActivity.class);
                i.putExtra("ISBN",isbn);
                getActivity().startActivity(i);
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}