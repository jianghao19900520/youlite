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
                startActivity(new Intent(mContext, RegisterUserActivity.class));
                finish();
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

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

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
