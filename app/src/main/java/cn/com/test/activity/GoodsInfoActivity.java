package cn.com.test.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yanzhenjie.nohttp.RequestMethod;
import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
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

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_goods_info);
    }

    @Override
    public void initTitle() {
        title.setText("商品详情");
    }

    @OnClick({R.id.goods_main_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.goods_main_img:
                ToastUtils.showShort("textview");
                break;
        }
    }

    @Override
    public void init() {
        setViewHeightByWidth(goods_main_img);
        goods_main_img.setImageResource(R.mipmap.ic_logo);
        goods_price_old.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
        ImageView iv = new ImageView(mContext);
        iv.setImageResource(R.mipmap.ic_logo);
        setViewHeightByWidth(iv);
        goods_info_imgs.addView(iv);
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

}
