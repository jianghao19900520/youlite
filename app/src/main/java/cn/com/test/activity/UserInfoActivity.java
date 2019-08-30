package cn.com.test.activity;

import android.content.Intent;
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
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.LoginUtils;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;

public class UserInfoActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.name_edit)
    TextView name_edit;
    @BindView(R.id.nick_edit)
    TextView nick_edit;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_info);
    }

    @Override
    public void initTitle() {
        title.setText("用户资料");
    }

    @Override
    public void init() {
        loadData(1, null, getString(R.string.string_loading), RequestMethod.GET);
    }

    /**
     * @param what 1.根据token获取用户数据 2修改用户信息
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                relativeUrl = "health/userInfo";
            } else if (what == 2) {
                object.put("realName", name_edit.getText().toString().trim());
                object.put("nickName", nick_edit.getText().toString().trim());
                relativeUrl = "health/modifyUserInfo";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                String realName = result.getString("realName");
                                String nickName = result.getString("nickName");
                                if (!TextUtils.isEmpty(realName)) {
                                    name_edit.setText(realName);
                                }
                                if (!TextUtils.isEmpty(nickName)) {
                                    nick_edit.setText(nickName);
                                }
                            } else if (what == 2) {
                                ToastUtils.showShort("修改成功");
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

    @OnClick({R.id.user_info_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_info_submit:
                if (TextUtils.isEmpty(name_edit.getText().toString().trim())) {
                    ToastUtils.showShort("姓名能不为空");
                    return;
                }
                if (TextUtils.isEmpty(nick_edit.getText().toString().trim())) {
                    ToastUtils.showShort("昵称能不为空");
                    return;
                }
                loadData(2, null, getString(R.string.string_loading), RequestMethod.POST);
                break;
        }
    }

}
