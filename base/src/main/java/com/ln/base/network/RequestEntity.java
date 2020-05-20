package com.ln.base.network;

import android.text.TextUtils;

import com.ln.base.model.JsonInterface;
import com.ln.base.tool.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求信息类，包含了请求的一些配置，和请求参数类
 */

public class RequestEntity {

    /**
     * 接口是否需要缓存
     */
    private boolean isShouldCache;

    /**
     * 请求开始的提示信息
     */
    private Tips mHintTips = Tips.REQUEST;

    /**
     * 请求发出的参数对象
     */
    private Object mParams;

    /**
     * 接口路径
     */
    private String mApiPath;

    /**
     * 临时Map，当接口请求参数对象里面传递Map对象时，此map作为引用，可以修改接口map中的请求数据，辅助RequestEntity对象添加put方法
     */
    private HashMap<String, String> mParamsMap;


    /**
     * 待上传的文件列表
     */
    private List<File> mFilesList;

    /**
     * 辅助缓存key，拼接在原始key后面
     */
    private String extraCacheKey;

    /**
     * 是否优先显示缓存数据，再加载网络数据, 此参数设置为ture， isShouldCache同时设置为ture，且不能改为false
     */
    private boolean isShowCacheFirst;

    /**
     * 是否当请求失败时显示缓存数据(如已经优先显示缓存数据，则失败时不在重复显示), 此参数设置为ture， isShouldCache同时设置为ture，且不能改为false
     */
    private boolean isShowCacheOnFail;

    private Builder mBuilder;

    /**
     * 页面onDestory之后，请求未完成时，是否持续
     */
    private boolean isPersistent;

    public RequestEntity(Builder builder) {
        if (builder != null) {
            mBuilder = builder;
            isShouldCache = builder.isShouldCache;
            mHintTips = builder.mHintTips;
            mParams = builder.mParams;
            mApiPath = builder.mApiPath;
            mParamsMap = builder.mParamsMap;
            mFilesList = builder.mFilesList;
            isShowCacheFirst = builder.isShowCacheFirst;
            isShowCacheOnFail = builder.isShowCacheOnFail;
            extraCacheKey = builder.extraCacheKey;
            isPersistent = builder.isPersistent;
        }
    }

    public boolean isPersistent() {
        return isPersistent;
    }

    public boolean isShouldCache() {
        return isShouldCache;
    }

    public boolean isShowCacheFirst() {
        return isShowCacheFirst;
    }

    public boolean isShowCacheOnFail() {
        return isShowCacheOnFail;
    }

    public String getExtraCacheKey() {
        if (TextUtils.isEmpty(extraCacheKey)) {
            extraCacheKey = "";
        }
        return extraCacheKey;
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    public int getPageSize() {
        if (mParamsMap != null) {
            return StringUtils.toInt(mParamsMap.get(Config.PAGE_SIZE));
        }
        if (mParams instanceof PageReq) {
            return ((Page) ((PageReq) mParams).getData()).getPageSize();
        }
        return 0;
    }

    public int getPageNum() {
        if (mParamsMap != null) {
            return StringUtils.toInt(mParamsMap.get(Config.PAGE_NUM));
        }
        if (mParams instanceof PageReq) {
            return ((Page) ((PageReq) mParams).getData()).getPageNum();
        }
        return 0;
    }

    public Object getParam(String param) {
        if (mParamsMap != null) {
            return mParamsMap.get(param);
        }
        return null;
    }

    public Tips getHintMsg() {
        return mHintTips;
    }

    public Object getParams() {
        return mParams;
    }

    public List<File> getFilesList() {
        return mFilesList;
    }

    public String getApiPath() {
        return mApiPath;
    }


    public static class Builder {

        private boolean isShouldCache;

        private Tips mHintTips = Tips.REQUEST;

        private Object mParams;

        private String mApiPath;

        private HashMap mParamsMap;

        private List<File> mFilesList;

        private String extraCacheKey;

        private boolean isShowCacheFirst;

        private boolean isShowCacheOnFail;

        private boolean isPersistent;

        public Builder() {
        }

        public Builder(String apiPath) {
            this.mApiPath = apiPath;
        }

        public Builder setApiPath(String apiPath) {
            this.mApiPath = apiPath;
            return this;
        }

        public Builder isShouldCache(boolean shouldCache) {
            if (isShowCacheFirst) {
                isShouldCache = true;
            } else if (isShowCacheOnFail) {
                isShouldCache = true;
            } else {
                isShouldCache = shouldCache;
            }
            return this;
        }

        public Builder addParams(String jsonData) {
            mParamsMap = null;
            mParams = jsonData;
            return this;
        }

        public Builder addParams(String key, Object value) {
            if (mParamsMap == null) {
                mParamsMap = new HashMap();
            }
            mParamsMap.put(key, value);
            mParams = mParamsMap;
            return this;
        }

        public Builder addParams(Map<String, Object> data) {
            if (mParamsMap == null) {
                mParamsMap = new HashMap();
            }
            mParamsMap.putAll(data);
            mParams = mParamsMap;
            return this;
        }

        public Builder addParams(JsonInterface data) {
            mParamsMap = null;
            mParams = data;
            return this;
        }

        public Builder addParams(List data) {
            mParamsMap = null;
            mParams = data;
            return this;
        }

        public Builder addFiles(List<File> filesList) {
            if (mFilesList == null) {
                mFilesList = new ArrayList<>();
            }
            mFilesList.addAll(filesList);
            return this;
        }

        public Builder addFiles(File file) {
            if (mFilesList == null) {
                mFilesList = new ArrayList<>();
            }
            mFilesList.add(file);
            return this;
        }

        public Builder hintTips(Tips hintTips) {
            mHintTips = hintTips;
            return this;
        }

        public Builder extraCacheKey(String cacheKey) {
            extraCacheKey = cacheKey;
            return this;
        }

        public Builder showCacheFirst(boolean isShowCacheFirst) {
            this.isShowCacheFirst = isShowCacheFirst;
            isShouldCache = true;
            return this;
        }

        public Builder showCacheOnFail(boolean isShowCacheOnFail) {
            this.isShowCacheOnFail = isShowCacheOnFail;
            isShouldCache = true;
            return this;
        }

        public Builder isPersistent(boolean persistent) {
            isPersistent = persistent;
            return this;
        }

        public RequestEntity build() {
            return new RequestEntity(this);
        }
    }
}
