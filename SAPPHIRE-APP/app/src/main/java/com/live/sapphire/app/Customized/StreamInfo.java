package com.live.sapphire.app.Customized;

import java.io.Serializable;

/**
 * Created by Husnain Iqnal on 01-Jun-17.
 */
public class StreamInfo implements Serializable{

    private String mName;
    private String mUrl;
    private String mQuality;
    private String mWatchTimeout;

    public StreamInfo(){};

    public StreamInfo(String name, String quality, String url, String watchTimeout) {
        mName = name;
        mUrl = url;
        mQuality = quality;
        mWatchTimeout = watchTimeout;
    }

    public String getName() {
        return mName;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getQuality() {
        return mQuality;
    }

    public String getWatchTimeout() {
        return mWatchTimeout;
    }
}
