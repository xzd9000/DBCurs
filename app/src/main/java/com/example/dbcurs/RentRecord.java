package com.example.dbcurs;

import android.content.Context;
import android.database.Cursor;

import java.io.Serializable;
import java.util.Date;

public class RentRecord implements Serializable {

    public enum DB
    {
        ID("ID", 0, 0),
        CLIENTID("ClientID", 1, R.string.dbColumnRentClient),
        MOVIEID("MovieID", 2, R.string.dbColumnRentMovie),
        GIVEDATE("GiveDate", 3, R.string.dbColumnRentGiveDate),
        RETURNDATE("ReturnDate", 4, R.string.dbColumnRentReturnDate);

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

    public static final String[] getDropdownColumns(Context context) {
        return new String[] {
                context.getString(DB.CLIENTID.stringResource),
                context.getString(DB.MOVIEID.stringResource),
                context.getString(DB.GIVEDATE.stringResource),
                context.getString(DB.RETURNDATE.stringResource),
        };
    }

    public final int id;
    public final String client;
    public final String movie;
    public final Date giveDate;
    public final Date returnDate;

    public RentRecord(int id, String client, String movie, Date giveDate, Date returnDate) {
        this.id = id;
        this.client = client;
        this.movie = movie;
        this.giveDate = giveDate;
        this.returnDate = returnDate;
    }
    public RentRecord(Cursor cursor) {
        id = cursor.getInt(RentRecord.DB.ID.index);
        client = cursor.getString(RentRecord.DB.CLIENTID.index);
        movie = cursor.getString(RentRecord.DB.MOVIEID.index);
        giveDate = new Date(cursor.getLong(RentRecord.DB.GIVEDATE.index) * 1000L);
        returnDate = cursor.isNull(RentRecord.DB.RETURNDATE.index) ? null : new Date(cursor.getLong(RentRecord.DB.RETURNDATE.index) * 1000L);
    }
}
