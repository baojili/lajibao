package com.ln.base.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.ln.base.tool.AndroidUtils;
import com.ln.base.tool.JsonUtils;
import com.ln.base.tool.Log;
import com.ln.base.tool.Md5Encrypt;
import com.ln.base.tool.StorageUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 接口缓存工具类
 */

public class HttpCacheWrapper {
    /**
     * 默认接口缓存大小
     */
    public static final long DEFAULT_CACHE_SIZE = 50 * 1024 * 1024;
    private static final Object SYNC_OBJECT = new Object();
    private static final int DEFAULT_VALUE_COUNT = 1;
    public static DiskLruCache mHttpLruCache;
    private static volatile HttpCacheWrapper INSTANCE;

    public static HttpCacheWrapper instance() {
        if (INSTANCE == null) {
            synchronized (SYNC_OBJECT) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpCacheWrapper();
                }
            }
        }
        return INSTANCE;
    }

    public static void clear() {
        try {
            if (mHttpLruCache == null) {
                Log.e("Http DiskLruCache 未初始化");
                return;
            }
            mHttpLruCache.delete();
            mHttpLruCache = null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("接口缓存清除失败");
        }
    }

    public void initDiskCache(Context context) {
        try {
            mHttpLruCache = DiskLruCache.open(StorageUtils.getCustomCacheDirectory(context, "httpCache"), AndroidUtils.getVersionCode(context, 0), DEFAULT_VALUE_COUNT, DEFAULT_CACHE_SIZE);
        } catch (IOException e) {
            mHttpLruCache = null;
            e.printStackTrace();
            Log.e("DiskLruCache接口缓存初始化失败");
        }
    }

    @SuppressLint("StaticFieldLeak")
    public <T extends BaseRsp> void put(String key, final T result) {
        if (mHttpLruCache == null) {
            Log.e("Http DiskLruCache 未初始化");
            return;
        }
        String cacheKey = Md5Encrypt.md5(key);
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                String cacheKeyInternal = params[0];
                DiskLruCache.Editor editor = null;
                try {
                    editor = mHttpLruCache.edit(cacheKeyInternal);
                    if (editor == null) {
                        Log.d("不能同时操作一个缓存editor");
                        return null;
                    }
                    editor.set(0, JsonUtils.gson().toJson(result));
                    editor.commit();
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        editor.abort();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    Log.e("接口缓存添加失败");
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cacheKey);
    }

    @SuppressLint("StaticFieldLeak")
    public <T extends BaseRsp> void get(final String key, final HttpCacheListener<T> httpCacheListener, final Class<T> clazz) {
        if (mHttpLruCache == null) {
            Log.e("Http DiskLruCache 未初始化");
            return;
        }
        String cacheKey = Md5Encrypt.md5(key);
        new AsyncTask<String, Void, T>() {
            @Override
            protected T doInBackground(String... params) {
                String cacheKeyInternal = params[0];
                String resultStr = null;
                try {
                    DiskLruCache.Value value = mHttpLruCache.get(cacheKeyInternal);
                    if (value != null) {
                        resultStr = value.getString(0);
                        T httpResult = JsonUtils.gson().toEntity(resultStr, clazz);
                        return httpResult;
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(T httpResult) {
                if (httpResult != null) {
                    Log.d("命中缓存， md5前cacheKey: " + key);
                    httpCacheListener.onRestore(httpResult);
                }
                super.onPostExecute(httpResult);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cacheKey);

    }

    private <T extends BaseRsp> Type getTypeOfTfromInterface(HttpCacheListener<T> original) {
        Type[] interfaces = original.getClass().getGenericInterfaces();
        Type type = null;
        if (interfaces[0] instanceof ParameterizedType) {
            type = ((ParameterizedType) interfaces[0]).getActualTypeArguments()[0];
        } else {
            throw new RuntimeException("the original should be a interface which has parameterized type");
        }
        return type;
    }

    public interface HttpCacheListener<T extends BaseRsp> {
        void onRestore(T result);
    }

}
