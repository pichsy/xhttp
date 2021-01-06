package com.pichs.xhttp;


import com.pichs.xhttp.exception.HttpError;

public interface IHttpCallBack {

    void onSuccess(String result);

    void onFailure(HttpError error);

}
