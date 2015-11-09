package com.futurehax.marvin.models;

/**
 * Created by FutureHax on 10/16/15.
 */
public class HMSensor {
    public static final String UUID = "74278BDA-B644-4520-8F0C-720EAF059935";
    public static final int MINOR = 65505;
    public static final int MAJOR = 65504;

    public static String getLayout() {
        return "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    }

    public static String[] UUID(int i) {
        String[] res = new String[i];
        for (int j=0;j<i;j++) {
            res[j] = UUID;
        }
        return res;
    }


    // [2, 1, 6, 26, -1, 76, 0, 2, 21, 116, 39, -117, -38, -74, 68, 69, 32, -113, 12, 114, 14, -81, 5, -103, 53, -1, -32, -1, -31, -59, 14, 9, 72, 77, 83, 101, 110, 115, 111, 114, 0, 0, 0, 0, 0, 2, 10, -23, 7, 22, 0, -80, 0, 23, 0, 0, 3, 2, -32, -1, 0, 0]
    // 02 01 06 1a ff preamble
    // 4c 00 man data company id
    // 02 15 man data adv
    // 74 27 8b da b6 44 45 20 8f 0c 72 0e af 05 99 35 man data uuid 16
    // ff e0 man data major
    // ff e1 man data minor
    // c5 man data tx power
    // 0e 09
    // 48 4d 53 65 6e 73 6f 72 00 00 00 00 00 name
    // 02 0a e9 07 16 00 b0 00 18 00 00 03 02 e0 ff 00 00
    
    
}
