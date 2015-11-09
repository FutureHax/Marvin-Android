package com.futurehax.marvin.api;

import android.app.Activity;

import com.futurehax.marvin.UrlGenerator;

public class SpeakTask extends BasicTask {

    String mToSpeak;

    public SpeakTask(Activity ctx, String toSpeak) {
        super(ctx, null);
        mToSpeak = toSpeak;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String attempt = mUrlGenerator.getRequest(mUrlGenerator.generate(UrlGenerator.SPEAK),
                    mToSpeak + "&say");
            return attempt == null ? "Failed" : attempt;
        } catch (Exception e) {
            return "Failed";
        }
    }


    @Override
    protected void onPostExecute(final String success) {
        if (success.equals("OK")) {
//            mActivity.startActivity(new Intent(mActivity, MainActivity.class));
//            mActivity.finish();
        }
    }
}
