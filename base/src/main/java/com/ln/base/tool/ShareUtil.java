/*
package com.ln.base.tool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import BaseActivity;
import ShareItem;

*/
/**
 * 分享的工具类
 * <p>
 * 微信分享的图标大小不能超过32K，所以缩图大小不能太大，暂定为120*120
 * <p>
 * 微信分享的图标大小不能超过32K，所以缩图大小不能太大，暂定为120*120
 * 为了方便调用，这里直接传入decorView，不需要调用者每次都来传入View
 *
 * @param siteId
 * @param toFriend true:分享微信朋友，false微信朋友圈
 * <p>
 * 因为微信和新浪微博的分享要传bitmap数据，所以要先进行异步下载；
 * @param siteType 分享的平台类型;
 * @param width    指定图片压缩后的宽度，0为不压缩，新浪微博分享不需要压缩，微信分享限定缩略图是32K需要压缩
 * @param height   指定图片压缩后的高度
 * ，0为不压缩，新浪微博分享不需要压缩，微信分享限定缩略图是32K需要压缩
 * 腾讯sdk里面的没有比对requestCode,需要我们加上请求code的判断，防止其它回调干扰，
 *//*

public class ShareUtil {
    */
/**
 * 微信分享的图标大小不能超过32K，所以缩图大小不能太大，暂定为120*120
 *//*

    public static final int SHARE_THUMB_WIDTH = 120;
    */
/**
 * 微信分享的图标大小不能超过32K，所以缩图大小不能太大，暂定为120*120
 *//*

    public static final int SHARE_THUMB_HEIDTH = 120;
    public static final String ITEM_URL = "itemUrl";

    public static final String ITEM_TITLE = "itemTitle";

    public static final String ITEM_ID = "itemId";

    public static final String ITEM_IMAGEURL = "itemImageUrl";

    public static final String ITEM_DESCRIPTION = "itemDescription";


    private BaseActivity mContext;

    private ShareItem mShareItem;

    private Tencent mTencent;

    private IWXAPI weChatApi;

    private ImageGetter mImageGetter;

    private IWeiboShareAPI mWeiboShareAPI;

    private ShareSinaWeiBoUtil mSinaWeiBoUtil;

    private BaseUiListener mBaseUiListener;

    private BottomWindow mShareWindow;

    public static final SiteType[] SITE_TYPES = {SiteType.WECHAT, SiteType.WX_CIRCLE, SiteType.QQ, SiteType.SINA_WEIBO};

    public ShareUtil(@NonNull BaseActivity context) {
        mContext = context;
        mImageGetter = new ImageGetter(context);
        mTencent = Tencent.createInstance(Config.QQ_APPKEY, mContext.getApplicationContext());
    }

    public ShareItem getShareItem() {
        return mShareItem;
    }

    private void share(ShareItem shareItem, SiteType siteType) {
        switch (siteType) {
            case WECHAT:
                shareToWecChat();
                break;
            case WX_CIRCLE:
                shareToWXCircle();
                break;
            case QQ:
                shareToQQ();
                break;
            case SINA_WEIBO:
                shareToSinaWeibo();
                break;
            default:
                break;
        }

    }


    private void shareToWecChat() {
        downloadBitmap(SiteType.WECHAT, mShareItem.getThumbUrl(), SHARE_THUMB_WIDTH, SHARE_THUMB_HEIDTH);
    }

    private void shareToWXCircle() {
        downloadBitmap(SiteType.WX_CIRCLE, mShareItem.getThumbUrl(), SHARE_THUMB_WIDTH, SHARE_THUMB_HEIDTH);
    }

    private void shareToSinaWeibo() {
        downloadBitmap(SiteType.SINA_WEIBO, mShareItem.getThumbUrl(), SHARE_THUMB_WIDTH, SHARE_THUMB_HEIDTH);
    }

    private void shareToQQ() {
        String appName = mContext.getString(R.string.app_name);
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, mShareItem.getTitle());
        String imageUrl = mShareItem.getThumbUrl();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mShareItem.getTargetUrl());
        String shareContent = mShareItem.getDesContent();
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareContent);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        if (mBaseUiListener == null) {
            mBaseUiListener = new BaseUiListener();
        }
        mTencent.shareToQQ(mContext, params, mBaseUiListener);
    }



    public void show(@NonNull ShareItem shareItem) {
        mShareItem = shareItem;
        if (mShareWindow == null) {
            mShareWindow = new BottomWindow(mContext, R.layout.dialog_share);
            GridView gridView = (GridView) mShareWindow.getContentlLayout().findViewById(R.id.gv_share);
            ShareGridAdapter shareGridAdapter = new ShareGridAdapter(SITE_TYPES, mContext);
            gridView.setAdapter(shareGridAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mShareWindow.dismiss();
                    share(mShareItem, SITE_TYPES[position]);
                }
            });
        }
        */
/**为了方便调用，这里直接传入decorView，不需要调用者每次都来传入View*//*

        mShareWindow.show(mContext.getWindow().getDecorView());
    }

    */
