package com.example.dj.bookomender;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by DJ on 3/15/2015.
 */
public class ResultAdapter extends CursorAdapter {


    public ResultAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_result, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView txtTitle = (TextView)view.findViewById(R.id.txtTitle);
        TextView txtAuthor = (TextView)view.findViewById(R.id.txtAuthor);
        TextView txtRating = (TextView)view.findViewById(R.id.txtRating);
        ImageView imageView = (ImageView)view.findViewById(R.id.imgBook);

        String x="★★★★★";
        int aveRating = (int) Math.round(Double.parseDouble((cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_RATING)))));

        if(aveRating!=0) {
            final SpannableStringBuilder sb = new SpannableStringBuilder(x);
            final ForegroundColorSpan fcs = new ForegroundColorSpan(context.getResources().getColor(R.color.main));
            sb.setSpan(fcs, 0, aveRating, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            try {
                txtRating.setText(sb);
            } catch (NullPointerException e) {
            }

            txtTitle.setText(cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_BOOK_TITLE)));
            txtAuthor.setText(cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_AUTHOR)));
            if ((cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_IMG)))
                    .equals("https://s.gr-assets.com/assets/nophoto/book/111x148-bcc042a9c91a29c1d680899eff700a03.png")
                    || (cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_IMG))).equals("noimage")) {
                Picasso.with(context)
                        .load(R.drawable.unknown_g)
                        .resize(80, 130)
                        .centerCrop()
                        .into(imageView);
            } else {
                Picasso.with(context)
                        .load((cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_IMG))))
                        .resize(80, 130)
                        .centerCrop()
                        .into(imageView);
            }
        }
    }
}