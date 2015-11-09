package com.futurehax.marvin.api;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ViewFlipper;

import com.futurehax.marvin.UrlGenerator;
import com.futurehax.marvin.activities.MainActivity;
import com.futurehax.marvin.service.RegistrationIntentService;

public class AuthenticateTask extends BasicTask {


    public AuthenticateTask(Activity ctx, ViewFlipper flippy) {
        super(ctx, flippy);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String attempt = mUrlGenerator.getRequest(mUrlGenerator.generate(UrlGenerator.REGISTER),
                    mPreferences.getEmail() + "&email",
                    mPreferences.getName() + "&name",
                    mPreferences.getId() + "&profile_id",
                    mPreferences.getToken() + "&gcm");
            return attempt == null ? "Failed" : attempt;
        } catch (Exception e) {
            return "Failed";
        }
    }


    @Override
    protected void onPostExecute(final String success) {
        if (success.equals("OK")) {
            mActivity.startActivity(new Intent(mActivity, MainActivity.class));
            mActivity.finish();
        } else {
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(RegistrationIntentService.REGISTRATION_FAILED));
        }
        mFlipper.setDisplayedChild(0);
    }
}
