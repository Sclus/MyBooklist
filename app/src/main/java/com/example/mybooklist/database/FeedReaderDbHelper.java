package com.example.mybooklist.database;

import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.COLUMN_NAME_AVAILABLE;
import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.COLUMN_NAME_EFFECTIVE_TITLE;
import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.COLUMN_NAME_MISSING;
import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.COLUMN_NAME_OWNED;
import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE;
import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mybooklist.Book;
import com.example.mybooklist.database.FeedReaderContract.*;

import java.util.ArrayList;

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_TITLE + " TEXT," +
                    COLUMN_NAME_OWNED + " INTEGER," +
                    COLUMN_NAME_AVAILABLE + " INTEGER," +
                    COLUMN_NAME_MISSING + " TEXT," +
                    COLUMN_NAME_EFFECTIVE_TITLE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean addNewBook(String title, int owned, int available, String missing){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME_TITLE, title);
        values.put(COLUMN_NAME_OWNED, owned);
        values.put(COLUMN_NAME_AVAILABLE, available);
        values.put(COLUMN_NAME_MISSING, missing);

        String effectiveTitle = title.toLowerCase().replaceFirst("^(the|a|le|la|les|un|une|des)\\s", "");

        values.put(COLUMN_NAME_EFFECTIVE_TITLE, effectiveTitle);

        boolean inserted = db.insert(TABLE_NAME, null, values) != -1;

        db.close();

        return inserted;
    }

    public boolean updateBook(String odlTitle, String title, int owned, int available, String missing){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME_TITLE, title);
        values.put(COLUMN_NAME_OWNED, owned);
        values.put(COLUMN_NAME_AVAILABLE, available);
        values.put(COLUMN_NAME_MISSING, missing);

        String effectiveTitle = title.toLowerCase().replaceFirst("^(the|a|le|la|les|un|une|des)\\s", "");

        values.put(COLUMN_NAME_EFFECTIVE_TITLE, effectiveTitle);

        boolean updated = db.update(TABLE_NAME, values, "title LIKE \"" + odlTitle + "\"", null) == 1;

        db.close();

        return updated;
    }

    public boolean deleteBook(String title){

        SQLiteDatabase db = this.getWritableDatabase();

        boolean deleted = db.delete(TABLE_NAME, "title LIKE \"" + title + "\"", null) == 1;

        db.close();

        return deleted;
    }

    public ArrayList<Book> getBooks(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_NAME_EFFECTIVE_TITLE + " ASC", null);

        ArrayList<Book> books = new ArrayList<>();

        if (cursor.moveToFirst()){
            do {
                books.add(new Book(
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(4),
                        cursor.getInt(3)
                        ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return books;
    }
}
