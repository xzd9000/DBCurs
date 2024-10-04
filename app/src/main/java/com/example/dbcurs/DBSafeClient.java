package com.example.dbcurs;

public class DBSafeClient extends DBSafeRecord {

    public final String passport;
    public final String name;
    public final String address;
    public final String phone;

    DBSafeClient(Client client) {
        passport =                   Long.toString(client.passport);
        name =    "'" + escapeString(client.name)                       + "'";
        address = "'" + escapeString(client.address)                    + "'";
        phone =                      Long.toString(client.phone);
    }
}
