package com.ln.base.network;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ln.base.BuildConfig;
import com.ln.base.tool.JsonUtils;
import com.ln.base.tool.Log;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 接口请求发起类
 */

public class HttpRequest<T extends BaseRsp> {

    private static final Map<String, List<HttpRequest>> ACTIVITY_REQUEST_MAP =
            new ConcurrentHashMap<>();
    protected Context mContext;
    protected Call<String> call;
    private RequestEntity mRequestEntity;
    private RequestUiHandler mRequestUiHandler;
    private boolean isSuccess = false;
    private HttpCacheWrapper.HttpCacheListener httpCacheListener = null;

    protected HttpRequest(@NotNull Context context, @NonNull RequestEntity requestEntity, RequestUiHandler requestUiHandler) {
        this.mContext = context;
        this.mRequestEntity = requestEntity;
        this.mRequestUiHandler = requestUiHandler;
    }

    protected HttpRequest(@NotNull Context context, @NonNull RequestEntity requestEntity) {
        this(context, requestEntity, null);
    }

    /**
     * 取消所有关联该Context的请求，一般在该Context销毁时调用该方法
     *
     * @param context
     */
    public static void cancelRequests(@NotNull Context context) {
        List<HttpRequest> requestList = ACTIVITY_REQUEST_MAP.get(context.toString());
        if (requestList != null) {
            for (int i = 0; i < requestList.size(); i++) {
                HttpRequest httpRequest = requestList.get(i);
                if (httpRequest != null) {
                    httpRequest.cancelInternal();
                }
            }
            requestList.clear();
        }
    }

    /**
     * 查询与context关联的httpRequest请求是否已经在请求维护表中
     *
     * @param context
     * @param httpRequest
     */
    public static boolean isRequesting(@NotNull Context context, @NotNull HttpRequest httpRequest) {
        if (httpRequest == null) return false;
        List<HttpRequest> requestList = ACTIVITY_REQUEST_MAP.get(context.toString());
        if (requestList == null) {
            return false;
        }
        return requestList.contains(httpRequest);
    }

    /**
     * 从请求维护表中，删除该Context关联的该HttpRequest请求
     *
     * @param context
     * @param httpRequest
     */
    private static void removeRequest(@NotNull Context context, @NotNull HttpRequest httpRequest) {
        List<HttpRequest> requestList = ACTIVITY_REQUEST_MAP.get(context.toString());
        if (requestList != null) {
            requestList.remove(httpRequest);
        }
    }

    /**
     * 从请求维护表中，删除所有关联该Context的请求
     */
    private static void removeRequests(@NotNull Context context) {
        ACTIVITY_REQUEST_MAP.remove(context.toString());
    }

    /**
     * 添加一个非持续类型的HttpRequest对象到请求维护表中，当该请求处理完毕或者{@link #cancel()} / {@link #cancelInternal()}被调用时，务必从请求维护表中清除
     */
    private static void addRequest(@NotNull Context context, @NotNull HttpRequest httpRequest) {
        List<HttpRequest> requestList = ACTIVITY_REQUEST_MAP.get(context.toString());
        if (requestList == null) {
            requestList = new CopyOnWriteArrayList<>();
            ACTIVITY_REQUEST_MAP.put(context.toString(), requestList);
        }
        requestList.add(httpRequest);
    }

    /**
     * common GET method request
     */
    public void get() {
        if (!beforeDefineCall()) {
            return;
        }
        if (mRequestEntity.getParams() == null) {
            call = RetrofitWrapper.getInstance().getService().commonGet(mRequestEntity.getApiPath());
        } else if (mRequestEntity.getParams() instanceof Map) {
            call = RetrofitWrapper.getInstance().getService().commonGet(mRequestEntity.getApiPath(), (Map<String, Object>) mRequestEntity.getParams());
        } else {
            call = RetrofitWrapper.getInstance().getService().commonGet(mRequestEntity.getApiPath(), JsonUtils.gson().toJson(mRequestEntity.getParams()));
        }
        if (!afterDefineCall()) {
            return;
        }
        executeInternal(call);
    }

    /**
     * common POST method request
     */
    public void post() {
        if (!beforeDefineCall()) {
            return;
        }
        Log.d(JsonUtils.gson().toJson(mRequestEntity.getParams()));
        if (mRequestEntity.getParams() == null) {
            call = RetrofitWrapper.getInstance().getService().commonPost(mRequestEntity.getApiPath());
        } else {
            call = RetrofitWrapper.getInstance().getService().commonPost(mRequestEntity.getApiPath(), mRequestEntity.getParams());
        }
        if (!afterDefineCall()) {
            return;
        }
        executeInternal(call);
    }

