package com.pichs.xhttp.impl.response;

import android.os.Handler;
import android.os.Looper;

import com.pichs.xhttp.callback.FileCallBack;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

public class DownloadResponseBody extends ResponseBody {
    private Response originalResponse;
    private FileCallBack fileCallBack;
    private Handler mHandler;

    public DownloadResponseBody(Response originalResponse, FileCallBack callBack) {
        mHandler = new Handler(Looper.getMainLooper());
        this.originalResponse = originalResponse;
        this.fileCallBack = callBack;
    }

    @Override
    public MediaType contentType() {
        return originalResponse.body().contentType();
    }

    @Override
    public long contentLength() {
        return originalResponse.body().contentLength();
    }

    @Override
    public BufferedSource source() {
        return Okio.buffer(new ForwardingSource(originalResponse.body().source()) {
            private long bytesReaded = 0;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                bytesReaded += bytesRead == -1 ? 0 : bytesRead;
                if (fileCallBack != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            fileCallBack.onProgress((int) ((bytesReaded * 100) / contentLength()));
                        }
                    });
                }
                return bytesRead;
            }
        });
    }
}
