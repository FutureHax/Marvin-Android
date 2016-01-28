package com.futurehax.marvin.api;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ViewFlipper;

import com.futurehax.marvin.activities.MainActivity;
import com.futurehax.marvin.service.RegistrationIntentService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class AuthenticateTask extends BasicTask {


    public AuthenticateTask(Activity ctx, ViewFlipper flippy) {
        super(ctx, flippy);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            JSONObject data = new JSONObject();
            data.put("email", mPreferences.getEmail());
            data.put("name", mPreferences.getName());
            data.put("profile_id", mPreferences.getId());
            data.put("gcm", mPreferences.getToken());
            data.put("enable_tracking", mPreferences.getHasTrackingEnabled());
            String attempt = mUrlGenerator.getBlockingRequestWithJson(mUrlGenerator.generate(UrlGenerator.REGISTER), data);
            return attempt == null ? "Failed" : attempt;
        } catch (IllegalStateException | JSONException e) {
            return "Failed";
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "Failed";
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
