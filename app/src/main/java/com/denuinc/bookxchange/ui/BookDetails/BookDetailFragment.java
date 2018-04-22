package com.denuinc.bookxchange.ui.BookDetails;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denuinc.bookxchange.R;
import com.denuinc.bookxchange.binding.FragmentDataBindingComponent;
import com.denuinc.bookxchange.databinding.BookDetailFragmentBinding;
import com.denuinc.bookxchange.databinding.FavoritesFragmentBinding;
import com.denuinc.bookxchange.di.Injectable;
import com.denuinc.bookxchange.utils.AutoClearedValue;
import com.denuinc.bookxchange.vo.Book;

import javax.inject.Inject;

public class BookDetailFragment extends Fragment implements Injectable {

    public final static String TAG = BookDetailFragment.class.getSimpleName();
    public final static String BOOK_EXTRA = "BOOK_DETAILs";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    android.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    AutoClearedValue<BookDetailFragmentBinding> binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BookDetailFragmentBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.book_detail_fragment, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        Book book = (Book) bundle.getParcelable(BOOK_EXTRA);
        binding.get().setBook(book);
    }
}
