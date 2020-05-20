package com.ln.base.network;


import com.ln.base.tool.JsonUtils;
import com.ln.base.tool.Log;

import java.io.IOException;
import java.util.Map;

import okhttp3.EventListener;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitWrapper {
    private static final Object SYNC_OBJECT = new Object();
    private static volatile RetrofitWrapper INSTANCE;
    private Retrofit retrofit = null;
    private APIService apiService;

    protected RetrofitWrapper() {
    }

    public static RetrofitWrapper getInstance() {
        if (INSTANCE == null) {
            synchronized (SYNC_OBJECT) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitWrapper();
                }
            }
        }
        return INSTANCE;
    }

    public APIService getService() {
        if (apiService == null && retrofit != null) {
            apiService = retrofit.create(APIService.class);
        }
        return apiService;
    }

    public void init(String apiHost, Interceptor... interceptors) {
        init(apiHost, null, null, interceptors);
    }

    public void init(String apiHost, EventListener eventListener, Interceptor... interceptors) {
        init(apiHost, null, eventListener, interceptors);
    }

    public void init(String apiHost, Map<String, String> headerParams, Interceptor... interceptors) {
        init(apiHost, headerParams, null, interceptors);
    }

    public void init(String apiHost, Map<String, String> headerParams, EventListener eventListener, Interceptor... interceptors) {
        if (apiHost.startsWith("https://")) {
            OkHttpClientWrapper.getInstance().initOKHttpForHttps(headerParams, eventListener, interceptors);
        } else {
            OkHttpClientWrapper.getInstance().initOkHttp(headerParams, eventListener, interceptors);
        }
        initRetrofit(apiHost);
        apiService = retrofit.create(APIService.class);
    }

    private boolean initRetrofit(String apiHost) {
        if (OkHttpClientWrapper.getInstance().getOkHttpClient() == null) return false;
        retrofit = new Retrofit.Builder()
                .baseUrl(apiHost)
                .client(OkHttpClientWrapper.getInstance().getOkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(JacksonConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(JsonUtils.gson().getGson()))
                .build();
        return true;
    }

    public <T> void execute(Call<T> resultCall, Callback<T> callback) {
        resultCall.enqueue(callback);
    }

    public String getServerUrl() {
        if (retrofit == null) {
            return "null";
        }
        return retrofit.baseUrl().url().toString();
    }


    private String bodyToString(final okhttp3.Request request) {
        try {
            final okhttp3.Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            RequestBody requestBody = copy.body();
            if (requestBody != null) {
                requestBody.writeTo(buffer);
            }
            return buffer.readUtf8();
        } catch (IOException e) {
            return "did not work";
        }
    }

    public void release() {
        OkHttpClientWrapper.getInstance().release();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public boolean updateOkHttpClient(OkHttpClient okHttpClient) {
        if (okHttpClient == null) return false;
        if (retrofit == null) return false;
        retrofit = retrofit.newBuilder().client(okHttpClient).build();
        apiService = retrofit.create(APIService.class);
        Log.e("updateOkHttpClient");
        return true;
    }
}
