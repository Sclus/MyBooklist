package com.example.mybooklist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mybooklist.database.FeedReaderDbHelper;
import com.example.mybooklist.databinding.ActivityDetailedBinding;
import com.google.android.material.snackbar.Snackbar;

public class DetailedActivity extends AppCompatActivity {

    ActivityDetailedBinding binding;
    private boolean modified = false;

    private String title;
    private boolean exists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityDetailedBinding.inflate(getLayoutInflater());


        setContentView(binding.getRoot());

        Intent intent = this.getIntent();
        if (intent != null) {

            this.title = intent.getStringExtra("title");
            String owned = intent.getStringExtra("owned");
            String missing = intent.getStringExtra("missing");
            String available = intent.getStringExtra("available");

            this.exists = intent.getBooleanExtra("exists", false);

            binding.detailBookTitle.setText(title);
            binding.bookOwned.setText(owned);
            binding.booksMissing.setText(missing);
            binding.bookAvailable.setText(available);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.save.setOnClickListener(
                view -> {
                    if (exists) {
                        if (updateBook()) {
                            Snackbar.make(view, "Values updated", Snackbar.LENGTH_LONG)
                                    .setAnchorView(R.id.save)
                                    .setAction("Action", null).show();
                            modified = true;
                        }
                    } else {
                        if (addBook()) {
                            Snackbar.make(view, "New book added", Snackbar.LENGTH_LONG)
                                    .setAnchorView(R.id.save)
                                    .setAction("Action", null).show();
                            modified = true;
                        }
                    }
                }
        );

        binding.back.setOnClickListener(view -> {

            if (modified) {
                Intent intentBack = new Intent(DetailedActivity.this, MainActivity.class);
                intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentBack);
            }
            finish();
        });


        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    deleteBook();

                    Intent intentBack = new Intent(DetailedActivity.this, MainActivity.class);
                    intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentBack);
                    finish();

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(binding.deleteBook.getContext());


        binding.deleteBook.setOnClickListener(view -> {
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });
    }


    private boolean addBook() {
        try (FeedReaderDbHelper dbHandler = new FeedReaderDbHelper(DetailedActivity.this)) {

            String newTitle = binding.detailBookTitle.getText().toString();
            int owned = Integer.parseInt(String.valueOf(binding.bookOwned.getText()));
            String missing = binding.booksMissing.getText().toString();
            int available = Integer.parseInt(String.valueOf(binding.bookAvailable.getText()));

            return dbHandler.addNewBook(newTitle, owned, available, missing);
        }
    }

    private boolean updateBook() {
        try (FeedReaderDbHelper dbHandler = new FeedReaderDbHelper(DetailedActivity.this)) {

            String newTitle = binding.detailBookTitle.getText().toString();
            int owned = Integer.parseInt(String.valueOf(binding.bookOwned.getText()));
            String missing = binding.booksMissing.getText().toString();
            int available = Integer.parseInt(String.valueOf(binding.bookAvailable.getText()));

            return dbHandler.updateBook(this.title, newTitle, owned, available, missing);
        }
    }

    private boolean deleteBook() {
        try (FeedReaderDbHelper dbHandler = new FeedReaderDbHelper(DetailedActivity.this)) {
            return dbHandler.deleteBook(this.title);
        }
    }
}