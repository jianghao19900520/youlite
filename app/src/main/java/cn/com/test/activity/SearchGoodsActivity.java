package cn.com.test.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
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
import cn.com.test.constant.Constant;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;

public class SearchGoodsActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.search_goods_gridview)
    GridView search_goods_gridview;

    private List<JSONObject> goodsList;
    private CommAdapter<JSONObject> mAdapter;
    private String searchKey = "";

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_goods);
    }

    @Override
    public void initTitle() {
        title.setText("商品搜索");
    }

    @Override
    public void init() {
        searchKey = getIntent().getStringExtra("searchKey");
        if (TextUtils.isEmpty(searchKey)) {
            return;
        }
        goodsList = new ArrayList<>();
        mAdapter = new CommAdapter<JSONObject>(mContext, goodsList, R.layout.item_search_goods) {
            @Override
            public void convert(final CommViewHolder holder, final JSONObject item, int position) {
                try {
                    holder.setImageByUrl(R.id.goods_img, item.getString("toLoad"));//商品图片
                    holder.setText(R.id.goods_name, item.getString("goodsName"));//商品名称
                    holder.setText(R.id.item_store_newprice, item.getString("newPrice"));//商品现价
                    TextView oldPrice = holder.getView(R.id.item_store_oldprice);//商品原价
                    oldPrice.setText(item.getString("oldPrice"));
                    oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                startActivity(new Intent(mContext, GoodsInfoActivity.class).putExtra("goodsId", item.getString("goodsNo")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        search_goods_gridview.setAdapter(mAdapter);
        loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
    }

    /**
     * @param what 1.条件查询
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("page", 0);
                object.put("limit", 10);
                object.put("keyword", searchKey);
                relativeUrl = "health/goodsList";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        JSONObject result = jsonObject.getJSONObject("result");
                        if (status == 0) {
                            if (what == 1) {
                                JSONArray list = result.getJSONArray("list");
                                setSearchGoodsList(list);
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

    @OnClick({R.id.title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title:
                break;
        }
    }

    public void setSearchGoodsList(JSONArray searchGoodsList) throws JSONException {
        for (int i = 0; i < searchGoodsList.length(); i++) {
            goodsList.add(searchGoodsList.getJSONObject(i));
            mAdapter.notifyDataSetChanged();
        }
    }
}
