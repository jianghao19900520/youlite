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

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.bean.CartBean;
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

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_goods_info);
    }

    @Override
    public void initTitle() {
        title.setText("商品详情");
    }

    @OnClick({R.id.goods_info_cart_btn, R.id.add_cart_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.goods_info_cart_btn:
                startActivity(new Intent(mContext, CartActivity.class));
                break;
            case R.id.add_cart_btn:
                try {
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
                    CartBean bean = new CartBean(goodsId, 1, "CKD低磷蛋白粉体验装", "￥99.00", "￥199.00");
                    bean.save();
                    cartRedpoint();
                } catch (Exception e) {
                    e.printStackTrace();
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

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

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
