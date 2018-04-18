package com.denuinc.bookxchange.di;

import android.app.Application;

import com.denuinc.bookxchange.BookXchangeApp;
import com.denuinc.bookxchange.widget.FetchDataService;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Created by Florian on 2/27/2018.
 */
@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        BookListActivityModule.class,
        FavoritesActivityModule.class,
        FetchDataServiceModule.class,
        BookDetailsActivityModule.class
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance Builder application(Application application);
        AppComponent build();
    }
    void inject(BookXchangeApp bookXchangeApp);
}
