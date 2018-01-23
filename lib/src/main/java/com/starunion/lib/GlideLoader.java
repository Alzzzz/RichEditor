package com.starunion.lib;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Discription:
 * Created by sz on 2018/1/22.
 */

public class GlideLoader implements ImageLoaderInterface {
    private static final String TAG = GlideLoader.class.getSimpleName();
    @Override
    public void getImageBitmapByPath(ImageView imageView, String path) {
        if (imageView != null && imageView.getContext()!=null){
            Glide.with(imageView).load(path).into(imageView);
        } else {
            Log.d(TAG, "getImageBitmapByPath: context == null");
        }
    }
}
