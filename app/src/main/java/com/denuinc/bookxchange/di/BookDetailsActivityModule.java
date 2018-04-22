package com.denuinc.bookxchange.di;

import com.denuinc.bookxchange.ui.BookDetails.BookDetailsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Florian on 2/27/2018.
 */

@Module
abstract class BookDetailsActivityModule {

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract BookDetailsActivity contributeBookDetailsActivity();
}