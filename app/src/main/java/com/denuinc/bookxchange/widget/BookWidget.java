package com.denuinc.bookxchange.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;

import com.denuinc.bookxchange.R;
import com.denuinc.bookxchange.ui.BookDetails.BookDetailFragment;
import com.denuinc.bookxchange.ui.BookDetails.BookDetailsActivity;
import com.denuinc.bookxchange.vo.Book;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Implementation of App Widget functionality.
 */
public class BookWidget extends AppWidgetProvider {

    public static final String DETAIL_ACTION = "com.denuinc.bookXchange.widget.DETAIL_ACTION";
    public static final String EXTRA_ITEM = "com.denuinc.bookXchange.widget.EXTRA_ITEM";
    public static final String DATA_FETCHED = "com.denuinc.bookXchange.widget.DATA_FETCHED";

    static void updateAppWidget(Context context,
                                int appWidgetId) {

        Intent intent = new Intent(context, FetchDataService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        FetchDataService.enqueueWork(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        LocalBroadcastManager.getInstance(context).registerReceiver(this, new IntentFilter(DATA_FETCHED));
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

        Intent intent = new Intent(context, StackWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.book_widget);

        if (FetchDataService.books != null) {
            ArrayList<String> thumnails = new ArrayList<>();
            for (Book book: FetchDataService.books) {
                thumnails.add(book.volumeInfo.imageLinks.thumbnail);
            }
            intent.putStringArrayListExtra("data", thumnails);
        }

        remoteViews.setRemoteAdapter(R.id.stack_view, intent);
        remoteViews.setEmptyView(R.id.stack_view, R.id.empty_view);

        Intent detailIntent = new Intent(context, BookWidget.class);
        detailIntent.setAction(BookWidget.DETAIL_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.stack_view, pendingIntent);
        return remoteViews;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (Objects.equals(intent.getAction(), DATA_FETCHED)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

        //noinspection ConstantConditions
        if (intent.getAction().equals(DETAIL_ACTION)) {
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            Intent detailsIntent = new Intent(context, BookDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(BookDetailFragment.BOOK_EXTRA, FetchDataService.books.get(viewIndex));
            detailsIntent.putExtras(bundle);
            context.startActivity(detailsIntent);
        }
    }
}

