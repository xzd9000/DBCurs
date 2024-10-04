package com.example.dbcurs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class DBItemAdapter<T, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {

    public interface OnItemClickListener<T> {
        void onItemClick(T item, int position);
    }

    private final Context context;
    private final LayoutInflater inflater;
    protected final List<T> items;
    protected final int layout;
    protected final OnItemClickListener<T> onItemClickListener;
    protected int selectedPosition;
    protected View selectedView;

    protected DBItemAdapter(Context context, List<T> items, int layout, OnItemClickListener<T> onItemClickListener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.layout = layout;
        this.onItemClickListener = onItemClickListener;
        selectedPosition = -1;
        selectedView = null;
    }

    public H onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(layout, parent, false);
        return createViewHolder(view);
    }
    protected abstract H createViewHolder(View view);

    @Override
    public void onBindViewHolder(H holder, int position) {
        if (position == selectedPosition) {
            selectedView = holder.itemView;
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selected));
        }
        else  holder.itemView.setBackgroundColor(0);

    }

    protected void itemClick(View view, T item, int position) {
        if (!(position == selectedPosition)) {
            if (selectedView != null) selectedView.setBackgroundColor(0);
            selectedView = view;
            selectedPosition = position;
            view.setBackgroundColor(context.getResources().getColor(R.color.selected));
        }
        else {
            selectedView = null;
            selectedPosition = -1;
            view.setBackgroundColor(0);
        }

        onItemClickListener.onItemClick(item, position);
    }

    public void resetSelection() {
        selectedPosition = -1;
        selectedView.setBackgroundColor(0);
        selectedView = null;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
