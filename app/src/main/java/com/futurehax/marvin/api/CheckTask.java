package com.futurehax.marvin.api;

import android.app.Activity;
import android.content.Intent;
import android.widget.ViewFlipper;

import com.futurehax.marvin.UrlGenerator;
import com.futurehax.marvin.activities.MainActivity;

public class CheckTask extends BasicTask {


    public CheckTask(Activity ctx, ViewFlipper flippy) {
        super(ctx, flippy);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String attempt = mUrlGenerator.getRequest(mUrlGenerator.generate(UrlGenerator.CHECK));
            return attempt == null ? "false" : attempt;
        } catch (Exception e) {
            return "Failed";
        }
    }

    @Override
    protected void onPostExecute(final String success) {
        if (!success.equals("false")) {
            mActivity.startActivity(new Intent(mActivity, MainActivity.class));
            mActivity.finish();
        }
        mFlipper.setDisplayedChild(0);
    }

}
