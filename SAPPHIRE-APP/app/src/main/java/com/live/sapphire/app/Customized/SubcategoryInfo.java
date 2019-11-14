package com.live.sapphire.app.Customized;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Husnain Iqnal on 19-May-17.
 */
public class SubcategoryInfo implements Serializable {
    private String mId;
    private String mSubcategoryName;
    private String mSubcategoryId;
    private String mMainCategoryId;
    private String mThumbnailUrl;
    private ArrayList<ChildCategoryInfo> mChildCategoryList;

    public SubcategoryInfo(String id, String subcategoryName, String subcategoryId, String mainCategoryId, String thumbnailUrl, ArrayList<ChildCategoryInfo> childCategoryList) {
        mId = id;
        mSubcategoryName = subcategoryName;
        mSubcategoryId = subcategoryId;
        mMainCategoryId = mainCategoryId;
        mThumbnailUrl = thumbnailUrl;
        mChildCategoryList = childCategoryList;
    }

    public String getId() {
        return mId;
    }

    public String getSubcategoryName() {
        return mSubcategoryName;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public String getSubcategoryId() {
        return mSubcategoryId;
    }

    public String getMainCategoryId() {
        return mMainCategoryId;
    }

    public ArrayList<ChildCategoryInfo> getChildCategoryList() {
        return mChildCategoryList;
    }
}
