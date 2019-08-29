package cn.com.test.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

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

public class UpdatePwdActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.register_phone_text)
    TextView register_phone_text;
    @BindView(R.id.register_pwd_text)
    TextView register_pwd_text;
    @BindView(R.id.register_code_text)
    TextView register_code_text;
    @BindView(R.id.register_code_btn)
    TextView register_code_btn;

    private CountDownTimer timer;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_find_pwd);
    }

    @Override
    public void initTitle() {
        title.setText("忘记密码");
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
     * @param what 1.获取短信验证码 2.注册
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("phone", register_phone_text.getText().toString().trim());
                object.put("type", "03");//01-注册 02-登录 03-找回密码
                relativeUrl = "health/sendSmsCode";
            } else if (what == 2) {
                object.put("phone", register_phone_text.getText().toString().trim());
                object.put("authCode", register_code_text.getText().toString().trim());
                relativeUrl = "health/findPassswdVerifyCode";
            } else if (what == 3) {
                object.put("phone", register_phone_text.getText().toString().trim());
                object.put("password", register_pwd_text.getText().toString().trim());
                relativeUrl = "health/findPassswdSet";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            if (what == 1) {
                                JSONObject result = jsonObject.getJSONObject("result");
                                register_code_text.setText(result.getString("authCode"));
                                if (timer == null) {
                                    timer = new CountDownTimer(60 * 1000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            register_code_btn.setEnabled(false);
                                            register_code_btn.setBackgroundColor(Color.parseColor("#d8d8d8"));
                                            register_code_btn.setText(millisUntilFinished / 1000 + "s");
                                        }

                                        @Override
                                        public void onFinish() {
                                            register_code_btn.setEnabled(true);
                                            register_code_btn.setBackgroundColor(Color.parseColor("#3ea0e0"));
                                            register_code_btn.setText("获取验证码");
                                            timer.cancel();
                                            timer = null;
                                        }
                                    }.start();
                                }
                            } else if (what == 2) {
                                loadData(3, null, getString(R.string.string_loading), RequestMethod.POST);
                            } else if (what == 3) {
                                ToastUtils.showShort("修改密码成功");
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

    @OnClick({R.id.register_code_btn, R.id.register_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.register_code_btn:
                if (TextUtils.isEmpty(register_phone_text.getText().toString().trim())) {
                    ToastUtils.showShort("手机号不能为空");
                    return;
                }
                loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
                break;
            case R.id.register_btn:
                if (TextUtils.isEmpty(register_phone_text.getText().toString().trim())) {
                    ToastUtils.showShort("手机号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(register_code_text.getText().toString().trim())) {
                    ToastUtils.showShort("验证码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(register_pwd_text.getText().toString().trim())) {
                    ToastUtils.showShort("新密码不能为空");
                    return;
                }
                loadData(2, null, getString(R.string.string_loading), RequestMethod.POST);
                break;
        }
    }

}
