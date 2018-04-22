package com.denuinc.bookxchange.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;

import com.denuinc.bookxchange.AppExecutors;
import com.denuinc.bookxchange.db.BookProvider;
import com.denuinc.bookxchange.vo.Book;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class FetchDataService extends JobIntentService {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public static ArrayList<Book> books;

    static final int JOB_ID = 1000;

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
        books = new ArrayList<>();
        appExecutors.diskIO().execute(() -> {
            Cursor cursor = getContentResolver().query(BookProvider.CONTENT_URI, null, null, null, null, null);
            ArrayList<Book> bs = new ArrayList<>();
            try {
                assert cursor != null;
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(BookProvider._ID));
                    String googleBookId = cursor.getString(cursor.getColumnIndex(BookProvider.GOOGLE_BOOK_ID));
                    String title = cursor.getString(cursor.getColumnIndex(BookProvider.TITLE));
                    String description  = cursor.getString(cursor.getColumnIndex(BookProvider.DESCRIPTION));
                    Boolean favorite = cursor.getInt(cursor.getColumnIndex(BookProvider.IS_FAVORITE)) == 1;
                    String smallThumbnail = cursor.getString(cursor.getColumnIndex(BookProvider.SMALL_THUMBNAIL));
                    String thumbnail = cursor.getString(cursor.getColumnIndex(BookProvider.THUMBNAIL));
                    Book book = new Book(id, googleBookId, new Book.VolumeInfo(title, description, new Book.VolumeInfo.ImageLinks(smallThumbnail, thumbnail)), favorite);
                    bs.add(book);
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
            books.addAll(bs);
            Intent widgetUpdateIntent = new Intent();
            widgetUpdateIntent.setAction(BookWidget.DATA_FETCHED);
            widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(widgetUpdateIntent);
            stopSelf();
        });
    }
}
