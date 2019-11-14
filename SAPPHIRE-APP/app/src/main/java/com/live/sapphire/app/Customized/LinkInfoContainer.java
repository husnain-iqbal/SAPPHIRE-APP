package com.live.sapphire.app.Customized;

import java.io.Serializable;

/**
 * Created by Husnain Iqnal on 31-May-17.
 */
public class LinkInfoContainer implements Serializable {
    private String mId;
    private String mTitle;
    private String mUrl;
    private String mThumbnailUrl;
    private String mLinkId;
    private String mChannelOrPlaylistId;
    private String mSource;
    private String mEbound;
    private String mDescription;
    private String mPublishDate;
    private String mLinkType;

    //TODO: used only for IndividualLinks (InLinks)
    public LinkInfoContainer(String title, String url, String thumbnailUrl, String linkId, String source, String eBound, String linkType) {
        mTitle = title;
        mUrl = url;
        mThumbnailUrl = thumbnailUrl;
        mLinkId = linkId;
        mSource = source;
        mEbound = eBound;
        mLinkType = linkType;
    }

    //TODO: used only for OtherLinks
    public LinkInfoContainer(String id, String title, String url, String thumbnailUrl, String source, String linkType) {
        mId = id;
        mTitle = title;
        mUrl = url;
        mThumbnailUrl = thumbnailUrl;
        mSource = source;
        mLinkType = linkType;
    }

    //TODO: used only for Channels and PlayLists
    public LinkInfoContainer(String id, String title, String url, String thumbnailUrl, String channelOrPlaylistId, String description, String publishDate, String linkType) {
        mId = id;
        mTitle = title;
        mUrl = url;
        mThumbnailUrl = thumbnailUrl;
        mChannelOrPlaylistId = channelOrPlaylistId;
        mDescription = description;
        mPublishDate = publishDate;
        mLinkType = linkType;
    }


    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public String getLinkId() {
        return mLinkId;
    }

    public String getChannelOrPlaylistId() {
        return mChannelOrPlaylistId;
    }

    public String getSource() {
        return mSource;
    }

    public String getEbound() {
        return mEbound;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getPublishDate() {
        return mPublishDate;
    }

    public String getLinkType() {
        return mLinkType;
    }
}
