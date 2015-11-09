package com.futurehax.marvin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futurehax.marvin.models.AttachedDevice;

import java.util.ArrayList;

/**
 * Created by FutureHax on 10/13/15.
 */
public class AttachedDevicesForRoomAdapter extends RecyclerView.Adapter<AttachedDevicesForRoomAdapter.DeviceViewHolder> implements HeaderRecyclerViewAdapterV1.FooterRecyclerView {

    ArrayList<AttachedDevice> attachedDevices;
    View.OnClickListener listener;

    public AttachedDevicesForRoomAdapter(ArrayList<AttachedDevice> beacons, View.OnClickListener listener) {
        this.attachedDevices = beacons;
        this.listener = listener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_row, parent, false);
        DeviceViewHolder vh = new DeviceViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        holder.content.setText(String.format("%s : %s", attachedDevices.get(position).type, attachedDevices.get(position).name));
    }

    @Override
    public int getItemCount() {
        return attachedDevices.size();
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

    public ArrayList<AttachedDevice> getItems() {
        return attachedDevices;
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView content;

        DeviceViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.content);
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
