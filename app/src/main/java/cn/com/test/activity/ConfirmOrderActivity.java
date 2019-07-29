package cn.com.test.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lljjcoder.citypickerview.widget.CityPicker;
import com.yanzhenjie.nohttp.RequestMethod;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.bean.CartBean;

public class ConfirmOrderActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.submit_address_text)
    TextView submit_address_text;
    @BindView(R.id.goods_list)
    ListView goods_list;

    private List<CartBean> goodsList;
    private GoodsAdapter mAdapter;

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
        goodsList = (ArrayList<CartBean>) bundleObject.getSerializable("goodsList");
        if (goodsList == null || goodsList.size() == 0) finish();
        mAdapter = new GoodsAdapter();
        goods_list.setAdapter(mAdapter);
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

    @OnClick({R.id.submit_address_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submit_address_text:
                selectAddress();
                break;
        }
    }

    /*
    选择地址
     */
    private void selectAddress() {
        CityPicker cityPicker = new CityPicker.Builder(mContext)
                .textSize(14)
                .title("地址选择")
                .titleBackgroundColor("#FFFFFF")
                .confirTextColor("#696969")
                .cancelTextColor("#696969")
                .province("北京市")
                .city("北京市")
                .district("东城区")
                .textColor(Color.parseColor("#000000"))
                .provinceCyclic(false)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(20)
                .onlyShowProvinceAndCity(false)
                .build();
        cityPicker.show();
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                String province = citySelected[0];
                String city = citySelected[1];
                String district = citySelected[2];
                String code = citySelected[3];
                submit_address_text.setText(province.trim() + "-" + city.trim() + "-" + district.trim());
            }
        });
    }

    private class GoodsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return goodsList.size();
        }

        @Override
        public Object getItem(int position) {
            return goodsList.get(position);
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
                view = LayoutInflater.from(mContext).inflate(R.layout.item_cart, null);
                viewHolder.item_cart_check = view.findViewById(R.id.item_cart_check);
                viewHolder.item_cart_name = view.findViewById(R.id.item_cart_name);
                viewHolder.item_cart_newprice = view.findViewById(R.id.item_cart_newprice);
                viewHolder.item_cart_oldprice = view.findViewById(R.id.item_cart_oldprice);
                viewHolder.item_cart_num = view.findViewById(R.id.item_cart_num);
                viewHolder.item_del_btn = view.findViewById(R.id.item_del_btn);
                viewHolder.item_cart_num_layout = view.findViewById(R.id.item_cart_num_layout);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.item_cart_name.setText(goodsList.get(position).getGoodsName());
            viewHolder.item_cart_newprice.setText("￥" + goodsList.get(position).getGoodsPriceNew());
            viewHolder.item_cart_oldprice.setText("￥" + goodsList.get(position).getGoodsPriceOld());
            viewHolder.item_cart_oldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
            viewHolder.item_cart_num.setText("x "+String.valueOf(goodsList.get(position).getGoodsNum()));
            viewHolder.item_cart_check.setVisibility(View.GONE);
            viewHolder.item_del_btn.setVisibility(View.GONE);
            viewHolder.item_cart_num_layout.setVisibility(View.GONE);
            return view;
        }

        class ViewHolder {
            CheckBox item_cart_check;
            TextView item_cart_name;
            TextView item_cart_newprice;
            TextView item_cart_oldprice;
            TextView item_cart_num;
            TextView item_del_btn;
            LinearLayout item_cart_num_layout;
        }

    }

}
