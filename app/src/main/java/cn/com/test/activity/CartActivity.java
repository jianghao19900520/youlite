package cn.com.test.activity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.bean.CartBean;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.ArithUtils;
import cn.com.test.utils.ToastUtils;

public class CartActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.cart_list)
    ListView cart_list;
    @BindView(R.id.cart_check)
    CheckBox cart_check;
    @BindView(R.id.cart_all_price)
    TextView cart_all_price;

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
        cart_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                for (CartBean node : cartList) {
                    node.setChecked(isChecked);
                }
                mAdapter.notifyDataSetChanged();
                refreshAllPrice();
            }
        });
        loadData(1, null, getString(R.string.string_loading), RequestMethod.GET);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCart();
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
        refreshAllPrice();
    }

    /*
    刷新合计金额
     */
    private void refreshAllPrice() {
        String allPrice = "0.00";
        int checkNum = 0;
        for (CartBean bean : cartList) {
            if (bean.isChecked()) {
                String itemPrice = ArithUtils.mul(bean.getGoodsPriceNew(), String.valueOf(bean.getGoodsNum()));
                allPrice = ArithUtils.add(allPrice, itemPrice);
                checkNum += 1;
            }
        }
        cart_all_price.setText("￥" + allPrice);
        if (checkNum != 0 && checkNum == cartList.size()) {
            cart_check.setChecked(true);
        }
        if (checkNum == 0) {
            cart_check.setChecked(false);
        }
    }

    @OnClick({R.id.cart_submit_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cart_submit_order:
                List<CartBean> submitOrderList = new ArrayList();
                for (CartBean bean : cartList) {
                    if (bean.isChecked()) {
                        submitOrderList.add(bean);
                    }
                }
                if (submitOrderList.size() > 0) {
                    Bundle bundleObject = new Bundle();
                    bundleObject.putSerializable("goodsList", (Serializable) submitOrderList);
                    startActivity(new Intent(mContext, ConfirmOrderActivity.class).putExtras(bundleObject));
                }
                break;
        }
    }

    /**
     * @param what 1.获取购物车列表
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                relativeUrl = "health/cartList";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                            }
                        } else {
                            ToastUtils.showShort(jsonObject.getString("errorMsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtils.showShort(getString(R.string.error_http));
                    }
                }

                @Override
                public void onFailed(int what, Response response) {
                    ToastUtils.showShort(getString(R.string.error_http));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CartAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cartList.size();
        }

        @Override
        public Object getItem(int position) {
            return cartList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            View view = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_goods, null);
                viewHolder.item_goods_scroll = view.findViewById(R.id.item_goods_scroll);
                viewHolder.item_goods_check = view.findViewById(R.id.item_goods_check);
                viewHolder.item_goods_name = view.findViewById(R.id.item_goods_name);
                viewHolder.item_goods_newprice = view.findViewById(R.id.item_goods_newprice);
                viewHolder.item_goods_oldprice = view.findViewById(R.id.item_goods_oldprice);
                viewHolder.item_goods_num = view.findViewById(R.id.item_goods_num);
                viewHolder.item_goods_num_btn = view.findViewById(R.id.item_goods_num_btn);
                viewHolder.item_goods_content_layout = view.findViewById(R.id.item_goods_content_layout);
                viewHolder.item_goods_reduce_btn = view.findViewById(R.id.item_goods_reduce_btn);
                viewHolder.item_goods_plus_btn = view.findViewById(R.id.item_goods_plus_btn);
                viewHolder.item_del_btn = view.findViewById(R.id.item_del_btn);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.item_goods_reduce_btn.setTag(position);
            viewHolder.item_goods_plus_btn.setTag(position);
            viewHolder.item_del_btn.setTag(position);
            viewHolder.item_goods_check.setTag(position);
            viewHolder.item_goods_scroll.scrollTo(0, 0);
            viewHolder.item_goods_check.setChecked(cartList.get(position).isChecked());
            viewHolder.item_goods_name.setText(cartList.get(position).getGoodsName());
            viewHolder.item_goods_newprice.setText("￥" + cartList.get(position).getGoodsPriceNew());
            viewHolder.item_goods_oldprice.setText("￥" + cartList.get(position).getGoodsPriceOld());
            viewHolder.item_goods_oldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
            viewHolder.item_goods_num.setText("x " + String.valueOf(cartList.get(position).getGoodsNum()));
            viewHolder.item_goods_num_btn.setText(String.valueOf(cartList.get(position).getGoodsNum()));
            //动态设置宽度
            ViewGroup.LayoutParams params = viewHolder.item_goods_content_layout.getLayoutParams();
            params.width = width;
            viewHolder.item_goods_content_layout.setLayoutParams(params);
            viewHolder.item_goods_reduce_btn.setOnClickListener(new View.OnClickListener() {
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
            viewHolder.item_goods_plus_btn.setOnClickListener(new View.OnClickListener() {
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
            viewHolder.item_goods_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    int position = (int) compoundButton.getTag();
                    CartBean bean = cartList.get(position);
                    bean.setChecked(isChecked);
                    bean.save();
                    refreshAllPrice();
                    if (isChecked) {
                        for (CartBean node : cartList) {
                            if (!node.isChecked()) {
                                return;
                            }
                        }
                        //全部都是选中
                        cart_check.setChecked(true);
                    } else {
                        for (CartBean node : cartList) {
                            if (node.isChecked()) {
                                return;
                            }
                        }
                        //全部都是不选中
                        cart_check.setChecked(false);
                    }
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
            HorizontalScrollView item_goods_scroll;
            CheckBox item_goods_check;
            TextView item_goods_name;
            TextView item_goods_newprice;
            TextView item_goods_oldprice;
            TextView item_goods_num;
            TextView item_goods_num_btn;
            LinearLayout item_goods_content_layout;
            TextView item_goods_reduce_btn;
            TextView item_goods_plus_btn;
            TextView item_del_btn;
        }

    }
}
