package com.futurehax.marvin;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.futurehax.marvin.models.Roommate;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by FutureHax on 10/13/15.
 */
public class RoommateAdapter extends RecyclerView.Adapter<RoommateAdapter.PersonViewHolder> {

    ArrayList<Roommate> roommates;

    public RoommateAdapter(ArrayList<Roommate> roommates) {
        this.roommates = roommates;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_card_row, parent, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        holder.personName.setText(roommates.get(position).getName());
        boolean isHome = roommates.get(position).isHome;
        String status;
        if (isHome) {
            status = roommates.get(position).room;
        } else {
            status = "Not Home";
        }
        holder.personStatus.setText(status);
        holder.checkin.setText(DateFormat.getTimeInstance().format(new Date(roommates.get(position).lastCheckin)));

    }

    @Override
    public int getItemCount() {
        return roommates.size();
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView personName;
        TextView personStatus;
        TextView checkin;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            personName = (TextView) itemView.findViewById(R.id.person_name);
            personStatus = (TextView) itemView.findViewById(R.id.person_status);
            checkin = (TextView) itemView.findViewById(R.id.time);
        }
    }
}
