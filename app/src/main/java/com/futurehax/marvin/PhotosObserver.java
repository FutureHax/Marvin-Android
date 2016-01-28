package com.futurehax.marvin;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

import com.futurehax.marvin.api.UploadTask;
import com.futurehax.marvin.manager.PreferencesProvider;

import java.io.File;

class PhotosObserver extends ContentObserver {
    Context mContext;

    public PhotosObserver(Context c) {
        super(new Handler());
        mContext = c;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        PreferencesProvider p = new PreferencesProvider(mContext);
        if (p.getId() != null &&
                p.getBackupEnabled()) {
            Media media = readFromMediaStore(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            new UploadTask(mContext, media.getFile()).execute();
        }
    }

    public void searchCurrent() {
        if (new PreferencesProvider(mContext).getId() != null) {
            Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, "date_added DESC");
            while (cursor != null && cursor.moveToNext()) {
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                String filePath = cursor.getString(dataColumn);
                new UploadTask(mContext, new File(filePath)).execute();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Media readFromMediaStore(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, "date_added DESC");
        Media media = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                String filePath = cursor.getString(dataColumn);
                int mimeTypeColumn = cursor
                        .getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE);
                String mimeType = cursor.getString(mimeTypeColumn);
                media = new Media(new File(filePath), mimeType);
            }
            cursor.close();
        }
        return media;
    }

    private class Media {
        private File file;
        private String type;

        public Media(File file, String type) {
            this.file = file;
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public File getFile() {
            return file;
        }
    }
}