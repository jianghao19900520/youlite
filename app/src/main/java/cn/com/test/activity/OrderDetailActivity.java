package cn.com.test.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import cn.com.test.adapter.CommAdapter;
import cn.com.test.adapter.CommViewHolder;
import cn.com.test.base.BaseActivity;
import cn.com.test.bean.CartBean;
import cn.com.test.bean.SearchKeyBean;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.ArithUtils;
import cn.com.test.utils.ToastUtils;
import cn.com.test.view.ListViewForScrollView;

public class OrderDetailActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.order_detail_listview)
    ListViewForScrollView order_detail_listview;
    @BindView(R.id.address_name)
    TextView address_name;
    @BindView(R.id.address_text)
    TextView address_text;
    @BindView(R.id.order_no_text)
    TextView order_no_text;
    @BindView(R.id.order_time_text)
    TextView order_time_text;
    @BindView(R.id.order_total_money_text)
    TextView order_total_money_text;
    @BindView(R.id.order_postfee_text)
    TextView order_postfee_text;
    @BindView(R.id.order_pay_money_title)
    TextView order_pay_money_title;
    @BindView(R.id.order_pay_money_text)
    TextView order_pay_money_text;
    @BindView(R.id.order_left_btn)
    TextView order_left_btn;
    @BindView(R.id.order_right_btn)
    TextView order_right_btn;

    private List<JSONObject> goodsList;
    private CommAdapter<JSONObject> mAdapter;
    private String orderNo;
    private String stt = "";//00=已完成 01=待付款 02=待发货 03=待收货 04=已取消
    private String totalAmount;//支付金额

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
        orderNo = getIntent().getStringExtra("orderNo");
        if (TextUtils.isEmpty(orderNo)) {
            finish();
        }
        goodsList = new ArrayList<>();
        mAdapter = new CommAdapter<JSONObject>(mContext, goodsList, R.layout.item_goods) {
            @Override
            public void convert(final CommViewHolder holder, JSONObject item, int position) {
                try {
                    CheckBox item_goods_check = holder.getView(R.id.item_goods_check);
                    ImageView item_goods_img = holder.getView(R.id.item_goods_img);
                    TextView item_goods_name = holder.getView(R.id.item_goods_name);
                    TextView item_goods_newprice = holder.getView(R.id.item_goods_newprice);
                    TextView item_goods_oldprice = holder.getView(R.id.item_goods_oldprice);
                    TextView item_goods_num = holder.getView(R.id.item_goods_num);
                    TextView item_del_btn = holder.getView(R.id.item_del_btn);
                    LinearLayout item_goods_num_layout = holder.getView(R.id.item_goods_num_layout);
                    item_goods_name.setText(item.getString("goodsName"));
                    item_goods_newprice.setText("￥" + item.getString("unitPrice"));
                    item_goods_oldprice.setText("");
                    item_goods_oldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
                    item_goods_num.setText("x " + item.getString("num"));
                    Glide.with(mContext).load(item.getString("toLoad")).into(item_goods_img);
                    item_goods_check.setVisibility(View.GONE);
                    item_del_btn.setVisibility(View.GONE);
                    item_goods_num_layout.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        order_detail_listview.setAdapter(mAdapter);
        loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
    }

    /**
     * @param what 1.获取订单详情 2删除订单 3支付订单 4取消订单 5确认收货 6确认发货
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("orderNo", orderNo);
                relativeUrl = "health/orderDetail";
            } else if (what == 2) {
                object.put("orderNoList", new JSONArray().put(new JSONObject().put("orderNo", orderNo)));
                relativeUrl = "health/delOrder";
            } else if (what == 3) {
                object.put("orderNo", orderNo);
                object.put("amount", totalAmount);
                object.put("channel", 03);//03-支付宝 06-微信
                relativeUrl = "health/payOrder";
            } else if (what == 4) {
                object.put("orderNo", orderNo);
                relativeUrl = "health/cancelOrder";
            } else if (what == 5) {
                object.put("orderNo", orderNo);
                relativeUrl = "health/receiveOrder";
            } else if (what == 6) {
                object.put("orderNo", orderNo);
                object.put("expressCompany", "测试快递");
                object.put("expressNo", "123456");
                relativeUrl = "health/sendOrder";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                setOrderDetail(result);
                            } else if (what == 2) {
                                finish();
                            } else if (what == 3 || what == 4 || what == 5 || what == 6) {
                                //刷新订单
                                loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
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

    @OnClick({R.id.order_left_btn, R.id.order_right_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.order_left_btn:
                if (stt.equals("01")) {
                    //取消订单
                    loadData(4, null, getString(R.string.string_loading), RequestMethod.POST);
                } else {
                    //删除订单
                    loadData(2, null, getString(R.string.string_loading), RequestMethod.POST);
                }
                break;
            case R.id.order_right_btn:
                if (stt.equals("01")) {
                    //支付
                    AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
                            .setMessage("确定支付?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    loadData(3, null, getString(R.string.string_loading), RequestMethod.POST);
                                }
                            }).setNegativeButton("取消", null).create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                } else if (stt.equals("02")) {
                    //确认发货
                    loadData(6, null, getString(R.string.string_loading), RequestMethod.POST);
                } else if (stt.equals("03")) {
                    //确认收货
                    loadData(5, null, getString(R.string.string_loading), RequestMethod.POST);
                } else {
                    //再次购买
                    List<CartBean> submitOrderList = new ArrayList();
                    for (JSONObject object : goodsList) {
                        CartBean bean = null;
                        try {
                            bean = new CartBean(object.getString("goodsNo"), object.getInt("num"), object.getString("goodsName"), object.getString("unitPrice"), object.getString("unitPrice"), object.getString("toLoad"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        submitOrderList.add(bean);
                    }
                    Bundle bundleObject = new Bundle();
                    bundleObject.putSerializable("goodsList", (Serializable) submitOrderList);
                    startActivity(new Intent(mContext, ConfirmOrderActivity.class).putExtras(bundleObject));
                    finish();
                }
        }
    }

    private void setOrderDetail(JSONObject result) throws JSONException {
        stt = result.getString("stt");
        address_name.setText(result.getString("linkMan") + "  " + result.getString("linkPhone"));
        address_text.setText(result.getString("address"));
        order_no_text.setText("订单编号 : " + result.getString("orderNo"));
        String createTime = result.getString("createTime");
        order_time_text.setText("下单时间 : " + createTime.substring(0, 4) + "-" + createTime.substring(4, 6) + "-" + createTime.substring(6, 8) + " " + createTime.substring(8, 10) + ":" + createTime.substring(10, 12) + ":" + createTime.substring(12, 14));
        order_total_money_text.setText("商品总额 : ￥" + result.getString("totalAmount"));
        order_postfee_text.setText("运费 : ￥" + result.getString("postFee"));
        if (stt.equals("01")) {
            order_pay_money_title.setText("待付款 : ");
            totalAmount = ArithUtils.add(result.getString("totalAmount"), result.getString("postFee"));
            order_pay_money_text.setText("￥" + totalAmount);
            order_left_btn.setText("取消订单");
            order_right_btn.setText("支付订单");
        } else if (stt.equals("02")) {
            order_pay_money_title.setText("实付款 : ");
            totalAmount = result.getString("payAmount");
            order_pay_money_text.setText("￥" + totalAmount);
            order_left_btn.setVisibility(View.GONE);
            order_right_btn.setText("确认发货");
        } else if (stt.equals("03")) {
            order_pay_money_title.setText("实付款 : ");
            totalAmount = result.getString("payAmount");
            order_pay_money_text.setText("￥" + totalAmount);
            order_left_btn.setVisibility(View.GONE);
            order_right_btn.setText("确认收货");
        } else if (stt.equals("00")) {
            order_pay_money_title.setText("实付款 : ");
            totalAmount = result.getString("payAmount");
            order_pay_money_text.setText("￥" + totalAmount);
            order_left_btn.setText("删除订单");
            order_right_btn.setText("再次购买");
        } else if (stt.equals("04")) {
            order_pay_money_title.setText("待付款 : ");
            totalAmount = ArithUtils.add(result.getString("totalAmount"), result.getString("postFee"));
            order_pay_money_text.setText("￥" + totalAmount);
            order_left_btn.setText("删除订单");
            order_right_btn.setText("再次购买");
        }
        goodsList.clear();
        JSONArray array = result.getJSONArray("detailList");
        for (int i = 0; i < array.length(); i++) {
            goodsList.add(array.getJSONObject(i));
        }
        mAdapter.notifyDataSetChanged();
    }

}
