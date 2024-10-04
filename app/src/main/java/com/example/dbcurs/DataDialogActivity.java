package com.example.dbcurs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kotlin.NotImplementedError;

@SuppressWarnings("unchecked")
public class DataDialogActivity extends AppCompatActivity {

    public enum IntentArgs {

        LAYOUT("layout"),
        ALTER("alterMode"),
        TABLE("tableIndex"),
        DATA("data"),
        SECONDARY_DATA("secondary"),
        DBCLIENTS("clients"),
        DBMOVIES("movies"),
        DBRENTS("rents"),
        DBSTUDIOS("studios")
        ;

        public final String key;

        IntentArgs(String key) { this.key = key; }
    }

    private boolean alterMode;
    private int tableIndex;
    private Serializable data;
    private Object[] secondaryData;

    private List<Client> clients;
    private List<Movie> movies;

    private Calendar calendar = Calendar.getInstance();
    private DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("ru", "RU"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getIntent().getExtras();

        setContentView(args.getInt(IntentArgs.LAYOUT.key));

        findViewById(R.id.ok).setOnClickListener(this::onOk);
        findViewById(R.id.cancel).setOnClickListener(this::onCancel);

        alterMode = args.getBoolean(IntentArgs.ALTER.key);
        tableIndex = args.getInt(IntentArgs.TABLE.key);

        clients = (List<Client>)args.getSerializable(IntentArgs.DBCLIENTS.key);
        movies = (List<Movie>)args.getSerializable(IntentArgs.DBMOVIES.key);

        if (tableIndex == DBTables.RENTS.pageIndex) {
            findViewById(R.id.dataRentGiveDate).setOnClickListener(this::giveDatePickerOnClick);
            findViewById(R.id.dataRentReturnDate).setOnClickListener(this::returnDatePickerOnClick);

            MainActivity.setArrayAdapter(findViewById(R.id.dataRentClientDropdown), clients.toArray(), this);
            MainActivity.setArrayAdapter(findViewById(R.id.dataRentMovieDropdown), movies.toArray(), this);
        }

        if (alterMode) {
            data = args.getSerializable(IntentArgs.DATA.key);
            secondaryData = (Object[])args.getSerializable(IntentArgs.SECONDARY_DATA.key);

            if (tableIndex == DBTables.CLIENTS.pageIndex) {

                Client client = (Client)data;
                ((TextView)findViewById(R.id.dataClientPassport)).setText(Long.toString(client.passport));
                ((TextView)findViewById(R.id.dataClientName)).setText(client.name);
                ((TextView)findViewById(R.id.dataClientAddress)).setText(client.address);
                ((TextView)findViewById(R.id.dataClientPhone)).setText(Long.toString(client.phone));

            }
            else if (tableIndex == DBTables.MOVIES.pageIndex) {

                Movie movie = (Movie)data;
                Studio studio = (Studio)secondaryData[0];

                ((TextView)findViewById(R.id.dataMovieIDLabel)).setText(Integer.toString(movie.id));
                ((TextView)findViewById(R.id.dataMovieTitle)).setText(movie.title);
                ((TextView)findViewById(R.id.dataMovieDirector)).setText(movie.director);
                ((TextView)findViewById(R.id.dataMovieGenre)).setText(movie.genre);
                ((TextView)findViewById(R.id.dataMovieStarring)).setText(movie.starring);
                ((TextView)findViewById(R.id.dataMovieReleaseYear)).setText(movie.releaseYear >= 0 ? Integer.toString(movie.releaseYear) : "");
                ((TextView)findViewById(R.id.dataMovieRent)).setText(Float.toString(movie.rentCost));
                ((TextView)findViewById(R.id.dataMovieStudioIDLabel)).setText(studio != null ? Integer.toString(studio.id) : "");
                ((TextView)findViewById(R.id.dataMovieStudioTitle)).setText(studio != null ? studio.title : "");
                ((TextView)findViewById(R.id.dataMovieStudioCountry)).setText(studio != null ? studio.country : "");
                ((TextView)findViewById(R.id.dataMovieAnnotation)).setText(movie.annotation);

            }
            else if (tableIndex == DBTables.RENTS.pageIndex) {

                RentRecord rent = (RentRecord)data;
                Client client = (Client)secondaryData[0];
                Movie movie = (Movie)secondaryData[1];

                ((Spinner)findViewById(R.id.dataRentClientDropdown)).setSelection(findIndexInList(clients, (c) -> c.passport == client.passport));
                ((Spinner)findViewById(R.id.dataRentMovieDropdown)).setSelection(findIndexInList(movies, (m) -> m.id == movie.id));
                ((TextView)findViewById(R.id.dataRentGiveDate)).setText(format.format(rent.giveDate));
                ((TextView)findViewById(R.id.dataRentReturnDate)).setText(rent.returnDate != null ? format.format(rent.returnDate) : "");

            }
        }
    }

