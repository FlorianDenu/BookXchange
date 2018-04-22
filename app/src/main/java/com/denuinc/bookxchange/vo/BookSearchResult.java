package com.denuinc.bookxchange.vo;


import android.support.annotation.NonNull;


import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by Florian on 3/6/2018.
 */

public class BookSearchResult {
    @NonNull
    public final String query;
    public final List<String> googleBookIds;
    public int totalCount;
    @Nullable
    public Integer next;

    public BookSearchResult(@NonNull String query, List<String> googleBookIds, int totalCount, @Nullable Integer next) {
        this.query = query;
        this.googleBookIds = googleBookIds;
        this.totalCount = totalCount;
        this.next = next;
    }
}
