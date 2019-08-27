package cn.com.test.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.OptionsPickerView;
import com.yanzhenjie.nohttp.RequestMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.bean.AddressBean;
import cn.com.test.bean.CartBean;
import cn.com.test.utils.ArithUtils;
import cn.com.test.utils.GetAssetsUtils;
import cn.com.test.utils.ToastUtils;

public class ConfirmOrderActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.submit_name_text)
    EditText submit_name_text;//收货人
    @BindView(R.id.submit_number_text)
    EditText submit_number_text;//手机号码
    @BindView(R.id.submit_address_text)
    TextView submit_address_text;//所在地址
    @BindView(R.id.submit_detailed_address_text)
    EditText submit_detailed_address_text;//详细地址
    @BindView(R.id.goods_list)
    ListView goods_list;
    @BindView(R.id.goods_money_text)
    TextView goods_money_text;//商品金额
    @BindView(R.id.pay_money_text)
    TextView pay_money_text;//实付款

    private List<AddressBean> options1Items = new ArrayList<>();//省
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();//市
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();//区县

    private List<CartBean> goodsList;//要购买的商品列表
    private GoodsAdapter mAdapter;
    private String goods_money = "0.00";//商品金额
    private String freight_money = "0.00";//运费

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
        for (CartBean bean : goodsList) {
            String itemPrice = ArithUtils.mul(bean.getNewPrice(), String.valueOf(bean.getNum()));
            goods_money = ArithUtils.add(goods_money, itemPrice);
        }
        goods_money_text.setText(goods_money);
        pay_money_text.setText(ArithUtils.add(goods_money, freight_money));
        mAdapter = new GoodsAdapter();
        goods_list.setAdapter(mAdapter);
        initAddress();
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

    @OnClick({R.id.submit_address_text, R.id.submit_order_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submit_address_text:
                ShowPickerView();
                break;
            case R.id.submit_order_btn:
                if (TextUtils.isEmpty(submit_name_text.getText().toString().trim())) {
                    ToastUtils.showShort("收货人不能为空");
                    return;
                }
                if (TextUtils.isEmpty(submit_number_text.getText().toString().trim())) {
                    ToastUtils.showShort("手机号码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(submit_address_text.getText().toString().trim())) {
                    ToastUtils.showShort("所在地址不能为空");
                    return;
                }
                if (TextUtils.isEmpty(submit_detailed_address_text.getText().toString().trim())) {
                    ToastUtils.showShort("详细地址不能为空");
                    return;
                }
                ToastUtils.showShort("订单提交成功");
                //删除选中的购物车数据
//                submit_address_text.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        List<CartBean> all = DataSupport.findAll(CartBean.class);
//                        for (CartBean bean : goodsList) {
//                            for (CartBean bean2 : all) {
//                                if (bean.getGoodsNo().equals(bean2.getGoodsNo())) {
//                                    //删除该条商品的数据库数据
//                                    bean2.delete();
//                                }
//                            }
//                        }
//                        finish();
//                    }
//                }, 1000);
                break;
        }
    }

    /*
    初始化省市县数据
     */
    private void initAddress() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //在子线程中解析省市区数据
                String jsonData = new GetAssetsUtils().getJson(mContext, "address.json");
                ArrayList<AddressBean> detail = new ArrayList<>();
                try {
                    JSONArray data = new JSONArray(jsonData);
                    for (int i = 0; i < data.length(); i++) {
                        AddressBean entity = JSON.parseObject(data.optJSONObject(i).toString(), AddressBean.class);
                        detail.add(entity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showShort("数据解析失败");
                }
                //添加省份数据
                options1Items = detail;
                for (int i = 0; i < detail.size(); i++) {//遍历省份
                    ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
                    ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

                    for (int c = 0; c < detail.get(i).getCity().size(); c++) {//遍历该省份的所有城市
                        String CityName = detail.get(i).getCity().get(c).getName();
                        CityList.add(CityName);//添加城市
                        ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                        //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                        if (detail.get(i).getCity().get(c).getArea() == null
                                || detail.get(i).getCity().get(c).getArea().size() == 0) {
                            City_AreaList.add("");
                        } else {
                            for (int d = 0; d < detail.get(i).getCity().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                                String AreaName = detail.get(i).getCity().get(c).getArea().get(d);
                                City_AreaList.add(AreaName);//添加该城市所有地区数据
                            }
                        }
                        Province_AreaList.add(City_AreaList);//添加该省所有地区数据
                    }
                    //添加城市数据
                    options2Items.add(CityList);
                    //添加地区数据
                    options3Items.add(Province_AreaList);
                }
            }
        });
        thread.start();
    }

    /*
    省市县弹框
     */
    private void ShowPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String address = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);
                submit_address_text.setText(address);
            }
        })
                .setTitleText("城市选择")
                .setDividerColor(mContext.getResources().getColor(R.color.mainColor))
                .setTextColorCenter(mContext.getResources().getColor(R.color.mainColor))
                .setContentTextSize(25)
                .build();
        //pvOptions.setPicker(options1Items);//一级选择器
        //pvOptions.setPicker(options1Items, options2Items);//二级选择器
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
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
                view = LayoutInflater.from(mContext).inflate(R.layout.item_goods, null);
                viewHolder.item_goods_check = view.findViewById(R.id.item_goods_check);
                viewHolder.item_goods_name = view.findViewById(R.id.item_goods_name);
                viewHolder.item_goods_newprice = view.findViewById(R.id.item_goods_newprice);
                viewHolder.item_goods_oldprice = view.findViewById(R.id.item_goods_oldprice);
                viewHolder.item_goods_num = view.findViewById(R.id.item_goods_num);
                viewHolder.item_del_btn = view.findViewById(R.id.item_del_btn);
                viewHolder.item_goods_num_layout = view.findViewById(R.id.item_goods_num_layout);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.item_goods_name.setText(goodsList.get(position).getGoodsName());
            viewHolder.item_goods_newprice.setText("￥" + goodsList.get(position).getNewPrice());
            viewHolder.item_goods_oldprice.setText("￥" + goodsList.get(position).getOldPrice());
            viewHolder.item_goods_oldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
            viewHolder.item_goods_num.setText("x " + String.valueOf(goodsList.get(position).getNum()));
            viewHolder.item_goods_check.setVisibility(View.GONE);
            viewHolder.item_del_btn.setVisibility(View.GONE);
            viewHolder.item_goods_num_layout.setVisibility(View.GONE);
            return view;
        }

        class ViewHolder {
            CheckBox item_goods_check;
            TextView item_goods_name;
            TextView item_goods_newprice;
            TextView item_goods_oldprice;
            TextView item_goods_num;
            TextView item_del_btn;
            LinearLayout item_goods_num_layout;
        }

    }

}
