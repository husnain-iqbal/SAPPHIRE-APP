package com.live.sapphire.app.Customized;

import java.io.Serializable;

/**
 * Created by Husnain Iqnal on 25-May-17.
 */
public class VideoInfo implements Serializable{

    private String mVideoId;
    private String mTitle;
    private String mThumbnailUrl;

    public String getVideoId() {
        return mVideoId;
    }

    public void setVideoId(String videoId) {
        mVideoId = videoId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    public VideoInfo(String videoId, String title, String thumbnailUrl) {
        mVideoId = videoId;
        mTitle = title;
        mThumbnailUrl = thumbnailUrl;
    }
}
