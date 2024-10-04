package com.example.dbcurs;

import android.content.Context;
import android.database.Cursor;
import android.util.Pair;

import java.io.Serializable;

public class Client implements Serializable {

    public enum DB {

        PASSPORT("PassportID", 0, R.string.dbColumnClientPassport),
        NAME("FullName", 1, R.string.dbColumnClientName),
        ADDRESS("Address", 2, R.string.dbColumnClientAddress),
        PHONE("Phone", 3, R.string.dbColumnClientPhone);

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

    public static final String[] getDropdownColumns(Context context)
    {
        return new String[] {
                context.getString(DB.PASSPORT.stringResource),
                context.getString(DB.NAME.stringResource),
                context.getString(DB.ADDRESS.stringResource),
                context.getString(DB.PHONE.stringResource),
        };
    }

    public final long passport;
    public final long phone;
    public final String name;
    public final String address;

    public Client(long passport, String name, String address, long phone) {
        this.passport = passport;
        this.phone = phone;
        this.name = name;
        this.address = address;
    }
    public Client(Cursor cursor) {
        passport = cursor.getLong(Client.DB.PASSPORT.index);
        name = cursor.getString(Client.DB.NAME.index);
        address = cursor.getString(Client.DB.ADDRESS.index);
        phone = cursor.getLong(Client.DB.PHONE.index);
    }

    @Override
    public String toString() { return name + " (" + phone + ")"; }
}
