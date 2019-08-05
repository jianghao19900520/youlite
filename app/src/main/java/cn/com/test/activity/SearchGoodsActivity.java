package cn.com.test.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.utils.ToastUtils;

public class SearchGoodsActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.search_goods_gridview)
    GridView search_goods_gridview;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_goods);
    }

    @Override
    public void initTitle() {
        title.setText("商品搜索");
    }

    @Override
    public void init() {
        String searchKey = getIntent().getStringExtra("searchKey");
        if (!TextUtils.isEmpty(searchKey)) {
            ToastUtils.showShort(searchKey);
        }
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

    @OnClick({R.id.title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title:
                break;
        }
    }
}
