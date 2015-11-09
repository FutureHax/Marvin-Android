package com.futurehax.marvin;

import android.content.Context;
import android.util.Log;

import com.futurehax.marvin.gitignore.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by FutureHax on 10/14/15.
 */
public class UrlGenerator {

    public static final String CHECK_PATH = "/check/";
    public static final String REGISTER_PATH = "/register";
    public static final String VERSION_PATH = "/marvin_android/version.txt";
    public static final String ROOMMATES_PATH = "/roommates/";
    public static final String HEARTBEAT_PATH = "/heartbeat/";
    public static final String LOG_PATH = "/log/";
    public static final String SPEAK_PATH = "/speak/";
    public static final String UPLOAD_PATH = "/upload/";
    public static final String DEVICES_PATH = "/devices/";
    public static final String ADD_ROOM_PATH = "/addRoom/";
    public static final String GET_ROOM_PATH = "/getRooms/";
    public static final String TOGGLE_PATH = "/toggle/";
    
    public static final int CHECK = 0;
    public static final int REGISTER = 1;
    public static final int VERSION = 2;
    public static final int ROOMMATES = 3;
    public static final int HEARTBEAT = 4;
    public static final int LOG = 5;
    public static final int SPEAK = 6;
    public static final int UPLOAD = 7;
    public static final int DEVICES = 8;
    public static final int ADD_ROOM = 9;
    public static final int ROOMS = 10;
    public static final int TOGGLE = 11;

    private final Context context;

    public UrlGenerator(Context context) {
        this.context = context;
    }

    public String generate(int opt, String... args) throws Exception {
        String host = new PreferencesProvider(context).getHost();
        if (host == null) {
            throw new Exception("Host not provided");
        }
//        String host = (remote ? MARVIN_LOCAL_HOST : MARVIN_REMOTE_HOST);
        switch (opt) {
            case CHECK:
                return  host + CHECK_PATH + args[0];
            case REGISTER:
                return host + REGISTER_PATH;
            case VERSION:
                if (host.contains("http:")) {
                    return host.split(":")[0] + ":" + host.split(":")[1] + VERSION_PATH;
                } else {
                    return host.split(":")[0] + VERSION_PATH;
                }
            case ROOMMATES:
                return host + ROOMMATES_PATH;
            case HEARTBEAT:
                return host + HEARTBEAT_PATH;
            case LOG:
                return host + LOG_PATH + args[0];
            case TOGGLE:
                return host + TOGGLE_PATH;
            case SPEAK:
                return host + SPEAK_PATH;
            case UPLOAD:
                return host + UPLOAD_PATH;
            case DEVICES:
                return host + DEVICES_PATH;
            case ADD_ROOM:
                return host + ADD_ROOM_PATH;
            case ROOMS:
                return host + GET_ROOM_PATH;
        }

        return null;
    }

    public String getUploadRequest(String url, File f) {
        if (!url.contains("http")) {
            url = "http://" + url;
        }
        Builders.Any.M builder = Ion.with(context)
                .load(url)
                .addHeader("Cache-Control", Boolean.toString(false))
                .addHeader("x-access-token", Constants.TOKEN)
                .setTimeout(15000)
                .setMultipartParameter("uploader", new PreferencesProvider(context).getId())
                .setMultipartFile("photo", f);

        try {
            return builder
                    .asString().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRequest(String url, String... args) {
        if (!url.contains("http")) {
            url = "http://" + url;
        }
        Builders.Any.B builder = Ion.with(context)
                .load(url)
                .addHeader("Cache-Control", Boolean.toString(false))
                .addHeader("x-access-token", Constants.TOKEN)
                .setTimeout(15000);

        for (String arg : args) {
            String value = arg.split("&")[0];
            String name = arg.split("&")[1];
            builder.setBodyParameter(name, value);
        }

        try {
            return builder
                    .asString().get();
        } catch (Exception e) {
            Log.e(url, e.getMessage());
        }
        return null;
    }

    public String getRequestWithJson(String url, JSONObject json) {
        if (!url.contains("http")) {
            url = "http://" + url;
        }
        Builders.Any.B builder = Ion.with(context)
                .load(url)
                .setTimeout(2000);

        builder.setJsonObjectBody((JsonObject)new JsonParser().parse(json.toString()));

        try {
            return builder
                    .asString().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}