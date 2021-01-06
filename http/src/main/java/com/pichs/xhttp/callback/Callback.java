package com.pichs.xhttp.callback;

import com.pichs.xhttp.IHttpCallBack;
import com.pichs.xhttp.exception.HttpError;
import com.pichs.xhttp.util.GenericUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public abstract class Callback<T> implements IHttpCallBack {

    public abstract void onSuccess(T t);

    @Override
    public void onSuccess(String result) {
        try {
            T t = new Gson().fromJson(result, GenericUtils.getSuperClassGenericType(this.getClass()));
            onSuccess(t);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            onFailure(new HttpError(e));
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            onFailure(new HttpError(e));
        } catch (ClassCastException e) {
            e.printStackTrace();
            onFailure(new HttpError(e));
        }
    }

}
