package com.futurehax.marvin.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futurehax.marvin.R;
import com.futurehax.marvin.models.BeaconIdentifier;

import java.util.ArrayList;

/**
 * Created by FutureHax on 10/13/15.
 */
public class BeaconsForRoomAdapter extends RecyclerView.Adapter<BeaconsForRoomAdapter.BeaconViewHolder> implements HeaderRecyclerViewAdapterV1.FooterRecyclerView {

    ArrayList<BeaconIdentifier> beacons;
    View.OnClickListener listener;

    public BeaconsForRoomAdapter(ArrayList<BeaconIdentifier> beacons, View.OnClickListener listener) {
        this.beacons = beacons;
        this.listener = listener;
    }

    @Override
    public BeaconViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_row, parent, false);
        BeaconViewHolder vh = new BeaconViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(BeaconViewHolder holder, int position) {
        holder.beaconId.setText(beacons.get(position).mac);
    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_row_footer, parent, false);
        return new FooterViewHolder(v);
    }

    @Override
    public void onBindFooterView(RecyclerView.ViewHolder holder, final int position) {
        final FooterViewHolder viewHolder = (FooterViewHolder) holder;
        viewHolder.content.setText("Add Item");
        viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
            }
        });
    }

    public ArrayList<BeaconIdentifier> getItems() {
        return beacons;
    }

    public static class BeaconViewHolder extends RecyclerView.ViewHolder {
        TextView beaconId;

        BeaconViewHolder(View itemView) {
            super(itemView);
            beaconId = (TextView) itemView.findViewById(R.id.beacon_id);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        View root;

        TextView content;

        FooterViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            content = (TextView) root.findViewById(R.id.footer);
        }

        void setOnClickListener(View.OnClickListener listener) {
            root.setOnClickListener(listener);
        }
    }
}
