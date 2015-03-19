package com.example.dj.bookomender;

import android.content.Intent;
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
    Button btnSearch,btnToRead;

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
                    Intent i = new Intent(getActivity(), ResultActivity.class);
                    i.putExtra("SEARCH", search);
                    startActivity(i);
                }
            }
        });

        return rootView;
    }
}
