package com.starunion.lib;

/**
 * Discription:
 * Created by sz on 2018/1/25.
 */

public interface EditVideoController {
    /**
     * 开始播放
     * @param filePath
     * @param coverPath
     * @param videoLength
     */
    void start(String filePath, String coverPath, String videoLength);

    /**
     * 点击切换视频封面
     * @param filePath
     * @param videoLength
     */
    void onClickChangeCover(String filePath, String videoLength);
}
