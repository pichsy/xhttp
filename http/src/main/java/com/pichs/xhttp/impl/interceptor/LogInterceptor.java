package com.pichs.xhttp.impl.interceptor;

import com.pichs.xhttp.HttpLog;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class LogInterceptor implements Interceptor {
    private static final String TAG = "HttpInterceptor$Log$";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpLog.i(TAG, "request: url-->" + request.url());
        HttpLog.i(TAG, "request: headers-->" + request.headers());
        HttpLog.i(TAG, "request: body--> " + request.body());
        Response response = chain.proceed(request);
        HttpLog.i(TAG, "response: message-->" + response.message());
        return response;
    }
}
