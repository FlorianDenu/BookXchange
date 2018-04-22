package com.denuinc.bookxchange.ui.BookDetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.denuinc.bookxchange.R;
import com.denuinc.bookxchange.utils.ActivityUtils;
import com.denuinc.bookxchange.vo.Book;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by Florian on 2/27/2018.
 */

public class BookDetailsActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<android.support.v4.app.Fragment> dispatchingAndroidInjector;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_details_activity);
        if (getIntent() != null) {
            Book book = getIntent().getParcelableExtra(BookDetailFragment.BOOK_EXTRA);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(book.volumeInfo.title);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            BookDetailFragment bookDetailFragment = new BookDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(BookDetailFragment.BOOK_EXTRA, book);
            bookDetailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.book_details_fragment_container, bookDetailFragment, BookDetailFragment.TAG)
                    .commit();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> ActivityUtils.activityUtils().emailIntent(this));
    }



    @Override
    public AndroidInjector<android.support.v4.app.Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
