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
import android.widget.ScrollView;
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

import org.json.JSONArray;
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
import cn.com.test.constant.Constant;
import cn.com.test.http.BaseCallBack;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.LoginUtils;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;
import cn.com.test.view.ListViewForScrollView;

public class StoreFragment extends BaseFragment implements OnBannerListener {

    @BindView(R.id.store_scroll_view)
    ScrollView store_scroll_view;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.store_new_layout)
    LinearLayout store_new_layout;
    @BindView(R.id.store_hot_list)
    ListViewForScrollView store_hot_list;

    private ArrayList<String> banner_path;
    private ArrayList<String> bannner_title;
    private ArrayList<String> bannner_goodsId;
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
        bannner_goodsId = new ArrayList<>();
        goodsList = new ArrayList<>();
        mAdapter = new CommAdapter<JSONObject>(mContext, goodsList, R.layout.item_store_goods_hot) {
            @Override
            public void convert(CommViewHolder holder, final JSONObject item, int position) {
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
        store_hot_list.setAdapter(mAdapter);
        loadData(1, null, getString(R.string.string_loading), RequestMethod.GET);
    }

    /**
     * @param what 1.商城首页数据
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                relativeUrl = "health/storeInfo";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                JSONArray bannerList = result.getJSONArray("bannerList");//banner
                                JSONArray newGoodsList = result.getJSONArray("newGoodsList");//新品上架
                                JSONArray hotGoodsList = result.getJSONArray("hotGoodsList");//热销商品
                                setBanner(bannerList);
                                setNewGoodsList(newGoodsList);
                                setHotGoodsList(hotGoodsList);
                                store_scroll_view.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        store_scroll_view.scrollTo(0, 0);
                                    }
                                }, 200);
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

    /**
     * banner
     */
    public void setBanner(JSONArray bannerList) throws JSONException {
        for (int i = 0; i < bannerList.length(); i++) {
            JSONObject bannerObject = bannerList.getJSONObject(i);
            banner_path.add(bannerObject.getString("toLoad"));
            bannner_title.add(bannerObject.getString("title"));
            bannner_goodsId.add(bannerObject.getString("goodsNo"));
        }
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
    }

    /**
     * banner轮播监听
     */
    @Override
    public void OnBannerClick(int position) {
        startActivity(new Intent(mContext, GoodsInfoActivity.class).putExtra("goodsId", bannner_goodsId.get(position)));
    }

    /**
     * banner加载图片
     */
    private class BanerImgLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(mContext).load((String) path).into(imageView);
        }
    }

    /**
     * 新品上架
     */
    public void setNewGoodsList(JSONArray newGoodsList) throws JSONException {
        for (int i = 0; i < newGoodsList.length(); i++) {
            final JSONObject newGoodsObject = newGoodsList.getJSONObject(i);
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_store_goods_new, null);
            ImageView goods_img = view.findViewById(R.id.goods_img);
            Glide.with(mContext).load(newGoodsObject.getString("toLoad")).into(goods_img);//商品图片
            TextView goods_name = view.findViewById(R.id.goods_name);
            goods_name.setText(newGoodsObject.getString("goodsName"));//商品名称
            TextView newPrice = view.findViewById(R.id.item_store_newprice);
            newPrice.setText(newGoodsObject.getString("newPrice"));//商品现价
            TextView oldPrice = view.findViewById(R.id.item_store_oldprice);
            oldPrice.setText(newGoodsObject.getString("oldPrice"));//商品原价
            oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(mContext, GoodsInfoActivity.class).putExtra("goodsId", newGoodsObject.getString("goodsNo")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            store_new_layout.addView(view);
        }
    }

    /**
     * 热销商品
     */
    public void setHotGoodsList(JSONArray newGoodsList) throws JSONException {
        for (int i = 0; i < newGoodsList.length(); i++) {
            goodsList.add(newGoodsList.getJSONObject(i));
            mAdapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.store_cart_btn, R.id.store_my_order_btn, R.id.store_search_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.store_cart_btn:
                if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                    startActivity(new Intent(mContext, CartActivity.class));
                }
                break;
            case R.id.store_my_order_btn:
                if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                    startActivity(new Intent(mContext, MyOrderActivity.class));
                }
                break;
            case R.id.store_search_layout:
                startActivity(new Intent(mContext, SearchActivity.class));
                break;
        }
    }

}
