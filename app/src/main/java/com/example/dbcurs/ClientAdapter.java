package com.example.dbcurs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClientAdapter extends DBItemAdapter<Client, ClientAdapter.ClientViewHolder> {

    public ClientAdapter(Context context, List<Client> list, OnItemClickListener<Client> onClickListener) { super(context, list, R.layout.client_item, onClickListener); }

    @Override
    protected ClientAdapter.ClientViewHolder createViewHolder(View view) { return new ClientAdapter.ClientViewHolder(view); }

    @Override
    public void onBindViewHolder(ClientViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Client client = items.get(position);
        holder.name.setText(client.name);
        holder.phone.setText(Long.toString(client.phone));
        holder.address.setText(client.address);

        holder.itemView.setOnClickListener(view -> itemClick(view, client, position));
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView
                name,
                phone,
                address;

        ClientViewHolder(View view)
        {
            super(view);
            name = view.findViewById(R.id.clientNameText);
            phone = view.findViewById(R.id.clientPhoneText);
            address = view.findViewById(R.id.clientAddressText);
        }

        public void setColor(int color) {
            name.setTextColor(color);
            phone.setTextColor(color);
            address.setTextColor(color);
        }
    }
}
