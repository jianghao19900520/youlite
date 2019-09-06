package cn.com.test.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.activity.AboutAsActivity;
import cn.com.test.activity.AddressManageActivity;
import cn.com.test.activity.CartActivity;
import cn.com.test.activity.HomeActivity;
import cn.com.test.activity.LoginActivity;
import cn.com.test.activity.MyCircleListActivity;
import cn.com.test.activity.MyCollectListActivity;
import cn.com.test.activity.MyOrderActivity;
import cn.com.test.activity.SettingActivity;
import cn.com.test.activity.UserInfoActivity;
import cn.com.test.base.BaseFragment;
import cn.com.test.constant.Constant;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.LoginUtils;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;

public class MineFragment extends BaseFragment {

    @BindView(R.id.user_img)
    ImageView user_img;
    @BindView(R.id.user_name)
    TextView user_name;

    @Override
    public View setContent(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_mine, null);
    }

    @Override
    public void initTitle() {
        HomeActivity activity = (HomeActivity) getActivity();
        activity.title.setText("我的");
    }

    @Override
    public void init() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (LoginUtils.getInstance().isLogin()) {
            loadData(1, null, "", RequestMethod.GET);
        } else {
            user_name.setText("用户未登录");
        }
    }

    /**
     * @param what 1.根据token获取用户数据
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                relativeUrl = "health/userInfo";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                String userPic = result.getString("userPic");
                                String userNo = result.getString("userNo");
                                String nickName = result.getString("nickName");
                                Glide.with(mContext).load(userPic).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(user_img);
                                if (TextUtils.isEmpty(nickName)) {
                                    user_name.setText("匿名用户");
                                } else {
                                    user_name.setText(nickName);
                                }
                                if (!TextUtils.isEmpty(userNo)) {
                                    SPUtils.getInstance().put(Constant.userNo, userNo);
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

    @OnClick({R.id.mine_head_layout, R.id.mine_person_layout,
            R.id.mine_page_layout, R.id.mine_grade_layout,
            R.id.mine_oder_layout, R.id.mine_address_layout,
            R.id.mine_aboutus_layout, R.id.mine_setting_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mine_head_layout:
                if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                    startActivity(new Intent(mContext, UserInfoActivity.class));
                }
                break;
            case R.id.mine_person_layout:
                if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                    ToastUtils.showShort("家庭成员");
                }
                break;
            case R.id.mine_page_layout:
                if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                    startActivity(new Intent(mContext, MyCircleListActivity.class));
                }
                break;
            case R.id.mine_grade_layout:
                if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                    startActivity(new Intent(mContext, MyCollectListActivity.class));
                }
                break;
            case R.id.mine_oder_layout:
                if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                    startActivity(new Intent(mContext, MyOrderActivity.class));
                }
                break;
            case R.id.mine_address_layout:
                if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                    startActivity(new Intent(mContext, AddressManageActivity.class));
                }
                break;
            case R.id.mine_aboutus_layout:
                startActivity(new Intent(mContext, AboutAsActivity.class));
                break;
            case R.id.mine_setting_layout:
                startActivity(new Intent(mContext, SettingActivity.class));
                break;
        }
    }


}
