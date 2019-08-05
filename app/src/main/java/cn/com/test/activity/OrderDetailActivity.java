package cn.com.test.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import cn.com.test.view.ListViewForScrollView;

public class OrderDetailActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.order_detail_listview)
    ListViewForScrollView order_detail_listview;

    private List<JSONObject> goodsList;
    private CommAdapter<JSONObject> mAdapter;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_detail);
    }

    @Override
    public void initTitle() {
        title.setText("订单详情");
    }

    @Override
    public void init() {
        goodsList = new ArrayList<>();
        goodsList.add(new JSONObject());
        mAdapter = new CommAdapter<JSONObject>(mContext, goodsList, R.layout.item_goods) {
            @Override
            public void convert(final CommViewHolder holder, JSONObject item, int position) {
                CheckBox item_goods_check = holder.getView(R.id.item_goods_check);
                TextView item_goods_name = holder.getView(R.id.item_goods_name);
                TextView item_goods_newprice = holder.getView(R.id.item_goods_newprice);
                TextView item_goods_oldprice = holder.getView(R.id.item_goods_oldprice);
                TextView item_goods_num = holder.getView(R.id.item_goods_num);
                TextView item_del_btn = holder.getView(R.id.item_del_btn);
                LinearLayout item_goods_num_layout = holder.getView(R.id.item_goods_num_layout);
                item_goods_name.setText("6盒尿14项6条装");
                item_goods_newprice.setText("￥360.00");
                item_goods_oldprice.setText("￥540.00");
                item_goods_oldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
                item_goods_num.setText("x 3");
                item_goods_check.setVisibility(View.GONE);
                item_del_btn.setVisibility(View.GONE);
                item_goods_num_layout.setVisibility(View.GONE);
            }
        };
        order_detail_listview.setAdapter(mAdapter);
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

}
