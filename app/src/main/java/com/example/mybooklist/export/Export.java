package com.example.mybooklist.export;

import android.content.ClipData;

import com.example.mybooklist.book.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Export {

    static public ClipData exportOwned(ArrayList<Book> books) {

        Map<String, List<Integer>> ownedBooks = new HashMap<>();

        books.forEach(book -> {
            List<Integer> ownedNo = new ArrayList<>();

            for (int i = 1; i <= book.getOwned(); i++) {
                ownedNo.add(i);
            }

            if (book.hasMissing()) {
                ownedNo.removeAll(stringToIntList(book.getMissing()));
            }

            ownedBooks.put(book.getTitle(), ownedNo);
        });

        return ClipData.newPlainText("exported books", format(ownedBooks));
    }

    static public ClipData exportMissing(ArrayList<Book> books) {

        Map<String, List<Integer>> missingBooks = new HashMap<>();

        books.forEach(book -> {

            List<Integer> missingNo = new ArrayList<>();

            if (book.hasMissing()) {
                missingNo.addAll(stringToIntList(book.getMissing()));
            }
            if (!book.isMaxOwned()) {
                for (int i = book.getOwned(); i < book.getAvailable(); i++) {
                    missingNo.add(i + 1);
                }
            }

            if (!missingNo.isEmpty()) {
                missingBooks.put(book.getTitle(), missingNo);
            }
        });


        return ClipData.newPlainText("exported missing books", format(missingBooks));
    }

    private static List<Integer> stringToIntList(String stringList) {

        stringList = stringList.replaceAll("\\D+", ",");
        List<Integer> intList = new ArrayList<>();

        for (String s : stringList.split(",")) {
            intList.add(Integer.parseInt(s));
        }
        return intList;
    }

    private static String format(Map<String, List<Integer>> booksNo) {
        StringBuilder stringBuilder = new StringBuilder();

        booksNo.forEach((bookTitle, numbers) -> stringBuilder.append(bookTitle).append(" : ").append(numbers).append("\n"));

        return stringBuilder.toString().replace("[", "").replace("]", "");
    }
}
