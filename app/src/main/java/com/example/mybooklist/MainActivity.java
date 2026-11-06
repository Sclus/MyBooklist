package com.example.mybooklist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybooklist.book.Book;
import com.example.mybooklist.book.BookAdapter;
import com.example.mybooklist.database.FeedReaderDbHelper;
import com.example.mybooklist.databinding.ActivityMainBinding;
import com.example.mybooklist.export.Export;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try (FeedReaderDbHelper dbHandler = new FeedReaderDbHelper(MainActivity.this)) {
            this.books = dbHandler.getBooks();
        }
        BookAdapter bookAdapter = new BookAdapter(MainActivity.this, this.books);

        binding.bookListView.setAdapter(bookAdapter);
        binding.bookListView.setClickable(true);

        binding.bookListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
            intent.putExtra("exists", true);
            intent.putExtra("book", books.get(i));
            startActivity(intent);
        });

        binding.addBookButton.setOnClickListener(
                view -> {
                    Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                    intent.putExtra("exists", false);
                    intent.putExtra("book", new Book("NewBook"));
                    startActivity(intent);
                }
        );

        binding.topAppBar.setOnMenuItemClickListener(item -> {

            Log.i("debug", "exportOwned Id" + R.id.exportOwned);
            Log.i("debug", "exportMissing Id" + R.id.exportMissing);

            if (item.getItemId() == R.id.exportOwned) {
                copyToClipboard(Export.exportOwned(this.books));
                return true;
            } else if (item.getItemId() == R.id.exportMissing) {
                copyToClipboard(Export.exportMissing(this.books));
                return true;
            }
            return false;
        });
    }

    private void copyToClipboard(ClipData clipData) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clipData);
    }


}