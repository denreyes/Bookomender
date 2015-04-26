package com.example.dj.bookomender;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by DJ on 3/9/2015.
 */
//A.K.A. SearchFragment
public class MainFragment extends Fragment{
    EditText edtSearch;
    Button btnSearch;
    String bookTitle,bookIsbn;
    ProgressDialog pd;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        edtSearch = (EditText)rootView.findViewById(R.id.edtSearch);
        btnSearch = (Button)rootView.findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = edtSearch.getText().toString();
                if(search.isEmpty()){
                    int rnd, max = 3, min = 1;
                    rnd = new Random().nextInt((max - min + 1) + min);
                    String toastMsg;
                    if(rnd==1)
                        toastMsg="Come on, there's bound to be some book you know of right?";
                    else if(rnd==2)
                        toastMsg="You need to give me something, dear.";
                    else
                        toastMsg="There isn't a book that hasn't got any title, dear.";
                    Toast.makeText(getActivity(),toastMsg,Toast.LENGTH_LONG).show();
                }else {
//                    SearchTask searchTask = new SearchTask();
//                    searchTask.execute(search);

                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(MainIntentService.TRANSACTION_DONE);
                    getActivity().registerReceiver(mainReceiver, intentFilter);
                    Intent i = new Intent(getActivity(), MainIntentService.class);
                    i.putExtra("SEARCH",search);
                    getActivity().startService(i);

                    String[] loadPhrases = getResources().getStringArray(R.array.load_phrases);
                    int rnd = new Random().nextInt(loadPhrases.length-1);
                    pd = ProgressDialog.show(getActivity(),null,loadPhrases[rnd]);
                }
            }
        });

        return rootView;
    }

    private BroadcastReceiver mainReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pd.dismiss();
            Cursor cursor = getActivity().getContentResolver().query(ResultContract.ResultEntry.CONTENT_URI,null,null,null,null);

            if(cursor.moveToFirst()){
                Intent i = new Intent(getActivity(), ResultActivity.class);
                i.putExtra("ISBN",intent.getStringExtra("ISBN"));
                i.putExtra("BOOK_TITLE",intent.getStringExtra("BOOK_TITLE"));
                startActivity(i);
            }
            else {
                Toast.makeText(getActivity(),"No books found, please be more precise.",Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        }
    };

