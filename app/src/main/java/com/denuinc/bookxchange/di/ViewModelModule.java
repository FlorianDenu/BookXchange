package com.denuinc.bookxchange.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.denuinc.bookxchange.ui.BooksViewModel;
import com.denuinc.bookxchange.viewmodel.BookxChangeViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by Florian on 2/27/2018.
 */
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(BooksViewModel.class)
    abstract ViewModel bindBookViewModel(BooksViewModel booksViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(BookxChangeViewModelFactory factory);
}
