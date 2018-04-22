package com.denuinc.bookxchange.ui.common;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.denuinc.bookxchange.R;
import com.denuinc.bookxchange.databinding.BookItemBinding;
import com.denuinc.bookxchange.vo.Book;
import com.denuinc.bookxchange.utils.Objects;

/**
 * Created by Florian on 3/1/2018.
 */

public class BookListAdapter extends DataBoundListAdapter<Book, BookItemBinding> {
    private final DataBindingComponent dataBindingComponent;
    private final BookClickCallback bookClickCallback;

    public BookListAdapter(DataBindingComponent dataBindingComponent, BookClickCallback bookClickCallback) {
        this.dataBindingComponent = dataBindingComponent;
        this.bookClickCallback = bookClickCallback;
    }

    @Override
    protected BookItemBinding createBinding(ViewGroup parent) {
        BookItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.book_item, parent, false, dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            Book book = binding.getBook();
            if (book != null && bookClickCallback != null) {
                bookClickCallback.onClick(book);
            }
        });
        binding.btnFavorite.setOnClickListener(v -> {
            Book book = binding.getBook();
            if (book != null && bookClickCallback != null) {
                bookClickCallback.onFavoriteClick(book);
                binding.btnFavorite.setSelected(!binding.btnFavorite.isSelected());
            }
        });
        return binding;
    }

    @Override
    protected void bind(BookItemBinding binding, Book book) {
        if (book.isFavorite != null) {
            binding.btnFavorite.setSelected(book.isFavorite);
        } else {
            binding.btnFavorite.setSelected(false);
        }
        binding.setBook(book);
    }

    @Override
    protected boolean areItemsTheSame(Book oldItem, Book newItem) {
        return Objects.equals(oldItem.bookId, newItem.bookId) &&
                Objects.equals(oldItem.googleBookId, newItem.googleBookId);
    }

    @Override
    protected boolean areContentsTheSame(Book oldItem, Book newItem) {
        return Objects.equals(oldItem.volumeInfo.title, newItem.volumeInfo.title);
    }

    public interface BookClickCallback {
        void onClick(Book book);
        void onFavoriteClick(Book book);
    }
}
