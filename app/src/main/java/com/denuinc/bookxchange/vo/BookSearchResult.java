package com.denuinc.bookxchange.vo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.denuinc.bookxchange.db.BookXchangeTypeConverters;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by Florian on 3/6/2018.
 */

@Entity(primaryKeys = {"query"})
@TypeConverters(BookXchangeTypeConverters.class)
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
