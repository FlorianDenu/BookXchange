package com.denuinc.bookxchange.di;

import com.denuinc.bookxchange.widget.FetchDataService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Florian on 2/27/2018.
 */

@Module
abstract class FetchDataServiceModule {

    @ContributesAndroidInjector
    abstract FetchDataService contributeFetchDataService();
}
