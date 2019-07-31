package cn.com.test.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;

public class MyOrderActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_order);
    }

    @Override
    public void initTitle() {
        title.setText("我的订单");
    }

    @Override
    public void init() {

    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

}
