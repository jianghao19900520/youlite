package cn.com.test.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.adapter.CommAdapter;
import cn.com.test.adapter.CommViewHolder;
import cn.com.test.base.BaseActivity;
import cn.com.test.utils.ToastUtils;

public class SearchGoodsActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.search_goods_gridview)
    GridView search_goods_gridview;

    private List<JSONObject> goodsList;
    private CommAdapter<JSONObject> mAdapter;

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
        if (TextUtils.isEmpty(searchKey)) {
            return;
        }
        goodsList = new ArrayList<>();
        goodsList.add(new JSONObject());
        goodsList.add(new JSONObject());
        goodsList.add(new JSONObject());
        mAdapter = new CommAdapter<JSONObject>(mContext, goodsList, R.layout.item_search_goods) {
            @Override
            public void convert(final CommViewHolder holder, JSONObject item, int position) {
                TextView oldPrice = holder.getView(R.id.item_store_new_oldprice);
                oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
            }
        };
        search_goods_gridview.setAdapter(mAdapter);
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
