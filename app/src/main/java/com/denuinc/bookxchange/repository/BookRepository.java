package com.denuinc.bookxchange.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.denuinc.bookxchange.AppExecutors;
import com.denuinc.bookxchange.api.ApiResponse;
import com.denuinc.bookxchange.api.BookSearchResponse;
import com.denuinc.bookxchange.api.GoogleBookService;
import com.denuinc.bookxchange.db.BookProvider;
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

    private final GoogleBookService googleBookService;
    private final AppExecutors appExecutors;
    private MediatorLiveData<List<Book>> favorites;
    private final ContentResolver bookProvider;

    private RateLimiter<String> bookListRateLimit = new RateLimiter<>(10, TimeUnit.MINUTES);

    @Inject
    public BookRepository(GoogleBookService googleBookService, AppExecutors appExecutors, Application app) {
        this.googleBookService = googleBookService;
        this.appExecutors = appExecutors;
        this.favorites = new MediatorLiveData<>();
        this.bookProvider = app.getContentResolver();

        favorites.addSource(loadFavorite(), books -> {
            if (books != null) {
                favorites.postValue(books);
            }
        });
    }

    public MediatorLiveData<List<Book>> getFavorites() {
        return favorites;
    }

    private LiveData<List<Book>> loadFavorite() {
        List<Book> fav = new ArrayList<>();
        Cursor cursor = bookProvider.query(BookProvider.CONTENT_URI, null, " isFavorite = " + "1", null, null);
        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(BookProvider._ID));
                String googleBookId = cursor.getString(cursor.getColumnIndex(BookProvider.GOOGLE_BOOK_ID));
                String title = cursor.getString(cursor.getColumnIndex(BookProvider.TITLE));
                String description = cursor.getString(cursor.getColumnIndex(BookProvider.DESCRIPTION));
                Boolean favorite = cursor.getInt(cursor.getColumnIndex(BookProvider.IS_FAVORITE)) == 1;
                String smallThumbnail = cursor.getString(cursor.getColumnIndex(BookProvider.SMALL_THUMBNAIL));
                String thumbnail = cursor.getString(cursor.getColumnIndex(BookProvider.THUMBNAIL));
                Book book = new Book(id, googleBookId, new Book.VolumeInfo(title, description, new Book.VolumeInfo.ImageLinks(smallThumbnail, thumbnail)), favorite);
                fav.add(book);
            }
        } finally {
            cursor.close();
        }

        MutableLiveData<List<Book>> mBooks = new MutableLiveData<>();
        mBooks.setValue(fav);
        favorites.postValue(fav);
        return mBooks;
    }

    public void updateFavorite(Book book) {

        appExecutors.diskIO().execute(() -> {
            Cursor cursor = bookProvider.query(BookProvider.CONTENT_URI, null, " id = " + "'" + book.googleBookId + "'", null, null);
            while (cursor.moveToNext()) {
                Boolean favorite = cursor.getInt(cursor.getColumnIndex(BookProvider.IS_FAVORITE)) == 1;
                ContentValues contentValues = new ContentValues();
                contentValues.put(BookProvider.IS_FAVORITE, !favorite);
                bookProvider.update(BookProvider.CONTENT_URI, contentValues, " id = " + "'" + book.googleBookId + "'", null);
                if (!favorite) {
                    book.isFavorite = true;
                    favorites.getValue().add(book);
                } else {
                    book.isFavorite = false;
                    favorites.getValue().remove(book);
                }
            }
        });
    }

    public LiveData<Resource<Boolean>> searchNextPage(String query) {
        FetchNextPage fetchNextPage = new FetchNextPage(query, googleBookService, bookProvider);
        appExecutors.networkIO().execute(fetchNextPage);
        return fetchNextPage.getLiveData();
    }

    public LiveData<Resource<List<Book>>> search(String query) {
        return new NetworkBoundResource<List<Book>, BookSearchResponse>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull BookSearchResponse item) {
                for (String googleBookId: item.getBookIds()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BookProvider.TOTAL_COUNT, item.getTotal());
                    contentValues.put(BookProvider.INDEX, item.getIndex());
                    contentValues.put(BookProvider.SEARCH_GOOGLE_BOOKS_ID, googleBookId);
                    contentValues.put(BookProvider.BOOK_QUERY, query);
                    bookProvider.insert(Uri.parse("content://" +BookProvider.PROVIDER_NAME + "/bookSearch"), contentValues);
                }

                for (Book book : item.getItems()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BookProvider.GOOGLE_BOOK_ID, book.googleBookId);
                    contentValues.put(BookProvider.TITLE, book.volumeInfo.title);
                    contentValues.put(BookProvider.DESCRIPTION, book.volumeInfo.description);
                    contentValues.put(BookProvider.IS_FAVORITE, book.isFavorite);
                    contentValues.put(BookProvider.SMALL_THUMBNAIL, book.volumeInfo.imageLinks.smallThumbnail);
                    contentValues.put(BookProvider.THUMBNAIL, book.volumeInfo.imageLinks.thumbnail);
                    bookProvider.insert(BookProvider.CONTENT_URI, contentValues);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Book> data) {
                if (data == null) {
                    return true;
                }
                return data.size() <= 0;
            }

            @NonNull
            @Override
            protected LiveData<List<Book>> loadFromDb() {

                BookSearchResult bookSearchResult = new BookSearchResult(query, new ArrayList<>(), 0, 0);
                Cursor c = bookProvider.query(Uri.parse("content://" + BookProvider.PROVIDER_NAME + "/bookSearch"), null, " bookQuery LIKE " + "'%" + query.substring(query.lastIndexOf(':') + 1) + "%'", null, null);
                try {
                    while (c.moveToNext()) {
                        String googleBookId = c.getString(c.getColumnIndex(BookProvider.SEARCH_GOOGLE_BOOKS_ID));
                        int totalCount = c.getInt(c.getColumnIndex(BookProvider.TOTAL_COUNT));
                        int index = c.getInt(c.getColumnIndex(BookProvider.INDEX));
                        bookSearchResult.googleBookIds.add(googleBookId);
                        if (bookSearchResult.totalCount <= totalCount) {
                            bookSearchResult.totalCount = totalCount;
                        }
                        if (bookSearchResult.next <= index) {
                            bookSearchResult.next = index;
                        }
                    }
                } finally {
                    assert c != null;
                    c.close();
                }
                if (bookSearchResult.googleBookIds.size() <= 0) {
                    return AbsentLiveData.create();
                } else {
                        ArrayList<Book> books = new ArrayList<>();

                        for (String _id : bookSearchResult.googleBookIds) {
                            Cursor cursor = bookProvider.query(BookProvider.CONTENT_URI, null, " id = " + "'" +_id + "'", null, null);
                            try {
                                while (cursor.moveToNext()) {
                                    String id = cursor.getString(cursor.getColumnIndex(BookProvider._ID));
                                    String googleBookId = cursor.getString(cursor.getColumnIndex(BookProvider.GOOGLE_BOOK_ID));
                                    String title = cursor.getString(cursor.getColumnIndex(BookProvider.TITLE));
                                    String description = cursor.getString(cursor.getColumnIndex(BookProvider.DESCRIPTION));
                                    Boolean favorite = cursor.getInt(cursor.getColumnIndex(BookProvider.IS_FAVORITE)) == 1;
                                    String smallThumbnail = cursor.getString(cursor.getColumnIndex(BookProvider.SMALL_THUMBNAIL));
                                    String thumbnail = cursor.getString(cursor.getColumnIndex(BookProvider.THUMBNAIL));
                                    Book book = new Book(id, googleBookId, new Book.VolumeInfo(title, description, new Book.VolumeInfo.ImageLinks(smallThumbnail, thumbnail)), favorite);
                                    books.add(book);
                                }
                            } finally {
                                cursor.close();
                            }
                        }
                        MutableLiveData<List<Book>> booksLiveData = new MutableLiveData<List<Book>>();
                        booksLiveData.setValue(books);
                        return booksLiveData;
                    }
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
