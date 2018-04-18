package com.denuinc.bookxchange.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.denuinc.bookxchange.AppExecutors;
import com.denuinc.bookxchange.api.ApiResponse;
import com.denuinc.bookxchange.api.BookSearchResponse;
import com.denuinc.bookxchange.api.GoogleBookService;
import com.denuinc.bookxchange.db.BookDao;
import com.denuinc.bookxchange.db.BookXchangeDB;
import com.denuinc.bookxchange.utils.AbsentLiveData;
import com.denuinc.bookxchange.utils.RateLimiter;
import com.denuinc.bookxchange.vo.Book;
import com.denuinc.bookxchange.vo.BookSearchResult;
import com.denuinc.bookxchange.vo.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Florian on 2/27/2018.
 */

@Singleton
public class BookRepository {

    private final BookXchangeDB db;
    private final BookDao bookDao;
    private final GoogleBookService googleBookService;
    private final AppExecutors appExecutors;
    private MediatorLiveData<List<Book>> favorites;

    private RateLimiter<String> bookListRateLimit = new RateLimiter<>(10, TimeUnit.MINUTES);

    @Inject
    public BookRepository(BookXchangeDB db, BookDao bookDao, GoogleBookService googleBookService, AppExecutors appExecutors) {
        this.db = db;
        this.bookDao = bookDao;
        this.googleBookService = googleBookService;
        this.appExecutors = appExecutors;
        this.favorites = new MediatorLiveData<>();

        favorites.addSource(bookDao.loadFavorites(), books -> {
            if (books != null) {
                favorites.postValue(books);
            }
        });
    }

    public MediatorLiveData<List<Book>> getFavorites() {
        return favorites;
    }

    public void updateFavorite(Book book) {
        appExecutors.diskIO().execute(() -> {
            db.beginTransaction();
            try {
                Boolean isFavorite = false;
                if (favorites.getValue() != null) {
                    for (Book b: favorites.getValue()) {
                        if (b.googleBookId.equals(book.googleBookId)) {
                            isFavorite = b.isFavorite;
                        }
                    }
                }
                bookDao.updateFavorite(book.googleBookId, isFavorite ? 0 : 1);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        });
    }

    public LiveData<Resource<List<Book>>> loadBooks(String title) {
        return new NetworkBoundResource<List<Book>, List<Book>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<Book> item) {
                bookDao.insertBooks(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Book> data) {
                return data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<Book>> loadFromDb() {
                return bookDao.loadBooks(title);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Book>>> createCall() {
                return googleBookService.getBookByTitle(title);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Book>> loadBook(String id) {
        return new NetworkBoundResource<Book, Book>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull Book item) {bookDao.insert(item); }

            @Override
            protected boolean shouldFetch(@Nullable Book data) { return data == null; }

            @NonNull
            @Override
            protected LiveData<Book> loadFromDb() { return bookDao.findById(id); }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Book>> createCall() {
                return googleBookService.getBookById(id);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> searchNextPage(String query) {
        FetchNextPage fetchNextPage = new FetchNextPage(query, googleBookService, db);
        appExecutors.networkIO().execute(fetchNextPage);
        return fetchNextPage.getLiveData();
    }

    public LiveData<Resource<List<Book>>> search(String query) {
        return new NetworkBoundResource<List<Book>, BookSearchResponse>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull BookSearchResponse item) {
                List<String> bookIds = item.getBookIds();
                BookSearchResult bookSearchResult = new BookSearchResult(query, bookIds, item.getTotal(), item.getIndex());
                db.beginTransaction();
                try {
                    bookDao.insertBooks(item.getItems());
                    bookDao.insert(bookSearchResult);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Book> data) { return data == null; }

            @NonNull
            @Override
            protected LiveData<List<Book>> loadFromDb() {
                return Transformations.switchMap(bookDao.search(query), searchData -> {
                    if (searchData == null) {
                        return AbsentLiveData.create();
                    } else {
                        return bookDao.loadBooks(searchData.googleBookIds);
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<BookSearchResponse>> createCall() {
                return googleBookService.searchBook(query);
            }

            @Override
            protected BookSearchResponse processResponse(ApiResponse<BookSearchResponse> response) {
                BookSearchResponse body = response.body;
                if (body != null && body.getItems() != null) {
                    int index =  (body.getIndex() == null) ? body.getItems().size() : (body.getIndex() + body.getItems().size());
                    body.setIndex(index);
                }
                return body;
            }
        }.asLiveData();
    }
}