/**
 * @param siteId
 * @param toFriend true:分享微信朋友，false微信朋友圈
 *//*

    private void shareWeiXin(int siteId, boolean toFriend, Bitmap thumbBitmap) {
        if (weChatApi == null) {
            weChatApi = WXAPIFactory.createWXAPI(mContext, Config.WEIXIN_APPKEY, true);
            weChatApi.registerApp(Config.WEIXIN_APPKEY);
        }
        if (!weChatApi.isWXAppInstalled()) {
            mContext.getToastDialog().showToast(R.string._install_weixin);
            return;
        }
        WXMediaMessage msg = new WXMediaMessage();
        String bt;
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mShareItem.getTargetUrl();
        msg.mediaObject = webpage;
        bt = "video" + System.currentTimeMillis();
        msg.title = mShareItem.getTitle();
        msg.description = mShareItem.getDesContent();
        msg.setThumbImage(thumbBitmap);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = SendMessageToWX.Req.WXSceneSession;
        if (!toFriend) {
            final int minVersion = 0x21020001;
            int wxSdkVersion = weChatApi.getWXAppSupportAPI();
            if (wxSdkVersion >= minVersion) {
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
            } else {
                Toast.makeText(mContext, R.string._weixin_version_warning, Toast.LENGTH_LONG).show();
                return;
            }
        }

        req.transaction = bt;
        req.message = msg;
        weChatApi.sendReq(req);

    }



    class BaseUiListener implements IUiListener {

        @Override
        public void onCancel() {
            mContext.getToastDialog().showToast(R.string._share_canceled);
        }

        @Override
        public void onComplete(Object respons) {
            mContext.getToastDialog().showToast(R.string._share_success);
        }

        @Override
        public void onError(UiError arg0) {
            mContext.getToastDialog().showToast(R.string._share_failed);
        }
    }

    */
/**
 * 因为微信和新浪微博的分享要传bitmap数据，所以要先进行异步下载；
 *
 * @param siteType 分享的平台类型;
 * @param width    指定图片压缩后的宽度，0为不压缩，新浪微博分享不需要压缩，微信分享限定缩略图是32K需要压缩
 * @param height   指定图片压缩后的高度
 *                 ，0为不压缩，新浪微博分享不需要压缩，微信分享限定缩略图是32K需要压缩
 *//*

    private void downloadBitmap(@NonNull final SiteType siteType, String imagePath, int width, int height) {

            mImageGetter.inflact(imagePath, width, height, new ImageGetter.OnLoadImageCallback() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    if (bitmap == null) {
                        BitmapDrawable defaultDrawable = (BitmapDrawable) mContext.getResources().getDrawable(
                                R.drawable.ic_share_logo);
                        bitmap = defaultDrawable != null ? defaultDrawable.getBitmap() : null;
                    }
                    switch (siteType) {
                        case SINA_WEIBO:
                            if (mSinaWeiBoUtil == null) {
                                mSinaWeiBoUtil = new ShareSinaWeiBoUtil(mContext, mShareItem);
                            }
                            mSinaWeiBoUtil.setThumbBitmap(bitmap);
                            mSinaWeiBoUtil.setmShareItem(mShareItem);
                            mSinaWeiBoUtil.shareSinaWeibo();
                            break;
                        case WECHAT:
                        case WX_CIRCLE:
                            shareWeiXin(siteType.mSiteId, siteType == SiteType.WECHAT, bitmap);
                            break;
                        default:
                            break;
                    }
                }
            });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        */
/**腾讯sdk里面的没有比对requestCode,需要我们加上请求code的判断，防止其它回调干扰，*//*

        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.handleResultData(data, mBaseUiListener);
        }
    }

    public enum SiteType {

        WECHAT(1002, R.string._wechat_friend, R.drawable.ic_share_wechat),

        WX_CIRCLE(1003, R.string._wx_circle, R.drawable.ic_share_wxcricle),

        QQ(1004, R.string._qq_friend, R.drawable.ic_share_qq),

        SINA_WEIBO(1001, R.string._sina_weibo, R.drawable.ic_share_sina);

        public final int mSiteId;
        public final int mIconRes;
        public final int mTitleRes;

        SiteType(int siteId, int titleRes, int iconRes) {
            this.mSiteId = siteId;
            this.mIconRes = iconRes;
            this.mTitleRes = titleRes;
        }
    }

    private static class ShareGridAdapter extends BaseAdapter {
        private ShareUtil.SiteType[] mSiteTypes;
        private Context mContext;

        public ShareGridAdapter(ShareUtil.SiteType[] siteTypes, Context context) {
            this.mSiteTypes = siteTypes;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mSiteTypes != null ? mSiteTypes.length : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.item_share_dialog, parent, false);
            }
            ImageView siteLogo = (ImageView) convertView.findViewById(R.id.iv_site_logo);
            TextView siteName = (TextView) convertView.findViewById(R.id.tv_site_name);
            ShareUtil.SiteType siteType = mSiteTypes[position];
            siteLogo.setImageResource(siteType.mIconRes);
            siteName.setText(siteType.mTitleRes);
            return convertView;
        }
    }
}
*/
