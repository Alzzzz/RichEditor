package com.starunion.lib;

import android.view.View;
import android.widget.ImageView;

/**
 * Discription:图片获取接口
 *
 * Created by sz on 2018/1/22.
 */

public interface ImageLoaderInterface {
    /**
     * 根据路径获取bitmap并加载到ImageView中
     *  @param imageView 目标imageview
     * @param path      路径
     * @param maskView
     */
    void loadBitmapIntoImageView(ImageView imageView, String path, View maskView);


    /**
     * 根据路径获取bitmap并加载到VideoView中
     *
     * @param imageView 目标imageview
     * @param path      路径
     */
    void loadBitmapIntoVideoView(ImageView imageView, String path);
}
