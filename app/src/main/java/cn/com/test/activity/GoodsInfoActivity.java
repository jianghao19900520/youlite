package cn.com.test.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.utils.ToastUtils;

public class GoodsInfoActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_goods_info);
    }

    @Override
    public void initTitle() {
        title.setText("商品详情");
    }

    @OnClick({R.id.textview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.textview:
                ToastUtils.showShort("textview");
                break;
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }
}
