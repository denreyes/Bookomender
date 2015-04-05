package com.example.dj.bookomender;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.Random;


/**
 * Created by DJ on 3/10/2015.
 */
public class ResultFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int RESULT_LOADER = 0;

    private static final String[] RESULT_COLUMNS = {
            ResultContract.ResultEntry.TABLE_NAME + "." + ResultContract.ResultEntry._ID,
            ResultContract.ResultEntry.COLUMN_BOOK_TITLE,
            ResultContract.ResultEntry.COLUMN_AUTHOR,
            ResultContract.ResultEntry.COLUMN_RATING,
            ResultContract.ResultEntry.COLUMN_ID,
            ResultContract.ResultEntry.COLUMN_IMG
    };

    static final int COL_ID = 0;
    static final int COL_TITLE = 1;
    static final int COL_AUTHOR = 2;
    static final int COL_RATING = 3;
    static final int COL_ISBN = 4;
    static final int COL_IMG = 5;

    ResultAdapter adapter;
    String searchIsbn;
    ListView listResult;
    LayoutInflater inflater;
    String search;

    public ResultFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        search = getArguments().getString("SEARCH");

        listResult = (ListView)rootView.findViewById(R.id.listview_search_result);
        SearchTask searchTask = new SearchTask(getActivity(),listResult);
        searchTask.execute(search);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getContentResolver().delete(ResultContract.ResultEntry.CONTENT_URI,null,null);
    }

    public void onItemClick(){
        if(getActivity().findViewById(R.id.container_x)!=null){
            BookFragment bookFragment = new BookFragment();
            Bundle b_bundle = new Bundle();
            b_bundle.putString("ISBN", searchIsbn);
            bookFragment.setArguments(b_bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_x, bookFragment)
                    .commit();
        }else {
            Intent i = new Intent(getActivity(),BookActivity.class);
            i.putExtra("ISBN",searchIsbn);
            getActivity().startActivity(i);
        }
    }

    //Cursor Loader
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                ResultContract.ResultEntry.CONTENT_URI,
                RESULT_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v("YO","In onLoadFinished");

        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader loader) {
        adapter.swapCursor(null);
    }

    public class SearchTask extends AsyncTask<String,Void,Void> {
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
        StringPretifier stringPretifier;
        Context context;
        ListView listResult;

        public SearchTask(Context context, ListView listResult){
            this.context = context;
            this.listResult = listResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String[] loadPhrases = getResources().getStringArray(R.array.load_phrases);
            int rnd = new Random().nextInt(loadPhrases.length-1);
            progressDialog = ProgressDialog.show(context,null,loadPhrases[rnd]);
        }

        @Override
        protected Void doInBackground(String... params) {
            Bundle bundle = null;
            urlConnection = null;
            reader = null;
            stringPretifier = new StringPretifier();

            String resultJsonStr = null;
            ArrayList<String> results = new ArrayList<String>();


            try {
                Uri builtUri = Uri.parse(RESULT_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(MAX_RESULT,VALUE_SINGLE_RESULT)
                        .appendQueryParameter(ORDER_BY,VALUE_ORDER_BY)
                        .build();
                URL url = new URL(builtUri.toString());

                resultJsonStr = getJsonString(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
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
            }
            return null;
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

                String bookTitle = jsonBook.getString(M_TITLE);
                String bookIsbn = jsonBook.getString(M_ISBN_13);
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
                contentValues.put(ResultContract.ResultEntry.COLUMN_BOOK_TITLE, bookTitle);
                contentValues.put(ResultContract.ResultEntry.COLUMN_ID, bookIsbn);
                contentValues.put(ResultContract.ResultEntry.COLUMN_AUTHOR, authorName);
                contentValues.put(ResultContract.ResultEntry.COLUMN_RATING,0);
                contentValues.put(ResultContract.ResultEntry.COLUMN_IMG,"");
                getActivity().getContentResolver().insert(ResultContract.ResultEntry.CONTENT_URI, contentValues);
                for(int z=0;z<simTitle.length;z++) {
                    contentValues.put(ResultContract.ResultEntry.COLUMN_BOOK_TITLE, simTitle[z]);
                    contentValues.put(ResultContract.ResultEntry.COLUMN_ID, simIsbn[z]);
                    contentValues.put(ResultContract.ResultEntry.COLUMN_AUTHOR, simAuthor[z]);
                    contentValues.put(ResultContract.ResultEntry.COLUMN_RATING,simRating[z]);
                    contentValues.put(ResultContract.ResultEntry.COLUMN_IMG,simImage[z]);
                    getActivity().getContentResolver().insert(ResultContract.ResultEntry.CONTENT_URI, contentValues);
                }
            }catch (NullPointerException e){
                Log.e(LOG_TAG ,"EMPTY ISBN",e);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Cursor cursor = getActivity().getContentResolver().query(ResultContract.ResultEntry.CONTENT_URI,null,null,null,null);
            if(cursor.moveToFirst()){
                ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
                actionBar.setTitle(cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_BOOK_TITLE)));

                if(getActivity().findViewById(R.id.container_x)!=null){
                    BookFragment bookFragment = new BookFragment();
                    Bundle b_bundle = new Bundle();
                    b_bundle.putString("ISBN", cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_ID)));
                    bookFragment.setArguments(b_bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.container_x, bookFragment)
                            .commit();
                }
//                String [] m_title = new String[cursor.getCount()-1];
//                String [] m_isbn = new String[cursor.getCount()-1];
//                String [] m_rate = new String[cursor.getCount()-1];
//                String [] m_img = new String[cursor.getCount()-1];
//                String [] m_author = new String[cursor.getCount()-1];
//                int w=0;
//                while(cursor.moveToNext()){
//                    m_title[w]=cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_BOOK_TITLE));
//                    m_isbn[w]=cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_ID));
//                    m_rate[w]=cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_RATING));
//                    m_img[w]=cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_IMG));
//                    m_author[w++]=cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_AUTHOR));
//                };
//                Bundle bundle = new Bundle();
//                bundle.putStringArray("M_SIMILAR_TITLE",m_title);
//                bundle.putStringArray("M_SIMILAR_ISBN_13",m_isbn);
//                bundle.putStringArray("M_SIMILAR_RATING",m_rate);
//                bundle.putStringArray("M_SIMILAR_IMAGE_URL",m_img);
//                bundle.putStringArray("M_SIMILAR_AUTHOR_NAME",m_author);
                adapter = new ResultAdapter(getActivity(),cursor,false);
                progressDialog.dismiss();
                listResult.setAdapter(adapter);
                listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String isbn = adapter.getItem(position).toString();
                        if(getActivity().findViewById(R.id.container_x)!=null){
                            BookFragment bookFragment = new BookFragment();
                            Bundle b_bundle = new Bundle();
                            b_bundle.putString("ISBN", isbn);
                            bookFragment.setArguments(b_bundle);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container_x, bookFragment)
                                    .commit();
                        }else {
                            Intent i = new Intent(context, BookActivity.class);
                            i.putExtra("ISBN", isbn);
                            context.startActivity(i);
                        }
                    }
                });
            }
            else {
                progressDialog.dismiss();
                Toast.makeText(context,"No books found, please be more precise.",Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        }
    }
}