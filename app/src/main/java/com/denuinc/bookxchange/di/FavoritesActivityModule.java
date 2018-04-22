package com.denuinc.bookxchange.di;

import com.denuinc.bookxchange.ui.FavoritesActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;


/**
 * Created by Florian on 2/27/2018.
 */

@Module
abstract class FavoritesActivityModule {


    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract FavoritesActivity contributeFavoritesActivity();

}
