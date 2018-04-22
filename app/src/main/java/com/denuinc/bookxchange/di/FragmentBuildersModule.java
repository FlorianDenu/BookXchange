package com.denuinc.bookxchange.di;

import com.denuinc.bookxchange.ui.BookDetails.BookDetailFragment;
import com.denuinc.bookxchange.ui.BookListFragment;
import com.denuinc.bookxchange.ui.FavoritesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Florian on 3/8/2018.
 */

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract BookListFragment contributeBookListFragment();

    @ContributesAndroidInjector
    abstract FavoritesFragment contributeFavoritesFragment();

    @ContributesAndroidInjector
    abstract BookDetailFragment contributeBookDetailFragment();
}
