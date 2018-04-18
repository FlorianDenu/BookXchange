package com.denuinc.bookxchange.api;

import android.arch.lifecycle.LiveData;

import com.denuinc.bookxchange.vo.Book;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Florian on 2/26/2018.
 */

public interface GoogleBookService {
    @GET("book/(id)")
    LiveData<ApiResponse<Book>> getBookById(@Query("q") String id);

    @GET("book/(intitle)")
    LiveData<ApiResponse<List<Book>>> getBookByTitle(@Path("intitle") String title);

    @GET("volumes")
    LiveData<ApiResponse<BookSearchResponse>> searchBook(@Query("q") String query);

    @GET("volumes")
    Call<BookSearchResponse> searchBook(@Query("q") String query, @Query("startIndex") int page);
}
