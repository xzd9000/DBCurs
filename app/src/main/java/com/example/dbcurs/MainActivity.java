package com.example.dbcurs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;


import com.example.dbcurs.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity  {

    private ActivityMainBinding binding;
    private DBController dbController;
    private MainState state;
    private int selectedPage;
    private PageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String dbScript = "";
        String dataScript = "";
        //String dropScript = "";

        try
        {
            dbScript = readRawResource(R.raw.dbscript);
            dataScript = readRawResource(R.raw.datascript);         //add any data into this script
            //dropScript = readRawResource(R.raw.dropscript);
        }
        catch (Exception e) { finish(); }

        dbController = new DBController(getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null));

        //dbController.execScript(dropScript);
        dbController.execScript(dbScript);
        dbController.execScript(dataScript);

        for (int i = 0; i < 3; i++)
        {
            dbController.selectTable(i);
            dbController.fillTableFromQuery(i);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        resetPagerAdapter();
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) ->
        {
            int text = 0;

            if (position == DBTables.CLIENTS.pageIndex) text = DBTables.CLIENTS.stringResource;
            else if (position == DBTables.MOVIES.pageIndex) text = DBTables.MOVIES.stringResource;
            else if (position == DBTables.RENTS.pageIndex) text = DBTables.RENTS.stringResource;

            tab.setText(text);
        });
        tabLayoutMediator.attach();

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == DBTables.RENTS.pageIndex) {
                    binding.specialButton.setClickable(false);
                    binding.specialButton.setText("");
                }
                else {
                    binding.specialButton.setClickable(true);
                    if (position == DBTables.CLIENTS.pageIndex) binding.specialButton.setText(R.string.tenDaysText);
                    else if (position == DBTables.MOVIES.pageIndex) binding.specialButton.setText(R.string.givenText);
                }

                selectedPage = position;

                resetDropdownColumns();
            }
        });

        binding.viewPager.setCurrentItem(0);
        state = null;
        enterState(MainState.DEFAULT, selectedPage);
    }

    private void resetPagerAdapter() { resetPagerAdapter(-1); }
    private void resetPagerAdapter(int setToPage) {
        pageAdapter = new PageAdapter(this, dbController.clients, dbController.movies, dbController.rentRecords);
        binding.viewPager.setAdapter(pageAdapter);
        if (setToPage >= 0) binding.viewPager.setCurrentItem(setToPage);
    }
    private void resetDropdownColumns() {

        String[] columns;
        if (selectedPage == DBTables.CLIENTS.pageIndex) columns = Client.getDropdownColumns(this);
        else if (selectedPage == DBTables.MOVIES.pageIndex) columns = Movie.getDropdownColumns(this);
        else if (selectedPage == DBTables.RENTS.pageIndex) columns = RentRecord.getDropdownColumns(this);
        else columns = new String[0];

        setStringArrayAdapter(binding.columnDropdown, columns);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dbController.close();
    }

    public void applyOnClick(View view) {

        if (state == MainState.FILTER || state == MainState.GROUP) {

            int index = binding.columnDropdown.getSelectedItemPosition();

            if (index != Spinner.INVALID_POSITION) {

                if (selectedPage == DBTables.RENTS.pageIndex || selectedPage == DBTables.MOVIES.pageIndex) index++;

                if (state == MainState.FILTER) {

                    String filter = binding.filterTextField.getText().toString();
                    dbController.filterTable(selectedPage, index, filter);
                    dbController.fillTableFromQuery(selectedPage);
                    resetPagerAdapter(selectedPage);

                } else if (state == MainState.GROUP) {
                    dbController.groupTable(selectedPage, index);
                    dbController.fillTableFromQuery(selectedPage);
                    resetPagerAdapter(selectedPage);
                }
            }
        }
    }
    public void resetOnClick(View view) {
        if (state == MainState.SPECIAL) enterState(MainState.DEFAULT, selectedPage);
        else {
            dbController.resetTable(selectedPage);
            resetPagerAdapter(selectedPage);
        }
    }
    public void filterOnClick(View view) { enterStateOrDefault(state, MainState.FILTER, selectedPage); }
    public void groupOnClick(View view) { enterStateOrDefault(state, MainState.GROUP, selectedPage); }
    public void specialOnClick(View view) {
        enterStateOrDefault(state, MainState.SPECIAL, selectedPage);
        dbController.specialQuery(selectedPage);
        if (selectedPage == DBTables.MOVIES.pageIndex || selectedPage == DBTables.CLIENTS.pageIndex) {
            dbController.fillTableFromQuery(selectedPage);
            resetPagerAdapter(selectedPage);
        }
    }
    public void optionsOnClick(View view) {
        PopupMenu menu = new PopupMenu(this, view);

        menu.setOnMenuItemClickListener(item -> {

            int layout;
            Serializable data = pageAdapter.getSelection(selectedPage);

            if (selectedPage == DBTables.CLIENTS.pageIndex) layout = R.layout.dialog_data_client;
            else if (selectedPage == DBTables.MOVIES.pageIndex) layout = R.layout.dialog_data_movie;
            else if (selectedPage == DBTables.RENTS.pageIndex) layout = R.layout.dialog_data_rent;
            else layout = 0;

            if (item.getItemId() == R.id.menuDelete || item.getItemId() == R.id.menuAlter) {
                if (data == null) {
                    (Toast.makeText(this, R.string.itemNotSelected, Toast.LENGTH_SHORT)).show();
                    return true;
                }
            }

            if (item.getItemId() != R.id.menuDelete) {
                Intent intent = new Intent(this, DataDialogActivity.class);

                intent.putExtra(DataDialogActivity.IntentArgs.LAYOUT.key, layout);
                intent.putExtra(DataDialogActivity.IntentArgs.TABLE.key, selectedPage);
                intent.putExtra(DataDialogActivity.IntentArgs.ALTER.key, item.getItemId() == R.id.menuAlter);

                if (item.getItemId() == R.id.menuAlter) {
                    intent.putExtra(DataDialogActivity.IntentArgs.DATA.key, data);

                    Serializable secondary;

                    if (selectedPage == DBTables.MOVIES.pageIndex) secondary = new Serializable[] { dbController.selectStudioFromMovie((Movie)data) };
                    else if (selectedPage == DBTables.RENTS.pageIndex) secondary = new Serializable[] {
                            dbController.selectClientFromRent((RentRecord)data),
                            dbController.selectMovieFromRent((RentRecord)data)
                    };
                    else secondary = new Serializable[0];

                    intent.putExtra(DataDialogActivity.IntentArgs.SECONDARY_DATA.key, secondary);
                }

                dbController.resetAllTables();

                intent.putExtra(DataDialogActivity.IntentArgs.DBCLIENTS.key, dbController.clients);
                intent.putExtra(DataDialogActivity.IntentArgs.DBMOVIES.key, dbController.movies);

                startForResult.launch(intent);
            }
            else {
                if (selectedPage != DBTables.RENTS.pageIndex) {
                    Movie movie = null;
                    Client client = null;
                    if (selectedPage == DBTables.MOVIES.pageIndex) movie = (Movie)data;
                    else if (selectedPage == DBTables.CLIENTS.pageIndex) client = (Client)data;

                    if (dbController.rentWithAnyItemExists(movie, client)) {
                        (Toast.makeText(this, R.string.itemConnected, Toast.LENGTH_SHORT)).show();
                        return true;
                    }
                }

                long id = -1;
                if (selectedPage == DBTables.CLIENTS.pageIndex) id = ((Client)data).passport;
                else if (selectedPage == DBTables.MOVIES.pageIndex) id = ((Movie)data).id;
                else if (selectedPage == DBTables.RENTS.pageIndex) id = ((RentRecord)data).id;

                if (id >= 0) {
                    dbController.deleteItem(selectedPage, id);
                    tableFullUpdate(selectedPage);
                }
            }

            return true;
        });


        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.menu, menu.getMenu());
        menu.show();
    }

    private ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            insertData(
                    data.getIntExtra(DataDialogActivity.IntentArgs.TABLE.key, -1),
                    data.getSerializableExtra(DataDialogActivity.IntentArgs.DATA.key),
                    data.getSerializableExtra(DataDialogActivity.IntentArgs.SECONDARY_DATA.key),
                    data.getBooleanExtra(DataDialogActivity.IntentArgs.ALTER.key, false)
            );
        }
    });

    public void insertData(int table, Serializable data, Serializable secondary, boolean alter) {
        if (table == DBTables.CLIENTS.pageIndex) dbController.insertOrUpdateClient((Client)data, alter);
        else if (table == DBTables.MOVIES.pageIndex) {
            Studio studio = (Studio)secondary;
            int studioID = -1;
            if (studio != null) {
                dbController.insertStudioIfNotExists(studio);
                dbController.selectStudioID(studio);
                studioID = dbController.getCursor().getInt(Studio.DB.ID.index);
            }
            dbController.insertOrUpdateMovie((Movie)data, studioID, alter);
        }
        else if (table == DBTables.RENTS.pageIndex) dbController.insertOrUpdateRent(((long[])secondary)[0], (int)((long[])secondary)[1], (RentRecord)data, alter);

        tableFullUpdate(table);
    }

    private void tableFullUpdate(int table) {
        dbController.resetTable(table);
        dbController.fillTableFromQuery(table);
        resetPagerAdapter(table);
    }

    private void enterStateOrDefault(MainState state, MainState targetState, int selectedPage) {
        if (state != targetState) enterState(targetState, selectedPage);
        else enterState(MainState.DEFAULT, selectedPage);
    }

    private void enterState(@NonNull MainState state, int selectedPage) {

        exitState(this.state, selectedPage);

        if (state == MainState.SPECIAL || state == MainState.DEFAULT) {

            binding.filterTextField.setVisibility(View.GONE);
            binding.columnDropdown.setVisibility(View.GONE);
            binding.applyButton.setVisibility(View.GONE);
            binding.resetButton.setVisibility(state == MainState.DEFAULT ? View.GONE : View.VISIBLE);
        }
        else {
            if (state == MainState.FILTER) binding.filterTextField.setVisibility(View.VISIBLE);
            else if (state == MainState.GROUP) binding.filterTextField.setVisibility(View.GONE);
            binding.columnDropdown.setVisibility(View.VISIBLE);
            binding.applyButton.setVisibility(View.VISIBLE);
            binding.resetButton.setVisibility(View.VISIBLE);
        }

        this.state = state;
    }
    private void exitState(MainState state, int selectedPage) {

        if (state != null)
        {
            if (state == MainState.GROUP || state == MainState.FILTER || state == MainState.SPECIAL) {
                dbController.resetTable(selectedPage);
                resetPagerAdapter(selectedPage);
            }
        }
    }

    private void setStringArrayAdapter(AdapterView view, String[] array) { setArrayAdapter(view, array, this); }
    public static <T> void setArrayAdapter(AdapterView view, T[] array, Context context) {
        ArrayAdapter<T> adapter = new ArrayAdapter<T>(context, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        view.setAdapter(adapter);
    }

    private String readRawResource(int id) throws IOException {
        InputStream in = getResources().openRawResource(id);
        byte[] bytes = new byte[in.available()];
        in.read(bytes);
        String ret = new String (bytes);
        in.close();
        return ret;
    }
}

//SELECT DISTINCT ClientID FROM MovieRents WHERE (julianday('now'))-julianday(GiveDate) > 10;

//SELECT Clients.* FROM (SELECT DISTINCT ClientID AS id FROM MovieRents WHERE (julianday('now'))-julianday(GiveDate) > 10), Clients WHERE id = Clients.PassportID;

//SELECT DISTINCT MovieID FROM MovieRents WHERE ReturnDate ISNULL;

//SELECT Movies.* FROM (SELECT DISTINCT MovieID FROM MovieRents WHERE ReturnDate ISNULL), Movies WHERE MovieID = Movies.ID;

//SELECT * FROM Studios WHERE Studios.ID = (SELECT StudioID FROM Movies WHERE ID = 9)

//INSERT INTO Studios(Title, Country) SELECT 'Legendary Pictures', 'США' WHERE NOT EXISTS (SELECT * FROM Studios Where Studios.Title = 'Legendary Pictures' AND Studios.Country = 'США');