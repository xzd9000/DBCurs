package com.example.dbcurs;

import android.database.Cursor;

import java.io.Serializable;

public class Studio implements Serializable {

    public enum DB
    {
        ID("ID", 0, 0),
        TITLE("Title", 1, 0),
        COUNTRY("Country", 2, 0);

        public final String column;
        public final int index;
        public final int stringResource;

        DB(String column, int index, int stringResource)
        {
            this.column = column;
            this.index = index;
            this.stringResource = stringResource;
        }
    }

    public final int id;
    public final String title;
    public final String country;

    public Studio(int id, String title, String country) {
        this.id = id;
        this.title = title;
        this.country = country;
    }
    public Studio(Cursor cursor) {
        id = cursor.getInt(DB.ID.index);
        title = cursor.getString(DB.TITLE.index);
        country = cursor.getString(DB.COUNTRY.index);
    }
}
