package com.denuinc.bookxchange.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.denuinc.bookxchange.R;
import com.denuinc.bookxchange.vo.Book;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class StackWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<String> post;

    private Context context;

    StackRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;

        if (intent.hasExtra("data")) {
            post = new ArrayList<>();
            ArrayList<String> intentBooks =  intent.getStringArrayListExtra("data");
            post.addAll(intentBooks);
        } else {
            populateListItem();
        }
    }

    private void populateListItem() {

        if (FetchDataService.books != null) {
            for (Book book: FetchDataService.books) {
                post.add(book.volumeInfo.imageLinks.smallThumbnail);
            }
        } else {
            post = new ArrayList<>();
        }
    }

    @Override
    public void onCreate() { }

    @Override
    public void onDataSetChanged() {
        if (FetchDataService.books != null) {
            for (Book book: FetchDataService.books) {
                post.add(book.volumeInfo.imageLinks.smallThumbnail);
            }
        }
    }

    @Override
    public void onDestroy() { }

    @Override
    public int getCount() {
        return this.post.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_item);

        Bundle bundle = new Bundle();
        bundle.putInt(BookWidget.EXTRA_ITEM, position);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        rv.setOnClickFillInIntent(R.id.click_item, intent);
        try {
            @SuppressWarnings("deprecation") Bitmap bitmap = Glide.with(context.getApplicationContext()).asBitmap().load(post.get(position)).into(400,400).get();

            rv.setImageViewBitmap(R.id.widget_image_thumbnail, bitmap);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}