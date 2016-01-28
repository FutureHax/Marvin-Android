package com.futurehax.marvin.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.futurehax.marvin.adapters.AttachedDevicesForRoomAdapter;
import com.futurehax.marvin.adapters.BeaconsForRoomAdapter;
import com.futurehax.marvin.adapters.HeaderRecyclerViewAdapterV1;
import com.futurehax.marvin.R;
import com.futurehax.marvin.api.GetAllDevicesTask;
import com.futurehax.marvin.api.SaveRoomTask;
import com.futurehax.marvin.models.AttachedDevice;
import com.futurehax.marvin.models.BeaconIdentifier;
import com.futurehax.marvin.models.UberRoom;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.Serializable;
import java.util.ArrayList;

public class AddRoomActivity extends AppCompatActivity {
    MaterialEditText roomName;
    RecyclerView beaconList, devicesList;

    AttachedDevicesForRoomAdapter dAdapter;
    BeaconsForRoomAdapter bAdapter;

    UberRoom roomToEdit;

    private View.OnClickListener devicesFooterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            new GetAllDevicesTask(AddRoomActivity.this, new GetAllDevicesTask.IGetAllDevicesTask() {
                @Override
                public void onDevicesFetched(final ArrayList<AttachedDevice> devices) {
                    if (devices != null) {
                        AlertDialog.Builder b = new AlertDialog.Builder(v.getContext());
                        b.setAdapter(new ArrayAdapter<AttachedDevice>(v.getContext(), android.R.layout.simple_list_item_1, devices), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, final int which) {
                                ArrayList<AttachedDevice> d = dAdapter.getItems();
                                d.add(devices.get(which));
                                dAdapter = new AttachedDevicesForRoomAdapter(d, devicesFooterClickListener);
                                devicesList.setAdapter(new HeaderRecyclerViewAdapterV1(dAdapter));
                            }
                        });
                        b.show();
                    }
                }
            }).execute();
        }
    };

    private View.OnClickListener beaconsFooterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            AlertDialog.Builder b = new AlertDialog.Builder(v.getContext());
            final View input = LayoutInflater.from(v.getContext()).inflate(R.layout.beacon_identifier, null, false);
            final MaterialEditText mac = (MaterialEditText) input.findViewById(R.id.mac);
            final MaterialEditText uuid = (MaterialEditText) input.findViewById(R.id.uuid);
            b.setView(input);
            b.setTitle("iBeacon MAC Address");
            b.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BeaconIdentifier id = new BeaconIdentifier(mac.getText().toString(), uuid.getText().toString());
                    Toast.makeText(v.getContext(), id.toString(), Toast.LENGTH_LONG).show();
                    ArrayList<BeaconIdentifier> d = bAdapter.getItems();
                    d.add(id);
                    bAdapter = new BeaconsForRoomAdapter(d, beaconsFooterClickListener);
                    beaconList.setAdapter(new HeaderRecyclerViewAdapterV1(bAdapter));
                }
            });
            b.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        Serializable data = getIntent().getSerializableExtra("data");
        if (data != null) {
            roomToEdit = (UberRoom) data;
        }

        roomName = (MaterialEditText) findViewById(R.id.room_name);
        beaconList = (RecyclerView) findViewById(R.id.beacon_list);
        devicesList = (RecyclerView) findViewById(R.id.devices_list);

        if (roomToEdit != null) {
            roomName.setText(roomToEdit.roomName);
        }
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveRoomTask(AddRoomActivity.this, generateRoom()).execute();
            }
        });

        LinearLayoutManager bllm = new LinearLayoutManager(this);
        LinearLayoutManager dllm = new LinearLayoutManager(this);

        ArrayList<BeaconIdentifier> beacons = new ArrayList<>();
        ArrayList<AttachedDevice> devices = new ArrayList<>();

        if (roomToEdit != null) {
            beacons = roomToEdit.beacons;
            devices = roomToEdit.devices;
        }
        dAdapter = new AttachedDevicesForRoomAdapter(devices, devicesFooterClickListener);

        bAdapter = new BeaconsForRoomAdapter(beacons, beaconsFooterClickListener);

        beaconList.setHasFixedSize(true);
        beaconList.setLayoutManager(bllm);
        beaconList.setAdapter(new HeaderRecyclerViewAdapterV1(bAdapter));

        devicesList.setHasFixedSize(true);
        devicesList.setLayoutManager(dllm);
        devicesList.setAdapter(new HeaderRecyclerViewAdapterV1(dAdapter));
    }

    private UberRoom generateRoom() {
        return new UberRoom(roomName.getText().toString(), dAdapter.getItems(), bAdapter.getItems());
    }
}
