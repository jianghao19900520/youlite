package cn.com.test.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.android.tu.loadingdialog.LoadingDailog;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.NotFoundCacheError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import cn.com.test.R;
import cn.com.test.utils.LogUtils;
import cn.com.test.utils.ToastUtils;

public class HttpResponseListener<T> implements OnResponseListener<T> {
    private Context mContext;
    /**
     * Dialog.
     */
    private LoadingDailog dialog;
    /**
     * 结果回调.
     */
    private HttpListener<T> callback;

    /**
     * @param context      context用来实例化dialog.
     * @param httpCallback 回调对象.
     * @param msg          显示dialog的提示信息.
     */
    public HttpResponseListener(Context context, HttpListener<T> httpCallback, String msg) {
        this.mContext = context;
        if (context != null && !TextUtils.isEmpty(msg)) {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog = null;
            }
            dialog = new LoadingDailog.Builder(mContext)
                    .setMessage(msg)
                    .setCancelable(true)
                    .setCancelOutside(true).create();
        }
        this.callback = httpCallback;
    }

    /**
     * 开始请求, 这里显示一个dialog.
     */
    @Override
    public void onStart(int what) {
        try {
            if (dialog != null && !dialog.isShowing())
                dialog.show();
        } catch (Exception e) {
        }
    }

    /**
     * 结束请求, 这里关闭dialog.
     */
    @Override
    public void onFinish(int what) {
        try {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        } catch (Exception e) {
        }
    }

    /**
     * 成功回调.
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        if (callback != null) {
            LogUtils.json(response.get().toString());
            callback.onSucceed(what, response);
        }
    }

    /**
     * 失败回调.
     */
    @Override
    public void onFailed(int what, Response<T> response) {
        Exception exception = response.getException();
        if (exception instanceof NetworkError) {// 网络不好
            ToastUtils.showShort(R.string.error_please_check_network);
        } else if (exception instanceof TimeoutError) {// 请求超时
            ToastUtils.showShort(R.string.error_timeout);
        } else if (exception instanceof UnKnownHostError) {// 找不到服务器
            ToastUtils.showShort(R.string.error_not_found_server);
        } else if (exception instanceof URLError) {// URL是错的
            ToastUtils.showShort(R.string.error_url_error);
        } else if (exception instanceof NotFoundCacheError) {
            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            // 没有缓存一般不提示用户，如果需要随你。
        } else {
            ToastUtils.showShort(R.string.error_unknow);
        }
        Logger.e("错误：" + exception.getMessage());
        if (callback != null)
            callback.onFailed(what, response);
    }
}
