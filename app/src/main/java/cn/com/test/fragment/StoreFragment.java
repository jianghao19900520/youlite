package cn.com.test.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.activity.CartActivity;
import cn.com.test.activity.GoodsInfoActivity;
import cn.com.test.activity.HomeActivity;
import cn.com.test.activity.MyOrderActivity;
import cn.com.test.activity.SearchActivity;
import cn.com.test.adapter.CommAdapter;
import cn.com.test.adapter.CommViewHolder;
import cn.com.test.base.BaseFragment;
import cn.com.test.http.BaseCallBack;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.ToastUtils;
import cn.com.test.view.ListViewForScrollView;

public class StoreFragment extends BaseFragment implements OnBannerListener {

    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.store_new_layout)
    LinearLayout store_new_layout;
    @BindView(R.id.store_hot_list)
    ListViewForScrollView store_hot_list;

    private ArrayList<String> banner_path;
    private ArrayList<String> bannner_title;
    private List<JSONObject> goodsList;
    private CommAdapter<JSONObject> mAdapter;

    @Override
    public View setContent(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_store, null);
    }

    @Override
    public void initTitle() {
        HomeActivity activity = (HomeActivity) getActivity();
        activity.title.setText("商城");
    }

    @Override
    public void init() {
        banner_path = new ArrayList<>();
        bannner_title = new ArrayList<>();
        goodsList = new ArrayList<>();
        goodsList.add(new JSONObject());
        goodsList.add(new JSONObject());
        goodsList.add(new JSONObject());
        goodsList.add(new JSONObject());
        goodsList.add(new JSONObject());
        banner_path.add("http://pic.90sjimg.com/back_pic/00/00/69/40/3d07141c9523530da7b3dca9878413ec.jpg");
        banner_path.add("http://aliyunzixunbucket.oss-cn-beijing.aliyuncs.com/jpg/f8f05342c765bdca7fd92ecf302c6a57.jpg?x-oss-process=image/resize,p_100/auto-orient,1/quality,q_90/format,jpg/watermark,image_eXVuY2VzaGk=,t_100");
        banner_path.add("http://pic.90sjimg.com/back_pic/00/00/69/40/531ac7b7f8b61276f1ad2dd0dd02921b.jpg");
        bannner_title.add("图片一");
        bannner_title.add("图片二");
        bannner_title.add("图片三");
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setImageLoader(new StoreFragment.BanerImgLoader());
        banner.setBannerAnimation(Transformer.Default);
        banner.setBannerTitles(bannner_title);
        banner.setDelayTime(3000);
        banner.isAutoPlay(true);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setImages(banner_path)
                .setOnBannerListener(this)
                .start();
        mAdapter = new CommAdapter<JSONObject>(mContext, goodsList, R.layout.item_store_goods_hot) {
            @Override
            public void convert(CommViewHolder holder, JSONObject item, int position) {
                try {
                    TextView oldPrice = holder.getView(R.id.item_store_hot_oldprice);
                    oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(mContext, GoodsInfoActivity.class).putExtra("goodsId", "123456"));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        store_hot_list.setAdapter(mAdapter);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_store_goods_new, null);
        TextView oldPrice = view.findViewById(R.id.item_store_new_oldprice);
        oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
        store_new_layout.addView(view);
        View view2 = LayoutInflater.from(mContext).inflate(R.layout.item_store_goods_new, null);
        TextView oldPrice2 = view2.findViewById(R.id.item_store_new_oldprice);
        oldPrice2.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
        store_new_layout.addView(view2);
        View view3 = LayoutInflater.from(mContext).inflate(R.layout.item_store_goods_new, null);
        TextView oldPrice3 = view3.findViewById(R.id.item_store_new_oldprice);
        oldPrice3.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
        store_new_layout.addView(view3);
        View view4 = LayoutInflater.from(mContext).inflate(R.layout.item_store_goods_new, null);
        TextView oldPrice4 = view4.findViewById(R.id.item_store_new_oldprice);
        oldPrice4.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
        store_new_layout.addView(view4);
        View view5 = LayoutInflater.from(mContext).inflate(R.layout.item_store_goods_new, null);
        TextView oldPrice5 = view5.findViewById(R.id.item_store_new_oldprice);
        oldPrice5.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
        store_new_layout.addView(view5);
        View view6 = LayoutInflater.from(mContext).inflate(R.layout.item_store_goods_new, null);
        TextView oldPrice6 = view6.findViewById(R.id.item_store_new_oldprice);
        oldPrice6.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
        store_new_layout.addView(view6);
        store_new_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, GoodsInfoActivity.class).putExtra("goodsId", "123456"));
            }
        });
        loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
    }

    /**
     * @param what 1.获取热销商品数据
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("pageIndex", "0");
                object.put("pageSize", "10");
                object.put("code", "1009000007");
                object.put("classId", "2vom16jhrfz");
                object.put("classLevel", "3");
                object.put("operatorNo", "1009000007");
                relativeUrl = "product/searchb2c";
            }
            NetHelper.getInstance().request(mContext, relativeUrl, object, method, new BaseCallBack(what, msg) {
                @Override
                public void onSucceed(int what, Response response) {
                    try {
                        JSONObject jsonObject = (JSONObject) response.get();
                        if (jsonObject.toString().contains("code")) {
                            int code = jsonObject.getInt("code");
                            if (code == 0) {
                                if (what == 1) {
                                    ToastUtils.showShort("接口请求成功");
                                }
                            } else {
                                ToastUtils.showShort(jsonObject.getString("message"));
                            }
                        } else {
                            ToastUtils.showShort(getString(R.string.error_http));
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

    /**
     * banner轮播监听
     */
    @Override
    public void OnBannerClick(int position) {
        Toast.makeText(mContext, "你点了第" + (position + 1) + "张轮播图", Toast.LENGTH_SHORT).show();
    }

    /**
     * banner加载图片
     */
    private class BanerImgLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext())
                    .load((String) path)
                    .into(imageView);
        }
    }

    @OnClick({R.id.store_cart_btn, R.id.store_my_order_btn, R.id.store_search_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.store_cart_btn:
                startActivity(new Intent(mContext, CartActivity.class));
                break;
            case R.id.store_my_order_btn:
                startActivity(new Intent(mContext, MyOrderActivity.class));
                break;
            case R.id.store_search_layout:
                startActivity(new Intent(mContext, SearchActivity.class));
                break;
        }
    }

}
