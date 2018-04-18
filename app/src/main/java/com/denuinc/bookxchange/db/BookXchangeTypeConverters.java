package com.denuinc.bookxchange.db;

import android.arch.persistence.room.TypeConverter;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Florian on 3/9/2018.
 */

public class BookXchangeTypeConverters {

    @TypeConverter
    public List<String> stringToListOfString(String value) {
        return Arrays.asList(value.split("\\s*,\\s*"));
    }


    @TypeConverter
    public static String stringListToString(List<String> strings) {
        String value = "";
        for (String s : strings) {
            value += s + ",";
        }
        return value;
    }
}
