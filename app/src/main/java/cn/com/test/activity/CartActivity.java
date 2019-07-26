package cn.com.test.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.adapter.CommAdapter;
import cn.com.test.adapter.CommViewHolder;
import cn.com.test.base.BaseActivity;
import cn.com.test.bean.CartBean;

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
        mAdapter = new CommAdapter<JSONObject>(mContext, cartList, R.layout.item_store_goods_hot) {
            @Override
            public void convert(CommViewHolder holder, final JSONObject item, final int position) {
                try {
                    holder.setText(R.id.item_cart_name, item.getString("goodsName"));
                    holder.setText(R.id.item_cart_newprice, item.getString("goodsPriceNew"));
                    holder.setText(R.id.item_cart_oldprice, item.getString("goodsPriceOld"));
                    holder.setText(R.id.item_cart_num, "X " + item.getString("goodsNum"));
                    holder.setText(R.id.item_cart_num_btn, item.getString("goodsNum"));
                    holder.getView(R.id.item_cart_reduce_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //数量减1
                            try {
                                int num = item.getInt("goodsNum") - 1;
                                if (num < 1) num = 1;
                                List<CartBean> all = DataSupport.findAll(CartBean.class);
                                for (CartBean bean : all) {
                                    if (bean.getGoodsId().equals(item.getString("goodsId"))) {
                                        //已经有数据，就数量+1
                                        bean.setGoodsNum(num);
                                        bean.save();
                                    }
                                }
                                refreshCart();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    holder.getView(R.id.item_cart_plus_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //数量加1
                            try {
                                int num = item.getInt("goodsNum") + 1;
                                if (num < 1) num = 1;
                                List<CartBean> all = DataSupport.findAll(CartBean.class);
                                for (CartBean bean : all) {
                                    if (bean.getGoodsId().equals(item.getString("goodsId"))) {
                                        //已经有数据，就数量+1
                                        bean.setGoodsNum(num);
                                        bean.save();
                                    }
                                }
                                refreshCart();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        cart_list.setAdapter(mAdapter);
        refreshCart();
    }

    /*
    刷新列表
     */
    private void refreshCart() {
        cartList.clear();
        List<CartBean> all = DataSupport.findAll(CartBean.class);
        for (CartBean bean : all) {
            try {
                JSONObject object = new JSONObject();
                object.put("goodsId", bean.getGoodsId());
                object.put("goodsNum", bean.getGoodsNum());
                object.put("goodsName", bean.getGoodsName());
                object.put("goodsPriceNew", bean.getGoodsPriceNew());
                object.put("goodsPriceOld", bean.getGoodsPriceOld());
                cartList.add(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }
}
