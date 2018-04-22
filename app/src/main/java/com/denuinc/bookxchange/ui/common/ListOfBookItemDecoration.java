package com.denuinc.bookxchange.ui.common;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;

import android.view.View;

/**
 * Created by Florian on 3/13/2018.
 */

public class ListOfBookItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;

    public ListOfBookItemDecoration(int spanCount, int spacing) {
        this.spanCount = spanCount <=1 ? 2 : spanCount;
        this.spacing = spacing == 0 ? 30 : spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;

            outRect.left = spacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;

            if (position < spanCount) {
                outRect.top = spacing;
            }
            outRect.bottom = spacing;
    }
}
