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

import com.example.mybooklist.book.Book;
import com.example.mybooklist.database.FeedReaderDbHelper;
import com.example.mybooklist.databinding.ActivityDetailedBinding;
import com.example.mybooklist.fragment.DatePickerFragment;
import com.example.mybooklist.release.ReleaseAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;

public class DetailedActivity extends AppCompatActivity {

    ActivityDetailedBinding binding;
    Book book;
    private boolean modified = false;
    private boolean exists;
    private ReleaseAdapter releaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        populateData();

        handleSaveClick();
        handleBackClick();
        handleDeleteClick();

        handleAddNextRelease();
    }

    private void populateData() {
        Intent intent = this.getIntent();
        if (intent != null) {

            this.book = intent.getParcelableExtra("book", Book.class);
            this.exists = intent.getBooleanExtra("exists", false);

            binding.detailBookTitle.setText(book.getTitle());
            binding.bookOwned.setText(String.valueOf(book.getOwned()));
            binding.booksMissing.setText(book.getMissing());
            binding.bookAvailable.setText(String.valueOf(book.getAvailable()));

            releaseAdapter = new ReleaseAdapter(DetailedActivity.this, this.book);
            releaseAdapter.setNotifyOnChange(true);
            binding.releaseListView.setAdapter(releaseAdapter);
        }
    }

    private void handleAddNextRelease() {

        getSupportFragmentManager().setFragmentResultListener(
                DatePickerFragment.REQUEST_KEY, this,
                (requestKey, bundle) -> {
                    Date release = new Date(bundle.getLong(DatePickerFragment.KEY_DATE));
                    this.book.getNextReleases().add(release);
                    releaseAdapter.notifyDataSetChanged();
                });

        binding.addNextRelease.setOnClickListener(v -> {
            DatePickerFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.show(getSupportFragmentManager(), "datePicker");
        });
    }

    private void handleSaveClick() {
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
    }

    private void handleBackClick() {
        binding.back.setOnClickListener(view -> {
            if (modified) {
                Intent intentBack = new Intent(DetailedActivity.this, MainActivity.class);
                intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentBack);
            }
            finish();
        });
    }

    private void handleDeleteClick() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:

                    if (deleteBook()) {
                        Intent intentBack = new Intent(DetailedActivity.this, MainActivity.class);
                        intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentBack);
                        finish();

                        break;
                    } else {
                        Snackbar.make(binding.getRoot().getRootView(), "Error while trying to delete", Snackbar.LENGTH_LONG)
                                .setAnchorView(R.id.save)
                                .setAction("Action", null).show();
                    }


                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(binding.deleteBook.getContext());

        binding.deleteBook.setOnClickListener(view ->
                builder.setMessage("Are you sure?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show());
    }


    private boolean addBook() {
        try (FeedReaderDbHelper dbHandler = new FeedReaderDbHelper(DetailedActivity.this)) {

            String newTitle = binding.detailBookTitle.getText().toString().strip();
            int owned = Integer.parseInt(String.valueOf(binding.bookOwned.getText()));
            String missing = binding.booksMissing.getText().toString();
            int available = Integer.parseInt(String.valueOf(binding.bookAvailable.getText()));

            return dbHandler.addNewBook(newTitle, owned, available, missing, this.book.getNextReleases());
        }
    }

    private boolean updateBook() {
        try (FeedReaderDbHelper dbHandler = new FeedReaderDbHelper(DetailedActivity.this)) {

            String newTitle = binding.detailBookTitle.getText().toString().strip();
            int owned = Integer.parseInt(String.valueOf(binding.bookOwned.getText()));
            String missing = binding.booksMissing.getText().toString();
            int available = Integer.parseInt(String.valueOf(binding.bookAvailable.getText()));

            return dbHandler.updateBook(this.book.getTitle(), newTitle, owned, available, missing, this.book.getNextReleases());
        }
    }

    private boolean deleteBook() {
        try (FeedReaderDbHelper dbHandler = new FeedReaderDbHelper(DetailedActivity.this)) {
            return dbHandler.deleteBook(this.book.getTitle());
        }
    }
}