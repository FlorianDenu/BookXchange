package com.denuinc.bookxchange.api;


import android.support.annotation.NonNull;
import android.util.Log;

import com.denuinc.bookxchange.vo.Book;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Florian on 3/6/2018.
 */

public class BookSearchResponse {
    @SerializedName("totalItems")
    private int total;
    @SerializedName("items")
    private List<Book> items;
    private Integer index;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Book> getItems() {
        if (items == null) {
            return new ArrayList<Book>();
        }
        return items;
    }

    public void setItems(List<Book> items) {
        this.items = items;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }

    @NonNull
    public List<String> getBookIds() {
        List<String> bookIds = new ArrayList<>();
        if (items == null) {
            return new ArrayList<String>();
        }
        for (Book book : items) {
            bookIds.add(book.googleBookId);
        }
        return bookIds;
    }
}
