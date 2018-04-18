package com.denuinc.bookxchange.di;

import com.denuinc.bookxchange.ui.BookListActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Florian on 3/8/2018.
 */

@Module
public abstract class BookListActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract BookListActivity contributeBookListActivity();
}