    public void onCancel(View view) { setResult(RESULT_CANCELED); finish(); }
    public void onOk(View view) {

        Serializable data = null;
        Serializable secondary = null;
        int messageID;

        if (tableIndex == DBTables.CLIENTS.pageIndex) {

            CharSequence passportText = ((TextView)findViewById(R.id.dataClientPassport)).getText(),
                         nameText = ((TextView)findViewById(R.id.dataClientName)).getText(),
                         addressText = ((TextView)findViewById(R.id.dataClientAddress)).getText(),
                         phoneText = ((TextView)findViewById(R.id.dataClientPhone)).getText();

            if (passportText.length() > 0 &&
                nameText.length() > 0 &&
                addressText.length() > 0 &&
                phoneText.length() > 0) {

                if (alterMode || findIndexInList(clients, (c) -> c.passport == Long.parseLong(passportText.toString())) < 0) {
                    data = new Client
                            (
                                Long.parseLong(passportText.toString()),
                                nameText.toString(),
                                addressText.toString(),
                                Long.parseLong(phoneText.toString())
                            );
                    finishSucceed(data);
                    return;
                }
                else messageID = R.string.dataErrorDuplicateClient;
            }
            else messageID = R.string.dataErrorNotFilled;

        }
        else if (tableIndex == DBTables.MOVIES.pageIndex) {

            CharSequence titleText = ((TextView)findViewById(R.id.dataMovieTitle)).getText(),
                         directorText = ((TextView)findViewById(R.id.dataMovieDirector)).getText(),
                         genreText = ((TextView)findViewById(R.id.dataMovieGenre)).getText(),
                         starringText = ((TextView)findViewById(R.id.dataMovieStarring)).getText(),
                         releaseYearText = ((TextView)findViewById(R.id.dataMovieReleaseYear)).getText(),
                         annotationText = ((TextView)findViewById(R.id.dataMovieAnnotation)).getText(),
                         rentCostText = ((TextView)findViewById(R.id.dataMovieRent)).getText(),
                         studioTitleText = ((TextView)findViewById(R.id.dataMovieStudioTitle)).getText(),
                         studioCountryText = ((TextView)findViewById(R.id.dataMovieStudioCountry)).getText();

            if (titleText.length() > 0 &&
                rentCostText.length() > 0
            ) {
                if (alterMode || findIndexInList(movies,
                        (m) ->
                        {
                            int year = releaseYearText.length() > 0 ? Integer.parseInt(releaseYearText.toString()) : -1;
                            return m.title.equals(titleText.toString()) &&
                                   m.director.equals(directorText.toString()) &&
                                   m.releaseYear == year;
                         }
                     ) < 0) {

                    if (Float.parseFloat(rentCostText.toString()) > 0f) {
                        data = new Movie
                                (
                                    alterMode ? ((Movie)this.data).id : -1,
                                    titleText.toString(),
                                    directorText.toString(),
                                    genreText.toString(),
                                    starringText.toString(),
                                    releaseYearText.length() > 0 ? Integer.parseInt(releaseYearText.toString()) : -1,
                                    annotationText.toString(),
                                    Float.parseFloat(rentCostText.toString()),
                                    studioTitleText.toString()
                                );
                        secondary = studioTitleText.length() > 0 ? new Studio
                                (
                                    -1,
                                    studioTitleText.toString(),
                                    studioCountryText.toString()
                                ) : null;
                        finishSucceed(data, secondary);
                        return;
                    }
                    else messageID = R.string.dataErrorWrongCost;
                }
                else messageID = R.string.dataErrorDuplicateMovie;
            }
            else messageID = R.string.dataErrorNotFilled;

        }
        else if (tableIndex == DBTables.RENTS.pageIndex) {

            int          clientSelection = ((Spinner)findViewById(R.id.dataRentClientDropdown)).getSelectedItemPosition(),
                         movieSelection = ((Spinner)findViewById(R.id.dataRentMovieDropdown)).getSelectedItemPosition();
            CharSequence giveDateText = ((TextView)findViewById(R.id.dataRentGiveDate)).getText(),
                         returnDateText  = ((TextView)findViewById(R.id.dataRentReturnDate)).getText();

            if (giveDateText.length() > 0) {
                try {
                    Date giveDate = reformatDate(giveDateText.toString()),
                         returnDate = returnDateText.length() > 0 ? reformatDate(returnDateText.toString()) : null;

                    if (returnDate == null || returnDate.compareTo(giveDate) >= 0) {
                        data = new RentRecord
                                (
                                    alterMode ? ((RentRecord)this.data).id : -1,
                                    "",
                                    "",
                                    giveDate,
                                    returnDate
                                );
                        secondary = new long[] {
                                    clients.get(clientSelection).passport,
                                    movies.get(movieSelection).id
                                };
                        finishSucceed(data, secondary);
                        return;
                    }
                    else messageID = R.string.dataErrorWrongReturnDate;
                }
                catch (IllegalArgumentException e) { messageID = R.string.dataErrorWrongDateFormat; }
            }
            else messageID = R.string.dataErrorNotFilled;

        }
        else throw new NotImplementedError();

        failMessage(messageID);
    }
    private void finishSucceed(Serializable data) { finishSucceed(data, null); }
    private void finishSucceed(Serializable data, Serializable secondary) {
        Intent ret = new Intent();
        ret.putExtra(IntentArgs.TABLE.key, tableIndex);
        ret.putExtra(IntentArgs.ALTER.key, alterMode);
        ret.putExtra(IntentArgs.DATA.key, data);
        ret.putExtra(IntentArgs.SECONDARY_DATA.key, secondary);
        setResult(RESULT_OK, ret);
        finish();
    }
    private void failMessage(int messageId) { (Toast.makeText(this, messageId, Toast.LENGTH_SHORT)).show(); }

