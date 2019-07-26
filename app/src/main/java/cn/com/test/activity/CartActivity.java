package cn.com.test.activity;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
    @BindView(R.id.cart_check)
    CheckBox cart_check;

    private List<CartBean> cartList;
    private CartAdapter mAdapter;
    private int width;

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
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        cartList = new ArrayList<>();
        mAdapter = new CartAdapter();
        cart_list.setAdapter(mAdapter);
        refreshCart();
        cart_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

            }
        });
    }

    /*
    刷新列表
     */
    private void refreshCart() {
        cartList.clear();
        List<CartBean> all = DataSupport.findAll(CartBean.class);
        for (CartBean bean : all) {
            cartList.add(bean);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

    private class CartAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cartList.size();
        }

        @Override
        public Object getItem(int i) {
            return cartList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            View view = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_cart, null);
                viewHolder.item_cart_name = view.findViewById(R.id.item_cart_name);
                viewHolder.item_cart_newprice = view.findViewById(R.id.item_cart_newprice);
                viewHolder.item_cart_oldprice = view.findViewById(R.id.item_cart_oldprice);
                viewHolder.item_cart_num = view.findViewById(R.id.item_cart_num);
                viewHolder.item_cart_num_btn = view.findViewById(R.id.item_cart_num_btn);
                viewHolder.item_cart_content_layout = view.findViewById(R.id.item_cart_content_layout);
                viewHolder.item_cart_reduce_btn = view.findViewById(R.id.item_cart_reduce_btn);
                viewHolder.item_cart_plus_btn = view.findViewById(R.id.item_cart_plus_btn);
                viewHolder.item_del_btn = view.findViewById(R.id.item_del_btn);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.item_cart_reduce_btn.setTag(position);
            viewHolder.item_cart_plus_btn.setTag(position);
            viewHolder.item_del_btn.setTag(position);
            viewHolder.item_cart_name.setText(cartList.get(position).getGoodsName());
            viewHolder.item_cart_newprice.setText(cartList.get(position).getGoodsPriceNew());
            viewHolder.item_cart_oldprice.setText(cartList.get(position).getGoodsPriceOld());
            viewHolder.item_cart_oldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
            viewHolder.item_cart_num.setText(String.valueOf(cartList.get(position).getGoodsNum()));
            viewHolder.item_cart_num_btn.setText(String.valueOf(cartList.get(position).getGoodsNum()));
            //动态设置宽度
            ViewGroup.LayoutParams params = viewHolder.item_cart_content_layout.getLayoutParams();
            params.width = width;
            viewHolder.item_cart_content_layout.setLayoutParams(params);
            viewHolder.item_cart_reduce_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //数量减1
                    int position = (int) view.getTag();
                    int num = cartList.get(position).getGoodsNum() - 1;
                    if (num < 1) num = 1;
                    List<CartBean> all = DataSupport.findAll(CartBean.class);
                    for (CartBean bean : all) {
                        if (bean.getGoodsId().equals(cartList.get(position).getGoodsId())) {
                            bean.setGoodsNum(num);
                            bean.save();
                        }
                    }
                    refreshCart();
                }
            });
            viewHolder.item_cart_plus_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //数量加1
                    int position = (int) view.getTag();
                    int num = cartList.get(position).getGoodsNum() + 1;
                    if (num < 1) num = 1;
                    List<CartBean> all = DataSupport.findAll(CartBean.class);
                    for (CartBean bean : all) {
                        if (bean.getGoodsId().equals(cartList.get(position).getGoodsId())) {
                            bean.setGoodsNum(num);
                            bean.save();
                        }
                    }
                    refreshCart();
                }
            });
            viewHolder.item_del_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //删除
                    int position = (int) view.getTag();
                    List<CartBean> all = DataSupport.findAll(CartBean.class);
                    for (CartBean bean : all) {
                        if (bean.getGoodsId().equals(cartList.get(position).getGoodsId())) {
                            bean.delete();
                        }
                    }
                    refreshCart();
                }
            });
            return view;
        }

        class ViewHolder {
            TextView item_cart_name;
            TextView item_cart_newprice;
            TextView item_cart_oldprice;
            TextView item_cart_num;
            TextView item_cart_num_btn;
            LinearLayout item_cart_content_layout;
            TextView item_cart_reduce_btn;
            TextView item_cart_plus_btn;
            TextView item_del_btn;
        }

    }
}
