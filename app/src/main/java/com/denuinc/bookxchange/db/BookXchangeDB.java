package com.denuinc.bookxchange.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.denuinc.bookxchange.vo.Book;
import com.denuinc.bookxchange.vo.BookSearchResult;

/**
 * Created by Florian on 3/8/2018.
 */
@Database(entities = {
        Book.class,
        BookSearchResult.class}, version = 6)
public abstract class BookXchangeDB extends RoomDatabase {

    abstract public BookDao bookDao();

}
