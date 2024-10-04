package com.example.dbcurs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.io.Closeable;
import java.util.Date;

public class DBController implements Closeable {

    public static final String DB_QUERYPART_SELECTMOVIES =
            "SELECT " +
                    Movie.DB.ID.column + " AS \"0\", " +
                    Movie.DB.TITLE.column + " AS \"1\", " +
                    Movie.DB.DIRECTOR.column + " AS \"2\", " +
                    Movie.DB.GENRE.column + " AS \"3\", " +
                    Movie.DB.STARRING.column + " AS \"4\", " +
                    Movie.DB.RELEASEYEAR.column + " AS \"5\", " +
                    Movie.DB.ANNOTATION.column + " AS \"6\", " +
                    Movie.DB.RENTCOST.column + " AS \"7\", " +
                    "(SELECT " + Studio.DB.TITLE.column + " FROM " + DBTables.STUDIOS.table + " WHERE " + DBTables.STUDIOS.table + "." + Studio.DB.ID.column + " = " + Movie.DB.STUDIOID.column +
                    ") AS \"8\"";
    public static final String DB_BASEQUERY_SELECTMOVIES = DB_QUERYPART_SELECTMOVIES + " FROM " + DBTables.MOVIES.table;

    public static final String DB_QUERYPART_SELECTCLIENTS = "SELECT " +
            Client.DB.PASSPORT.column + " AS \"0\", " +
            Client.DB.NAME.column + " AS \"1\", " +
            Client.DB.ADDRESS.column + " AS \"2\", " +
            Client.DB.PHONE.column + " AS \"3\"";
    public static final String DB_BASEQUERY_SELECTCLIENTS = DB_QUERYPART_SELECTCLIENTS + " FROM " + DBTables.CLIENTS;

    public static final String DB_QUERYPART_SELECTRENTS = "SELECT " +
            RentRecord.DB.ID.column + " AS \"0\", " +
            "(SELECT " + Client.DB.NAME.column + " FROM " + DBTables.CLIENTS.table + " WHERE " + Client.DB.PASSPORT.column + " = " + RentRecord.DB.CLIENTID.column + ")  AS \"1\", " +
            "(SELECT " + Movie.DB.TITLE.column + " FROM " + DBTables.MOVIES.table + " WHERE " + DBTables.MOVIES.table + "." + Movie.DB.ID.column + " = " + RentRecord.DB.MOVIEID.column + ")  AS \"2\", " +
            "strftime('%s', " + RentRecord.DB.GIVEDATE.column + ") AS \"3\", strftime('%s', " + RentRecord.DB.RETURNDATE.column + ") AS \"4\"";
    public static final String DB_BASEQUERY_SELECTRENTS = DB_QUERYPART_SELECTRENTS + " FROM " + DBTables.RENTS.table + "";

    public static final String DB_BASEQUERY_SELECTSTUDIOS = "SELECT * FROM " + DBTables.STUDIOS.table;


    public static String getBaseQuery(int table)
    {
        String baseQuery;
        if (table == DBTables.CLIENTS.pageIndex) baseQuery = DB_BASEQUERY_SELECTCLIENTS;
        else if (table == DBTables.MOVIES.pageIndex) baseQuery = DB_BASEQUERY_SELECTMOVIES;
        else if (table == DBTables.RENTS.pageIndex) baseQuery = DB_BASEQUERY_SELECTRENTS;
        else if (table == DBTables.STUDIOS.pageIndex) baseQuery = DB_BASEQUERY_SELECTSTUDIOS;
        else baseQuery = "";
        return baseQuery;
    }

    private final SQLiteDatabase db;
    private Cursor cursor = null;
    private String query = "";
    private String[][] columns = new String[][]{ new String[0], new String[0], new String[0], new String[0] };

    public ArrayList<Client> clients = new ArrayList<>();
    public ArrayList<Movie> movies = new ArrayList<>();
    public ArrayList<Studio> studios = new ArrayList<>();
    public ArrayList<RentRecord> rentRecords = new ArrayList<>();

