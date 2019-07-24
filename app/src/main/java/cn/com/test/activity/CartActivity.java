package cn.com.test.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.adapter.CommAdapter;
import cn.com.test.adapter.CommViewHolder;
import cn.com.test.base.BaseActivity;

public class CartActivity extends BaseActivity {


    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.cart_list)
    ListView cart_list;

    private List<JSONObject> cartList;
    private CommAdapter<JSONObject> mAdapter;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cart);
    }

    @Override
    public void initTitle() {
        title.setText("购物车");
    }

    @Override
    public void init() {
        cartList = new ArrayList<>();
        cartList.add(new JSONObject());
        mAdapter = new CommAdapter<JSONObject>(mContext, cartList, R.layout.item_store_goods_hot) {
            @Override
            public void convert(CommViewHolder holder, JSONObject item, int position) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        cart_list.setAdapter(mAdapter);
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }
}
