package com.pichs.xhttp.impl.processor;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.pichs.xhttp.IHttpCallBack;
import com.pichs.xhttp.IHttpProcessor;
import com.pichs.xhttp.callback.FileCallBack;
import com.pichs.xhttp.impl.interceptor.DownloadInterceptor;
import com.pichs.xhttp.impl.interceptor.LogInterceptor;
import com.pichs.xhttp.impl.interceptor.RetryInterceptor;
import com.pichs.xhttp.exception.HttpError;
import com.pichs.xhttp.util.SslUtils;
import com.pichs.xhttp.HttpLog;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpProcessor implements IHttpProcessor {
    private static final String TAG = "OkHttpProcessor";
    private Handler mHandler;
    private OkHttpClient mRequestClient;
    private DownloadInterceptor mDownloadInterceptor;
    private String md5 = "d288d7aa87c6dc8ccd6e3e744d388508";

    public OkHttpProcessor() {
        mHandler = new Handler(Looper.getMainLooper());
        mDownloadInterceptor = new DownloadInterceptor();
        mRequestClient = getCommonClientBuilder()
                .connectTimeout(CONN_TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .addNetworkInterceptor(mDownloadInterceptor)
                .build();
    }

    private OkHttpClient.Builder getCommonClientBuilder() {
        return new OkHttpClient.Builder()
                .addInterceptor(new LogInterceptor())
                .addInterceptor(new RetryInterceptor())
                .sslSocketFactory(SslUtils.getSslSocketFactory(), SslUtils.getTrustManager(md5));
    }

    @Override
    public void cancelAll() {
        mRequestClient.dispatcher().cancelAll();
    }

    @Override
    public void get(String url, @Nullable Map<String, Object> params, final IHttpCallBack callBack) {
        get(url, null, params, callBack);
    }

    @Override
    public void get(String url, @Nullable Map<String, Object> headers, @Nullable Map<String, Object> params, final IHttpCallBack callBack) {
        Request request = new Request.Builder()
                .url(createGetUrl(url, params))
                .get()
                .headers(createHeaders(headers, ACCEPT_VALUE_JSON, CONTENT_TYPE_VALUE_JSON))
                .build();
        mRequestClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(new HttpError(e));
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                handleResponse(callBack, response);
            }
        });
    }

    @Override
    public void post(String url, @Nullable Map<String, Object> params, final IHttpCallBack callBack) {
        post(url, null, params, callBack);
    }

    @Override
    public void post(String url, @Nullable Map<String, Object> headers, @Nullable Map<String, Object> params, final IHttpCallBack callBack) {
        JSONObject jsonParams = (params == null || params.isEmpty()) ? new JSONObject() : new JSONObject(params);
        post(url, headers, jsonParams, callBack);
    }

    @Override
    public void post(String url, JSONObject params, final IHttpCallBack callBack) {
        post(url, null, params, callBack);
    }

    @Override
    public void post(String url, Map<String, Object> headers, JSONObject params, final IHttpCallBack callBack) {
        Request request = new Request.Builder()
                .url(url)
                .post(createRequestBody(params))
                .headers(createHeaders(headers, ACCEPT_VALUE_JSON, CONTENT_TYPE_VALUE_JSON))
                .build();
        mRequestClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(new HttpError(e));
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                handleResponse(callBack, response);
            }
        });
    }

    @Override
    public void upload(String url, String filePath, String fileName, final IHttpCallBack callBack) {
        Request request = new Request.Builder()
                .post(createMultipartBody(fileName, filePath))
                .headers(createHeaders(null, ACCEPT_VALUE_FORM, CONTENT_TYPE_VALUE_FORM))
                .build();
        mRequestClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(new HttpError(e));
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                handleResponse(callBack, response);
            }
        });
    }

    private void handleResponse(final IHttpCallBack callBack, final Response response) {
        if (response.isSuccessful()) {
            try {
                final String result = response.body().string();
                HttpLog.i(TAG, "==> code: " + response.code() + "==>url: " + response.request().url() + "\n==>response: " + result);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onSuccess(result);
                    }
                });
            } catch (final IOException e) {
                HttpLog.d("HTTP: okhttp3: ", e.toString());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(new HttpError(e));
                    }
                });
            }

        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callBack.onFailure(new HttpError(response.code(), response.message()));
                }
            });
        }
    }

    @Override
    public void download(String url, final String fileDestPath, final IHttpCallBack callBack) {
        download(url, null, null, fileDestPath, callBack);
    }

    @Override
    public void download(String url, @Nullable Map<String, Object> headers, @Nullable Map<String, Object> params, final String fileDestPath, IHttpCallBack callBack) {
        if (!(callBack instanceof FileCallBack)) {
            callBack.onFailure(new HttpError("Please use '#{@link FileCallBack}' as the IHttpCallBack listener"));
            return;
        }
        File file = new File(fileDestPath);
        if (file.exists()) {
            file.delete();
        }
        final FileCallBack fileCallBack = (FileCallBack) callBack;
        mDownloadInterceptor.setFileCallBack(fileCallBack);
        Request request = new Request.Builder()
                .url(createGetUrl(url, params))
                .headers(createHeaders(headers, "*/*", "*/*"))
                .get()
                .build();
        mRequestClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        fileCallBack.onFailure(new HttpError(e));
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            fileCallBack.onFailure(new HttpError(response.code(), response.message()));
                        }
                    });
                    return;
                }
                final File destFile = new File(fileDestPath);
                if (!destFile.exists()) {
                    destFile.getParentFile().mkdirs();
                    destFile.createNewFile();
                }
                final long length = response.body().contentLength();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        fileCallBack.onStart(length);
                    }
                });
                // 保存文件到本地
                InputStream is = null;
                FileOutputStream fos = null;
                BufferedInputStream bis = null;
                byte[] buff = new byte[8192];
                int len;
                try {
                    is = response.body().byteStream();
                    fos = new FileOutputStream(destFile);
                    bis = new BufferedInputStream(is);
                    while ((len = bis.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                    }
                    fos.flush();
                    // 下载完成
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            fileCallBack.onSuccess(destFile);
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            fileCallBack.onFailure(new HttpError(e));
                        }
                    });
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (bis != null) {
                            bis.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // 拼接get请求的url地址
    private String createGetUrl(String url, @Nullable Map<String, Object> params) {
        if (url == null) {
            return "";
        }
        if (params == null || params.size() == 0) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = encode(String.valueOf(entry.getValue()));
            if (sb.length() == 0) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(encode(key));
            sb.append("=");
            sb.append(value);
        }
        return url + sb.toString();
    }

    // Url 字符串不允许有空格等非法参数，需要转换
    private String encode(String value) {
        try {
            return URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            HttpLog.d("HTTP: okhttp3: ", e.toString());
            throw new RuntimeException(e);
        }
    }

    private Headers createHeaders(@Nullable Map<String, Object> headers, String acceptValue, String contentTypeValue) {
        Headers.Builder builder = new Headers.Builder();
        builder.add(ACCEPT, acceptValue)
                .add(CONTENT_TYPE, contentTypeValue)
                .add("User-Agent", "a");
        if (headers != null && headers.size() != 0) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                builder.removeAll(entry.getKey());
                builder.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        return builder.build();
    }

    private RequestBody createRequestBody(@Nullable JSONObject params) {
        if (params == null) {
            params = new JSONObject();
        }
        return RequestBody.create(MediaType.parse("application/json"), params.toString());
    }

    private MultipartBody createMultipartBody(String fileName, String filePath) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(fileName, filePath, RequestBody.create(MediaType.parse(CONTENT_TYPE_VALUE_FORM), new File(filePath)));
        return builder.build();
    }

    @Override
    public String postSync(String url, @Nullable Map<String, Object> headers, @Nullable JSONObject params) throws HttpError {
        Request request = new Request.Builder()
                .url(url)
                .post(createRequestBody(params))
                .headers(createHeaders(headers, ACCEPT_VALUE_JSON, CONTENT_TYPE_VALUE_JSON))
                .build();
        try {
            Response response = mRequestClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body() != null ? response.body().string() : null;
            } else {
                throw new HttpError(response.code(), response.message());
            }
        } catch (IOException e) {
            throw new HttpError(e);
        }
    }

    @Override
    public String getSync(String url, @Nullable Map<String, Object> headers) throws HttpError {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .headers(createHeaders(headers, ACCEPT_VALUE_JSON, CONTENT_TYPE_VALUE_JSON))
                .build();
        try {
            Response response = mRequestClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body() != null ? response.body().string() : null;
            } else {
                throw new HttpError(response.code(), response.message());
            }
        } catch (IOException e) {
            throw new HttpError(e);
        }
    }
}
