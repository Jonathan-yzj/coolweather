package com.example.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HTTPUtil {

    /*与服务器交互代码，现在发起一条HTTP请求只需调用sendOkHttpRequert方法，传入请求地址，并注册一个回调来处理服务器响应就可以。*/
    public static void sendOkHttpRequert(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
