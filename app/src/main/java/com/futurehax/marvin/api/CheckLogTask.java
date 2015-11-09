package com.futurehax.marvin.api;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.futurehax.marvin.UrlGenerator;

public class CheckLogTask extends BasicTask {

    int mCount;
    TextView mTextView;

    public CheckLogTask(Activity ctx, int count, TextView logView, ViewFlipper flippy) {
        super(ctx, flippy);
        mCount = count;
        mTextView = logView;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String attempt = mUrlGenerator.getRequest(mUrlGenerator.generate(UrlGenerator.LOG, Integer.toString(mCount)));
            return attempt == null ? "false" : attempt.split("\n")[0];
        } catch (Exception e) {
            return "Failed";
        }
    }

    @Override
    protected void onPostExecute(final String success) {
        if (!success.equals("false")) {
            Log.d("THE SERVER LOG", success);

            final ScrollView sv = (ScrollView) mTextView.getParent();
            mTextView.setText(success);
            sv.post(new Runnable() {
                @Override
                public void run() {
                    sv.fullScroll(View.FOCUS_DOWN);
                }
            });

            if (mFlipper.getDisplayedChild() == 0) {
                mFlipper.setDisplayedChild(1);
            }
        }
    }

}
