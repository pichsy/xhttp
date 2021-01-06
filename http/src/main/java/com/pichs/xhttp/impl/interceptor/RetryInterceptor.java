package com.pichs.xhttp.impl.interceptor;

import android.os.SystemClock;


import com.pichs.xhttp.HttpLog;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {

    private static final String TAG = "OkHttpProcessor";

    private int maxRetry = 3;//最大重试次数
    // 延迟 暂时用不到
    private long delay = 0;

    public RetryInterceptor() {
    }

    public RetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public RetryInterceptor(int maxRetry, long delay) {
        this.maxRetry = maxRetry;
        this.delay = delay;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        RetryWrapper retryWrapper = proceed(chain);
        while (retryWrapper.isNeedReTry()) {
            retryWrapper.retryNum++;
            HttpLog.d(TAG, retryWrapper.request.url().toString() + " -->>>> retry times: " + retryWrapper.retryNum);
            HttpLog.d(TAG, "重试机制： retryCount: -->>>>  " + retryWrapper.retryNum);
            if (delay > 0) {
                SystemClock.sleep(delay);
            }
            proceed(chain, retryWrapper.request, retryWrapper);
        }
        return retryWrapper.response == null ? chain.proceed(chain.request()) : retryWrapper.response;
    }

    private RetryWrapper proceed(Chain chain) throws IOException {
        Request request = chain.request();
        RetryWrapper retryWrapper = new RetryWrapper(request, maxRetry);
        proceed(chain, request, retryWrapper);
        return retryWrapper;
    }

    private void proceed(Chain chain, Request request, RetryWrapper retryWrapper) throws IOException {
        try {
            Response response = chain.proceed(request);
            retryWrapper.setResponse(response);
        } catch (SocketException | SocketTimeoutException e) {
            e.printStackTrace();
        }
    }

    static class RetryWrapper {
        //假如设置为3次重试的话，则最大可能请求5次（默认1次+3次重试 + 最后一次默认）
        Request request;
        volatile int retryNum = 0;
        Response response;
        private int maxRetry;

        public RetryWrapper(Request request, int maxRetry) {
            this.request = request;
            this.maxRetry = maxRetry;
        }

        public void setResponse(Response response) {
            this.response = response;
        }

        Response response() {
            return this.response;
        }

        Request request() {
            return this.request;
        }

        public boolean isSuccessful() {
            return response != null && response.isSuccessful();
        }

        public boolean isNeedReTry() {
            return !isSuccessful() && retryNum < maxRetry;
        }

        public void setRetryNum(int retryNum) {
            this.retryNum = retryNum;
        }

        public void setMaxRetry(int maxRetry) {
            this.maxRetry = maxRetry;
        }
    }
}
