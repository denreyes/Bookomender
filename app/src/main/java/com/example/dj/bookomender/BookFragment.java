package com.example.dj.bookomender;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
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

import org.jsoup.Jsoup;

import java.util.Random;

/**
 * Created by DJ on 3/16/2015.
 */
public class BookFragment extends Fragment{
    String isbn;
    TextView txtTitle,txtAuthor,txtRating,txtDescription,txtSRating;
    ImageView imgBook;
    String post_title,post_desc,post_author,post_rate,post_id,post_img;
    ProgressDialog pd;
    ViewGroup bookView;

    public BookFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book, container, false);
        isbn = getArguments().getString("ISBN");

        txtTitle = (TextView) rootView.findViewById(R.id.txtTitle);
        txtAuthor = (TextView) rootView.findViewById(R.id.txtAuthor);
        txtRating = (TextView) rootView.findViewById(R.id.txtRating);
        txtSRating = (TextView) rootView.findViewById(R.id.txtSRating);
        txtDescription = (TextView) rootView.findViewById(R.id.txtDescription);
        imgBook = (ImageView) rootView.findViewById(R.id.imgBook);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BookIntentService.TRANSACTION_DONE);
        getActivity().registerReceiver(bookReceiver, intentFilter);
        Intent i = new Intent(getActivity(), BookIntentService.class);
        i.putExtra("ISBN",isbn);
        getActivity().startService(i);

        String[] loadPhrases = getResources().getStringArray(R.array.load_phrases);
        int rnd = new Random().nextInt(loadPhrases.length-1);
        pd = ProgressDialog.show(getActivity(),null,loadPhrases[rnd]);

        return rootView;
    }

    public void addBook(){
        BookDBHelper bookDBHelper = new BookDBHelper(getActivity());
        SQLiteDatabase sqLiteDatabase = bookDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(BookContract.BookEntry.COLUMN_BOOK_TITLE, post_title);
        contentValues.put(BookContract.BookEntry.COLUMN_DESC, post_desc);
        contentValues.put(BookContract.BookEntry.COLUMN_AUTHOR, post_author);
        contentValues.put(BookContract.BookEntry.COLUMN_RATING, post_rate);
        contentValues.put(BookContract.BookEntry.COLUMN_ID, post_id);
        contentValues.put(BookContract.BookEntry.COLUMN_IMG, post_img);

        long locationRowId = sqLiteDatabase.insert(BookContract.BookEntry.TABLE_NAME, null, contentValues);

        if (locationRowId == -1) {
            Toast.makeText(getActivity(), "Book Already exists.", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getActivity(),post_title+" Added.",Toast.LENGTH_LONG).show();
        }
    }

    private BroadcastReceiver bookReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();

            pd.dismiss();
            post_title = bundle.getString("M_TITLE");

            String text = Jsoup.parse(bundle.getString("M_DESC").replaceAll("(?i)<br[^>]*>", "CH4NG3S")
                    .replaceAll("<p>", "CH4NG3S").replaceAll("</p>", "CH4NG3SCH4NG3S")).text();
            post_desc = text.replaceAll("CH4NG3S", "\n").replaceAll("â", "'");

            post_author = bundle.getString("M_AUTHOR_NAME");
            post_rate = bundle.getString("M_RATE");
            post_id = bundle.getString("M_ID");
            post_img = bundle.getString("M_IMG");

            txtTitle.setText(post_title);
            txtDescription.setText(post_desc);
            txtAuthor.setText(post_author);
            txtRating.setText("Averate Rating: "+ post_rate);

            String x="★★★★★";
            int aveRating = (int) Math.round(Double.parseDouble(post_rate));

            final SpannableStringBuilder sb = new SpannableStringBuilder(x);
            final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.dark_main));
            sb.setSpan(fcs, 0, aveRating, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            txtSRating.setText(sb);

            String l_img = bundle.getString("M_IMG");

            if(l_img=="noimage"){
                Picasso.with(getActivity())
                        .load(R.drawable.unknown_y)
                        .resize(500, 810)
                        .centerCrop()
                        .into(imgBook);
            }
            else {
                Picasso.with(getActivity())
                        .load(l_img)
                        .resize(500, 810)
                        .centerCrop()
                        .into(imgBook);
            }

            if (getActivity().findViewById(R.id.container_x) != null) {
                bookView = (LinearLayout) getActivity().findViewById(R.id.linearBookView);
            } else {
                bookView = (ScrollView) getActivity().findViewById(R.id.scrollBookView);
            }
            try {
                bookView.setVisibility(View.VISIBLE);
            }catch (NullPointerException e){
                if(getActivity().getLocalClassName().equals("BookActivity")) {
                    getActivity().finish();
                }
            }
        }
    };
}