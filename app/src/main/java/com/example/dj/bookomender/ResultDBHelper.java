package com.example.dj.bookomender;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DJ on 4/5/2015.
 */
public class ResultDBHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    final static String DATABASE_NAME = "result.db";

    public ResultDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_RESULT_TABLE = "CREATE TABLE " + ResultContract.ResultEntry.TABLE_NAME + " (" +
                ResultContract.ResultEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                ResultContract.ResultEntry.COLUMN_BOOK_TITLE + " TEXT NOT NULL,"+
                ResultContract.ResultEntry.COLUMN_AUTHOR + " TEXT NOT NULL,"+
                ResultContract.ResultEntry.COLUMN_RATING + " TEXT NOT NULL,"+
                ResultContract.ResultEntry.COLUMN_ID + " TEXT NOT NULL UNIQUE,"+
                ResultContract.ResultEntry.COLUMN_IMG + " TEXT NOT NULL );";

        db.execSQL(SQL_CREATE_RESULT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ResultContract.ResultEntry.TABLE_NAME);
        onCreate(db);
    }
}