//    public class SearchTask extends AsyncTask<String,Void,Void> {
//        private final String LOG_TAG = getClass().getSimpleName();
//        StringBuffer buffer;
//        HttpURLConnection urlConnection;
//        BufferedReader reader;
//        private static final String RESULT_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
//        private static final String QUERY_PARAM = "q";
//        private  static final String MAX_RESULT = "maxResults";
//        private static final String ORDER_BY = "orderBy";
//        private static final String VALUE_SINGLE_RESULT = "1";
//        private static final String VALUE_ORDER_BY = "relevance";
//        ProgressDialog progressDialog;
//
//        ResultAdapter adapter;
//        String searchIsbn;
//        LayoutInflater inflater;
//        String search;
//
//        public SearchTask(){
//        }

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            getActivity().getContentResolver().delete(ResultContract.ResultEntry.CONTENT_URI,null,null);
//            String[] loadPhrases = getResources().getStringArray(R.array.load_phrases);
//            int rnd = new Random().nextInt(loadPhrases.length-1);
//            progressDialog = ProgressDialog.show(getActivity(),null,loadPhrases[rnd]);
//        }
//
//        @Override
//        protected Void doInBackground(String... params) {
//            Bundle bundle = null;
//            urlConnection = null;
//            reader = null;
//
//            String resultJsonStr = null;
//            ArrayList<String> results = new ArrayList<String>();
//
//
//            try {
//                Uri builtUri = Uri.parse(RESULT_BASE_URL).buildUpon()
//                        .appendQueryParameter(QUERY_PARAM, params[0])
//                        .appendQueryParameter(MAX_RESULT,VALUE_SINGLE_RESULT)
//                        .appendQueryParameter(ORDER_BY,VALUE_ORDER_BY)
//                        .build();
//                URL url = new URL(builtUri.toString());
//
//                resultJsonStr = getJsonString(url);
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error ", e);
//                return null;
//            }
//            finally{
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(LOG_TAG, "Error closing stream", e);
//                    }
//                }
//
//                try {
//                    searchIsbn = getISBN(buffer.toString());
//                    similarReads(connectGoodreads(getISBN(buffer.toString())));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            return null;
//        }
//
//        private String getJsonString(URL url) throws IOException {
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//
//            InputStream inputStream = urlConnection.getInputStream();
//            buffer = new StringBuffer();
//            if (inputStream == null) {
//                return null;
//            }
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                buffer.append(line + "\n");
//            }
//
//            if (buffer.length() == 0) {
//                return null;
//            }
//            return buffer.toString();
//        }
//
//        private String getISBN(String resultJSON) throws JSONException {
//            final String M_ITEMS = "items";
//            final String M_VOLUME_INFO = "volumeInfo";
//            final String M_INDUSTRY_IDENTIFIER = "industryIdentifiers";
//
//            final String M_TYPE = "type";
//            final String M_IDENTIFIER = "identifier";
//
//            JSONObject jsonObject = new JSONObject(resultJSON);
//            JSONArray items = jsonObject.getJSONArray(M_ITEMS);
//            JSONObject itemOne = items.getJSONObject(0);
//            JSONObject volumeInfo = itemOne.getJSONObject(M_VOLUME_INFO);
//            JSONArray industryId = volumeInfo.getJSONArray(M_INDUSTRY_IDENTIFIER);
//            int x = 0;
//            String isbn;
//            do{
//                isbn=industryId.getJSONObject(x).getString(M_IDENTIFIER).toString();
//            }while (industryId.getJSONObject(x++).getString(M_TYPE).toString() == "ISBN_13");
//
//            return isbn;
//        }
//
//        private String connectGoodreads(String value_isbn)    {
//            final String M_RESULT_BASE_URL = "https://www.goodreads.com/book/isbn?";
//            final String M_ISBN = "isbn";
//            final String M_KEY = "key";
//
//            final String M_VALUE_KEY = "UpH4L0IYjAXcezlfg0yT2Q";
//
//            String xml = null;
//            try {
//                Uri builtUri = Uri.parse(M_RESULT_BASE_URL).buildUpon()
//                        .appendQueryParameter(M_ISBN, value_isbn)
//                        .appendQueryParameter(M_KEY,M_VALUE_KEY)
//                        .build();
//                URL url = new URL(builtUri.toString());
//
//                InputStream is = url.openStream();
//                int ptr = 0;
//                StringBuilder builder = new StringBuilder();
//                while ((ptr = is.read()) != -1) {
//                    builder.append((char) ptr);
//                }
//
//                xml = builder.toString();
//                try {
//                    return XML.toJSONObject(xml).toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        private void similarReads(String resultJsonStr)
//                throws JSONException {
//            final String GOOD_READS_RESPONSE = "GoodreadsResponse";
//            final String BOOK = "book";
//
//            final String M_TITLE = "title";
//            final String M_ISBN_13 = "isbn13";
//            final String M_AUTHORS = "authors";
//            final String M_AUTHOR = "author";
//            final String M_AUTHOR_NAME = "name";
//
//            final String M_SIMILAR = "similar_books";
//            final String M_SIMILAR_BOOK = "book";
//            final String M_SIMILAR_TITLE = "title";
//            final String M_SIMILAR_ISBN_13 = "isbn13";
//            final String M_SIMILAR_RATING = "average_rating";
//            final String M_SIMILAR_IMAGE_URL = "image_url";
//            final String M_SIMILAR_AUTHORS = "authors";
//            final String M_SIMILAR_AUTHOR = "author";
//            final String M_SIMILAR_AUTHOR_NAME = "name";
//
//            try {
//                JSONObject jsonObject = new JSONObject(resultJsonStr);
//                JSONObject jsonGoodReadsResponse = jsonObject.getJSONObject(GOOD_READS_RESPONSE);
//                JSONObject jsonBook = jsonGoodReadsResponse.getJSONObject(BOOK);
//
//                bookTitle = jsonBook.getString(M_TITLE);
//                bookIsbn = jsonBook.getString(M_ISBN_13);
//                String authorName = null;
//                JSONObject jsonAuthors = jsonBook.getJSONObject(M_AUTHORS);
//
//
//                try {
//                    JSONArray arrayAuthor = jsonAuthors.getJSONArray(M_AUTHOR);
//                    for (int i = 0; i < arrayAuthor.length(); i++) {
//                        if (i == 0) {
//                            authorName = arrayAuthor.getJSONObject(i).getString(M_AUTHOR_NAME);
//                        } else if (i != arrayAuthor.length()) {
//                            authorName = authorName + ", " + arrayAuthor.getJSONObject(i).getString(M_AUTHOR_NAME);
//                        }
//                    }
//                } catch (JSONException e) {
//                    JSONObject jsonAuthor = jsonAuthors.getJSONObject(M_AUTHOR);
//                    authorName = jsonAuthor.getString(M_AUTHOR_NAME);
//                }
//
//                JSONObject jsonSimilar = jsonBook.getJSONObject(M_SIMILAR);
//                JSONArray arraySimBooks = jsonSimilar.getJSONArray(M_SIMILAR_BOOK);
//
//                String[] simTitle = new String[arraySimBooks.length()];
//                String[] simIsbn = new String[arraySimBooks.length()];
//                String[] simRating = new String[arraySimBooks.length()];
//                String[] simImage = new String[arraySimBooks.length()];
//                String[] simAuthor = new String[arraySimBooks.length()];
//                for (int y = 0; y < arraySimBooks.length(); y++) {
//                    simTitle[y] = arraySimBooks.getJSONObject(y).getString(M_SIMILAR_TITLE);
//                    simIsbn[y] = arraySimBooks.getJSONObject(y).getString(M_SIMILAR_ISBN_13);
//                    simRating[y] = arraySimBooks.getJSONObject(y).getString(M_SIMILAR_RATING);
//
//                    JSONObject jsonSimAuthors = arraySimBooks.getJSONObject(y).getJSONObject(M_SIMILAR_AUTHORS);
//                    try {
//                        JSONArray arraySimAuthor = jsonSimAuthors.getJSONArray(M_SIMILAR_AUTHOR);
//                        for (int i = 0; i < arraySimAuthor.length(); i++) {
//                            if (i == 0) {
//                                simAuthor[y] = arraySimAuthor.getJSONObject(i).getString(M_SIMILAR_AUTHOR_NAME);
//                            } else if (i != arraySimAuthor.length()) {
//                                simAuthor[y] = simAuthor + ", " + arraySimAuthor.getJSONObject(i).getString(M_SIMILAR_AUTHOR_NAME);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        JSONObject jsonAuthor = jsonSimAuthors.getJSONObject(M_AUTHOR);
//                        simAuthor[y] = jsonAuthor.getString(M_AUTHOR_NAME);
//                    }
//                    simImage[y] = arraySimBooks.getJSONObject(y).getString(M_SIMILAR_IMAGE_URL);
//                }
//
//                ContentValues contentValues = new ContentValues();
//                for(int z=0;z<arraySimBooks.length();z++) {
//                    contentValues.put(ResultContract.ResultEntry.COLUMN_BOOK_TITLE, simTitle[z]);
//                    contentValues.put(ResultContract.ResultEntry.COLUMN_ID, simIsbn[z]);
//                    contentValues.put(ResultContract.ResultEntry.COLUMN_AUTHOR, simAuthor[z]);
//                    contentValues.put(ResultContract.ResultEntry.COLUMN_RATING,simRating[z]);
//                    contentValues.put(ResultContract.ResultEntry.COLUMN_IMG,simImage[z]);
//                    getActivity().getContentResolver().insert(ResultContract.ResultEntry.CONTENT_URI, contentValues);
//                }
//            }catch (NullPointerException e){
//                Log.e(LOG_TAG ,"EMPTY ISBN",e);
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            Cursor cursor = getActivity().getContentResolver().query(ResultContract.ResultEntry.CONTENT_URI,null,null,null,null);
//            progressDialog.dismiss();
//            if(cursor.moveToFirst()){
//                Intent i = new Intent(getActivity(), ResultActivity.class);
//                i.putExtra("ISBN",bookIsbn);
//                i.putExtra("BOOK_TITLE",bookTitle);
//                startActivity(i);
//            }
//            else {
//                Toast.makeText(getActivity(),"No books found, please be more precise.",Toast.LENGTH_LONG).show();
//                getActivity().finish();
//            }
//        }
//    }
}
