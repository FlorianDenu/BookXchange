package com.denuinc.bookxchange.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;

import com.denuinc.bookxchange.AppExecutors;
import com.denuinc.bookxchange.db.BookDao;
import com.denuinc.bookxchange.vo.Book;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class FetchDataService extends JobIntentService {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public static ArrayList<Book> books;

    static final int JOB_ID = 1000;

    @Inject
    BookDao bookDao;

    @Inject
    AppExecutors appExecutors;

    public FetchDataService() { }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
        super.onCreate();
    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, FetchDataService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        try {
            if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            }
        } catch (NullPointerException e) {
            appWidgetId = 0;
        }
        fetchData();
    }

    private void fetchData() {
        if (bookDao == null) return;
        books = new ArrayList<>();
        appExecutors.diskIO().execute(() -> {
            // If no favorite, load all the books
            ArrayList<Book> bs = bookDao.loadFavoritesList().size() > 0 ? new ArrayList<>(bookDao.loadFavoritesList()) : new ArrayList<>(bookDao.loadAllBooks());
            books.addAll(bs);
            Intent widgetUpdateIntent = new Intent();
            widgetUpdateIntent.setAction(BookWidget.DATA_FETCHED);
            widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(widgetUpdateIntent);
            stopSelf();
        });
    }
}
