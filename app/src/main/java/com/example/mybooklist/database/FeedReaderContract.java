package com.example.mybooklist.database;

import android.provider.BaseColumns;

public final class FeedReaderContract {

    private FeedReaderContract() {
    }

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME_BOOKS = "books";
        public static final String TABLE_NAME_RELEASES = "releases";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_OWNED = "owned";
        public static final String COLUMN_NAME_AVAILABLE = "available";
        public static final String COLUMN_NAME_MISSING = "missing";
        public static final String COLUMN_NAME_EFFECTIVE_TITLE = "effective_title";
        public static final String COLUMN_NAME_NEXT_RELEASE_DATE = "next_release_date";
    }
}
