package com.denuinc.bookxchange.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.denuinc.bookxchange.R;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by Florian on 3/25/2018.
 */

public class FloatActionButtonScroll extends FloatingActionButton.Behavior {

    private Context context;
    private Boolean isHiden = false;

    public FloatActionButtonScroll(Context context, AttributeSet attrs) {
        super();
        this.context = context;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        Animation hideAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_hide_animation);
        Animation showAnimation = AnimationUtils.loadAnimation(context, R.anim.fab_show_animation);
        hideAnimation.setFillAfter(true);
        showAnimation.setFillAfter(true);

        if (dyConsumed > 0 && !isHiden) {
            child.startAnimation(hideAnimation);
            isHiden = true;
        } else if (dyConsumed < 0 && isHiden) {
            child.startAnimation(showAnimation);
            isHiden = false;
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}