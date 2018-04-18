package com.denuinc.bookxchange.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.denuinc.bookxchange.api.ApiResponse;
import com.denuinc.bookxchange.api.BookSearchResponse;
import com.denuinc.bookxchange.api.GoogleBookService;
import com.denuinc.bookxchange.db.BookXchangeDB;
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
    private final BookXchangeDB db;

    public FetchNextPage(String query, GoogleBookService googleBookService, BookXchangeDB db) {
        this.query = query;
        this.googleBookService = googleBookService;
        this.db = db;
    }

    @Override
    public void run() {
        BookSearchResult current = db.bookDao().findSearchResult(query);
        if(current == null) {
            liveData.postValue(null);
            return;
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
                // we merge all repo ids into 1 list so that it is easier to fetch the result list.
                List<String> ids = new ArrayList<>();
                ids.addAll(current.googleBookIds);
                //noinspection ConstantConditions
                ids.addAll(apiResponse.body.getBookIds());
                BookSearchResult merged = new BookSearchResult(query, ids,
                        apiResponse.body.getTotal(), nextPage);
                try {
                    db.beginTransaction();
                    db.bookDao().insert(merged);
                    db.bookDao().insertBooks(apiResponse.body.getItems());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
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
