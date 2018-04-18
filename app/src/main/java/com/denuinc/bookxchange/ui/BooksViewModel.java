package com.denuinc.bookxchange.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.denuinc.bookxchange.vo.Book;
import com.denuinc.bookxchange.repository.BookRepository;
import com.denuinc.bookxchange.utils.AbsentLiveData;
import com.denuinc.bookxchange.utils.Objects;
import com.denuinc.bookxchange.vo.Resource;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by Florian on 3/5/2018.
 */

public class BooksViewModel extends ViewModel {

    private final MutableLiveData<String> query = new MutableLiveData<>();

    private final LiveData<Resource<List<Book>>> results;

    private MediatorLiveData<List<Book>> favorites;

    private final NextPageHandler nextPageHandler;

    private BookRepository bookRepository;

    @Inject
    BooksViewModel(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        nextPageHandler = new NextPageHandler(bookRepository);
        results = Transformations.switchMap(query, search -> {
            if (search == null || search.trim().length() == 0) {
                return AbsentLiveData.create();
            } else {
                return bookRepository.search(search);
            }
        });

        this.favorites = new MediatorLiveData<>();

        favorites = new MediatorLiveData<>();
        favorites.setValue(null);
        LiveData<List<Book>> books = bookRepository.getFavorites();
        favorites.addSource(books, favorites::setValue);
    }


    public void updateFavoriteBook(Book book) {
        bookRepository.updateFavorite(book);
    }

    public LiveData<List<Book>> getFavorites() {
        return favorites;
    }

    @VisibleForTesting
    public LiveData<Resource<List<Book>>> getResults() { return results; }

    public void setQuery(@NonNull String originalInput) {
        String input = originalInput.toLowerCase(Locale.getDefault()).trim();
        if (Objects.equals(input, query.getValue())) {
            return;
        }
        nextPageHandler.reset();
        query.setValue(input);
    }

    @VisibleForTesting
    public LiveData<LoadMoreState> getLoadMoreStatus() {
        return nextPageHandler.getLoadMoreState();
    }

    @VisibleForTesting
    public void loadNextPage() {
        String value = query.getValue();
        if (value == null || value.trim().length() == 0) {
            return;
        }
        nextPageHandler.queryNextPage(value);
    }

    void refresh() {
        if (query.getValue() != null) {
            query.setValue(query.getValue());
        }
    }

    static class LoadMoreState {
        private final boolean running;
        private final String errorMessage;
        private boolean handledError = false;

        LoadMoreState(boolean running, String errorMessage) {
            this.running = running;
            this.errorMessage = errorMessage;
        }

        boolean isRunning() {
            return running;
        }

        @SuppressWarnings("unused")
        String getErrorMessage() {
            return errorMessage;
        }

        String getErrorMessageIfNotHandled() {
            if (handledError) {
                return null;
            }
            handledError = true;
            return errorMessage;
        }
    }

    static class NextPageHandler implements Observer<Resource<Boolean>> {
        @Nullable
        private LiveData<Resource<Boolean>> nextPageLiveData;
        private final MutableLiveData<LoadMoreState> loadMoreState = new MutableLiveData<>();
        private String query;
        private final BookRepository repository;
        @VisibleForTesting
        boolean hasMore;

        @SuppressWarnings("WeakerAccess")
        @VisibleForTesting
        public NextPageHandler(BookRepository repository) {
            this.repository = repository;
            reset();
        }

        void queryNextPage(String query) {
            if (Objects.equals(this.query, query)) {
                return;
            }
            unregister();
            this.query = query;
            nextPageLiveData = repository.searchNextPage(query);
            loadMoreState.setValue(new LoadMoreState(true, null));
            //noinspection ConstantConditions
            nextPageLiveData.observeForever(this);
        }

        @Override
        public void onChanged(@Nullable Resource<Boolean> result) {
            if (result == null) {
                reset();
            } else {
                switch (result.status) {
                    case SUCCESS:
                        hasMore = Boolean.TRUE.equals(result.data);
                        unregister();
                        loadMoreState.setValue(new LoadMoreState(false, null));
                        break;
                    case ERROR:
                        hasMore = true;
                        unregister();
                        loadMoreState.setValue(new LoadMoreState(false, result.message));
                        break;
                }
            }
        }

        private void unregister() {
            if (nextPageLiveData != null) {
                nextPageLiveData.removeObserver(this);
                nextPageLiveData = null;
                if (hasMore) {
                    query = null;
                }
            }
        }

        private void reset() {
            unregister();
            hasMore = true;
            loadMoreState.setValue(new LoadMoreState(false, null));
        }

        MutableLiveData<LoadMoreState> getLoadMoreState() { return loadMoreState; }
    }
}
