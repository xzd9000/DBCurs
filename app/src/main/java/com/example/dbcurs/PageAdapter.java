package com.example.dbcurs;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.io.Serializable;
import java.util.List;

public class PageAdapter extends FragmentStateAdapter {

    private FragmentActivity activity;
    private List clientsList;
    private List moviesList;
    private List rentsList;
    private Client selectedClient;

    private Movie selectedMovie;
    private RentRecord selectedRent;

    public PageAdapter(FragmentActivity activity, List clientsList, List moviesList, List rentsList) {
        super(activity);
        this.activity = activity;
        this.clientsList = clientsList;
        this.moviesList = moviesList;
        this.rentsList = rentsList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        DBItemAdapter adapter = null;

        selectedClient = null;
        selectedMovie = null;
        selectedRent = null;

        if (position == DBTables.CLIENTS.pageIndex) adapter = new ClientAdapter(activity, clientsList, (item, pos) -> {
            if (item != selectedClient) selectedClient = item;
            else selectedClient = null;
        });
        else if (position == DBTables.MOVIES.pageIndex) adapter = new MovieAdapter(activity, moviesList, (item, pos) -> {
            if (item != selectedMovie) selectedMovie = item;
            else selectedMovie = null;
        });
        else if (position == DBTables.RENTS.pageIndex) adapter = new RentAdapter(activity, rentsList, (item, pos) -> {
            if (item != selectedRent) selectedRent = item;
            else selectedRent = null;
        });

        return (new PageFragment(position, adapter));
    }

    public Serializable getSelection(int table) {
        if (table == DBTables.CLIENTS.pageIndex) return selectedClient;
        else if (table == DBTables.MOVIES.pageIndex) return selectedMovie;
        else if (table == DBTables.RENTS.pageIndex) return selectedRent;
        else return null;
    }

    @Override
    public int getItemCount()  { return 3; }
}
