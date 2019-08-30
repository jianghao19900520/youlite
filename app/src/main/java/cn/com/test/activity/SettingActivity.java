package cn.com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.constant.Constant;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.LoginUtils;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;

public class SettingActivity extends BaseActivity {


    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void initTitle() {

    }

    @Override
    public void init() {

    }

    /**
     * @param what 1.注销登录
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                relativeUrl = "health/logout";
            } else if (what == 2) {
                object.put("orderNo", "201908300000000019");
                object.put("expressCompany", "测试快递");
                object.put("expressNo", "123456");
                relativeUrl = "health/sendOrder";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            if (what == 1) {
                                SPUtils.getInstance().put(Constant.token, "");
                                finish();
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

    @OnClick({R.id.setting_update_pwd_layout, R.id.setting_check_update_layout, R.id.setting_logout_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting_update_pwd_layout:
                if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                    startActivity(new Intent(mContext, UpdatePwdActivity.class));
                }
                break;
            case R.id.setting_check_update_layout:
                startActivity(new Intent(mContext, AboutAsActivity.class));
                break;
            case R.id.setting_logout_layout:
                if (LoginUtils.getInstance().isLogin()) {
                    loadData(2, null, getString(R.string.string_loading), RequestMethod.POST);
                    //loadData(1, null, getString(R.string.string_loading), RequestMethod.GET);
                }else{
                    ToastUtils.showShort("用户未登录");
                }
                break;
        }
    }
}
