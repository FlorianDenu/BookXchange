package com.denuinc.bookxchange.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.denuinc.bookxchange.api.ApiResponse;
import com.denuinc.bookxchange.api.BookSearchResponse;
import com.denuinc.bookxchange.api.GoogleBookService;
import com.denuinc.bookxchange.db.BookProvider;
import com.denuinc.bookxchange.vo.Book;
import com.denuinc.bookxchange.vo.BookSearchResult;
import com.denuinc.bookxchange.vo.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by Florian on 3/13/2018.
 */

public class FetchNextPage implements Runnable {
    private final MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
    private final String query;
    private final GoogleBookService googleBookService;
    private final ContentResolver provider;

    FetchNextPage(String query, GoogleBookService googleBookService, ContentResolver provider) {
        this.query = query;
        this.googleBookService = googleBookService;
        this.provider = provider;
    }

    @Override
    public void run() {
        BookSearchResult current = new BookSearchResult(query, new ArrayList<>(), 0, 0);
        try (Cursor cursor = provider.query(Uri.parse("content://" + BookProvider.PROVIDER_NAME + "/bookSearch"), null, " bookQuery LIKE " + "'%" + query.substring(query.lastIndexOf(':') + 1) + "%'", null, null)) {
            assert cursor != null;
            while (cursor.moveToNext()) {
                String googleBookId = cursor.getString(cursor.getColumnIndex(BookProvider.BOOK_QUERY));
                int totalCount = cursor.getInt(cursor.getColumnIndex(BookProvider.TOTAL_COUNT));
                int index = cursor.getInt(cursor.getColumnIndex(BookProvider.INDEX));
                current.googleBookIds.add(googleBookId);
                current.next = index;
                current.totalCount = totalCount;
                current.googleBookIds.add(googleBookId);
            }
        }
        Integer nextPage = current.next;
        Log.d("", "This is the value of the next page " + nextPage);
        if (nextPage == null) {
            return;
        }
        nextPage += 10;
        try {
            Response<BookSearchResponse> response = googleBookService
                    .searchBook(query, nextPage).execute();
            ApiResponse<BookSearchResponse> apiResponse = new ApiResponse<>(response);
            if (apiResponse.isSuccessful()) {
                List<String> ids = new ArrayList<>(current.googleBookIds);
                assert apiResponse.body != null;
                ids.addAll(apiResponse.body.getBookIds());
                for (Book book : apiResponse.body.getItems()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BookProvider.GOOGLE_BOOK_ID, book.googleBookId);
                    contentValues.put(BookProvider.TITLE, book.volumeInfo.title);
                    contentValues.put(BookProvider.DESCRIPTION, book.volumeInfo.description);
                    contentValues.put(BookProvider.IS_FAVORITE, book.isFavorite);
                    contentValues.put(BookProvider.SMALL_THUMBNAIL, book.volumeInfo.imageLinks.smallThumbnail);
                    contentValues.put(BookProvider.THUMBNAIL, book.volumeInfo.imageLinks.thumbnail);
                    provider.insert(BookProvider.CONTENT_URI, contentValues);
                }

                for (String id : ids) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BookProvider.BOOK_QUERY, query);
                    contentValues.put(BookProvider.SEARCH_GOOGLE_BOOKS_ID, id);
                    contentValues.put(BookProvider.TOTAL_COUNT, apiResponse.body.getTotal());
                    contentValues.put(BookProvider.INDEX, nextPage);
                    provider.insert(Uri.parse("content://" + BookProvider.PROVIDER_NAME + "/bookSearch"), contentValues);
                }
                if (current.totalCount > nextPage + 10) {
                    liveData.postValue(Resource.success(true));
                }
            } else {
                liveData.postValue(Resource.error(apiResponse.errorMessage, true));
            }
        } catch (IOException e) {
            liveData.postValue(Resource.error(e.getMessage(), true));
        }
    }

    LiveData<Resource<Boolean>> getLiveData() {
        return liveData;
    }
}
