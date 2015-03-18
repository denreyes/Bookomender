package com.example.dj.bookomender;

import android.content.Context;
import android.os.Bundle;
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
    String title, isbn, author;
    String[] s_title, s_isbn, s_rating, s_image, s_author;
    LayoutInflater inflater;
    Context context;

    public ResultAdapter(Bundle bundle, Context context){
        inflater  = LayoutInflater.from(context);
        try {
            this.context = context;
            title = bundle.getString("M_TITLE");
            isbn = bundle.getString("M_ISBN_13");
            author = bundle.getString("M_AUTHOR_NAME");

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

        String x="";
        for(int y=0;y<((int) Math.round(Double.parseDouble(s_rating[position])));y++)
            x = x+"*";

        txtTitle.setText(s_title[position]);
        txtAuthor.setText(s_author[position]);
        txtRating.setText(x);
        Picasso.with(context)
                .load(s_image[position])
                .resize(80, 130)
                .centerCrop()
                .into(imageView);
        return row;
    }
}