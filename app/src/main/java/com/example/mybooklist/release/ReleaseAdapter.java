package com.example.mybooklist.release;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.example.mybooklist.R;
import com.example.mybooklist.book.Book;

import java.text.DateFormat;
import java.util.Date;

public class ReleaseAdapter extends ArrayAdapter<Date> {

    private final Book book;

    public ReleaseAdapter(@NonNull Context context, Book book) {
        super(context, R.layout.release_dates, book.getNextReleases());
        this.book = book;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Date release = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.release_dates, parent, false);
        }

        TextView dateText = view.findViewById(R.id.nextReleaseText);

        assert release != null;
        dateText.setText(computeDate(release));

        CardView deleteButton = view.findViewById(R.id.deleteNextRelease);

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    this.book.removeNextRelease(release);
                    this.notifyDataSetChanged();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(deleteButton.getContext());

        deleteButton.setOnClickListener(v ->
                builder.setMessage("Are you sure?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show());

        return view;
    }

    private String computeDate(Date date) {
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
        return formatter.format(date);
    }
}
