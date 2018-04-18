package com.denuinc.bookxchange.utils;

import com.denuinc.bookxchange.vo.Book;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static Book createBook(String googleBookId) {

        Book.VolumeInfo volumeInfo = new Book.VolumeInfo("Test",
                "2012/01",
                new Book.VolumeInfo.ReadingModes("false", "false"),
                "BOOK",
                "NOT_MATURE",
                false,
                "0.1",
                new Book.VolumeInfo.ImageLinks("http://books.google.com/books/content?id=ftO4jLQ2RM8C&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
                        "http://books.google.com/books/content?id=ftO4jLQ2RM8C&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"),
                "en",
                "http://books.google.ca/books?id=ftO4jLQ2RM8C&printsec=frontcover&dq=subject:art&hl=&cd=1&source=gbs_api",
                "http://books.google.ca/books?id=ftO4jLQ2RM8C&dq=subject:art&hl=&source=gbs_api",
                "https://books.google.com/books/about/Modest_Witness_Second_Millennium_FemaleM.html?hl=&id=ftO4jLQ2RM8C");

        Book.AccessInfo accessInfo = new Book.AccessInfo(
                "CA",
                "PARTIAL",
                "true",
                "false",
                "ALLOWED",
                new Book.AccessInfo.Epub("false", ""),
                new Book.AccessInfo.Pdf("false", ""),

        "http://play.google.com/books/reader?id=ftO4jLQ2RM8C&hl=&printsec=frontcover&source=gbs_api",
                "SAMPLE",
                false
        );

        return new Book(0,
                googleBookId,
                "SnIInW92kzg",
                "https://www.googleapis.com/books/v1/volumes/ftO4jLQ2RM8C",
                volumeInfo,
                new Book.SaleInfo("CA", "not_for_sale", false,""),
                accessInfo,
                new Book.Epub("false", ""),
                new Book.Pdf("false", ""),
                new Book.SearchInfo(""),
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
