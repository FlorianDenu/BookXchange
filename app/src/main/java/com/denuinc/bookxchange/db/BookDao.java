package com.denuinc.bookxchange.db;

/**
 * Created by Florian on 2/26/2018.
 */

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.denuinc.bookxchange.vo.Book;
import com.denuinc.bookxchange.vo.BookSearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Florian on 2/26/2018.
 */
@Dao
public abstract class BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Book... books);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertBooks(List<Book> books);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long createBookIfNotExists(Book book);

    @Query("SELECT * FROM book WHERE isFavorite = 1")
    public abstract LiveData<List<Book>> loadFavorites();

    @Query("SELECT * FROM book WHERE isFavorite = 1")
    public abstract List<Book> loadFavoritesList();

    @Query("SELECT * FROM book")
    public abstract List<Book> loadAllBooks();

    @Query("UPDATE book SET isFavorite = :isFavorite WHERE googleBookId = :googleId")
    public abstract void updateFavorite(String googleId, int isFavorite);

    @Query("SELECT * FROM book WHERE googleBookId = :googleId")
    public abstract LiveData<Book> load(String googleId);

    @Query("SELECT * FROM book WHERE bookId = :bookId")
    public abstract LiveData<Book> findById(String bookId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(BookSearchResult result);

    @Query("SELECT * FROM BookSearchResult WHERE query = :query")
    public abstract LiveData<BookSearchResult> search(String query);

    public LiveData<List<Book>> loadBooks(List<String> bookIds) {
        return Transformations.map(loadById(bookIds), repositories -> repositories);
    }

    public LiveData<List<Book>> loadBooks(String title) {
        return Transformations.map(loadBytitle(title), repositories -> repositories);
    }

    @Query("SELECT * FROM Book WHERE googleBookId in (:googleBookIds)")
    abstract LiveData<List<Book>> loadById(List<String> googleBookIds);

    @Query("SELECT * FROM BOOK WHERE volume_info_title = (:title)")
    abstract LiveData<List<Book>> loadBytitle(String title);

    @Query("SELECT * FROM BookSearchResult WHERE query = :query")
    public abstract BookSearchResult findSearchResult(String query);
}