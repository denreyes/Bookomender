package com.example.dj.bookomender;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * Created by DJ on 3/10/2015.
 */
public class ResultFragment extends Fragment{
    private static final int RESULT_LOADER = 0;

    ListView listResult;
    LayoutInflater inflater;
    String search;

    public ResultFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
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
}