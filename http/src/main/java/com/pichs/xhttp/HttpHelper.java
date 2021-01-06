package com.pichs.xhttp;

import androidx.annotation.Nullable;

import com.pichs.xhttp.impl.processor.OkHttpProcessor;
import com.pichs.xhttp.exception.HttpError;

import org.json.JSONObject;

import java.util.Map;

public class HttpHelper implements IHttpProcessor {

    private static HttpHelper mInstance;

    private IHttpProcessor mHttpProcessor;

    public static HttpHelper obtain() {
        if (mInstance == null) {
            synchronized (HttpHelper.class) {
                if (mInstance == null) {
                    mInstance = new HttpHelper();
                }
            }
        }
        return mInstance;
    }

    public void setDebugEnable(boolean enable) {
        HttpLog.init(enable);
    }

    // 默认初始化
    public void init() {
        mHttpProcessor = new OkHttpProcessor();
    }

    // 传入实现接口的类的参数 初始化
    public void init(IHttpProcessor httpProcessor) {
        mHttpProcessor = httpProcessor;
    }

    public void cancelAll() {
        mHttpProcessor.cancelAll();
    }

    @Override
    public void get(String url, @Nullable Map<String, Object> params, IHttpCallBack callBack) {
        mHttpProcessor.get(url, params, callBack);
    }

    @Override
    public void get(String url, @Nullable Map<String, Object> headers, @Nullable Map<String, Object> params, IHttpCallBack callBack) {
        mHttpProcessor.get(url, headers, params, callBack);
    }

    @Override
    public void post(String url, @Nullable Map<String, Object> params, IHttpCallBack callBack) {
        mHttpProcessor.post(url, params, callBack);
    }

    @Override
    public void post(String url, @Nullable Map<String, Object> headers, @Nullable Map<String, Object> params, IHttpCallBack callBack) {
        mHttpProcessor.post(url, headers, params, callBack);
    }

    @Override
    public void post(String url, @Nullable JSONObject params, IHttpCallBack callBack) {
        mHttpProcessor.post(url, params, callBack);
    }

    @Override
    public void post(String url, @Nullable Map<String, Object> headers, @Nullable JSONObject params, IHttpCallBack callBack) {
        mHttpProcessor.post(url, headers, params, callBack);
    }

    @Override
    public void upload(String url, String filePath, String fileName, IHttpCallBack callBack) {
        mHttpProcessor.upload(url, filePath, fileName, callBack);
    }

    @Override
    public void download(String url, String fileDestPath, IHttpCallBack callBack) {
        mHttpProcessor.download(url, fileDestPath, callBack);
    }

    @Override
    public void download(String url, @Nullable Map<String, Object> headers, @Nullable Map<String, Object> params, String fileDestPath, IHttpCallBack callBack) {
        mHttpProcessor.download(url, headers, params, fileDestPath, callBack);
    }

    @Override
    public String postSync(String url, @Nullable Map<String, Object> headers, @Nullable JSONObject params) throws HttpError {
        return mHttpProcessor.postSync(url, headers, params);
    }

    @Override
    public String getSync(String url, @Nullable Map<String, Object> headers) throws HttpError {
        return mHttpProcessor.getSync(url, headers);
    }
}
