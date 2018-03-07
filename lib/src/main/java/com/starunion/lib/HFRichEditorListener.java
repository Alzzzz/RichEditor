package com.starunion.lib;

import android.view.View;

/**
 * Discription:富文本编辑器监听
 * Created by sz on 2018/1/25.
 */

public interface HFRichEditorListener {
    /**
     * 监听字数
     * @param count
     */
    void onTextCountChanged(int count);

    /**
     * 焦点更换
     *
     * @param view
     * @param focus
     */
    void onFocusChange(View view, boolean focus);

    /**
     * 图片操作
     * @param imgSize
     */
    void onImgSizeChanged(int imgSize);

    /**
     * 视频操作
     * @param videoSize
     */
    void onVideoSizeChanged(int videoSize);
}
