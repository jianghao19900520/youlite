package cn.com.test.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.yanzhenjie.nohttp.RequestMethod;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    public Context mContext;

    /**
     * 记录所有活动的Activity
     */
    private static final List<BaseActivity> mActivities = new LinkedList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null &&
                    intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        mActivities.add(this);
        setContent(savedInstanceState);
        ButterKnife.bind(this);
        mContext = this;
        initTitle();
        init();
    }

    public abstract void setContent(Bundle savedInstanceState);

    public abstract void initTitle();

    public abstract void init();

    public abstract void loadData(int what, String[] value, String msg, RequestMethod method);


    /**
     * 启动Activity，界面可见时.
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 获得焦点，按钮可以点击
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 暂停Activity，界面获取焦点，按钮可以点击
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /*
    标题栏返回键
     */
    public void back(View view) {
        onBackPressed();
    }

    /*
    标题栏关闭键
     */
    public void close(View view) {
        finish();
    }

    /**
     * 关闭所有Activity
     */
    public static void finishAll() {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseActivity>(mActivities);
        }
        for (BaseActivity activity : copy) {
            activity.finish();
        }
    }

    @Override
    protected void onDestroy() {
        mActivities.remove(this);
        super.onDestroy();
    }

}
