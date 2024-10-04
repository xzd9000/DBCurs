package com.example.dbcurs;

public class DBSafeStudio extends DBSafeRecord {
    public final String id;
    public final String title;
    public final String country;

    public DBSafeStudio(Studio studio) {
        id =                                      Integer.toString(studio.id);
        title =                                 "'" + escapeString(studio.title)   + "'";
        country = studio.country.length() > 0 ? "'" + escapeString(studio.country) + "'" : "NULL";
    }
}
