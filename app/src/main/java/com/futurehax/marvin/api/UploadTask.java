package com.futurehax.marvin.api;

import android.content.Context;

import java.io.File;

/**
 * Created by FutureHax on 10/26/15.
 */
public class UploadTask extends BasicTask {
    File toUpload;

    public UploadTask(Context ctx, File toUpload) {
        super(ctx, null);
        this.toUpload = toUpload;
    }

    @Override
    protected String doInBackground(Void... params) {

        try {
            String attempt = mUrlGenerator.getUploadRequest(mUrlGenerator.generate(UrlGenerator.UPLOAD), toUpload);
            return attempt == null ? "Failed" : attempt;
        } catch (IllegalStateException e) {
            return "Failed";
        }

    }


    @Override
    protected void onPostExecute(final String success) {
        if (!success.equals("Failed") && !success.equals("Unauthorized")) {
            mPreferences.setImageUploaded(toUpload, true);
        } else {
            mPreferences.setImageUploaded(toUpload, false);
        }
    }
}
