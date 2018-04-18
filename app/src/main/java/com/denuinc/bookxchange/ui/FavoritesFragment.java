package com.denuinc.bookxchange.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denuinc.bookxchange.R;
import com.denuinc.bookxchange.binding.FragmentDataBindingComponent;
import com.denuinc.bookxchange.databinding.BookListFragmentBinding;
import com.denuinc.bookxchange.databinding.FavoritesFragmentBinding;
import com.denuinc.bookxchange.di.Injectable;
import com.denuinc.bookxchange.ui.common.BookListAdapter;
import com.denuinc.bookxchange.ui.common.ListOfBookItemDecoration;
import com.denuinc.bookxchange.utils.AutoClearedValue;
import com.denuinc.bookxchange.vo.Book;

import java.util.Objects;

import javax.inject.Inject;

public class FavoritesFragment extends Fragment implements Injectable, BookListAdapter.BookClickCallback{

    public final static String TAG = FavoritesFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    android.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    AutoClearedValue<FavoritesFragmentBinding> binding;

    AutoClearedValue<BookListAdapter> adapter;

    private BooksViewModel booksViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FavoritesFragmentBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.favorites_fragment, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        booksViewModel = ViewModelProviders.of(this, viewModelFactory).get(BooksViewModel.class);
        initRecyclerView();
        BookListAdapter bookListAdapter = new BookListAdapter(dataBindingComponent, this);
        binding.get().bookList.setAdapter(bookListAdapter);
        adapter = new AutoClearedValue<>(this, bookListAdapter);
    }

    private void initRecyclerView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        RecyclerView recyclerView = binding.get().bookList;
        int spanCount = displayMetrics.widthPixels / 700;
        recyclerView.addItemDecoration(new ListOfBookItemDecoration(spanCount,50));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(layoutManager);
        booksViewModel.getFavorites().observe(this, result -> {
            binding.get().setFavorites(result);
            binding.get().setTotalItems((result == null) ? 0 : result.size());
            adapter.get().replace(result);
            binding.get().executePendingBindings();
        });
    }

    @Override
    public void onClick(Book book) {

    }

    @Override
    public void onFavoriteClick(Book book) {
        booksViewModel.updateFavoriteBook(book);
    }
}
