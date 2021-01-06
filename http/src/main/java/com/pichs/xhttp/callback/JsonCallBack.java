package com.pichs.xhttp.callback;

import com.pichs.xhttp.IHttpCallBack;
import com.pichs.xhttp.exception.HttpError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author chuanyong.zhu
 */
public abstract class JsonCallBack implements IHttpCallBack {

    @Override
    public void onSuccess(String result) {
        JSONObject response;
        try {
            response = new JSONObject(result);
        } catch (JSONException e) {
            onFailure(new HttpError(e));
            return;
        }
        onSuccess(response);
    }

    /**
     * 返回JSONObject类型结果
     *
     * @param response JSONObject response
     */
    public abstract void onSuccess(JSONObject response);
}
