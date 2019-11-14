package com.live.sapphire.app.Customized;

import java.io.Serializable;

/**
 * Created by Husnain Iqnal on 31-May-17.
 */
public class ChildCategoryInfo implements Serializable {
    private String mChildCategoryName;
    private String mCategoryId;
    private String mSubcategoryId;
    private String mMainCategoryId;
    private String mThumbnailUrl;

    public ChildCategoryInfo(String childCategoryName, String categoryId, String subcategoryId, String mainCategoryId, String thumbnailUrl) {
        mChildCategoryName = childCategoryName;
        mCategoryId = categoryId;
        mSubcategoryId = subcategoryId;
        mMainCategoryId = mainCategoryId;
        mThumbnailUrl = thumbnailUrl;
    }

    public String getChildCategoryName() {
        return mChildCategoryName;
    }

    public String getCategoryId() {
        return mCategoryId;
    }

    public String getSubcategoryId() {
        return mSubcategoryId;
    }

    public String getMainCategoryId() {
        return mMainCategoryId;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }
}
