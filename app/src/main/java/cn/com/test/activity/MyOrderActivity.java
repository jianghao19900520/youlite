package cn.com.test.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.adapter.CommAdapter;
import cn.com.test.adapter.CommViewHolder;
import cn.com.test.base.BaseActivity;
import cn.com.test.base.BaseApplication;
import cn.com.test.bean.CartBean;
import cn.com.test.bean.SearchKeyBean;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.ToastUtils;

public class MyOrderActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.order_all_text)
    TextView order_all_text;
    @BindView(R.id.order_all_line)
    View order_all_line;
    @BindView(R.id.order_pay_text)
    TextView order_pay_text;
    @BindView(R.id.order_pay_line)
    View order_pay_line;
    @BindView(R.id.order_receive_text)
    TextView order_receive_text;
    @BindView(R.id.order_receive_line)
    View order_receive_line;
    @BindView(R.id.order_finish_text)
    TextView order_finish_text;
    @BindView(R.id.order_finish_line)
    View order_finish_line;
    @BindView(R.id.order_cancle_text)
    TextView order_cancle_text;
    @BindView(R.id.order_cancle_line)
    View order_cancle_line;
    @BindView(R.id.order_list)
    ListView order_list;

    float x1, x2, y1, y2 = 0;//listview里面scrollview的手势监听
    private String orderType = "00";//00=全部 01=待付款 02=待收货 03=已完成 04=已取消
    private List<JSONObject> orderList;
    private List<JSONObject> showList;//根据状态来显示的列表
    private CommAdapter<JSONObject> mAdapter;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_order);
    }

    @Override
    public void initTitle() {
        title.setText("我的订单");
    }

    @Override
    public void init() {
        orderList = new ArrayList<>();
        showList = new ArrayList<>();
        mAdapter = new CommAdapter<JSONObject>(mContext, showList, R.layout.item_my_order) {
            @Override
            public void convert(final CommViewHolder holder, final JSONObject item, int position) {
                try {
                    LinearLayout item_my_order_goods_layout = holder.getView(R.id.item_my_order_goods_layout);
                    TextView left_text = holder.getView(R.id.item_my_order_bottom_left_text);
                    TextView right_text = holder.getView(R.id.item_my_order_bottom_right_text);
                    String createTime = item.getString("createTime");
                    holder.setText(R.id.order_time_text, "下单时间：" + createTime.substring(0, 4) + "-" + createTime.substring(4, 6) + "-" + createTime.substring(6, 8) + " " + createTime.substring(8, 10) + ":" + createTime.substring(10, 12) + ":" + createTime.substring(12, 14));
                    JSONArray goodsList = item.getJSONArray("detailList");
                    holder.setText(R.id.order_total_text, "共" + goodsList.length() + "件商品 付款金额：￥" + item.getString("totalAmount"));
                    item_my_order_goods_layout.removeAllViews();
                    for (int i = 0; i < goodsList.length(); i++) {
                        ImageView img = new ImageView(mContext);
                        RequestOptions options = new RequestOptions()
                                .override(96, 96)
                                .diskCacheStrategy(DiskCacheStrategy.ALL);
                        Glide.with(mContext).setDefaultRequestOptions(options)
                                .load(goodsList.getJSONObject(i).getString("toLoad"))
                                .into(img);
                        item_my_order_goods_layout.addView(img);
                    }
                    switch (item.getString("stt")) {
                        case "01":
                            holder.setText(R.id.item_my_order_status_text, "待付款");
                            holder.getView(R.id.item_my_order_delete_img).setVisibility(View.GONE);
                            left_text.setVisibility(View.GONE);
                            right_text.setText("支付订单");
                            right_text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
                                                .setMessage("确定支付" + item.getString("totalAmount") + "元?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        try {
                                                            loadData(2, new String[]{item.getString("orderNo"), item.getString("totalAmount")}, getString(R.string.string_loading), RequestMethod.POST);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }).setNegativeButton("取消", null).create();
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;
                        case "02":
                            holder.setText(R.id.item_my_order_status_text, "待收货");
                            holder.getView(R.id.item_my_order_delete_img).setVisibility(View.GONE);
                            left_text.setVisibility(View.VISIBLE);
                            left_text.setVisibility(View.GONE);
                            left_text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ToastUtils.showShort("查看物流");
                                }
                            });
                            right_text.setText("确认收货");
                            right_text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //确认收货
                                    try {
                                        loadData(4, new String[]{item.getString("orderNo")}, getString(R.string.string_loading), RequestMethod.POST);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;
                        case "00":
                            // TODO: 2019/8/30  
                            holder.setText(R.id.item_my_order_status_text, "已完成");
                            holder.getView(R.id.item_my_order_delete_img).setVisibility(View.GONE);
                            left_text.setVisibility(View.VISIBLE);
                            left_text.setText("删除订单");
                            left_text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        loadData(5, new String[]{item.getString("orderNo")}, getString(R.string.string_loading), RequestMethod.POST);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            right_text.setText("再次购买");
                            right_text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    buyAgain(item);
                                }
                            });
                            break;
                        case "04":
                            holder.setText(R.id.item_my_order_status_text, "再次购买");
                            holder.getView(R.id.item_my_order_delete_img).setVisibility(View.VISIBLE);
                            left_text.setText("删除订单");
                            left_text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        loadData(5, new String[]{item.getString("orderNo")}, getString(R.string.string_loading), RequestMethod.POST);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            right_text.setText("再次购买");
                            right_text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    buyAgain(item);
                                }
                            });
                            break;
                    }
                    holder.getView(R.id.item_my_order_scroll_layout).setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (motionEvent.getAction() == motionEvent.ACTION_DOWN) {
                                x1 = motionEvent.getX();
                                y1 = motionEvent.getY();
                            }
                            if (motionEvent.getAction() == motionEvent.ACTION_UP) {
                                x2 = motionEvent.getX();
                                y2 = motionEvent.getY();
                                if (x1 == x2 && y1 == y2) {
                                    //监听scrollview判断手势，如果是点击，则调用itemd的点击事件
                                    holder.getConvertView().performClick();
                                }
                            }
                            return false;
                        }

                    });
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                startActivity(new Intent(mContext, OrderDetailActivity.class).putExtra("orderNo", item.getString("orderNo")));
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
        order_list.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
    }

    private void buyAgain(JSONObject item) {
        //再次购买
        try {
            List<CartBean> submitOrderList = new ArrayList();
            JSONArray detailList = item.getJSONArray("detailList");
            for (int i = 0; i < detailList.length(); i++) {
                JSONObject goods = detailList.getJSONObject(i);
                CartBean bean = null;
                try {
                    bean = new CartBean(goods.getString("goodsNo"), goods.getInt("num"), goods.getString("goodsName"), goods.getString("unitPrice"), goods.getString("unitPrice"), goods.getString("toLoad"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                submitOrderList.add(bean);
            }
            Bundle bundleObject = new Bundle();
            bundleObject.putSerializable("goodsList", (Serializable) submitOrderList);
            startActivity(new Intent(mContext, ConfirmOrderActivity.class).putExtras(bundleObject));
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param what 1.获取订单列表 2支付订单 3确认收货 4确认收货(后台) 5删除订单
     */
    @Override
    public void loadData(int what, final String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("page", 0);
                object.put("limit", 10);
                relativeUrl = "health/userOrderList";
            } else if (what == 2) {
                object.put("orderNo", value[0]);
                object.put("amount", value[1]);
                object.put("channel", 03);//03-支付宝 06-微信
                relativeUrl = "health/payOrder";
            } else if (what == 3) {
                object.put("orderNo", value[0]);
                relativeUrl = "health/receiveOrder";
            } else if (what == 4) {
                // TODO: 2019/8/30 临时后台通过发货
                object.put("orderNo", value[0]);
                object.put("expressCompany", "测试快递");
                object.put("expressNo", "123456");
                relativeUrl = "health/sendOrder";
            } else if (what == 5) {
                object.put("orderNoList", new JSONArray().put(new JSONObject().put("orderNo", value[0])));
                relativeUrl = "health/delOrder";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                setOrderList(result.getJSONArray("list"));
                            } else if (what == 2 || what == 3 || what == 5) {
                                loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
                            } else if (what == 4) {
                                loadData(3, value, getString(R.string.string_loading), RequestMethod.POST);
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

    @OnClick({R.id.order_all_text, R.id.order_pay_text, R.id.order_receive_text, R.id.order_finish_text, R.id.order_cancle_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.order_all_text:
                orderType = "00";
                order_all_text.setTextColor(getResources().getColor(R.color.mainColor));
                order_pay_text.setTextColor(Color.parseColor("#333333"));
                order_receive_text.setTextColor(Color.parseColor("#333333"));
                order_finish_text.setTextColor(Color.parseColor("#333333"));
                order_cancle_text.setTextColor(Color.parseColor("#333333"));
                order_all_line.setVisibility(View.VISIBLE);
                order_pay_line.setVisibility(View.INVISIBLE);
                order_receive_line.setVisibility(View.INVISIBLE);
                order_finish_line.setVisibility(View.INVISIBLE);
                order_cancle_line.setVisibility(View.INVISIBLE);
                refreshListStatus();
                break;
            case R.id.order_pay_text:
                orderType = "01";
                order_all_text.setTextColor(Color.parseColor("#333333"));
                order_pay_text.setTextColor(getResources().getColor(R.color.mainColor));
                order_receive_text.setTextColor(Color.parseColor("#333333"));
                order_finish_text.setTextColor(Color.parseColor("#333333"));
                order_cancle_text.setTextColor(Color.parseColor("#333333"));
                order_all_line.setVisibility(View.INVISIBLE);
                order_pay_line.setVisibility(View.VISIBLE);
                order_receive_line.setVisibility(View.INVISIBLE);
                order_finish_line.setVisibility(View.INVISIBLE);
                order_cancle_line.setVisibility(View.INVISIBLE);
                refreshListStatus();
                break;
            case R.id.order_receive_text:
                orderType = "02";
                order_all_text.setTextColor(Color.parseColor("#333333"));
                order_pay_text.setTextColor(Color.parseColor("#333333"));
                order_receive_text.setTextColor(getResources().getColor(R.color.mainColor));
                order_finish_text.setTextColor(Color.parseColor("#333333"));
                order_cancle_text.setTextColor(Color.parseColor("#333333"));
                order_all_line.setVisibility(View.INVISIBLE);
                order_pay_line.setVisibility(View.INVISIBLE);
                order_receive_line.setVisibility(View.VISIBLE);
                order_finish_line.setVisibility(View.INVISIBLE);
                order_cancle_line.setVisibility(View.INVISIBLE);
                refreshListStatus();
                break;
            case R.id.order_finish_text:
                orderType = "03";
                order_all_text.setTextColor(Color.parseColor("#333333"));
                order_pay_text.setTextColor(Color.parseColor("#333333"));
                order_receive_text.setTextColor(Color.parseColor("#333333"));
                order_finish_text.setTextColor(getResources().getColor(R.color.mainColor));
                order_cancle_text.setTextColor(Color.parseColor("#333333"));
                order_all_line.setVisibility(View.INVISIBLE);
                order_pay_line.setVisibility(View.INVISIBLE);
                order_receive_line.setVisibility(View.INVISIBLE);
                order_finish_line.setVisibility(View.VISIBLE);
                order_cancle_line.setVisibility(View.INVISIBLE);
                refreshListStatus();
                break;
            case R.id.order_cancle_text:
                orderType = "04";
                order_all_text.setTextColor(Color.parseColor("#333333"));
                order_pay_text.setTextColor(Color.parseColor("#333333"));
                order_receive_text.setTextColor(Color.parseColor("#333333"));
                order_finish_text.setTextColor(Color.parseColor("#333333"));
                order_cancle_text.setTextColor(getResources().getColor(R.color.mainColor));
                order_all_line.setVisibility(View.INVISIBLE);
                order_pay_line.setVisibility(View.INVISIBLE);
                order_receive_line.setVisibility(View.INVISIBLE);
                order_finish_line.setVisibility(View.INVISIBLE);
                order_cancle_line.setVisibility(View.VISIBLE);
                refreshListStatus();
                break;
        }
    }

    private void setOrderList(JSONArray array) throws JSONException {
        orderList.clear();
        for (int i = 0; i < array.length(); i++) {
            orderList.add(array.getJSONObject(i));
        }
        refreshListStatus();
    }

    /**
     * 刷新列表状态
     */
    private void refreshListStatus() {
        switch (orderType) {
            case "00":
                showList.clear();
                showList.addAll(orderList);
                mAdapter.notifyDataSetChanged();
                break;
            case "01":
                showList.clear();
                for (JSONObject object : orderList) {
                    try {
                        if (object.getString("stt").equals("01")) {
                            showList.add(object);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            case "02":
                showList.clear();
                for (JSONObject object : orderList) {
                    try {
                        if (object.getString("stt").equals("02")) {
                            showList.add(object);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            case "03":
                showList.clear();
                for (JSONObject object : orderList) {
                    try {
                        if (object.getString("stt").equals("00")) {
                            // TODO: 2019/8/30  
                            showList.add(object);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            case "04":
                showList.clear();
                for (JSONObject object : orderList) {
                    try {
                        if (object.getString("stt").equals("04")) {
                            showList.add(object);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

}
