package com.denuinc.bookxchange.di;

import android.app.Application;

import com.denuinc.bookxchange.api.GoogleBookService;
import com.denuinc.bookxchange.db.BookProvider;
import com.denuinc.bookxchange.utils.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Florian on 2/27/2018.
 */

@Module(includes = ViewModelModule.class)
class AppModule {
    @Singleton @Provides
    GoogleBookService provideGoogleBookService() {
        return new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(GoogleBookService.class);
    }
}
