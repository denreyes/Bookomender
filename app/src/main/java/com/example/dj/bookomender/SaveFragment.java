package com.example.dj.bookomender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by DJ on 3/16/2015.
 */
public class SaveFragment extends Fragment{
    SaveDetailFragment saveDetailFragment;
    ListView listResult;

    public SaveFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        listResult = (ListView)rootView.findViewById(R.id.listview_search_result);
        queryList();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryList();
    }

    public void queryList(){
        final Cursor cursor = new BookDBHelper(getActivity()).getReadableDatabase().
                query(BookContract.BookEntry.TABLE_NAME, null, null, null, null, null, BookContract.BookEntry._ID + " DESC");
        listResult.setAdapter(new CursorAdapter(getActivity(),cursor,false) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.list_item_result, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ImageView imgBook = (ImageView)view.findViewById(R.id.imgBook);
                TextView txtTitle = (TextView)view.findViewById(R.id.txtTitle);
                TextView txtAuthor = (TextView)view.findViewById(R.id.txtAuthor);
                TextView txtRating = (TextView)view.findViewById(R.id.txtRating);

                txtTitle.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE)));
                txtAuthor.setText(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR)));
                if(getActivity().findViewById(R.id.container_x)==null) {
                    String x = "★★★★★";
                    int aveRating = (int) Math.round(Double.parseDouble(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RATING))));
                    final SpannableStringBuilder sb = new SpannableStringBuilder(x);
                    final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.main));
                    sb.setSpan(fcs, 0, aveRating, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    txtRating.setText(sb);
                }
                String img = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_IMG));
                if(img.equals("noimage")){
                    Picasso.with(getActivity())
                            .load(R.drawable.unknown_g)
                            .resize(80, 131)
                            .centerCrop()
                            .into(imgBook);
                }
                else {
                    Picasso.with(getActivity())
                            .load(img)
                            .resize(80, 131)
                            .centerCrop()
                            .into(imgBook);
                }
            }
        });
        listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s_id = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry._ID));
                if(getActivity().findViewById(R.id.container_x)!=null){
                    saveDetailFragment = new SaveDetailFragment();
                    Bundle b_bundle = new Bundle();
                    b_bundle.putString("ID", s_id);
                    saveDetailFragment.setArguments(b_bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_x, saveDetailFragment)
                            .commit();
                }
                else {
                    Intent i = new Intent(getActivity(), SaveDetailActivity.class);
                    i.putExtra("ID", s_id);
                    getActivity().startActivity(i);
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void delete(){
        saveDetailFragment.delete();
    }
}