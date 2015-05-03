package com.example.dj.bookomender;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by DJ on 4/26/2015.
 */
public class MainIntentService extends IntentService {
    private final String LOG_TAG = getClass().getSimpleName();
    StringBuffer buffer;
    HttpURLConnection urlConnection;
    BufferedReader reader;
    private static final String RESULT_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static final String QUERY_PARAM = "q";
    private  static final String MAX_RESULT = "maxResults";
    private static final String ORDER_BY = "orderBy";
    private static final String VALUE_SINGLE_RESULT = "1";
    private static final String VALUE_ORDER_BY = "relevance";
    ProgressDialog progressDialog;

    ResultAdapter adapter;
    String searchIsbn;
    LayoutInflater inflater;
    String search, bookTitle, bookIsbn;

    public MainIntentService() {
        super("MainIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle bundle = null;
        urlConnection = null;
        reader = null;

        String resultJsonStr = null;
        ArrayList<String> results = new ArrayList<String>();

        getContentResolver().delete(ResultContract.ResultEntry.CONTENT_URI,null,null);
        try {
            Uri builtUri = Uri.parse(RESULT_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, intent.getStringExtra("SEARCH"))
                    .appendQueryParameter(MAX_RESULT,VALUE_SINGLE_RESULT)
                    .appendQueryParameter(ORDER_BY,VALUE_ORDER_BY)
                    .build();
            URL url = new URL(builtUri.toString());

            resultJsonStr = getJsonString(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to network", e);
            return;
        }
        finally{
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

            try {
                searchIsbn = getISBN(buffer.toString());
                similarReads(connectGoodreads(getISBN(buffer.toString())));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            notifyFinished();
        }
    }

    public static final String TRANSACTION_DONE =
            "com.example.dj.bookomender.MAIN_TRANSACTION_DONE";
    private void notifyFinished(){
        Intent i = new Intent(TRANSACTION_DONE);
        i.putExtra("ISBN",bookIsbn);
        i.putExtra("BOOK_TITLE",bookTitle);
        MainIntentService.this.sendBroadcast(i);
    }

    private String getJsonString(URL url) throws IOException {
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        buffer = new StringBuffer();
        if (inputStream == null) {
            return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            return null;
        }
        return buffer.toString();
    }

    private String getISBN(String resultJSON) throws JSONException {
        final String M_ITEMS = "items";
        final String M_VOLUME_INFO = "volumeInfo";
        final String M_INDUSTRY_IDENTIFIER = "industryIdentifiers";

        final String M_TYPE = "type";
        final String M_IDENTIFIER = "identifier";

        JSONObject jsonObject = new JSONObject(resultJSON);
        JSONArray items = jsonObject.getJSONArray(M_ITEMS);
        JSONObject itemOne = items.getJSONObject(0);
        JSONObject volumeInfo = itemOne.getJSONObject(M_VOLUME_INFO);
        JSONArray industryId = volumeInfo.getJSONArray(M_INDUSTRY_IDENTIFIER);
        int x = 0;
        String isbn;
        do{
            isbn=industryId.getJSONObject(x).getString(M_IDENTIFIER).toString();
        }while (industryId.getJSONObject(x++).getString(M_TYPE).toString() == "ISBN_13");

        return isbn;
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

    private void similarReads(String resultJsonStr)
            throws JSONException {
        final String GOOD_READS_RESPONSE = "GoodreadsResponse";
        final String BOOK = "book";

        final String M_TITLE = "title";
        final String M_ISBN_13 = "isbn13";
        final String M_AUTHORS = "authors";
        final String M_AUTHOR = "author";
        final String M_AUTHOR_NAME = "name";

        final String M_SIMILAR = "similar_books";
        final String M_SIMILAR_BOOK = "book";
        final String M_SIMILAR_TITLE = "title";
        final String M_SIMILAR_ISBN_13 = "isbn13";
        final String M_SIMILAR_RATING = "average_rating";
        final String M_SIMILAR_IMAGE_URL = "image_url";
        final String M_SIMILAR_AUTHORS = "authors";
        final String M_SIMILAR_AUTHOR = "author";
        final String M_SIMILAR_AUTHOR_NAME = "name";

        try {
            JSONObject jsonObject = new JSONObject(resultJsonStr);
            JSONObject jsonGoodReadsResponse = jsonObject.getJSONObject(GOOD_READS_RESPONSE);
            JSONObject jsonBook = jsonGoodReadsResponse.getJSONObject(BOOK);

            bookTitle = jsonBook.getString(M_TITLE);
            bookIsbn = jsonBook.getString(M_ISBN_13);
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

            JSONObject jsonSimilar = jsonBook.getJSONObject(M_SIMILAR);
            JSONArray arraySimBooks = jsonSimilar.getJSONArray(M_SIMILAR_BOOK);

            String[] simTitle = new String[arraySimBooks.length()];
            String[] simIsbn = new String[arraySimBooks.length()];
            String[] simRating = new String[arraySimBooks.length()];
            String[] simImage = new String[arraySimBooks.length()];
            String[] simAuthor = new String[arraySimBooks.length()];
            for (int y = 0; y < arraySimBooks.length(); y++) {
                simTitle[y] = arraySimBooks.getJSONObject(y).getString(M_SIMILAR_TITLE);
                simIsbn[y] = arraySimBooks.getJSONObject(y).getString(M_SIMILAR_ISBN_13);
                simRating[y] = arraySimBooks.getJSONObject(y).getString(M_SIMILAR_RATING);

                JSONObject jsonSimAuthors = arraySimBooks.getJSONObject(y).getJSONObject(M_SIMILAR_AUTHORS);
                try {
                    JSONArray arraySimAuthor = jsonSimAuthors.getJSONArray(M_SIMILAR_AUTHOR);
                    for (int i = 0; i < arraySimAuthor.length(); i++) {
                        if (i == 0) {
                            simAuthor[y] = arraySimAuthor.getJSONObject(i).getString(M_SIMILAR_AUTHOR_NAME);
                        } else if (i != arraySimAuthor.length()) {
                            simAuthor[y] = simAuthor + ", " + arraySimAuthor.getJSONObject(i).getString(M_SIMILAR_AUTHOR_NAME);
                        }
                    }
                } catch (JSONException e) {
                    JSONObject jsonAuthor = jsonSimAuthors.getJSONObject(M_AUTHOR);
                    simAuthor[y] = jsonAuthor.getString(M_AUTHOR_NAME);
                }
                simImage[y] = arraySimBooks.getJSONObject(y).getString(M_SIMILAR_IMAGE_URL);
            }

            ContentValues contentValues = new ContentValues();
            for(int z=0;z<arraySimBooks.length();z++) {
                contentValues.put(ResultContract.ResultEntry.COLUMN_BOOK_TITLE, simTitle[z]);
                contentValues.put(ResultContract.ResultEntry.COLUMN_ID, simIsbn[z]);
                contentValues.put(ResultContract.ResultEntry.COLUMN_AUTHOR, simAuthor[z]);
                contentValues.put(ResultContract.ResultEntry.COLUMN_RATING,simRating[z]);
                contentValues.put(ResultContract.ResultEntry.COLUMN_IMG,simImage[z]);
                getContentResolver().insert(ResultContract.ResultEntry.CONTENT_URI, contentValues);
            }
        }catch (NullPointerException e){
            Log.e(LOG_TAG, "EMPTY ISBN", e);
        }
    }
}
