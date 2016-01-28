/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurehax.marvin.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futurehax.marvin.R;
import com.futurehax.marvin.models.UberRoom;

import java.util.ArrayList;

/**
 * HeaderRecyclerViewAdapter extension created to show how to use the library using DragonBall
 * characters.
 */
public class UberRoomAdapter extends RecyclerView.Adapter<UberRoomAdapter.ItemViewHolder> {

    final ArrayList<UberRoom> items;

    public UberRoomAdapter(ArrayList<UberRoom> items, View.OnClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    View.OnClickListener listener;

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_beacon_item, parent, false);
        ItemViewHolder pvh = new ItemViewHolder(v);
        pvh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
            }
        });

        return pvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        UberRoom room = items.get(position);
        holder.setTag(room);
        holder.roomName.setText(room.roomName);
        holder.beaconId.setText(room.getBeacons());
        holder.attachedDevices.setText(room.getDevices());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView roomName, beaconId, attachedDevices;
        View root;

        public ItemViewHolder(View itemView) {
            super(itemView);

            root = itemView;
            this.roomName = (TextView) root.findViewById(R.id.room_name);
            this.beaconId = (TextView) root.findViewById(R.id.beacon_name);
            this.attachedDevices = (TextView) root.findViewById(R.id.attached_devices);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            root.setOnClickListener(onClickListener);
        }

        public void setTag(Object tag) {
            root.setTag(tag);
        }
    }
}