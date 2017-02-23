package com.codepath.android.booksearch.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Book implements Parcelable {
    private String openLibraryId;
    private String author;
    private String title;
    private String publisher;
    private String publishYear;

    public String getOpenLibraryId() {
        return openLibraryId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(String publishYear) {
        this.publishYear = publishYear;
    }

    // Get book cover from covers API
    public String getCoverUrl() {
        return "http://covers.openlibrary.org/b/olid/" + openLibraryId + "-L.jpg?default=false";
    }

    // Returns a Book given the expected JSON
    public static Book fromJson(JSONObject jsonObject) {
        Book book = new Book();
        try {
            // Deserialize json into object fields
            // Check if a cover edition is available
            if (jsonObject.has("cover_edition_key")) {
                book.openLibraryId = jsonObject.getString("cover_edition_key");
            } else if (jsonObject.has("edition_key")) {
                final JSONArray ids = jsonObject.getJSONArray("edition_key");
                book.openLibraryId = ids.getString(0);
            }
            book.title = jsonObject.has("title_suggest") ? jsonObject.getString("title_suggest") : "";
            book.author = getAuthor(jsonObject);
            book.publisher = getPublisher(jsonObject);
            book.publishYear = getPublishYear(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return book;
    }

    // Return comma separated author list when there is more than one author
    private static String getAuthor(final JSONObject jsonObject) {
        try {
            final JSONArray authors = jsonObject.getJSONArray("author_name");
            int numAuthors = authors.length();
            final String[] authorStrings = new String[numAuthors];
            for (int i = 0; i < numAuthors; ++i) {
                authorStrings[i] = authors.getString(i);
            }
            return TextUtils.join(", ", authorStrings);
        } catch (JSONException e) {
            return "";
        }
    }

    // Return comma separated publisher list when there is more than one publisher
    private static String getPublisher(final JSONObject jsonObject) {
        try {
            final JSONArray publishers = jsonObject.getJSONArray("publisher");
            int numPublishers = publishers.length();
            final String[] publisherStrings = new String[numPublishers];
            for (int i = 0; i < numPublishers; ++i) {
                publisherStrings[i] = publishers.getString(i);
            }
            return TextUtils.join(", ", publisherStrings);
        } catch (JSONException e) {
            return "";
        }
    }

    // Return comma separated publish_date list when there is more than one publish_date
    private static String getPublishYear(final JSONObject jsonObject) {
        try {
            final JSONArray publishYears = jsonObject.getJSONArray("publish_year");
            int numPublishYears = publishYears.length();
            final String[] publishYearStrings = new String[numPublishYears];
            for (int i = 0; i < numPublishYears; ++i) {
                publishYearStrings[i] = publishYears.getString(i);
            }
            return TextUtils.join(", ", publishYearStrings);
        } catch (JSONException e) {
            return "";
        }
    }

    // Decodes array of book json results into business model objects
    public static ArrayList<Book> fromJson(JSONArray jsonArray) {
        ArrayList<Book> books = new ArrayList<>(jsonArray.length());
        // Process each result in json array, decode and convert to business
        // object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject bookJson;
            try {
                bookJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Book book = Book.fromJson(bookJson);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.openLibraryId);
        dest.writeString(this.author);
        dest.writeString(this.title);
        dest.writeString(this.publisher);
        dest.writeString(this.publishYear);
    }

    public Book() {
    }

    protected Book(Parcel in) {
        this.openLibraryId = in.readString();
        this.author = in.readString();
        this.title = in.readString();
        this.publisher = in.readString();
        this.publishYear = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
