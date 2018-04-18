package com.denuinc.bookxchange.di;

import com.denuinc.bookxchange.ui.FavoritesActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FavoritesActivityModule {


    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract FavoritesActivity contributeFavoritesActivity();

}
