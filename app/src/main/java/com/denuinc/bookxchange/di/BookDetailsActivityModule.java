package com.denuinc.bookxchange.di;

import com.denuinc.bookxchange.ui.BookDetails.BookDetailsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BookDetailsActivityModule {

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract BookDetailsActivity contributeBookDetailsActivity();
}