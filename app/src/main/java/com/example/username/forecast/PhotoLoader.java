package com.example.username.forecast;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by username on 10/5/2016.
 */

public class PhotoLoader extends AsyncTask<Photo, Void, Bitmap> {

    private final String PHOTO_URL_TEMPLATE = "https://farm%s.staticflickr.com/%s/%s_%s_h.jpg";
    @Override
    protected Bitmap doInBackground(Photo... params) {
        Photo src = params[0];
        URL url = null;
        Bitmap bmp = null;
        try {
            url = new URL(String.format(PHOTO_URL_TEMPLATE, src.getFarm(), src.getServer(), src.getId(), src.getSecret()));
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bmp;
    }
}
