package cn.com.test.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yanzhenjie.nohttp.RequestMethod;

import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.activity.AboutAsActivity;
import cn.com.test.activity.HomeActivity;
import cn.com.test.activity.LoginActivity;
import cn.com.test.base.BaseFragment;
import cn.com.test.utils.ToastUtils;

public class MineFragment extends BaseFragment {


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
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

    @OnClick({R.id.mine_head_layout, R.id.mine_person_layout,
            R.id.mine_page_layout, R.id.mine_grade_layout,
            R.id.mine_oder_layout, R.id.mine_address_layout,
            R.id.mine_aboutus_layout, R.id.mine_setting_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mine_head_layout:
                startActivity(new Intent(mContext, LoginActivity.class));
                break;
            case R.id.mine_person_layout:
                ToastUtils.showShort("家庭成员");
                break;
            case R.id.mine_page_layout:
                ToastUtils.showShort("我的贴子");
                break;
            case R.id.mine_grade_layout:
                ToastUtils.showShort("我的收藏");
                break;
            case R.id.mine_oder_layout:
                ToastUtils.showShort("我的订单");
                break;
            case R.id.mine_address_layout:
                ToastUtils.showShort("地址管理");
                break;
            case R.id.mine_aboutus_layout:
                startActivity(new Intent(mContext, AboutAsActivity.class));
                break;
            case R.id.mine_setting_layout:
                ToastUtils.showShort("设置");
                break;
        }
    }


}
