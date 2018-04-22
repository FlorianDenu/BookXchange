package com.denuinc.bookxchange.utils;

import com.denuinc.bookxchange.vo.Book;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static Book createBook(String googleBookId) {


        return new Book("0",
                googleBookId,
                new Book.VolumeInfo("Test", "description", new Book.VolumeInfo.ImageLinks("http://books.google.com/books/content?id=ftO4jLQ2RM8C&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
                        "http://books.google.com/books/content?id=ftO4jLQ2RM8C&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api")),
                false
        );
    }

    public static List<Book> createBooks(int count) {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            books.add(createBook("book"+i));
        }
        return books;
    }
}