    /**
     * upload one file POST method request
     */
    public void uploadOneFilePost() {
        if (mRequestEntity.getFilesList() == null || mRequestEntity.getFilesList().size() == 0 || !((mRequestEntity.getParams() instanceof Map))) {
            throw new IllegalArgumentException("parameters illegal");
        }
        if (!beforeDefineCall()) {
            return;
        }
        Log.d(JsonUtils.gson().toJson(mRequestEntity.getParams()));
        Log.d(mRequestEntity.getFilesList().get(0).getName());
        RequestBody reqFile = RequestBody.create(MediaType.parse("file/*"), mRequestEntity.getFilesList().get(0));
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData("file", mRequestEntity.getFilesList().get(0).getName(), reqFile);
        String description = "file";
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("multipart/form-data"), description);
        if (mRequestEntity.getParams() == null) {
            call = RetrofitWrapper.getInstance().getService().uploadOneFile(mRequestEntity.getApiPath(), descriptionBody, fileBody);
        } else {
            call = RetrofitWrapper.getInstance().getService().uploadOneFile(mRequestEntity.getApiPath(), (Map<String, Object>) mRequestEntity.getParams(), descriptionBody, fileBody);
        }
        if (!afterDefineCall()) {
            return;
        }
        executeInternal(call);
    }

    private boolean beforeDefineCall() {
        isSuccess = false;
        if (mRequestEntity == null) {
            Log.e("RequestEntity cannot be null");
            return false;
        }
        if (mRequestUiHandler != null) {
            mRequestUiHandler.onStart(mRequestEntity.getHintMsg().getMsg());
        }
        return true;
    }

    private boolean afterDefineCall() {
        if (!mRequestEntity.isPersistent()) {
            addRequest(mContext, this);
        }
        if (mRequestEntity.isShowCacheFirst() || mRequestEntity.isShowCacheOnFail()) {
            httpCacheListener = new HttpCacheWrapper.HttpCacheListener<T>() {

                @Override
                public void onRestore(T result) {
                    HttpRequest.this.onRestore(result);
                }
            };
            if (mRequestEntity.isShowCacheFirst()) {
                fetchResultFromCache(call.request().url().toString() + mRequestEntity.getExtraCacheKey(), httpCacheListener, getClazzOfT(this));
            }
        }
        return true;
    }

    protected void executeInternal(@NotNull Call<String> call) {
        RetrofitWrapper.getInstance().execute(call, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                handRetrofitOnResponse(call, response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                handRetrofitOnFailure(call, t);
            }
        });
    }

    /**
     * retrofit execute
     *
     * @param requestCall
     * @param requestEntity
     * @param requestUiHandler
     */
    public void execute(@NotNull Call<T> requestCall, @NotNull RequestEntity requestEntity, RequestUiHandler requestUiHandler) {
        isSuccess = false;
        mRequestEntity = requestEntity;
        mRequestUiHandler = requestUiHandler;
        if (!beforeDefineCall()) {
            return;
        }
        if (!afterDefineCall()) {
            return;
        }
        RetrofitWrapper.getInstance().execute(requestCall, new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (call.isCanceled()) {
                    return;
                }
                if (response.isSuccessful()) {
                    T result = response.body();
                    onSuccess(result);
                    if (mRequestEntity.isShouldCache()) {
                        putResultToCache(call.request().url().toString() + mRequestEntity.getExtraCacheKey(), result);
                    }
                } else {
                    handRetrofitOnResponseIsNotSuccessful(response);
                }
                removeFinishedRequest();
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                handRetrofitOnFailure(call, t);
            }
        });
    }

    /**
     * handle response when retrofit onResponse() called
     *
     * @param call
     * @param response
     */
    private void handRetrofitOnResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
        if (call.isCanceled()) {
            return;
        }
        if (response.isSuccessful()) {
            handRetrofitOnResponseIsSuccessful(call, response);
        } else {
            handRetrofitOnResponseIsNotSuccessful(response);
        }
        removeFinishedRequest();
    }

    /**
     * handle the successful case when retrofit onResponse() called
     *
     * @param response
     */
    protected void handRetrofitOnResponseIsSuccessful(@NotNull Call<String> call, @NotNull Response<String> response) {
        Log.d(response.body());
        T objectTresult = JsonUtils.gson().toEntity(response.body(), getTypeOfTfromSupperclass(this));
        if (objectTresult != null && objectTresult.isSuccess()) {
            onSuccess(objectTresult);
            if (mRequestEntity.isShouldCache()) {
                putResultToCache(call.request().url().toString() + mRequestEntity.getExtraCacheKey(), objectTresult);
            }
        } else {
            if (objectTresult == null) {
                BaseRsp result = new BaseRsp(CodeMsg.PARSE_FAILURE);
                onFail(result);
            } else {
                onFail(objectTresult);
            }
        }
    }

    /**
     * handle the not successful case when retrofit onResponse() called
     *
     * @param response
     */
    protected void handRetrofitOnResponseIsNotSuccessful(@NotNull Response response) {
        if (response.code() == 401 || response.code() == 403) { //授权异常
            BaseRsp result = new BaseRsp(CodeMsg.AUTH_FAILURE);
            onFail(result);
        } else {
            BaseRsp result = new BaseRsp(CodeMsg.SERVER_ANOMALY);
            onFail(result);
        }
    }

    /**
     * handle the failure when retrofit onFailure() called
     *
     * @param call
     * @param t
     */
    private void handRetrofitOnFailure(@NotNull Call call, @NotNull Throwable t) {
        if (call.isCanceled()) {
            return;
        }
        BaseRsp result = new BaseRsp();
        if (t instanceof SocketTimeoutException) {
            result.setCodeMsg(CodeMsg.NETWORK_UNSTABLE);
        } else if (t instanceof ConnectException) {
            result.setCodeMsg(CodeMsg.NETWORK_UNAVAILABLE);
        } else {
            result.setCodeMsg(CodeMsg.REQUEST_ANOMALY);
        }
        onFail(result);
        removeFinishedRequest();
        if (BuildConfig.DEBUG) {
            Log.e(t.getMessage());
            t.printStackTrace();
        }
    }

    /**
     * the default request error process logic
     *
     * @param result
     */
    protected void performRequestErrorByDefault(@NotNull BaseRsp result) {
        if (mContext instanceof RequestUiHandler) {
            ((RequestUiHandler) mContext).onError(result.getCode(), result.getMsg());
        } else if (mContext instanceof Activity) {
            Toast.makeText(mContext, result.getMsg(), Toast.LENGTH_LONG).show();
        } else {
            Log.w(result.getMsg());
        }
    }

    /**
     * 请求成功回调，与后台API确定errorCode为成功值时回调
     *
     * @param result
     */
    protected void onSuccess(@NotNull T result) {
        isSuccess = true;
        fillResultToUiHandler(result);
    }

    /**
     * 请求失败回调，除{@link #onSuccess(BaseRsp)}情况外都回调
     *
     * @param result
     */
    protected void onFail(@NotNull BaseRsp result) {
        if (mRequestUiHandler != null && result != null) {
            mRequestUiHandler.onError(result.getCode(), result.getMsg());
        } else {
            performRequestErrorByDefault(result);
        }
        //如果已经优先显示了缓存数据，则不再重新调用缓存
        if (call != null && !mRequestEntity.isShowCacheFirst() && mRequestEntity.isShowCacheOnFail()) {
            fetchResultFromCache(call.request().url().toString() + mRequestEntity.getExtraCacheKey(), httpCacheListener, getClazzOfT(this));
        }
    }

    private void fillResultToUiHandler(@NotNull T result) {
        if (mRequestUiHandler != null) {
            mRequestUiHandler.onSuccess();
            if (mRequestUiHandler instanceof ListViewUiHandler && result instanceof PageRspInterface) {
                ((ListViewUiHandler) mRequestUiHandler).onRspSuccess((PageRspInterface<?>) result, mRequestEntity.getPageNum(), mRequestEntity.getPageSize());
            }
        }
    }

    /**
     * called when result is restored from the cache data of last request
     *
     * @param result
     */
    protected void onRestore(@NotNull T result) {
    }

    /**
     * 取消该请求，并从请求维护表中删除
     */
    public void cancel() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
            removeRequest(mContext, this);
            onCanceled();
        }
    }

    /**
     * 请求被取消时回调
     */
    protected void onCanceled() {
        Log.d("接口请求已取消(并非绝对) - " + mRequestEntity.getApiPath() + mRequestEntity.getExtraCacheKey());
    }

    /**
     * 查询请求是否成功，当该方法返回true后，该方法只有再次调用{@link #get()} / {@link #post()} / {@link #execute(Call, RequestEntity, RequestUiHandler)}时才会被返回false
     *
     * @return
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    public RequestEntity getRequestEntity() {
        return mRequestEntity;
    }

    /**
     * 将请求返回结果保存到本地缓存中
     *
     * @param key
     * @param result
     */
    private void putResultToCache(@NotNull String key, @NotNull T result) {
        HttpCacheWrapper.instance().put(key, result);
    }

    /**
     * 获取请求的保存在本地缓存的数据
     *
     * @param key
     * @param listener
     */
    private void fetchResultFromCache(@NotNull String key, @NotNull HttpCacheWrapper.HttpCacheListener<T> listener, @NotNull Class<T> clazz) {
        HttpCacheWrapper.instance().get(key, listener, clazz);
    }

    /**
     * 取消该请求，但未从请求维护表中删除，供内部调用
     */
    private void cancelInternal() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
            onCanceled();
        }
    }

    /**
     * 请求完成后一定要回调，无论成功或者失败返回，处理完毕即时回调
     */
    private void removeFinishedRequest() {
        removeRequest(mContext, this);
    }

    private Type getTypeOfTfromSupperclass(@NotNull HttpRequest<T> original) {
        Type superClazz = original.getClass().getGenericSuperclass();
        Type type = null;
        if (superClazz instanceof ParameterizedType) {
            type = ((ParameterizedType) superClazz).getActualTypeArguments()[0];
        } else {
            throw new RuntimeException("the original should be a child class of some class which has parameterized type");
        }
        return type;
    }

    private Class<T> getClazzOfT(@NotNull HttpRequest<T> original) {
        return (Class<T>) JsonUtils.getRawType(getTypeOfTfromSupperclass(original));
    }
}
