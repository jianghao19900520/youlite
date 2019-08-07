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
        activity.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, AboutAsActivity.class));
            }
        });
    }

    @Override
    public void init() {

    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

    @OnClick({R.id.mine_head_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mine_head_layout:
                ToastUtils.showShort("用户资料");
                break;
        }
    }


}
