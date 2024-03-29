package cn.com.test.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.activity.UpdatePwdActivity;
import cn.com.test.base.BaseFragment;
import cn.com.test.constant.Constant;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;

public class LoginPwdFragment extends BaseFragment {

    @BindView(R.id.login_phone_text)
    TextView login_phone_text;
    @BindView(R.id.login_pwd_text)
    TextView login_pwd_text;

    @Override
    public View setContent(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_login_pwd, null);
    }

    @Override
    public void initTitle() {

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
                object.put("phone", login_phone_text.getText().toString().trim());
                object.put("password", login_pwd_text.getText().toString().trim());
                object.put("type", "0");//0-密码登录 1-验证码登录
                relativeUrl = "health/login";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                String token = result.getString("token");
                                if (!TextUtils.isEmpty(token)) {
                                    SPUtils.getInstance().put(Constant.token, token);
                                    getActivity().finish();
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

    @OnClick({R.id.login_btn, R.id.forget_pwd_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                if (TextUtils.isEmpty(login_phone_text.getText().toString().trim())) {
                    ToastUtils.showShort("手机号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(login_pwd_text.getText().toString().trim())) {
                    ToastUtils.showShort("密码不能为空");
                    return;
                }
                loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
                break;
            case R.id.forget_pwd_btn:
                startActivity(new Intent(mContext, UpdatePwdActivity.class));
                break;
        }
    }

}
