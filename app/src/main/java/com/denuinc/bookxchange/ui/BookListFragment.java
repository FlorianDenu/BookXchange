package com.denuinc.bookxchange.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
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
import com.denuinc.bookxchange.di.Injectable;
import com.denuinc.bookxchange.ui.BookDetails.BookDetailFragment;
import com.denuinc.bookxchange.ui.BookDetails.BookDetailsActivity;
import com.denuinc.bookxchange.ui.common.ListOfBookItemDecoration;
import com.denuinc.bookxchange.ui.common.NavigationController;
import com.denuinc.bookxchange.vo.Book;
import com.denuinc.bookxchange.ui.common.BookListAdapter;
import com.denuinc.bookxchange.utils.AutoClearedValue;
import com.denuinc.bookxchange.vo.Category;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

/**
 * Created by Florian on 3/1/2018.
 */

public class BookListFragment extends Fragment implements Injectable, BookListAdapter.BookClickCallback {

    public final static String TAG = BookListFragment.class.getSimpleName();
    private final static String QUERY_TAG = "QUERY";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    AutoClearedValue<BookListFragmentBinding> binding;

    AutoClearedValue<BookListAdapter> adapter;

    private BooksViewModel booksViewModel;

    private Boolean isScrooled = false;

    private String query = "subject:science+fiction";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(QUERY_TAG);
        }
        BookListFragmentBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.book_list_fragment, container, false, dataBindingComponent);
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
        binding.get().setQuery(query);
        booksViewModel.setQuery(query);
        binding.get().setCallback(() -> booksViewModel.refresh());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QUERY_TAG, query);
    }

    void fetchCategory(Category category) {
        query = category.getSearch();
        binding.get().setQuery(query);
        booksViewModel.setQuery(query);
        binding.get().setCallback(() -> booksViewModel.refresh());
    }

    void search(String query) {
        if (booksViewModel == null) return;
        this.query = query;
        binding.get().setQuery(query);
        booksViewModel.setQuery(query);
        binding.get().setCallback(() -> booksViewModel.refresh());
    }

    @Override
    public void onClick(Book book) {
        Intent intent = new Intent(getActivity(), BookDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BookDetailFragment.BOOK_EXTRA, book);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(Book book) {
        booksViewModel.updateFavoriteBook(book);
    }

    @SuppressLint("VisibleForTests")
    private void initRecyclerView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        RecyclerView recyclerView = binding.get().bookList;
        int spanCount = displayMetrics.widthPixels / 700;
        recyclerView.addItemDecoration(new ListOfBookItemDecoration(spanCount,50));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(layoutManager);
        binding.get().bookList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                if (isScrooled && lastPosition == adapter.get().getItemCount() - 1) {
                    booksViewModel.loadNextPage();
                }
                isScrooled = true;
            }
        });
        booksViewModel.getResults().observe(this, result -> {
            binding.get().setSearchResource(result);
            binding.get().setTotalItems((result == null || result.data == null)
                    ? 0 : result.data.size());
            adapter.get().replace(result == null ? null : result.data);
            binding.get().executePendingBindings();
        });

        booksViewModel.getLoadMoreStatus().observe(this, loadMoreState -> {
            if (loadMoreState == null) {
                binding.get().setLoadingMore(false);
            } else {
                binding.get().setLoadingMore(loadMoreState.isRunning());
                String error = loadMoreState.getErrorMessageIfNotHandled();
                if (error != null) {
                    Snackbar.make(binding.get().listLayout, error, Snackbar.LENGTH_SHORT).show();
                }
            }
            binding.get().executePendingBindings();
        });
    }
}
