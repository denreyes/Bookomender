package com.example.dj.bookomender;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


/**
 * Created by DJ on 3/10/2015.
 */
public class ResultFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int RESULT_LOADER = 0;
    BookFragment bookFragment;

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

        listResult = (ListView)rootView.findViewById(R.id.listview_search_result);

        final Cursor cursor = getActivity().getContentResolver().query(ResultContract.ResultEntry.CONTENT_URI,null,null,null,null);
        if(cursor.moveToFirst()) {
            adapter = new ResultAdapter(getActivity(), cursor, false);
            listResult.setAdapter(adapter);
            listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String isbn = cursor.getString(cursor.getColumnIndex(ResultContract.ResultEntry.COLUMN_ID));
                    if (getActivity().findViewById(R.id.container_x) != null) {
                        bookFragment = new BookFragment();
                        Bundle b_bundle = new Bundle();
                        b_bundle.putString("ISBN", isbn);
                        bookFragment.setArguments(b_bundle);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_x, bookFragment)
                                .commit();
                    } else {
                        Intent i = new Intent(getActivity(), BookActivity.class);
                        i.putExtra("ISBN", isbn);
                        getActivity().startActivity(i);
                    }
                }
            });
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onItemClick(String isbn){
        if(getActivity().findViewById(R.id.container_x)!=null){
            BookFragment bookFragment = new BookFragment();
            Bundle b_bundle = new Bundle();
            b_bundle.putString("ISBN", isbn);
            bookFragment.setArguments(b_bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_x, bookFragment)
                    .commit();
        }else {
            Intent i = new Intent(getActivity(),BookActivity.class);
            i.putExtra("ISBN",isbn);
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

        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader loader) {
        adapter.swapCursor(null);
    }

    public void addBook() {
        try {
            bookFragment.addBook();
        }catch (NullPointerException e){
            Toast.makeText(getActivity(),"Select a Book First.",Toast.LENGTH_LONG).show();
        }
    }
}