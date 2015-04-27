package com.example.dj.bookomender;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by DJ on 4/26/2015.
 */
public class BookIntentService extends IntentService {

    String LOG_TAG = getClass().getSimpleName();
    HttpURLConnection urlConnection;
    BufferedReader reader;
    Bundle bundle = null;

    public BookIntentService() {
        super("BookIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String[] loadPhrases = getResources().getStringArray(R.array.load_phrases);

        try {
            bundle = getItems(connectGoodreads(intent.getExtras().getString("ISBN")));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            return;
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
            notifyFinished(bundle);
        }
    }

    public static final String TRANSACTION_DONE =
            "com.example.dj.bookomender.BOOK_TRANSACTION_DONE";
    private void notifyFinished(Bundle bundle){
        Intent i = new Intent(TRANSACTION_DONE);
        i.putExtras(bundle);

        BookIntentService.this.sendBroadcast(i);
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
}
