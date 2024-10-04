package com.example.dbcurs;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PageFragment extends Fragment {

    private int pageNumber;
    private DBItemAdapter adapter;

    public PageFragment(int page, DBItemAdapter adapter)
    {
        Bundle args = new Bundle();
        args.putInt("num", page);
        setArguments(args);
        this.adapter = adapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments()  != null ? getArguments().getInt("num") : 1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View ret = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView list = ret.findViewById(R.id.pageList);
        list.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        list.setAdapter(adapter);

        return ret;
    }
}
