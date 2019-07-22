package cn.com.test.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yanzhenjie.nohttp.RequestMethod;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    public Context mContext;
    private View rootView;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = setContent(inflater, container, savedInstanceState);
        mContext = getContext();
        unbinder = ButterKnife.bind(this, rootView);
        initTitle();
        init();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public abstract View setContent(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    public abstract void initTitle();

    public abstract void init();

    public abstract void loadData(int what, String[] value, String msg, RequestMethod method);

}
