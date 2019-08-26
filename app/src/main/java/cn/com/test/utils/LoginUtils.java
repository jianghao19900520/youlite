package cn.com.test.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import cn.com.test.activity.LoginActivity;
import cn.com.test.constant.Constant;

public class LoginUtils {

    private static LoginUtils instance;

    private LoginUtils() {

    }

    public static LoginUtils getInstance() {
        if (instance == null) {
            instance = new LoginUtils();
        }
        return instance;
    }

    /**
     * 是否已登录
     */
    public boolean isLogin() {
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constant.token))) {
            return false;
        }
        return true;
    }

    /**
     * 检查登录状态，如果没登陆就跳转到登录页面
     */
    public boolean checkLoginStatus(Context context) {
        if (TextUtils.isEmpty(SPUtils.getInstance().getString(Constant.token))) {
            context.startActivity(new Intent(context, LoginActivity.class));
            return false;
        }
        return true;
    }

}
