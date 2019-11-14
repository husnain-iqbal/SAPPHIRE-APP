package com.live.sapphire.app.Customized;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Husnain Iqnal on 01-Jun-17.
 */
public class FilmonInfo implements Serializable {
    private String mCode;
    private String mMessage;
    private String mId;
    private String mTitle;
    private String mAlias;
    private String mDescription;
    private String mGroup;
    private HashMap<String, StreamInfo> mStreamInfoRecord;

    public FilmonInfo(String code, String message, String id, String title, String alias, String description, String group, HashMap<String, StreamInfo> streamInfoRecord) {
        mCode = code;
        mMessage = message;
        mId = id;
        mTitle = title;
        mAlias = alias;
        mDescription = description;
        mGroup = group;
        mStreamInfoRecord = streamInfoRecord;
    }

    public String getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAlias() {
        return mAlias;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getGroup() {
        return mGroup;
    }

    public HashMap<String, StreamInfo> getStreamRecords() {
        return mStreamInfoRecord;
    }
}
