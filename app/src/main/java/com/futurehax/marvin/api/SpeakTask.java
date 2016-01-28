package com.futurehax.marvin.api;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class SpeakTask extends BasicTask {

    String mToSpeak;

    public SpeakTask(Activity ctx, String toSpeak) {
        super(ctx, null);
        mToSpeak = toSpeak;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            JSONObject o = new JSONObject();
            o.put("say", mToSpeak);
            String attempt = mUrlGenerator.getBlockingRequestWithJson(mUrlGenerator.generate(UrlGenerator.SPEAK), o);
            return attempt == null ? "Failed" : attempt;
        } catch (IllegalStateException  | JSONException e) {
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
//            mActivity.startActivity(new Intent(mActivity, MainActivity.class));
//            mActivity.finish();
        }
    }
}
