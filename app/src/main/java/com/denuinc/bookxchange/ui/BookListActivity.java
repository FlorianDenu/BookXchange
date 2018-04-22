package com.denuinc.bookxchange.ui;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.MenuInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.denuinc.bookxchange.BookXchangeApp;
import com.denuinc.bookxchange.R;
import com.denuinc.bookxchange.ui.common.NavigationController;
import com.denuinc.bookxchange.utils.ActivityUtils;
import com.denuinc.bookxchange.vo.Category;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by Florian on 2/27/2018.
 *
 * In order to realise this project, i used the following web site to learn more about android architecture components and data binding
 *
 * https://developer.android.com/topic/libraries/architecture/index.html
 *
 * https://github.com/googlesamples/android-architecture
 *
 *https://github.com/googlesamples/android-architecture-components
 * 
 */

public class BookListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HasSupportFragmentInjector, OnCategorieSelectedListener, SearchView.OnQueryTextListener {

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject
    NavigationController navigationController;
    private BookListFragment bookListFragment;

    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, R.string.add_book_details, Snackbar.LENGTH_LONG)
                .show());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            bookListFragment = new BookListFragment();
            BookXchangeApp analyticsApplication = (BookXchangeApp) getApplication();
            this.tracker = analyticsApplication.getDefaultTracker();
        } else {
            bookListFragment = (BookListFragment) getSupportFragmentManager().findFragmentByTag(BookListFragment.TAG);
        }
        navigationController.navigateToBookList(bookListFragment);

        MobileAds.initialize(this, getString(R.string.adbMobKey));
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        tracker.setScreenName("Image~" + BookListActivity.class.getSimpleName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.book_list_acitivty, menu);
        inflater.inflate(R.menu.options_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            ActivityUtils.activityUtils().displayInformation(this,
                    getString(R.string.information_title),
                    getString(R.string.information_details),
                    (dialog, which) -> ActivityUtils.activityUtils().emailIntent(getApplicationContext()),
                    (dialog, which) -> Toast.makeText(BookListActivity.this, ":)", Toast.LENGTH_SHORT).show());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_favorites) {
            navigationController.navigateToFavoritesActivity(this);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void categorySelected(Category category) {
        bookListFragment.fetchCategory(category);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Change category to " + category.getSearch())
                .build()
        );
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        bookListFragment.search(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
