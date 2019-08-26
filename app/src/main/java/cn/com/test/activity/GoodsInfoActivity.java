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
    private String goodsName = "CKD低磷蛋白粉体验装";//商品名称
    private String goodsPriceNew = "99.00";//商品现价
    private String goodsPriceOld = "199.00";//商品原价

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
                startActivity(new Intent(mContext, CartActivity.class));
                break;
            case R.id.add_cart_btn:
                List<CartBean> all = DataSupport.findAll(CartBean.class);
                for (CartBean bean : all) {
                    if (bean.getGoodsId().equals(goodsId)) {
                        //已经有数据，就数量+1
                        bean.setGoodsNum(bean.getGoodsNum() + 1);
                        bean.save();
                        cartRedpoint();
                        return;
                    }
                }
                //数据库还没有该条数据，则新增
                //CartBean bean = new CartBean(goodsId, 1, "CKD低磷蛋白粉体验装", "￥99.00", "￥199.00");
                //bean.save();
                new CartBean(goodsId, 1, "低磷蛋白粉体验装111", "99.00", "199.00").save();
                new CartBean("654321", 2, "低磷蛋白粉体验装222", "39.00", "79.00").save();
                cartRedpoint();
                break;
            case R.id.buy_now_btn:
                List<CartBean> submitOrderList = new ArrayList();
                //先检查该商品是否已经添加到购物车
                List<CartBean> all2 = DataSupport.findAll(CartBean.class);
                for (CartBean bean2 : all2) {
                    if (bean2.getGoodsId().equals(goodsId)) {
                        //有的话就直接拿来用
                        submitOrderList.add(bean2);
                    }
                }
                if (submitOrderList.size() == 0) {
                    //没有的话就新建一个，同时也添加到购物车
                    CartBean bean3 = new CartBean(goodsId, 1, goodsName, goodsPriceNew, goodsPriceOld);
                    bean3.save();
                    submitOrderList.add(bean3);
                }
                Bundle bundleObject = new Bundle();
                bundleObject.putSerializable("goodsList", (Serializable) submitOrderList);
                startActivity(new Intent(mContext, ConfirmOrderActivity.class).putExtras(bundleObject));
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
        Glide.with(mContext).load("http://pic.90sjimg.com/back_pic/00/00/69/40/531ac7b7f8b61276f1ad2dd0dd02921b.jpg").into(goods_main_img);
        goods_price_old.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
        for (int i = 0; i < 5; i++) {
            ImageView iv = new ImageView(mContext);
            Glide.with(mContext).load("http://pic.90sjimg.com/back_pic/00/00/69/40/531ac7b7f8b61276f1ad2dd0dd02921b.jpg").into(iv);
            goods_info_imgs.addView(iv);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cartRedpoint();
    }

    /**
     * @param what 1.密码登录
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("goodsNo", goodsId);
                relativeUrl = "health/goodsInfo";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                String token = result.getString("token");
                                if (!TextUtils.isEmpty(token)) {
                                    SPUtils.getInstance().put(Constant.token, token);
                                    finish();
                                }
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
    public void cartRedpoint() {
        int num = 0;
        List<CartBean> all = DataSupport.findAll(CartBean.class);
        for (CartBean bean : all) {
            num += bean.getGoodsNum();
        }
        cart_num_text.isShowZeroNumPoint(false);
        cart_num_text.setZeroRadius(10);
        cart_num_text.setmCount(num);
    }

}
