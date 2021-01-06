package com.pichs.xhttp;

import androidx.annotation.Nullable;

import com.pichs.xhttp.exception.HttpError;

import org.json.JSONObject;

import java.util.Map;

public interface IHttpProcessor {

    int CONN_TIME_OUT_SECONDS = 25;
    int READ_TIME_OUT_SECONDS = 25;

    String AUTHORIZATION = "Authorization";
    String SECRET = "Secret";
    String ACCEPT = "Accept";
    String CONTENT_TYPE = "Content-Type";
    String ACCEPT_VALUE_JSON = "application/json";
    String ACCEPT_VALUE_FORM = "text/html,application/json,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    String CONTENT_TYPE_VALUE_JSON = "application/json; charset=UTF-8";
    String CONTENT_TYPE_VALUE_FORM = "multipart/form-data";

    void cancelAll();

    void get(String url, @Nullable Map<String, Object> params, IHttpCallBack callBack);

    void get(String url, @Nullable Map<String, Object> headers, @Nullable Map<String, Object> params, IHttpCallBack callBack);

    void post(String url, @Nullable Map<String, Object> params, IHttpCallBack callBack);

    void post(String url, @Nullable Map<String, Object> headers, @Nullable Map<String, Object> params, IHttpCallBack callBack);

    void post(String url, @Nullable JSONObject params, IHttpCallBack callBack);

    void post(String url, @Nullable Map<String, Object> headers, @Nullable JSONObject params, IHttpCallBack callBack);

    void upload(String url, String filePath, String fileName, IHttpCallBack callBack);

    void download(String url, String fileDestPath, IHttpCallBack callBack);

    void download(String url, @Nullable Map<String, Object> headers, @Nullable Map<String, Object> params, String fileDestPath, IHttpCallBack callBack);

    String postSync(String url, @Nullable Map<String, Object> headers, @Nullable JSONObject params) throws HttpError;

    String getSync(String url, @Nullable Map<String, Object> headers) throws HttpError;
}
