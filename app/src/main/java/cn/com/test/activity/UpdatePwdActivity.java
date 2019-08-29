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
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.ToastUtils;

public class UpdatePwdActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.update_phone_text)
    TextView update_phone_text;
    @BindView(R.id.update_pwd_text)
    TextView update_pwd_text;
    @BindView(R.id.update_code_text)
    TextView update_code_text;
    @BindView(R.id.update_code_btn)
    TextView update_code_btn;

    private CountDownTimer timer;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_update_pwd);
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
     * @param what 1.获取短信验证码 2.校验短信验证码 3.提交新密码
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("phone", update_phone_text.getText().toString().trim());
                object.put("type", "03");//01-注册 02-登录 03-找回密码
                relativeUrl = "health/sendSmsCode";
            } else if (what == 2) {
                object.put("phone", update_phone_text.getText().toString().trim());
                object.put("authCode", update_code_text.getText().toString().trim());
                relativeUrl = "health/findPassswdVerifyCode";
            } else if (what == 3) {
                object.put("phone", update_phone_text.getText().toString().trim());
                object.put("password", update_pwd_text.getText().toString().trim());
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
                                update_code_text.setText(result.getString("authCode"));
                                if (timer == null) {
                                    timer = new CountDownTimer(60 * 1000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            update_code_btn.setEnabled(false);
                                            update_code_btn.setBackgroundColor(Color.parseColor("#d8d8d8"));
                                            update_code_btn.setText(millisUntilFinished / 1000 + "s");
                                        }

                                        @Override
                                        public void onFinish() {
                                            update_code_btn.setEnabled(true);
                                            update_code_btn.setBackgroundColor(Color.parseColor("#3ea0e0"));
                                            update_code_btn.setText("获取验证码");
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

    @OnClick({R.id.update_code_btn, R.id.update_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.update_code_btn:
                if (TextUtils.isEmpty(update_phone_text.getText().toString().trim())) {
                    ToastUtils.showShort("手机号不能为空");
                    return;
                }
                loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
                break;
            case R.id.update_btn:
                if (TextUtils.isEmpty(update_phone_text.getText().toString().trim())) {
                    ToastUtils.showShort("手机号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(update_code_text.getText().toString().trim())) {
                    ToastUtils.showShort("验证码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(update_pwd_text.getText().toString().trim())) {
                    ToastUtils.showShort("新密码不能为空");
                    return;
                }
                loadData(2, null, getString(R.string.string_loading), RequestMethod.POST);
                break;
        }
    }

}
