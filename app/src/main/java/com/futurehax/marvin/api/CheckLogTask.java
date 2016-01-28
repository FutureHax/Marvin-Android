package com.futurehax.marvin.api;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.concurrent.ExecutionException;

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
            String attempt = mUrlGenerator.getBlockingRequestWithJson(mUrlGenerator.generate(UrlGenerator.LOG, Integer.toString(mCount)));
            return attempt == null ? "Failed" : attempt.trim();
        } catch (IllegalStateException e) {
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
        if (!success.equals("Failed") && !success.equals("Unauthorized")) {
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
