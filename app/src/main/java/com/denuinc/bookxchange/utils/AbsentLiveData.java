package com.denuinc.bookxchange.utils;

import android.arch.lifecycle.LiveData;

/**
 * Created by Florian on 2/27/2018.
 */

public class AbsentLiveData extends LiveData {
    private AbsentLiveData() {
        postValue(null);
    }
    public static <T> LiveData<T> create() {
        //noinspection unchecked
        return new AbsentLiveData();
    }
}
