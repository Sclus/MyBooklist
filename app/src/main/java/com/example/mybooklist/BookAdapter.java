package com.example.mybooklist;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, R.layout.list_item, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent){
        Book book = getItem(position);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView bookTitle = view.findViewById(R.id.listBookTitle);
        TextView bookOwned = view.findViewById(R.id.listBookOwned);
        TextView bookAvailable = view.findViewById(R.id.listBookAvailable);

        assert book != null;

        bookTitle.setText(book.getTitle());
        bookOwned.setText(String.valueOf(book.getOwned()));
        bookAvailable.setText(String.valueOf(book.getAvailable()));

        if (book.getMissing() != null && !book.getMissing().isBlank()){
            CardView bookCard = view.findViewById(R.id.bookCard);
            bookCard.setCardBackgroundColor(Color.rgb(120,50,50));
        }

        return view;
    }
}
