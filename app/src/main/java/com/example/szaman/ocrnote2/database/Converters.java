package com.example.szaman.ocrnote2.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by szaman on 23.12.17.
 */

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        if (value == null) return null;
        else return new Date(value);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        if (date == null) return null;
        else return date.getTime();
    }
}