    public void giveDatePickerOnClick(View view) { new DatePickerDialog(this, giveDateSetListener, calendar.get(Calendar.YEAR), Calendar.MONTH, Calendar.DAY_OF_MONTH).show(); }
    public void returnDatePickerOnClick(View view) { new DatePickerDialog(this, returnDateSetListener, calendar.get(Calendar.YEAR), Calendar.MONTH, Calendar.DAY_OF_MONTH).show(); }

    private DatePickerDialog.OnDateSetListener giveDateSetListener = (view, year, month, dayOfMonth) -> setDateText(findViewById(R.id.dataRentGiveDate), year, month, dayOfMonth);
    private DatePickerDialog.OnDateSetListener returnDateSetListener = (view, year, month, dayOfMonth) -> setDateText(findViewById(R.id.dataRentReturnDate), year, month, dayOfMonth);
    @SuppressLint("SetTextI18n")
    private void setDateText(TextView view, int year, int month, int day) { view.setText(day + "." + month + "." + year); }

    private static Date reformatDate(@NonNull String date) {
        try {
            String ret;
            String[] t;

            t = date.toString().split("\\.");
            ret = t[2] + "-" + t[1] + "-" + t[0];
            return Date.valueOf(ret);
        }
        catch (IndexOutOfBoundsException e) { throw new IllegalArgumentException(); }
    }

    public static <T> int findIndexInList(List<T> list, Predicate<T> match) {

        if (list != null) {
            T item;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                if (match.match(item)) return i;
            }
        }
        return -1;
    }
}