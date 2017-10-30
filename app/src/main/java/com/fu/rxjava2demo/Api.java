package com.fu.rxjava2demo;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;

/**
 * Created by Fu.
 * QQ:908323236
 * 2017/10/30 15:37
 */

public interface Api {

    @GET("user")
    Observable<User> getUser();   //与Rxjava2结合的话这里就要返回Observable

    @GET("login")
    Observable<ResponseBody> login();

    @GET("register")
    Observable<ResponseBody> register();

    @GET("user_base_info")
    Observable<ResponseBody> getUserBaseInfo();

    @GET("user_extra_info")
    Observable<ResponseBody> getUserExtraInfo();
}
