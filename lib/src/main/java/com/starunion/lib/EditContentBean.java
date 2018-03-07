package com.starunion.lib;

/**
 * Discription:
 * Created by sz on 2018/1/26.
 */

public class EditContentBean {
    private int index;
    //视频、图片本地路径
    private String filePath;
    private String coverPath;
    private String videoLength;
    //文字
    private String content;
    //类型
    private int type;

    //图片视频url
    private String uploadUrl;
    //视频coverUrl
    private String uploadCoverUrl;
    //图片size
    private String uploadSizeUrl;
    private String bucket;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(String videoLength) {
        this.videoLength = videoLength;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getUploadSizeUrl() {
        return uploadSizeUrl;
    }

    public void setUploadSizeUrl(String uploadSizeUrl) {
        this.uploadSizeUrl = uploadSizeUrl;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getUploadCoverUrl() {
        return uploadCoverUrl;
    }

    public void setUploadCoverUrl(String uploadCoverUrl) {
        this.uploadCoverUrl = uploadCoverUrl;
    }
}