    public DBController(SQLiteDatabase db) { this.db = db; }

    public void customQuery(String sql) {

        if (sql != query) {
            query = sql;
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
        }
    }

    public void insertClient(Client client) {
        DBSafeClient insert = new DBSafeClient(client);
        customQuery("INSERT INTO " + DBTables.CLIENTS.table + " ("
                + Client.DB.PASSPORT.column + ", "
                + Client.DB.NAME.column + ", "
                + Client.DB.ADDRESS.column + ", "
                + Client.DB.PHONE.column
        + ") VALUES (" + insert.passport + ", " + insert.name + ", " + insert.address + ", " + insert.phone + ")");
    }
    public void insertMovie(Movie movie, int studioID) {
        DBSafeMovie insert = new DBSafeMovie(movie, studioID);
        customQuery("INSERT INTO " + DBTables.MOVIES.table + " ("
                + Movie.DB.TITLE.column + ", "
                + Movie.DB.DIRECTOR.column + ", "
                + Movie.DB.GENRE.column + ", "
                + Movie.DB.STARRING.column + ", "
                + Movie.DB.RELEASEYEAR.column + ", "
                + Movie.DB.ANNOTATION.column + ", "
                + Movie.DB.RENTCOST.column + ", "
                + Movie.DB.STUDIOID.column
                + ") VALUES " +
                "(" + insert.title       + ", " + insert.director   + ", " + insert.genre    + ", " + insert.starring + ", "
                    + insert.releaseYear + ", " + insert.annotation + ", " + insert.rentCost + ", " + insert.studioID + ")");
    }
    public void insertRent(long clientID, int movieID, RentRecord rent) {
        DBSafeRentRecord insert = new DBSafeRentRecord(clientID, movieID, rent);
        customQuery("INSERT INTO " + DBTables.RENTS.table + " ("
                + RentRecord.DB.CLIENTID.column + ", "
                + RentRecord.DB.MOVIEID.column + ", "
                + RentRecord.DB.GIVEDATE.column + ", "
                + RentRecord.DB.RETURNDATE.column
                + ") VALUES (" + insert.clientID + ", " + insert.movieID + ", " + insert.giveDate + ", " + insert.returnDate + ")");
    }
    public void insertStudioIfNotExists(Studio studio) {
        DBSafeStudio insert = new DBSafeStudio(studio);
        customQuery("INSERT INTO " + DBTables.STUDIOS.table +"(" +
                Studio.DB.TITLE.column + ", " + Studio.DB.COUNTRY.column + ") SELECT " +
                insert.title + ", " + insert.country + " WHERE NOT EXISTS (SELECT * FROM " + DBTables.STUDIOS.table +" WHERE " +
                DBTables.STUDIOS.table + "." + Studio.DB.TITLE.column   + " = " + insert.title + " AND " +
                DBTables.STUDIOS.table + "." + Studio.DB.COUNTRY.column + " = " + insert.country + ")");
    }

    public void updateClient(Client client) {
        DBSafeClient update = new DBSafeClient(client);
        customQuery("UPDATE " + DBTables.CLIENTS.table + " SET " +
                Client.DB.PASSPORT.column + " = " + update.passport + ", " +
                Client.DB.NAME.column + " = " + update.name + ", " +
                Client.DB.ADDRESS.column + " = " + update.address + ", " +
                Client.DB.PHONE.column + " = " + update.phone +
                " WHERE " + Client.DB.PASSPORT.column + " = " + update.passport);

    }
    public void updateMovie(Movie movie, int studioID) {
        DBSafeMovie update = new DBSafeMovie(movie, studioID);
        customQuery("UPDATE " + DBTables.MOVIES.table + " SET "
                + Movie.DB.TITLE.column + " = " + update.title + ", "
                + Movie.DB.DIRECTOR.column + " = " + update.director + ", "
                + Movie.DB.GENRE.column + " = " + update.genre + ", "
                + Movie.DB.STARRING.column + " = " + update.starring + ", "
                + Movie.DB.RELEASEYEAR.column + " = " + update.releaseYear + ", "
                + Movie.DB.ANNOTATION.column + " = " + update.annotation + ", "
                + Movie.DB.RENTCOST.column + " = " + update.rentCost + ", "
                + Movie.DB.STUDIOID.column + " = " + studioID +
                " WHERE " + Movie.DB.ID.column + " = " + update.id);
    }
    public void updateRent(long clientID, int movieID, RentRecord rent) {
        DBSafeRentRecord update = new DBSafeRentRecord(clientID, movieID, rent);
        customQuery("UPDATE " + DBTables.RENTS.table + " SET "
                + RentRecord.DB.CLIENTID.column + " = " + update.clientID + ", "
                + RentRecord.DB.MOVIEID.column + " = " + update.movieID + ", "
                + RentRecord.DB.GIVEDATE.column + " = " + update.giveDate + ", "
                + RentRecord.DB.RETURNDATE.column + " = " + update.returnDate +
                " WHERE " + RentRecord.DB.ID.column + " = " + update.id);
    }

