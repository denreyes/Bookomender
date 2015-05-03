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
                    Toast.makeText(getActivity(),"Input a book or something.",Toast.LENGTH_LONG).show();
                }else {

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
}
