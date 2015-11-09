package com.futurehax.marvin.models;

import java.util.ArrayList;

/**
 * Created by FutureHax on 10/16/15.
 */
public class BeaconMap {

    public static ArrayList<UberRoom> getHardcodedMap() {
        ArrayList<UberRoom> map = new ArrayList<>();
        for (HardcodedRoom room : HardcodedRoom.values()) {
            map.add(new UberRoom(room.roomName, room.deviceIds, room.identifiers));
        }

        return map;
    }

    private enum HardcodedRoom {
        //        MARVIN_MBP("Office", new String[]{"9"}, new String[]{"74:91:07:1F:A7:4C"}, new String[]{"FD5B667C-73C8-11E5-8BCF-FEFF819CDC9F"}),
        MARVIN("Office",
                new String[]{"9"},
                new String[]{"4C:55:2E:8E:D3:65"},
                new String[]{"FD5B667C-73C8-11E5-8BCF-FEFF819CDC9F"}),
        ALFRED("Ken's Room",
                new String[]{"10", "11", "12"},
                new String[]{"74:DA:EA:AC:8B:0B",
                        "68:9E:19:00:13:E7"},
                HMSensor.UUID(2)),
        BERNARD("Living/Kitchen",
                new String[]{"2", "3", "4", "5", "6"},
                new String[]{"74:DA:EA:AC:7F:D0",
                        "68:9E:19:00:1C:81",
                        "74:DA:EA:AC:B8:96",
                        "68:9E:19:08:8B:B0"},
                HMSensor.UUID(4)),
        DILBERT("Saki's Room",
                new String[]{"7"},
                new String[]{"74:DA:EA:B1:83:0D"},
                HMSensor.UUID(1));

        public String roomName;
        public ArrayList<BeaconIdentifier> identifiers;
        public ArrayList<AttachedDevice> deviceIds;

        HardcodedRoom(String roomName, String[] deviceIds, String[] mac, String[] uuid) {
            this.deviceIds = new ArrayList<>();
            this.identifiers = new ArrayList<>();
            for (int i = 0; i < deviceIds.length; i++) {
                this.deviceIds.add(new AttachedDevice("Bulb " + deviceIds[i], deviceIds[i], "Bulb"));
            }
            this.roomName = roomName;
            for (int i = 0; i < mac.length; i++) {
                this.identifiers.add(new BeaconIdentifier(mac[i], uuid[i]));
            }
        }

        public static HardcodedRoom get(String identifier) {
            return valueOf(identifier);
        }

    }

}
