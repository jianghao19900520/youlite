package cn.com.test.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.adapter.CommAdapter;
import cn.com.test.adapter.CommViewHolder;
import cn.com.test.base.BaseActivity;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
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
    @BindView(R.id.order_pay_money_text)
    TextView order_pay_money_text;

    private List<JSONObject> goodsList;
    private CommAdapter<JSONObject> mAdapter;
    private String orderNo;

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
        loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
    }

    /**
     * @param what 1.获取订单详情
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("orderNo", orderNo);
                relativeUrl = "health/orderDetail";
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

    private void setOrderDetail(JSONObject result) throws JSONException {
        address_name.setText(result.getString("linkMan") + "  " + result.getString("linkPhone"));
        address_text.setText(result.getString("address"));
        order_no_text.setText("订单编号 : " + result.getString("orderNo"));
        String createTime = result.getString("createTime");
        order_time_text.setText("下单时间 : " + createTime.substring(0, 4) + "-" + createTime.substring(4, 6) + "-" + createTime.substring(6, 8) + " " + createTime.substring(8, 10) + ":" + createTime.substring(10, 12) + ":" + createTime.substring(12, 14));
        order_total_money_text.setText("商品总额 : ￥" + result.getString("totalAmount"));
        order_postfee_text.setText("运费 : ￥" + result.getString("postFee"));
        order_pay_money_text.setText("￥" + result.getString("payAmount"));
    }

}
