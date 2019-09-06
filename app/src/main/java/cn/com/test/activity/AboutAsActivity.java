package cn.com.test.activity;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;

public class AboutAsActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.about_as_version_text)
    TextView about_as_version_text;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about_as);
    }

    @Override
    public void initTitle() {
        title.setText("关于我们");
    }

    @Override
    public void init() {
        try {
            PackageInfo packageInfo = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            about_as_version_text.setText("当前软件版本：" + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

}
