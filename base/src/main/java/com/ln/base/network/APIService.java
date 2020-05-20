package com.ln.base.network;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * 接口请求的方法类，所有的接口请求方法都写在这里
 */

public interface APIService {

    @POST("{apiURL}")
    Call<String> commonPost(@Path(value = "apiURL", encoded = true) String apiURL);

    @POST("{apiURL}")
    Call<String> commonPost(@Path(value = "apiURL", encoded = true) String apiURL, @Body String dataBody);

    @POST("{apiURL}")
    Call<String> commonPost(@Path(value = "apiURL", encoded = true) String apiURL, @Body Object dataBody);

    @GET("{apiURL}")
    Call<String> commonGet(@Path(value = "apiURL", encoded = true) String apiURL);

    @GET("{apiURL}")
    Call<String> commonGet(@Path(value = "apiURL", encoded = true) String apiURL, @Query("data") String data);

    @GET("{apiURL}")
    Call<String> commonGet(@Path(value = "apiURL", encoded = true) String apiURL, @QueryMap Map<String, Object> params);

    @Multipart
    @POST("{apiURL}")
    Call<String> uploadOneFile(@Path(value = "apiURL", encoded = true) String apiURL, @QueryMap Map<String, Object> data, @Part("pic") RequestBody description, @Part MultipartBody.Part file);

    @Multipart
    @POST("{apiURL}")
    Call<String> uploadOneFile(@Path(value = "apiURL", encoded = true) String apiURL, @Part("file") RequestBody description, @Part MultipartBody.Part file);
}
