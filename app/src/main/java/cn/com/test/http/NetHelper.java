package cn.com.test.http;

import android.content.Context;
import android.text.TextUtils;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

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
    public void request(Context context, int what, String relativeUrl, JSONObject params, RequestMethod method, String msg, final HttpListener listener) {
        try {
            String url = getAbsoluteUrl(relativeUrl);
            Request<String> request = NoHttp.createStringRequest(url, method);
            if (method == RequestMethod.POST) {
                Iterator iterator = params.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    request.add(key, params.getString(key));
                }
                LogUtils.i("post url------->", url);
            }
            String token = SPUtils.getInstance().getString(Constant.token);
            if (!TextUtils.isEmpty(token)) {
                request.addHeader("token", token);
                LogUtils.i("token------->        " + token);
            }
            LogUtils.json("params", params.toString());
            mQueue.add(what, request, new HttpResponseListener<String>(context, listener, msg));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
