package com.pichs.xhttp.callback;

import com.pichs.xhttp.IHttpCallBack;

import java.io.File;

public abstract class FileCallBack implements IHttpCallBack {

    public abstract void onStart(long max);

    public abstract void onProgress(int progress);

    public abstract void onSuccess(File file);

    @Override
    public void onSuccess(String result) {
    }

}
