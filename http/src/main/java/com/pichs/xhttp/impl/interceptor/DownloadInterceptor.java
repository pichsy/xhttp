package com.pichs.xhttp.impl.interceptor;

import com.pichs.xhttp.callback.FileCallBack;
import com.pichs.xhttp.impl.response.DownloadResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class DownloadInterceptor implements Interceptor {

    private FileCallBack mFileCallBack;

    public void setFileCallBack(FileCallBack fileCallBack) {
        this.mFileCallBack = fileCallBack;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        if (mFileCallBack == null) return originalResponse;
        return originalResponse.newBuilder()
                .body(new DownloadResponseBody(originalResponse, mFileCallBack))
                .build();
    }
}
