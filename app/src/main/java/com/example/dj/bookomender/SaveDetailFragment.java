package com.example.dj.bookomender;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by DJ on 3/25/2015.
 */
public class SaveDetailFragment extends Fragment{
    TextView txtTitle,txtAuthor,txtRating,txtDescription,txtSRating;
    ImageView imgBook;
    ViewGroup layoutView;
    Cursor cursor;

    public SaveDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book, container, false);
        Toast.makeText(getActivity(), getActivity().getLocalClassName(),Toast.LENGTH_LONG).show();

        txtTitle = (TextView) rootView.findViewById(R.id.txtTitle);
        txtAuthor = (TextView) rootView.findViewById(R.id.txtAuthor);
        txtRating = (TextView) rootView.findViewById(R.id.txtRating);
        txtSRating = (TextView) rootView.findViewById(R.id.txtSRating);
        txtDescription = (TextView) rootView.findViewById(R.id.txtDescription);
        imgBook = (ImageView) rootView.findViewById(R.id.imgBook);

            if (getActivity().findViewById(R.id.container_x) != null)
                layoutView = (LinearLayout) rootView.findViewById(R.id.linearBookView);
            else
                layoutView = (ScrollView) rootView.findViewById(R.id.scrollBookView);
        queryList();
        return rootView;
    }

    public void delete(){
        new BookDBHelper(getActivity()).getReadableDatabase().delete(BookContract.BookEntry.TABLE_NAME,
                "_id = " + getArguments().getString("ID"), null);
        if(getActivity().findViewById(R.id.container_x)!=null)
            layoutView.setVisibility(View.INVISIBLE);
    }

    public void queryList(){

        cursor = new BookDBHelper(getActivity()).getReadableDatabase().
                query(BookContract.BookEntry.TABLE_NAME, null, "_id = " + getArguments().getString("ID") , null, null, null, null);
        if(cursor.moveToFirst()){
            try{
                layoutView.setVisibility(View.VISIBLE);}
            catch (NullPointerException e){
                getActivity().finish();
            }

            String rate = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RATING));
            String x="★★★★★";
            int aveRating = (int) Math.round(Double.parseDouble(rate));
            final SpannableStringBuilder sb = new SpannableStringBuilder(x);
            final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.dark_main));
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
