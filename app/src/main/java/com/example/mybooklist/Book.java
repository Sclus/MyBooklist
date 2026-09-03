package com.example.mybooklist;


public class Book {

    private String title;
    private int owned;
    private String missing;
    private int available;

    public Book(String title, int owned, String missing, int available) {
        this.title = title;
        this.owned = owned;
        this.missing = missing;
        this.available = available;
    }

    public Book(String title, int owned, int available) {
        this(title, owned, "", available);
    }

    public Book(String title, int owned) {
        this(title, owned, owned);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOwned() {
        return owned;
    }

    public void setOwned(int owned) {
        this.owned = owned;
    }

    public String getMissing() {
        return missing;
    }

    public void setMissing(String missing) {
        this.missing = missing;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
}
