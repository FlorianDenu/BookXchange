package com.denuinc.bookxchange.ui.common;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;

import com.denuinc.bookxchange.R;
import com.denuinc.bookxchange.ui.BookListActivity;
import com.denuinc.bookxchange.ui.BookListFragment;
import com.denuinc.bookxchange.ui.CategoriesFragment;
import com.denuinc.bookxchange.ui.FavoritesActivity;

import javax.inject.Inject;

/**
 * Created by Florian on 3/6/2018.
 */

public class NavigationController {
    private final int containerId;
    private final int menuContainerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(BookListActivity bookListActivity) {
        this.containerId = R.id.fragment_container;
        this.menuContainerId = R.id.menu_container;
        this.fragmentManager = bookListActivity.getSupportFragmentManager();
    }

    public void navigateToBookList(BookListFragment bookListFragment) {
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        fragmentManager.beginTransaction()
                .replace(containerId, bookListFragment, BookListFragment.TAG)
                .commit();
        fragmentManager.beginTransaction()
                .replace(menuContainerId, categoriesFragment)
                .commit();
    }

    public void navigateToFavoritesActivity(Context fromActivity) {
        Intent intent = new Intent(fromActivity, FavoritesActivity.class);
        fromActivity.startActivity(intent);
    }
}
