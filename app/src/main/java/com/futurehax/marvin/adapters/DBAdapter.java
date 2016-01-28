package com.futurehax.marvin.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.futurehax.marvin.models.InterestingApp;

import java.util.ArrayList;

public class DBAdapter {
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_INTERESTING_PACKAGES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + INTERESTING_PACKAGES);
            onCreate(db);
        }
    }

    private static final int DB_VERSION = 6;
    public static final String DB_NAME = "notifier.db";
    public static final String INTERESTING_PACKAGES = "packages";
    public static final String APP_NAME = "app_name";
    public static final String PACKAGE_NAME = "package_name";
    public static final String HUE = "hue";
    public static final String LENGTH = "length";
    public static final String STATUS = "status";
    public static final String GROUPS = "groups";


    private static final String CREATE_INTERESTING_PACKAGES = "create table "
            + INTERESTING_PACKAGES + "(_id integer primary key autoincrement, "
            + GROUPS + " text, "
            + APP_NAME + " text, " + HUE + " text, " + PACKAGE_NAME + " text, " + LENGTH + " text, "
            + STATUS + " text, unique(" + PACKAGE_NAME + ") on conflict replace);";

    private final Context context;
    private static DatabaseHelper DBHelper;
    public SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    public void close() {
        DBHelper.close();
    }

    public InterestingApp getInterestingApp(String pName) {
        Cursor mCursor = db.query(INTERESTING_PACKAGES, new String[]{
                        PACKAGE_NAME, APP_NAME, HUE, LENGTH, STATUS, GROUPS}, PACKAGE_NAME + " = ?",
                new String[]{pName}, null, null, null, null
        );
        InterestingApp app = null;
        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            app = new InterestingApp(mCursor.getString(mCursor
                    .getColumnIndex(PACKAGE_NAME)), Boolean.parseBoolean((mCursor.getString(mCursor
                    .getColumnIndex(STATUS)))), context);
            app.setAppName(mCursor.getString(mCursor.getColumnIndex(APP_NAME)));

            app.setHue(Integer.parseInt(mCursor.getString(mCursor
                    .getColumnIndex(HUE))));
            app.setLength(Integer.parseInt(mCursor.getString(mCursor
                    .getColumnIndex(LENGTH))));
            app.setName(mCursor.getString(mCursor.getColumnIndex(APP_NAME)));
            app.setGroupIds(mCursor.getString(mCursor.getColumnIndex(GROUPS)));

        }
        mCursor.close();
        return app;
    }

    public ArrayList<InterestingApp> getInterestingApps() {
        ArrayList<InterestingApp> apps = new ArrayList<InterestingApp>();
        Cursor mCursor = db.query(INTERESTING_PACKAGES, new String[]{
                        PACKAGE_NAME, APP_NAME, HUE, LENGTH, GROUPS}, STATUS + " = ?",
                new String[]{"true"}, null, null, null, null
        );
        while (mCursor.moveToNext()) {
            InterestingApp app = new InterestingApp(mCursor.getString(mCursor
                    .getColumnIndex(PACKAGE_NAME)), true, context);
            app.setAppName(mCursor.getString(mCursor.getColumnIndex(APP_NAME)));
            app.setHue(Integer.parseInt(mCursor.getString(mCursor
                    .getColumnIndex(HUE))));
            app.setLength(Integer.parseInt(mCursor.getString(mCursor
                    .getColumnIndex(LENGTH))));
            app.setName(mCursor.getString(mCursor.getColumnIndex(APP_NAME)));
            app.setGroupIds(mCursor.getString(mCursor.getColumnIndex(GROUPS)));
            apps.add(app);
        }
        mCursor.close();
        return apps;
    }

    public ArrayList<String> getInterestingAppPackageNames() {
        ArrayList<String> apps = new ArrayList<String>();
        Cursor mCursor = db.query(INTERESTING_PACKAGES, new String[]{
                        PACKAGE_NAME}, STATUS + " = ?",
                new String[]{"true"}, null, null, null, null
        );
        if (mCursor.getCount() > 0) {
            while (mCursor.moveToNext()) {
                apps.add(mCursor.getString(mCursor
                        .getColumnIndex(PACKAGE_NAME)));
            }
        }
        mCursor.close();
        return apps;
    }

    public void insertAppData(InterestingApp data) {
        ContentValues v = new ContentValues();
        v.put(APP_NAME, data.getAppName());
        v.put(PACKAGE_NAME, data.getPackageName());
        v.put(HUE, data.getHue());
        v.put(LENGTH, data.getLength());
        v.put(STATUS, Boolean.toString(data.isActive()));
        v.put(GROUPS, data.getGroupIds());
        if (getInterestingApp(data.getPackageName()) == null) {
            db.insert(INTERESTING_PACKAGES, null, v);
        } else {
            db.update(INTERESTING_PACKAGES, v, PACKAGE_NAME + " = ? ",
                    new String[]{data.getPackageName()});
        }
    }

    public boolean isAppInteresting(String pName) {
        Cursor mCursor = db.query(INTERESTING_PACKAGES, new String[]{
                        PACKAGE_NAME, APP_NAME}, PACKAGE_NAME + " = ? AND " + STATUS + " = ?",
                new String[]{pName, "true"}, null, null, null, null
        );
        boolean b = mCursor.getCount() > 0;
        mCursor.close();
        return b;
    }

    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void deleteInterestingPackage(String value) {
        ContentValues values = new ContentValues();
        values.put(STATUS, "false");
        db.update(INTERESTING_PACKAGES, values, PACKAGE_NAME + " = ? ",
                new String[]{value});
    }

}