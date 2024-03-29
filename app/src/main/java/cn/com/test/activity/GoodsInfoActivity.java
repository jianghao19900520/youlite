package cn.com.test.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atoliu.redpoint.TLRedPointView;
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
import cn.com.test.constant.Constant;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.LoginUtils;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;

public class GoodsInfoActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.goods_main_img)
    ImageView goods_main_img;
    @BindView(R.id.goods_name)
    TextView goods_name;
    @BindView(R.id.goods_price_new)
    TextView goods_price_new;
    @BindView(R.id.goods_price_old)
    TextView goods_price_old;
    @BindView(R.id.goods_info_imgs)
    LinearLayout goods_info_imgs;
    @BindView(R.id.cart_num_text)
    TLRedPointView cart_num_text;

    private String goodsId;//商品id
    private String toLoad;//商品图片
    private String goodsName = "";//商品名称
    private String goodsPriceNew = "0.00";//商品现价
    private String goodsPriceOld = "0.00";//商品原价

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_goods_info);
    }

    @Override
    public void initTitle() {
        title.setText("商品详情");
    }

    @OnClick({R.id.goods_info_cart_btn, R.id.add_cart_btn, R.id.buy_now_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.goods_info_cart_btn:
                if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                    startActivity(new Intent(mContext, CartActivity.class));
                }
                break;
            case R.id.add_cart_btn:
                if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                    loadData(3, null, getString(R.string.string_loading), RequestMethod.POST);
                }
                break;
            case R.id.buy_now_btn:
                if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                    List<CartBean> submitOrderList = new ArrayList();
                    CartBean bean = new CartBean(goodsId, 1, goodsName, goodsPriceNew, goodsPriceOld, toLoad);
                    submitOrderList.add(bean);
                    Bundle bundleObject = new Bundle();
                    bundleObject.putSerializable("goodsList", (Serializable) submitOrderList);
                    startActivity(new Intent(mContext, ConfirmOrderActivity.class).putExtras(bundleObject));
                    finish();
                }
                break;
        }
    }

    @Override
    public void init() {
        goodsId = getIntent().getStringExtra("goodsId");
        if (TextUtils.isEmpty(goodsId)) {
            finish();
        }
        setViewHeightByWidth(goods_main_img);
        cart_num_text.isShowZeroNumPoint(false);
        cart_num_text.setZeroRadius(10);
        cart_num_text.setmCount(0);
        loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(2, null, "", RequestMethod.GET);
    }

    /**
     * @param what 1.获取商品详情 2.获取购物车列表 3.添加到购物车
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("goodsNo", goodsId);
                relativeUrl = "health/goodsInfo";
            } else if (what == 2) {
                relativeUrl = "health/cartList";
            } else if (what == 3) {
                object.put("goodsNo", goodsId);
                object.put("num", 1);
                relativeUrl = "health/addCart";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                Glide.with(mContext).load(result.getString("toLoad")).into(goods_main_img);
                                goodsName = result.getString("goodsName");//商品名称
                                toLoad = result.getString("toLoad");//商品图片
                                goodsPriceNew = result.getString("newPrice");//商品现价
                                goodsPriceOld = result.getString("oldPrice");//商品原价
                                goods_name.setText(goodsName);
                                goods_price_new.setText(goodsPriceNew);
                                goods_price_old.setText(goodsPriceOld);
                                goods_price_old.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
                                JSONArray picList = result.getJSONArray("picList");
                                for (int i = 0; i < picList.length(); i++) {
                                    ImageView iv = new ImageView(mContext);
                                    Glide.with(mContext).load(picList.getJSONObject(i).getString("toLoad")).into(iv);
                                    goods_info_imgs.addView(iv);
                                }
                            } else if (what == 2 || what == 3) {
                                cartRedpoint(result.getJSONArray("carList"));
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

    /*
    将ImageView设为宽高相等
     */
    public void setViewHeightByWidth(ImageView view) {
        final ImageView mv = view;
        final ViewTreeObserver observer = mv.getViewTreeObserver();
        final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                android.view.ViewGroup.LayoutParams lp = mv.getLayoutParams();
                lp.height = mv.getMeasuredWidth();
                mv.setLayoutParams(lp);
                final ViewTreeObserver vto1 = mv.getViewTreeObserver();
                vto1.removeOnPreDrawListener(this);
                return true;
            }
        };
        observer.addOnPreDrawListener(preDrawListener);
    }

    /*
    更新购物车红点
     */
    public void cartRedpoint(JSONArray carList) throws JSONException {
        int num = 0;
        for (int i = 0; i < carList.length(); i++) {
            num += carList.getJSONObject(i).getInt("num");
        }
        cart_num_text.setmCount(num);
    }

}
