package cn.com.test.http;

import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONObject;

public interface HttpListener<T> {
    void onSucceed(int what, JSONObject jsonObject);

    void onFailed(int what, Response<T> response);
}
