package com.denuinc.bookxchange.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.denuinc.bookxchange.BookXchangeApp;
import com.denuinc.bookxchange.api.GoogleBookService;
import com.denuinc.bookxchange.db.BookDao;
import com.denuinc.bookxchange.db.BookXchangeDB;
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

    @Singleton @Provides
    BookXchangeDB provideDb(Application app) {
        return Room.databaseBuilder(app, BookXchangeDB.class, "bookXchange.db").fallbackToDestructiveMigration().build();
    }

    @Singleton @Provides
    BookDao provideBookDao(BookXchangeDB db) { return db.bookDao(); }
}
