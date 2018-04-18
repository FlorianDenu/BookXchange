package com.denuinc.bookxchange.di;

import com.denuinc.bookxchange.widget.FetchDataService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FetchDataServiceModule {

    @ContributesAndroidInjector
    abstract FetchDataService contributeFetchDataService();
}
