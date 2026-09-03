package com.example.mybooklist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybooklist.database.FeedReaderDbHelper;
import com.example.mybooklist.databinding.ActivityMainBinding;

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

        binding.listView.setAdapter(bookAdapter);
        binding.listView.setClickable(true);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                intent.putExtra("exists", true);
                intent.putExtra("title", books.get(i).getTitle());
                intent.putExtra("owned", String.valueOf(books.get(i).getOwned()));
                intent.putExtra("missing", books.get(i).getMissing());
                intent.putExtra("available", String.valueOf(books.get(i).getAvailable()));
                startActivity(intent);
            }
        });

        binding.addBookButton.setOnClickListener(
                view -> {
                    Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                    intent.putExtra("exists", false);
                    intent.putExtra("title", "New Book");
                    startActivity(intent);
                }
        );
    }
}