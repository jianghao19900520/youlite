package cn.com.test.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.adapter.CommAdapter;
import cn.com.test.adapter.CommViewHolder;
import cn.com.test.base.BaseActivity;

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
        mAdapter = new CommAdapter<JSONObject>(mContext, orderList, R.layout.item_my_order) {
            @Override
            public void convert(CommViewHolder holder, JSONObject item, int position) {
                try {

                } catch (Exception e) {
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
                break;
            case R.id.order_pay_text:
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
                break;
            case R.id.order_receive_text:
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
                break;
            case R.id.order_finish_text:
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
                break;
            case R.id.order_cancle_text:
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
                break;
        }
    }

}
