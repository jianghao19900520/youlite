package cn.com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.yanzhenjie.nohttp.RequestMethod;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.utils.SPUtils;

public class SplashActivity extends BaseActivity{

    @BindView(R.id.ad_img)
    ImageView adImg;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void initTitle() {

    }

    @Override
    public void init() {
        adImg.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isShowGuide = SPUtils.getInstance().getBoolean("isShowGuide", false);
                if (isShowGuide){
                    startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                }else {
                    SPUtils.getInstance().put("isShowGuide", true);
                    startActivity(new Intent(SplashActivity.this,GuideActivity.class));
                }
                finish();
            }
        }, 1000);
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }
}
