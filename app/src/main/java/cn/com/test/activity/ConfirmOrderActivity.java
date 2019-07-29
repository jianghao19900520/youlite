package cn.com.test.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.bean.CartBean;

public class ConfirmOrderActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;

    private List<CartBean> cartList;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_confirm_order);
    }

    @Override
    public void initTitle() {
        title.setText("确认订单");
    }

    @Override
    public void init() {
        Bundle bundleObject = getIntent().getExtras();
        cartList = (ArrayList<CartBean>) bundleObject.getSerializable("cartList");
        if (cartList == null || cartList.size() == 0) finish();
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

}
