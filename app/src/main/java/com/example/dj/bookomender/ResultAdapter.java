package com.example.dj.bookomender;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by DJ on 3/15/2015.
 */
public class ResultAdapter extends BaseAdapter {
    String[] s_title, s_isbn, s_rating, s_image, s_author;
    LayoutInflater inflater;
    Context context;

    public ResultAdapter(Bundle bundle, Context context){
        inflater  = LayoutInflater.from(context);
        try {
            this.context = context;

            int size = bundle.getInt("M_SIMILAR_SIZE");
            s_title = new String[size];
            s_isbn = new String[size];
            s_rating = new String[size];
            s_image = new String[size];
            s_author = new String[size];

            s_title = bundle.getStringArray("M_SIMILAR_TITLE");
            s_isbn = bundle.getStringArray("M_SIMILAR_ISBN_13");
            s_rating = bundle.getStringArray("M_SIMILAR_RATING");
            s_image = bundle.getStringArray("M_SIMILAR_IMAGE_URL");
            s_author = bundle.getStringArray("M_SIMILAR_AUTHOR_NAME");
        }catch (NullPointerException e){
            Log.e(getClass().getSimpleName(),"Nothing is Inside!");
        }
    }

    @Override
    public int getCount() {
        return s_title.length;
    }

    @Override
    public Object getItem(int position) {
        return s_isbn[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.list_item_result, parent, false);
        TextView txtTitle = (TextView)row.findViewById(R.id.txtTitle);
        TextView txtAuthor = (TextView)row.findViewById(R.id.txtAuthor);
        TextView txtRating = (TextView)row.findViewById(R.id.txtRating);
        ImageView imageView = (ImageView)row.findViewById(R.id.imgBook);

        String x="★★★★★";
        int aveRating = (int) Math.round(Double.parseDouble(s_rating[position]));

        final SpannableStringBuilder sb = new SpannableStringBuilder(x);
        final ForegroundColorSpan fcs = new ForegroundColorSpan(context.getResources().getColor(R.color.main));
        sb.setSpan(fcs, 0, aveRating, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        try {
            txtRating.setText(sb);
        }catch (NullPointerException e){}

        txtTitle.setText(s_title[position]);
        txtAuthor.setText(s_author[position]);
        if(s_image[position].equals("https://s.gr-assets.com/assets/nophoto/book/111x148-bcc042a9c91a29c1d680899eff700a03.png")
                || s_image[position].equals("noimage")) {
            Picasso.with(context)
                    .load(R.drawable.unknown_g)
                    .resize(80, 130)
                    .centerCrop()
                    .into(imageView);
        }else {
            Picasso.with(context)
                    .load(s_image[position])
                    .resize(80, 130)
                    .centerCrop()
                    .into(imageView);
        }
        return row;
    }
}