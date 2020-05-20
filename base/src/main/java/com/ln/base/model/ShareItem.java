package com.ln.base.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.ln.base.R;

/**
 * 分享的基础类型
 */
public class ShareItem implements Parcelable, JsonInterface {

    public static final Creator<ShareItem> CREATOR = new Creator<ShareItem>() {
        @Override
        public ShareItem createFromParcel(Parcel in) {
            return new ShareItem(in);
        }

        @Override
        public ShareItem[] newArray(int size) {
            return new ShareItem[size];
        }
    };
    /**
     * 分享的标题
     */
    private String mTitle;
    /**
     * 分享出去的承载页码，需要有对应的网页
     */
    private String mTargetUrl;
    /**
     * 需要分享的图片地址
     */
    private String mImageUrl;
    /**
     * 分享时的缩略图地址
     */
    private String mThumbUrl;
    /**
     * 分享的描述性文案
     */
    private String mDesContent;

    protected ShareItem(Parcel in) {
        mTitle = in.readString();
        mTargetUrl = in.readString();
        mImageUrl = in.readString();
        mThumbUrl = in.readString();
        mDesContent = in.readString();
    }

    public ShareItem() {
    }

    public static ShareItem createCommenShareItem(Context context) {
        ShareItem shareItem = new ShareItem();
        shareItem.setTitle(context.getString(R.string.app_name));
        shareItem.setDesContent(context.getString(R.string.share_content));
        shareItem.setTargetUrl(context.getString(R.string.share_url));
        return shareItem;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTargetUrl() {
        return mTargetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.mTargetUrl = targetUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    public String getThumbUrl() {
        return mThumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.mThumbUrl = thumbUrl;
    }

    public String getDesContent() {
        return mDesContent;
    }

    public void setDesContent(String desContent) {
        this.mDesContent = desContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mTargetUrl);
        dest.writeString(mImageUrl);
        dest.writeString(mThumbUrl);
        dest.writeString(mDesContent);
    }
}
