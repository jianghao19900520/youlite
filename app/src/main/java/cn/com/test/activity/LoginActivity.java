package cn.com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.youth.banner.Banner;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.constant.Constant;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initTitle() {
        title.setText("登录");
    }

    @Override
    public void init() {
    }

    /**
     * @param what 1.密码登录
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("phone", "13267019050");
                object.put("password", "123456");
                relativeUrl = "health/login";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            if (what == 1) {
                                String token = jsonObject.getJSONObject("result").getString("token");
                                if (!TextUtils.isEmpty(token)) {
                                    SPUtils.getInstance().put(Constant.token, token);
                                    finish();
                                }
                            }
                        } else {
                            ToastUtils.showShort(jsonObject.getString("errorMsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtils.showShort(getString(R.string.error_http));
                    }
                }

                @Override
                public void onFailed(int what, Response response) {
                    ToastUtils.showShort(getString(R.string.error_http));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.login_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
                break;
        }
    }

}
