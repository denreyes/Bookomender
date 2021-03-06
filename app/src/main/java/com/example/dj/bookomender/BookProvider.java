package com.example.dj.bookomender;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by DJ on 3/16/2015.
 */
public class BookProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private BookDBHelper mOpenHelper;

    static final int BOOK = 100;
    static final int RESULT = 101;

    private static final SQLiteQueryBuilder sBookByIdQueryBuilder;
    private static final SQLiteQueryBuilder sResultByIdQueryBuilder;

    static{
        sBookByIdQueryBuilder = new SQLiteQueryBuilder();
        sBookByIdQueryBuilder.setTables(
                BookContract.BookEntry.TABLE_NAME);

        sResultByIdQueryBuilder = new SQLiteQueryBuilder();
        sResultByIdQueryBuilder.setTables(
                ResultContract.ResultEntry.TABLE_NAME);
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOK);
        matcher.addURI(ResultContract.CONTENT_AUTHORITY, ResultContract.PATH_RESULT, RESULT);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new BookDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case BOOK: {
                retCursor = sBookByIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case RESULT: {
                retCursor = sResultByIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOK:
                return BookContract.BookEntry.CONTENT_TYPE;
            case RESULT:
                return ResultContract.ResultEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowID = db.insert(ResultContract.ResultEntry.TABLE_NAME, "", values);

        if(rowID > 0){
            Uri u = ContentUris.withAppendedId(uri, rowID);
            getContext().getContentResolver().notifyChange(u, null);
            db.close();
            return u;
        }
        throw new SQLException("Failed to insert");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowDeleted;

        switch (match) {
            case BOOK:
                rowDeleted = db.delete(
                        BookContract.BookEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case RESULT:
                rowDeleted = db.delete(
                        ResultContract.ResultEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (selection == null || rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}