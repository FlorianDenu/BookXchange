package com.denuinc.bookxchange.binding;

import android.databinding.BindingAdapter;
import android.view.View;

/**
 * Created by Florian on 3/1/2018.
 */

public class BindingAdapters {
    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
