package com.example.dj.bookomender;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by DJ on 3/16/2015.
 */
public class BookProvider extends ContentProvider{
    private static final int BOOKS = 1;

    private SQLiteDatabase db;
    private BookDBHelper bookDBHelper;

    private static final UriMatcher matcher;

    static{
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BookContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, BookContract.PATH_BOOKS, BOOKS);
    }

    @Override
    public boolean onCreate() {
        bookDBHelper = new BookDBHelper(getContext());

        db = bookDBHelper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch(matcher.match(uri)){
            case 1:
                return db.query
                        (BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(BookContract.BookEntry.TABLE_NAME, "", values);

        if(rowID > 0){
            Uri u = ContentUris.withAppendedId(BookContract.BASE_CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(u, null);
            return u;
        }
        throw new SQLException("Failed to insert");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;

        switch (matcher.match(uri)) {
            case 1:
                count = db.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case 2:
                String segment = uri.getLastPathSegment();
                String whereClause = BookContract.BookEntry._ID + "=" + segment
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                count = db.delete(BookContract.BookEntry.TABLE_NAME, whereClause, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;

        switch (matcher.match(uri)) {
            case 1:
                count = db.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case 2:
                String segment = uri.getLastPathSegment();
                String whereClause = BookContract.BookEntry._ID + "=" + segment
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                count = db.update(BookContract.BookEntry.TABLE_NAME, values, whereClause, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }
}