package com.denuinc.bookxchange.api;

import android.arch.lifecycle.LiveData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Florian on 2/26/2018.
 */

public interface GoogleBookService {

    @GET("volumes")
    LiveData<ApiResponse<BookSearchResponse>> searchBook(@Query("q") String query);

    @GET("volumes")
    Call<BookSearchResponse> searchBook(@Query("q") String query, @Query("startIndex") int page);
}
