package com.example.dbcurs;

public class DBSafeMovie extends DBSafeRecord {

    public final String id;
    public final String title;
    public final String director;
    public final String genre;
    public final String starring;
    public final String releaseYear;
    public final String annotation;
    public final String rentCost;
    public final String studioID;

    DBSafeMovie(Movie movie, int studioID) {
             id =                                                             Integer.toString(movie.id);
             title =                                       "'" + escapeString(movie.title)                         + "'";
             director =    movie.director.length() > 0 ?   "'" + escapeString(movie.director)                      + "'" : "NULL";
             genre =       movie.genre.length() > 0 ?      "'" + escapeString(movie.genre)                         + "'" : "NULL";
             starring =    movie.starring.length() > 0 ?   "'" + escapeString(movie.starring)                      + "'" : "NULL";
             releaseYear = movie.releaseYear > 0 ?                            Integer.toString(movie.releaseYear)        : "NULL";
             annotation =  movie.annotation.length() > 0 ? "'" + escapeString(movie.annotation)                    + "'" : "NULL";
             rentCost =                                                       Float.toString(movie.rentCost);
        this.studioID =    studioID > 0 ?                                     Integer.toString(studioID)                 : "NULL";
    }
}
