package com.live.sapphire.app.Customized;

import java.io.Serializable;

/**
 * Created by Husnain Iqnal on 23-May-17.
 */
public class PlayListsInfo implements Serializable {
    private String mTitle;
    private String mUrl;
    private String mPlayListId;
    private String mDescription;
    private String mPublishDate;
    private String mThumbnailUrl;

    public PlayListsInfo(String title, String url, String playListId, String description, String publishDate, String thumbnailUrl) {
        mTitle = title;
        mUrl = url;
        mPlayListId = playListId;
        mDescription = description;
        mPublishDate = publishDate;
        mThumbnailUrl = thumbnailUrl;
    }

    public PlayListsInfo(String title, String playListId, String publishDate, String thumbnailUrl) {
        mTitle = title;
        mPlayListId = playListId;
        mPublishDate = publishDate;
        mThumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getPlayListId() {
        return mPlayListId;
    }

    public void setPlayListId(String playListId) {
        mPlayListId = playListId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getPublishDate() {
        return mPublishDate;
    }

    public void setPublishDate(String publishDate) {
        mPublishDate = publishDate;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }
}
