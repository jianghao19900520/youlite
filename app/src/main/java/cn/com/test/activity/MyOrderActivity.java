package cn.com.test.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import org.json.JSONException;
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

    private int orderType = 0;//0=全部 1=待付款 2=待收货 3=已完成 4=已取消
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
        try {
            orderList.add(new JSONObject().put("orderType", 1));
            orderList.add(new JSONObject().put("orderType", 2));
            orderList.add(new JSONObject().put("orderType", 3));
            orderList.add(new JSONObject().put("orderType", 4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showList.addAll(orderList);
        mAdapter = new CommAdapter<JSONObject>(mContext, showList, R.layout.item_my_order) {
            @Override
            public void convert(CommViewHolder holder, JSONObject item, int position) {
                try {
                    LinearLayout item_my_order_goods_layout = holder.getView(R.id.item_my_order_goods_layout);
                    TextView left_text = holder.getView(R.id.item_my_order_bottom_left_text);
                    TextView right_text = holder.getView(R.id.item_my_order_bottom_right_text);
                    item_my_order_goods_layout.removeAllViews();
                    for (int i = 0; i < item.getInt("orderType") * 2; i++) {
                        ImageView img = new ImageView(mContext);
                        img.setImageResource(R.mipmap.ic_logo);
                        item_my_order_goods_layout.addView(img);
                    }
                    switch (item.getInt("orderType")) {
                        case 1:
                            holder.setText(R.id.item_my_order_status_text, "待付款");
                            holder.getView(R.id.item_my_order_delete_img).setVisibility(View.GONE);
                            left_text.setVisibility(View.GONE);
                            right_text.setText("支付");
                            right_text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ToastUtils.showShort("支付");
                                }
                            });
                            break;
                        case 2:
                            holder.setText(R.id.item_my_order_status_text, "待收货");
                            holder.getView(R.id.item_my_order_delete_img).setVisibility(View.GONE);
                            left_text.setVisibility(View.VISIBLE);
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
                                    ToastUtils.showShort("确认收货");
                                }
                            });
                            break;
                        case 3:
                            holder.setText(R.id.item_my_order_status_text, "已完成");
                            holder.getView(R.id.item_my_order_delete_img).setVisibility(View.VISIBLE);
                            left_text.setVisibility(View.VISIBLE);
                            left_text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ToastUtils.showShort("查看物流");
                                }
                            });
                            right_text.setText("再次购买");
                            right_text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ToastUtils.showShort("再次购买");
                                }
                            });
                            break;
                        case 4:
                            holder.setText(R.id.item_my_order_status_text, "再次购买");
                            holder.getView(R.id.item_my_order_delete_img).setVisibility(View.VISIBLE);
                            left_text.setVisibility(View.GONE);
                            right_text.setText("再次购买");
                            right_text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ToastUtils.showShort("再次购买");
                                }
                            });
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        order_list.setAdapter(mAdapter);
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

    @OnClick({R.id.order_all_text, R.id.order_pay_text, R.id.order_receive_text, R.id.order_finish_text, R.id.order_cancle_text})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.order_all_text:
                orderType = 0;
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
                orderType = 1;
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
                orderType = 2;
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
                orderType = 3;
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
                orderType = 4;
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

    /**
     * 刷新列表状态
     */
    private void refreshListStatus() {
        switch (orderType) {
            case 0:
                showList.clear();
                showList.addAll(orderList);
                mAdapter.notifyDataSetChanged();
                break;
            case 1:
                showList.clear();
                for (JSONObject object : orderList) {
                    try {
                        if (object.getInt("orderType") == 1) {
                            showList.add(object);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            case 2:
                showList.clear();
                for (JSONObject object : orderList) {
                    try {
                        if (object.getInt("orderType") == 2) {
                            showList.add(object);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            case 3:
                showList.clear();
                for (JSONObject object : orderList) {
                    try {
                        if (object.getInt("orderType") == 3) {
                            showList.add(object);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            case 4:
                showList.clear();
                for (JSONObject object : orderList) {
                    try {
                        if (object.getInt("orderType") == 4) {
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