    public void insertOrUpdateClient(Client client, boolean update) {
        if (update) updateClient(client);
        else insertClient(client);
    }
    public void insertOrUpdateMovie(Movie movie, int studioID, boolean update) {
        if (update) updateMovie(movie, studioID);
        else insertMovie(movie, studioID);
    }
    public void insertOrUpdateRent(long clientID, int movieID, RentRecord rent, boolean update) {
        if (update) updateRent(clientID, movieID, rent);
        else insertRent(clientID, movieID, rent);
    }

    public void deleteItem(int table, long id) {
        String table_ = "", column = "";
        if (table == DBTables.CLIENTS.pageIndex) {
            table_ = DBTables.CLIENTS.table;
            column = Client.DB.PASSPORT.column;
        }
        else if (table == DBTables.MOVIES.pageIndex) {
            table_ = DBTables.MOVIES.table;
            column = Movie.DB.ID.column;
        }
        else if (table == DBTables.RENTS.pageIndex) {
            table_ = DBTables.RENTS.table;
            column = RentRecord.DB.ID.column;
        }
        if (table_.length() > 0)  customQuery("DELETE FROM " + table_ + " WHERE " + column + " = " + id);
    }

    public Studio selectStudioFromMovie(@NonNull Movie movie) {
        customQuery("SELECT * " +  selectRowFromValueQueryPart(DBTables.STUDIOS.table, Studio.DB.ID.column, Movie.DB.STUDIOID.column, DBTables.MOVIES.table, Movie.DB.ID.column, movie.id));
        if (cursor.moveToFirst()) return new Studio(cursor);
        else return null;
    }
    public Client selectClientFromRent(@NonNull RentRecord rent) {
        customQuery(DB_QUERYPART_SELECTCLIENTS + " " + selectRowFromValueQueryPart(DBTables.CLIENTS.table, Client.DB.PASSPORT.column, RentRecord.DB.CLIENTID.column, DBTables.RENTS.table, RentRecord.DB.ID.column, rent.id));
        if (cursor.moveToFirst()) return new Client(cursor);
        else return null;
    }
    public Movie selectMovieFromRent(@NonNull RentRecord rent) {
        customQuery(DB_QUERYPART_SELECTMOVIES + " " + selectRowFromValueQueryPart(DBTables.MOVIES.table, Movie.DB.ID.column, RentRecord.DB.MOVIEID.column, DBTables.RENTS.table, RentRecord.DB.ID.column, rent.id));
        if (cursor.moveToFirst()) return new Movie(cursor);
        else return null;
    }
    private String selectRowFromValueQueryPart(String targetTable, String targetIDColumn, String foreignKey, String sourceTable, String sourceIDColumn, int sourceIDValue)
    { return "FROM " + targetTable + " WHERE " + targetTable + "." + targetIDColumn + " = (SELECT " + foreignKey + " FROM " + sourceTable + " WHERE " + sourceIDColumn + " = " + sourceIDValue + ")"; }

