package com.futurehax.marvin.api;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ViewFlipper;

import com.futurehax.marvin.PreferencesProvider;
import com.futurehax.marvin.UrlGenerator;

/**
 * Created by FutureHax on 10/16/15.
 */
public abstract class BasicTask extends AsyncTask<Void, Void, String> {
    Activity mActivity;
    Context mContext;
    PreferencesProvider mPreferences;
    UrlGenerator mUrlGenerator;
    ViewFlipper mFlipper;

    public BasicTask(Activity ctx, ViewFlipper flippy) {
        mActivity = ctx;
        mContext = mActivity;
        mPreferences = new PreferencesProvider(mActivity);
        mUrlGenerator = new UrlGenerator(mActivity);
        mFlipper = flippy;
    }

    public BasicTask(Context ctx, ViewFlipper flippy) {
        mContext = ctx;
        mPreferences = new PreferencesProvider(mContext);
        mUrlGenerator = new UrlGenerator(mContext);
        mFlipper = flippy;
    }


}
