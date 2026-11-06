package com.example.mybooklist.book;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Book implements Parcelable {

    public static final Creator<Book> CREATOR = new Creator<>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
    private String title;
    private int owned;
    private String missing;
    private int available;
    private List<Date> nextReleases;

    public Book(String title, int owned, String missing, int available, List<Date> nextReleases) {
        this.title = title;
        this.owned = owned;
        this.missing = missing;
        this.available = available;
        this.nextReleases = nextReleases;
    }

    public Book(String title, int owned, String missing, int available) {
        this(title, owned, missing, available, new ArrayList<>());
    }

    public Book(String title, int owned, int available) {
        this(title, owned, "", available);
    }

    public Book(String title) {
        this(title, 0, 0);
    }

    protected Book(Parcel in) {
        title = in.readString();
        owned = in.readInt();
        missing = in.readString();
        available = in.readInt();

        long[] releasesTab = in.createLongArray();

        if (releasesTab != null) {
            nextReleases = new ArrayList<>(releasesTab.length);
            for (long release : releasesTab) {
                nextReleases.add(new Date(release));
            }
        } else {
            nextReleases = new ArrayList<>();
        }

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

    public List<Date> getNextReleases() {
        return nextReleases;
    }

    public void setNextReleases(List<Date> nextReleases) {
        this.nextReleases = nextReleases;
    }

    public void removeNextRelease(Date nextRelease) {
        this.nextReleases.remove(nextRelease);
    }

    public boolean hasMissing() {
        return missing != null && !missing.isBlank();
    }

    public boolean isMaxOwned() {
        return getOwned() == getAvailable();
    }

    @NonNull
    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", owned=" + owned +
                ", missing='" + missing + '\'' +
                ", available=" + available +
                ", nextRelease=" + nextReleases +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(owned);
        dest.writeString(missing);
        dest.writeInt(available);

        int tabSize = nextReleases != null ? nextReleases.size() : 0;

        long[] releasesTab = new long[tabSize];
        for (int i = 0; i < releasesTab.length; i++) releasesTab[i] = nextReleases.get(i).getTime();
        dest.writeLongArray(releasesTab);
    }
}
