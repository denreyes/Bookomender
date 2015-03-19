package com.example.dj.bookomender;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by DJ on 3/16/2015.
 */
public class BookContract {

    public static final String CONTENT_AUTHORITY = "com.example.dj.bookomender.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "books";

    public static final class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String TABLE_NAME = "books";

        public static final String COLUMN_BOOK_TITLE = "book_title";
        public static final String COLUMN_DESC = "description";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_IMG = "img";

        public static Uri buildBooksUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
