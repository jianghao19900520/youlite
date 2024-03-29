package cn.com.test.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import cn.com.test.base.BaseFragment;
import cn.com.test.constant.Constant;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;

public class LoginCodeFragment extends BaseFragment {

    @BindView(R.id.login_phone_text)
    TextView login_phone_text;
    @BindView(R.id.login_code_text)
    TextView login_code_text;
    @BindView(R.id.login_code_btn)
    TextView login_code_btn;

    private CountDownTimer timer;

    @Override
    public View setContent(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_login_code, null);
    }

    @Override
    public void initTitle() {

    }

    @Override
    public void init() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * @param what 1.获取短信验证码 2.短信验证码登录
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("phone", login_phone_text.getText().toString().trim());
                object.put("type", "02");//01-注册 02-登录 03-找回密码
                relativeUrl = "health/sendSmsCode";
            } else if (what == 2) {
                object.put("phone", login_phone_text.getText().toString().trim());
                object.put("authCode", login_code_text.getText().toString().trim());
                object.put("type", "1");//0-密码登录 1-验证码登录
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
                                login_code_text.setText(result.getString("authCode"));
                                if (timer == null) {
                                    timer = new CountDownTimer(60 * 1000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            login_code_btn.setEnabled(false);
                                            login_code_btn.setBackgroundColor(Color.parseColor("#d8d8d8"));
                                            login_code_btn.setText(millisUntilFinished / 1000 + "s");
                                        }

                                        @Override
                                        public void onFinish() {
                                            login_code_btn.setEnabled(true);
                                            login_code_btn.setBackgroundColor(Color.parseColor("#3ea0e0"));
                                            login_code_btn.setText("获取验证码");
                                            timer.cancel();
                                            timer = null;
                                        }
                                    }.start();
                                }
                            } else if (what == 2) {
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

    @OnClick({R.id.login_code_btn, R.id.login_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_code_btn:
                if (TextUtils.isEmpty(login_phone_text.getText().toString().trim())) {
                    ToastUtils.showShort("手机号不能为空");
                    return;
                }
                loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
                break;
            case R.id.login_btn:
                if (TextUtils.isEmpty(login_phone_text.getText().toString().trim())) {
                    ToastUtils.showShort("手机号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(login_code_text.getText().toString().trim())) {
                    ToastUtils.showShort("验证码不能为空");
                    return;
                }
                loadData(2, null, getString(R.string.string_loading), RequestMethod.POST);
                break;
        }
    }

}
