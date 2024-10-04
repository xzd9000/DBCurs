package com.example.dbcurs;

public class DBSafeRentRecord extends DBSafeRecord {

    public final String id;
    public final String clientID;
    public final String movieID;
    public final String giveDate;
    public final String returnDate;

    DBSafeRentRecord(long clientID, int movieID, RentRecord rent) {
             id =                                          Integer.toString(rent.id);
        this.clientID =                                    Long.toString(clientID);
        this.movieID =                                     Integer.toString(movieID);
             giveDate =                              "'" + rent.giveDate              + "'";
             returnDate = rent.returnDate != null ?  "'" + rent.returnDate            + "'" : "";
    }
}
