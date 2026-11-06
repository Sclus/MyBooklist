package com.example.mybooklist.database;

import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.COLUMN_NAME_AVAILABLE;
import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.COLUMN_NAME_EFFECTIVE_TITLE;
import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.COLUMN_NAME_MISSING;
import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.COLUMN_NAME_NEXT_RELEASE_DATE;
import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.COLUMN_NAME_OWNED;
import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE;
import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.TABLE_NAME_BOOKS;
import static com.example.mybooklist.database.FeedReaderContract.FeedEntry.TABLE_NAME_RELEASES;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mybooklist.book.Book;
import com.example.mybooklist.database.FeedReaderContract.FeedEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    private static final String SQL_CREATE_BOOK_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + FeedEntry.TABLE_NAME_BOOKS + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_TITLE + " TEXT UNIQUE," +
                    COLUMN_NAME_OWNED + " INTEGER," +
                    COLUMN_NAME_AVAILABLE + " INTEGER," +
                    COLUMN_NAME_MISSING + " TEXT," +
                    COLUMN_NAME_EFFECTIVE_TITLE + " TEXT)";

    private static final String SQL_CREATE_RELEASE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + FeedEntry.TABLE_NAME_RELEASES + " (" +
                    COLUMN_NAME_TITLE + " TEXT," +
                    COLUMN_NAME_NEXT_RELEASE_DATE + " INTEGER," +
                    "FOREIGN KEY(" + COLUMN_NAME_TITLE + ") REFERENCES " + TABLE_NAME_BOOKS + "(" + COLUMN_NAME_TITLE + ") " +
                    "ON DELETE CASCADE ON UPDATE CASCADE," +
                    "PRIMARY KEY(" + COLUMN_NAME_TITLE + ", " + COLUMN_NAME_NEXT_RELEASE_DATE + ")" +
                    ");";


    private static final String SQL_DELETE_BOOK_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_BOOKS;
    private static final String SQL_DELETE_RELEASE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_RELEASES;

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_BOOK_ENTRIES);
        db.execSQL(SQL_CREATE_RELEASE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_RELEASE_ENTRIES);
        db.execSQL(SQL_DELETE_BOOK_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean addNewBook(String title, int owned, int available, String missing, List<Date> nextReleases) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues bookValues = new ContentValues();

        bookValues.put(COLUMN_NAME_TITLE, title);
        bookValues.put(COLUMN_NAME_OWNED, owned);
        bookValues.put(COLUMN_NAME_AVAILABLE, available);
        bookValues.put(COLUMN_NAME_MISSING, missing);

        String effectiveTitle = getEffectiveTitle(title);

        bookValues.put(COLUMN_NAME_EFFECTIVE_TITLE, effectiveTitle);

        boolean inserted = db.insert(TABLE_NAME_BOOKS, null, bookValues) != -1;
        db.close();

        if (!nextReleases.isEmpty()) {
            addReleases(title, nextReleases);
        }

        return inserted;
    }

    public boolean updateBook(String odlTitle, String title, int owned, int available, String missing, List<Date> nextReleases) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME_TITLE, title);
        values.put(COLUMN_NAME_OWNED, owned);
        values.put(COLUMN_NAME_AVAILABLE, available);
        values.put(COLUMN_NAME_MISSING, missing);

        String effectiveTitle = getEffectiveTitle(title);

        values.put(COLUMN_NAME_EFFECTIVE_TITLE, effectiveTitle);

        boolean updated = db.update(TABLE_NAME_BOOKS, values, "title LIKE \"" + odlTitle + "\"", null) == 1;
        db.close();

        updateReleases(title, nextReleases);

        return updated;
    }

    public boolean deleteBook(String title) {

        SQLiteDatabase db = this.getWritableDatabase();

        boolean deleted = db.delete(TABLE_NAME_BOOKS, "title LIKE \"" + title + "\"", null) == 1;

        db.close();

        return deleted;
    }

    public ArrayList<Book> getBooks() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_BOOKS +
                " ORDER BY " + COLUMN_NAME_EFFECTIVE_TITLE + " ASC", null);

        ArrayList<Book> books = new ArrayList<>();
        List<Date> releases;
        Date now = new Date();
        Book newBook;

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(1);
                releases = this.getNextReleases(title);

                List<Date> releasesToDelete = new ArrayList<>();
                for (Date release : releases) {
                    if (release.before(now)) {
                        releasesToDelete.add(release);
                    }
                }
                releases.removeAll(releasesToDelete);
                newBook = new Book(
                        title,
                        cursor.getInt(2),
                        cursor.getString(4),
                        cursor.getInt(3) + releasesToDelete.size(),
                        releases);

                this.updateBook(newBook.getTitle(), newBook.getTitle(), newBook.getOwned(),
                        newBook.getAvailable(), newBook.getMissing(), releases);

                books.add(newBook);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return books;
    }

    public void addReleases(String title, List<Date> nextReleases) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues releaseValues = new ContentValues();
        releaseValues.put(COLUMN_NAME_TITLE, title);

        nextReleases.forEach(date -> {
            releaseValues.put(COLUMN_NAME_NEXT_RELEASE_DATE, date.getTime());

            db.insert(TABLE_NAME_RELEASES, null, releaseValues);

            releaseValues.remove(COLUMN_NAME_NEXT_RELEASE_DATE);
        });

        db.close();
    }

    public void deleteReleases(String title) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME_RELEASES,
                COLUMN_NAME_TITLE + " LIKE \"" + title + "\"",
                null);

        db.close();
    }

    public void updateReleases(String title, List<Date> nextReleases) {

        deleteReleases(title);
        addReleases(title, nextReleases);
    }

    public Map<String, List<Date>> getNextReleases() {
        Map<String, List<Date>> releases = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_RELEASES +
                " ORDER BY " + COLUMN_NAME_NEXT_RELEASE_DATE + " ASC", null);

        String title;
        Date releaseDate;

        if (cursor.moveToFirst()) {
            do {
                title = cursor.getString(0);
                releaseDate = new Date(cursor.getLong(1));

                if (!releases.containsKey(title)) {
                    releases.put(title, List.of(releaseDate));
                } else {
                    Objects.requireNonNull(releases.get(title)).add(releaseDate);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return releases;
    }

    public List<Date> getNextReleases(String title) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_NAME_NEXT_RELEASE_DATE + " FROM " + TABLE_NAME_RELEASES +
                " WHERE " + COLUMN_NAME_TITLE + " LIKE \"" + title + "\"" +
                " ORDER BY " + COLUMN_NAME_NEXT_RELEASE_DATE + " ASC", null);

        ArrayList<Date> nextReleases = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                nextReleases.add(new Date(cursor.getLong(0)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return nextReleases;
    }

    private String getEffectiveTitle(String title) {
        return title.toLowerCase().replaceFirst("^(the|a|le|la|les|un|une|des)\\s", "");
    }
}
