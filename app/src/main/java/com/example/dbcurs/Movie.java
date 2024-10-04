package com.example.dbcurs;

import android.content.Context;
import android.database.Cursor;

import java.io.Serializable;

public class Movie implements Serializable {

    public enum DB
    {
        ID("ID", 0, 0),
        TITLE("Title", 1, R.string.dbColumnMovieTitle),
        DIRECTOR("Director", 2, R.string.dbColumnMovieDirector),
        GENRE("Genre", 3, R.string.dbColumnMovieGenre),
        STARRING("Starring", 4, R.string.dbColumnMovieStarring),
        RELEASEYEAR("ReleaseYear", 5, R.string.dbColumnMovieReleaseYear),
        ANNOTATION("Annotation", 6, R.string.dbColumnMovieAnnotation),
        RENTCOST("RentCost", 7, R.string.dbColumnMovieRentCost),
        STUDIOID("StudioID", 8, R.string.dbColumnMovieStudio);

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
                context.getString(DB.TITLE.stringResource),
                context.getString(DB.DIRECTOR.stringResource),
                context.getString(DB.GENRE.stringResource),
                context.getString(DB.STARRING.stringResource),
                context.getString(DB.RELEASEYEAR.stringResource),
                context.getString(DB.ANNOTATION.stringResource),
                context.getString(DB.RENTCOST.stringResource),
                context.getString(DB.STUDIOID.stringResource),
        };
    }

    public final int id;
    public final String title;
    public final String director;
    public final String genre;
    public final String starring;
    public final int releaseYear;
    public final String annotation;
    public final float rentCost;
    public final String studio;

    public Movie(int id, String title, String director, String genre, String starring, int releaseYear, String annotation, float rentCost, String studio) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.starring = starring;
        this.releaseYear = releaseYear;
        this.annotation = annotation;
        this.rentCost = rentCost;
        this.studio = studio;
    }
    public Movie(Cursor cursor) {
        id = cursor.getInt(Movie.DB.ID.index);
        title = cursor.getString(Movie.DB.TITLE.index);
        director = cursor.isNull(Movie.DB.DIRECTOR.index) ? "" : cursor.getString(Movie.DB.DIRECTOR.index);
        genre = cursor.isNull(Movie.DB.GENRE.index) ? "" : cursor.getString(Movie.DB.GENRE.index);
        starring = cursor.isNull(Movie.DB.STARRING.index) ? "" : cursor.getString(Movie.DB.STARRING.index);
        releaseYear = cursor.isNull(DB.RELEASEYEAR.index) ? -1 : cursor.getInt(Movie.DB.RELEASEYEAR.index);
        annotation = cursor.isNull(Movie.DB.ANNOTATION.index) ? "" : cursor.getString(Movie.DB.ANNOTATION.index);
        rentCost = cursor.getFloat(Movie.DB.RENTCOST.index);
        studio = cursor.isNull(Movie.DB.STUDIOID.index) ? "" : cursor.getString(Movie.DB.STUDIOID.index);
    }

    @Override
    public String toString() { return title + "(" + releaseYear + ", " + director + ")"; }
}
