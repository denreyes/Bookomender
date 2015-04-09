package com.example.dj.bookomender;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

/**
 * Created by DJ on 3/16/2015.
 */
public class BookFragment extends Fragment {
    String isbn;
    TextView txtTitle,txtAuthor,txtRating,txtDescription,txtSRating;
    ImageView imgBook;
    String post_title,post_desc,post_author,post_rate,post_id,post_img;
    ProgressDialog progressDialog;
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

        BookTask searchTask = new BookTask();
        searchTask.execute(isbn);
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


    public class BookTask extends AsyncTask<String,Void,Bundle> {
        private final String LOG_TAG = getClass().getSimpleName();
        StringBuffer buffer;
        HttpURLConnection urlConnection;
        BufferedReader reader;
        Bundle bundle = null;

        public BookTask(){}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String[] loadPhrases = getResources().getStringArray(R.array.load_phrases);
            int rnd = new Random().nextInt(loadPhrases.length-1);
            progressDialog = ProgressDialog.show(getActivity(),null,loadPhrases[rnd]);
        }

        @Override
        protected Bundle doInBackground(String... params) {
            try {
                bundle = getItems(connectGoodreads(params[0]));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return bundle;
        }

        private String connectGoodreads(String value_isbn)    {
            final String M_RESULT_BASE_URL = "https://www.goodreads.com/book/isbn?";
            final String M_ISBN = "isbn";
            final String M_KEY = "key";

            final String M_VALUE_KEY = "UpH4L0IYjAXcezlfg0yT2Q";

            String xml = null;
            try {
                Uri builtUri = Uri.parse(M_RESULT_BASE_URL).buildUpon()
                        .appendQueryParameter(M_ISBN, value_isbn)
                        .appendQueryParameter(M_KEY,M_VALUE_KEY)
                        .build();
                URL url = new URL(builtUri.toString());

                InputStream is = url.openStream();
                int ptr = 0;
                StringBuilder builder = new StringBuilder();
                while ((ptr = is.read()) != -1) {
                    builder.append((char) ptr);
                }

                xml = builder.toString();
                try {
                    return XML.toJSONObject(xml).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private Bundle getItems(String resultJsonStr)
                throws JSONException {
            try {
                final String GOOD_READS_RESPONSE = "GoodreadsResponse";
                final String BOOK = "book";

                final String M_TITLE = "title";
                final String M_ID = "id";
                final String M_DESC = "description";
                final String M_IMG = "small_image_url";
                final String M_RATE = "average_rating";
                final String M_AUTHORS = "authors";
                final String M_AUTHOR = "author";
                final String M_AUTHOR_NAME = "name";

                JSONObject jsonObject = new JSONObject(resultJsonStr);
                JSONObject jsonGoodReadsResponse = jsonObject.getJSONObject(GOOD_READS_RESPONSE);
                JSONObject jsonBook = jsonGoodReadsResponse.getJSONObject(BOOK);

                String bookTitle = jsonBook.getString(M_TITLE);
                String bookId = jsonBook.getString(M_ID);
                String desc = jsonBook.getString(M_DESC);
                String rate = jsonBook.getString(M_RATE);
                String img = jsonBook.getString(M_IMG);
                if(img.equals("https://s.gr-assets.com/assets/nophoto/book/50x75-a91bf249278a81aabab721ef782c4a74.png")) {
                    img="noimage";
                }else{
                    img = img.replace("s/", "l/");
                    img = img.replace("kl/", "ks/");}
                String authorName = null;
                JSONObject jsonAuthors = jsonBook.getJSONObject(M_AUTHORS);


                try {
                    JSONArray arrayAuthor = jsonAuthors.getJSONArray(M_AUTHOR);
                    for (int i = 0; i < arrayAuthor.length(); i++) {
                        if (i == 0) {
                            authorName = arrayAuthor.getJSONObject(i).getString(M_AUTHOR_NAME);
                        } else if (i != arrayAuthor.length()) {
                            authorName = authorName + ", " + arrayAuthor.getJSONObject(i).getString(M_AUTHOR_NAME);
                        }
                    }
                } catch (JSONException e) {
                    JSONObject jsonAuthor = jsonAuthors.getJSONObject(M_AUTHOR);
                    authorName = jsonAuthor.getString(M_AUTHOR_NAME);
                }

                Bundle bundle = new Bundle();

                bundle.putString("M_TITLE", bookTitle);
                bundle.putString("M_ID", bookId);
                bundle.putString("M_AUTHOR_NAME", authorName);
                bundle.putString("M_DESC", desc);
                bundle.putString("M_RATE", rate);
                bundle.putString("M_IMG", img);

                return bundle;
            }catch(NullPointerException e){
                return null;
            }
        }


        @Override
        protected void onPostExecute(Bundle bundle) {
            super.onPostExecute(bundle);
                progressDialog.dismiss();
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
    }
}