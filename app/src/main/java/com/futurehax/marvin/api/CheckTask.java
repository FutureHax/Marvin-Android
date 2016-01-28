package com.futurehax.marvin.api;

import android.app.Activity;
import android.content.Intent;
import android.widget.ViewFlipper;

import com.futurehax.marvin.activities.MainActivity;

import java.util.concurrent.ExecutionException;

public class CheckTask extends BasicTask {


    public CheckTask(Activity ctx, ViewFlipper flippy) {
        super(ctx, flippy);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String attempt = mUrlGenerator.getBlockingRequestWithJson(mUrlGenerator.generate(UrlGenerator.CHECK));
            return attempt == null ? "Failed" : attempt;
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
            mActivity.startActivity(new Intent(mActivity, MainActivity.class));
            mActivity.finish();
        }
        mFlipper.setDisplayedChild(0);
    }

}
