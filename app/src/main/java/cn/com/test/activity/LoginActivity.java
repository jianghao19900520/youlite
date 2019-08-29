package cn.com.test.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import cn.com.test.fragment.LoginCodeFragment;
import cn.com.test.fragment.LoginPwdFragment;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.title_right_text_btn)
    TextView title_right_text_btn;
    @BindView(R.id.code_login_btn)
    TextView code_login_btn;
    @BindView(R.id.pwd_login_btn)
    TextView pwd_login_btn;

    private LoginCodeFragment codeFragment;
    private LoginPwdFragment pwdFragment;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initTitle() {
        title.setText("登录");
        title_right_text_btn.setText("注册");
        title_right_text_btn.setVisibility(View.VISIBLE);
        title_right_text_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, GoodsInfoActivity.class));
            }
        });
    }

    @Override
    public void init() {
        codeFragment = new LoginCodeFragment();
        pwdFragment = new LoginPwdFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.login_fragment_layout, codeFragment, "fragment")
                .commit();
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

    @OnClick({R.id.code_login_btn, R.id.pwd_login_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.code_login_btn:
                code_login_btn.setTextColor(Color.parseColor("#3ea0e0"));
                pwd_login_btn.setTextColor(Color.parseColor("#666666"));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.login_fragment_layout, codeFragment, "fragment")
                        .commit();
                break;
            case R.id.pwd_login_btn:
                code_login_btn.setTextColor(Color.parseColor("#666666"));
                pwd_login_btn.setTextColor(Color.parseColor("#3ea0e0"));
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.login_fragment_layout, pwdFragment, "fragment")
                        .commit();
                break;
        }
    }

}
