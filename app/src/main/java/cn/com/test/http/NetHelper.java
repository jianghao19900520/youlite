package cn.com.test.http;

import android.content.Context;
import android.text.TextUtils;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.com.test.constant.Constant;
import cn.com.test.utils.LogUtils;
import cn.com.test.utils.SPUtils;

public class NetHelper {

    static private NetHelper netHelper;
    private Object object = new Object();
    private RequestQueue mQueue;

    private NetHelper() {
        mQueue = NoHttp.newRequestQueue();
    }

    public static synchronized NetHelper getInstance() {
        if (null == netHelper) {
            netHelper = new NetHelper();
        }
        return netHelper;
    }

    /**
     * 接口请求
     */
    public void request(Context context, String relativeUrl, JSONObject params, RequestMethod method, BaseCallBack callBack) {
        try {
            Request<JSONObject> request = buildRequest(relativeUrl, params, method);
            request.setCancelSign(object);
            mQueue.add(callBack.what, request, new HttpResponseListener<JSONObject>(context, callBack, callBack.msg));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 构建请求数据
     *
     * @param relativeUrl 相对路径
     * @param params      参数
     * @param method      请求方法
     * @return Request对象
     */
    private Request<JSONObject> buildRequest(String relativeUrl, JSONObject params, RequestMethod method) throws JSONException {
        String url = getAbsoluteUrl(relativeUrl);
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(url, method);
        request.setAccept(Headers.HEAD_VALUE_CONTENT_TYPE_JSON);
        request.setContentType(Headers.HEAD_VALUE_CONTENT_TYPE_JSON);
        if (null != params && params.length() > 0) {
            if (method == RequestMethod.GET) {
                if (!url.endsWith("?")) {
                    url += "?";
                }
                Iterator<String> keys = params.keys();
                Map<String, Object> map = new HashMap<>();
                while (keys.hasNext()) {
                    String key = keys.next();
                    map.put(key, params.get(key));
                    url += key + "=" + params.get(key) + "&";
                }
                url = url.substring(0, url.length() - 1);
                LogUtils.i("get url------->        ", url);
                request.set(map);
            } else if (method == RequestMethod.POST || method == RequestMethod.PUT || method == RequestMethod.DELETE) {
                LogUtils.i("post url------->", url);
                if (params.has("noKey")) {
                    request.setDefineRequestBodyForJson(params.getJSONArray("noKey").toString());
                    LogUtils.json("params", params.getJSONArray("noKey").toString());
                } else {
                    request.setDefineRequestBodyForJson(params);
                    LogUtils.json("params", params.toString());
                }
            }
        } else {
            LogUtils.i("http url------->        ", url);
        }
        String token = SPUtils.getInstance().getString(Constant.token);
        if (!TextUtils.isEmpty(token)) {
            request.addHeader("token", token);
            LogUtils.i("token------->        " + token);
        }
        return request;
    }


    /**
     * 根据相对路径获取全路径
     *
     * @param relativeUrl 相对路径
     * @return 全路径
     */
    private static String getAbsoluteUrl(String relativeUrl) {
        if (relativeUrl.startsWith("http")) {
            return relativeUrl;
        }
        if (Constant.BASE_URL.endsWith("/")) {
            return Constant.BASE_URL + relativeUrl;
        } else {
            return Constant.BASE_URL + "/" + relativeUrl;
        }
    }
}