    public int selectStudioID(Studio studio) {
        customQuery("SELECT " + Studio.DB.ID.column + " FROM " + DBTables.STUDIOS.table + " WHERE " +
        Studio.DB.TITLE + " = '" + studio.title + "' AND " + Studio.DB.COUNTRY + " = '" + studio.country + "'");
        return -1;
    }

    public boolean rentWithAnyItemExists(Movie movie, Client client) {
        customQuery("SELECT * FROM " + DBTables.RENTS.table + " WHERE " +
                RentRecord.DB.MOVIEID.column + " = " + (movie != null ? Integer.toString(movie.id) : "NULL") + " OR " +
                RentRecord.DB.CLIENTID.column + " = " + (client != null ? Long.toString(client.passport) : "NULL"));
        return cursor.moveToFirst();
    }

    public void selectTable(int table) { customQuery(getBaseQuery(table)); }

    public void fillTableFromQuery(int table) {

        if (table == DBTables.CLIENTS.pageIndex) fillListFromQuery(cursor, clients, clients -> clients.add(new Client(cursor)));
        else if (table == DBTables.MOVIES.pageIndex) fillListFromQuery(cursor, movies, movies -> movies.add(new Movie(cursor)));
        else if (table == DBTables.RENTS.pageIndex) fillListFromQuery(cursor, rentRecords, rentRecords -> rentRecords.add(new RentRecord(cursor)));
    }
    private <T> void fillListFromQuery(Cursor cursor, ArrayList<T> list, Action<ArrayList<T>> action) {
        list.clear();
        list.ensureCapacity(cursor.getCount());
        if (cursor.moveToFirst())
        {
            do action.commit(list);
            while (cursor.moveToNext());
        }
    }

    public void resetTable(int table) {
        selectTable(table);
        fillTableFromQuery(table);
    }
    public void resetAllTables() { for (int i = 0; i < 3; i++) resetTable(i); }

    public void filterTable(int table, int column, String filter) {
        String baseQuery = getBaseQuery(table);

        baseQuery += " WHERE \"" + column + "\" = '" + filter + "'";

        customQuery(baseQuery);
    }
    public void groupTable(int table, int column) {
        String baseQuery = getBaseQuery(table);

        baseQuery += " GROUP BY \"" + column + "\"";

        customQuery(baseQuery);
    }

    public void specialQuery(int table) {

        //SELECT Movies.* FROM (SELECT DISTINCT MovieID FROM MovieRents WHERE ReturnDate ISNULL), Movies WHERE MovieID = Movies.ID;
        if (table == DBTables.MOVIES.pageIndex) customQuery(
                "SELECT " + DBTables.MOVIES.table + ".* FROM (SELECT DISTINCT " + RentRecord.DB.MOVIEID.column + " FROM " + DBTables.RENTS.table +
                    " WHERE " + RentRecord.DB.RETURNDATE.column + " ISNULL), " + DBTables.MOVIES.table +
                    " WHERE " + RentRecord.DB.MOVIEID.column + " = " + DBTables.MOVIES + "." + Movie.DB.ID.column + ";");

        //SELECT Clients.* FROM (SELECT DISTINCT ClientID AS id FROM MovieRents WHERE (julianday('now'))-julianday(GiveDate) > 10), Clients WHERE id = Clients.PassportID;
        else if (table == DBTables.CLIENTS.pageIndex) customQuery(
                "SELECT " + DBTables.CLIENTS.table + ".* FROM (SELECT DISTINCT " + RentRecord.DB.CLIENTID.column + " AS id FROM " + DBTables.RENTS.table +
                    " WHERE (julianday('now'))-julianday(" + RentRecord.DB.GIVEDATE.column + ") > 10), "+ DBTables.CLIENTS.table +
                    " WHERE id = " + DBTables.CLIENTS.table + "." + Client.DB.PASSPORT.column + ";");
    }

    public void execScript(String script) {
        String[] statements = script.split(";");
        for (int i = 0; i < statements.length; i++) db.execSQL(statements[i]);
    }

    public Cursor getCursor() { return cursor; }

    @Override
    public void close() {
        if (cursor != null) cursor.close();
        db.close();
    }
}
