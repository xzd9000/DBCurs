package com.example.dbcurs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RentAdapter extends DBItemAdapter<RentRecord, RentAdapter.RentViewHolder> {

    public RentAdapter(Context context, List<RentRecord> list, OnItemClickListener<RentRecord> onClickListener) { super(context, list, R.layout.rent_item, onClickListener); }

    @Override
    protected RentViewHolder createViewHolder(View view) { return new RentViewHolder(view); }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RentViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        RentRecord rent = items.get(position);
        holder.client.setText(rent.client);
        holder.movie.setText(rent.movie);

        DateFormat format = DateFormat.getDateInstance();

        holder.giveDate.setText(format.format(rent.giveDate));
        holder.returnDate.setText(rent.returnDate != null ? format.format((rent.returnDate)) : "");

        holder.itemView.setOnClickListener(view -> itemClick(view, rent, position));
    }

    public static class RentViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView
                client,
                movie,
                giveDate,
                returnDate;

        RentViewHolder(View view)
        {
            super(view);
            client = view.findViewById(R.id.rentNameText);
            movie = view.findViewById(R.id.rentMovieText);
            giveDate = view.findViewById(R.id.rentGiveDateText);
            returnDate = view.findViewById(R.id.rentReturnDateText);
        }
    }
}
