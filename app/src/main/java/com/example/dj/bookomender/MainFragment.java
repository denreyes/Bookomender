package com.example.dj.bookomender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by DJ on 3/9/2015.
 */
//A.K.A. SearchFragment
public class MainFragment extends Fragment{
    EditText edtSearch;
    Button btnSearch,btnToRead;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        edtSearch = (EditText)rootView.findViewById(R.id.edtSearch);
        btnSearch = (Button)rootView.findViewById(R.id.btnSearch);
        btnToRead = (Button)rootView.findViewById(R.id.btnToRead);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = edtSearch.getText().toString();
                Intent i = new Intent(getActivity(),ResultActivity.class);
                i.putExtra("SEARCH",search);
                startActivity(i);
            }
        });

        btnToRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),SaveActivity.class);
                startActivity(i);
            }
        });

        return rootView;
    }
}
