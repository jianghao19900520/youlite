package cn.com.test.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
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
import cn.com.test.activity.CircleListActivity;
import cn.com.test.activity.CirclePostingActivity;
import cn.com.test.activity.HomeActivity;
import cn.com.test.activity.OrderDetailActivity;
import cn.com.test.adapter.CommAdapter;
import cn.com.test.adapter.CommViewHolder;
import cn.com.test.base.BaseFragment;
import cn.com.test.constant.Constant;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;
import cn.com.test.view.ListViewForScrollView;

public class CircleFragment extends BaseFragment implements OnBannerListener {

    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.circle_listview)
    ListViewForScrollView circle_listview;
    @BindView(R.id.new_dynamic_text)
    TextView new_dynamic_text;
    @BindView(R.id.new_dynamic_line)
    View new_dynamic_line;
    @BindView(R.id.friend_dynamic_text)
    TextView friend_dynamic_text;
    @BindView(R.id.friend_dynamic_line)
    View friend_dynamic_line;
    @BindView(R.id.circle_type_text_1)
    TextView circle_type_text_1;
    @BindView(R.id.circle_type_img_1)
    ImageView circle_type_img_1;
    @BindView(R.id.circle_type_text_2)
    TextView circle_type_text_2;
    @BindView(R.id.circle_type_img_2)
    ImageView circle_type_img_2;
    @BindView(R.id.circle_type_text_3)
    TextView circle_type_text_3;
    @BindView(R.id.circle_type_img_3)
    ImageView circle_type_img_3;
    @BindView(R.id.circle_type_text_4)
    TextView circle_type_text_4;
    @BindView(R.id.circle_type_img_4)
    ImageView circle_type_img_4;

    private ArrayList<String> banner_path;
    private ArrayList<String> banner_title;
    private List<JSONObject> circleList;
    private CommAdapter<JSONObject> mAdapter;

    @Override
    public View setContent(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_circle, null);
    }

    @Override
    public void initTitle() {
        HomeActivity activity = (HomeActivity) getActivity();
        activity.title.setText("圈子");
    }

    @Override
    public void init() {
        banner_path = new ArrayList<>();
        banner_title = new ArrayList<>();
        banner_path.add("http://pic.90sjimg.com/back_pic/00/00/69/40/3d07141c9523530da7b3dca9878413ec.jpg");
        banner_path.add("http://aliyunzixunbucket.oss-cn-beijing.aliyuncs.com/jpg/f8f05342c765bdca7fd92ecf302c6a57.jpg?x-oss-process=image/resize,p_100/auto-orient,1/quality,q_90/format,jpg/watermark,image_eXVuY2VzaGk=,t_100");
        banner_path.add("http://pic.90sjimg.com/back_pic/00/00/69/40/531ac7b7f8b61276f1ad2dd0dd02921b.jpg");
        banner_title.add("图片一");
        banner_title.add("图片二");
        banner_title.add("图片三");
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setImageLoader(new MyLoader());
        banner.setBannerAnimation(Transformer.Default);
        banner.setBannerTitles(banner_title);
        banner.setDelayTime(3000);
        banner.isAutoPlay(true);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setImages(banner_path)
                .setOnBannerListener(this)
                .start();

        circleList = new ArrayList<>();
        try {
            circleList.add(new JSONObject().put("type", 1));
            circleList.add(new JSONObject().put("type", 2));
            circleList.add(new JSONObject().put("type", 3));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter = new CommAdapter<JSONObject>(mContext, circleList, R.layout.item_circle) {
            @Override
            public void convert(final CommViewHolder holder, JSONObject item, int position) {
                try {
                    View item_circle_title = holder.getView(R.id.item_circle_title);
                    View item_circle_pic = holder.getView(R.id.item_circle_pic);
                    switch (item.getInt("type")) {
                        case 1:
                            holder.setText(R.id.item_circle_name, "匿名用户1");
                            item_circle_title.setVisibility(View.VISIBLE);
                            item_circle_pic.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            holder.setText(R.id.item_circle_name, "匿名用户2");
                            item_circle_title.setVisibility(View.GONE);
                            item_circle_pic.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            holder.setText(R.id.item_circle_name, "匿名用户3");
                            item_circle_title.setVisibility(View.VISIBLE);
                            item_circle_pic.setVisibility(View.GONE);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        circle_listview.setAdapter(mAdapter);
        loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
    }

    /**
     * @param what 1.获取帖子类型
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                relativeUrl = "health/bbsType";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                JSONArray list = result.getJSONArray("list");
                                if (list.length() >= 4) {
                                    circle_type_text_1.setText(list.getJSONObject(0).getString("typeName"));
                                    circle_type_img_1.setTag(list.getJSONObject(0).getString("typeNo"));
                                    circle_type_text_2.setText(list.getJSONObject(1).getString("typeName"));
                                    circle_type_img_2.setTag(list.getJSONObject(1).getString("typeNo"));
                                    circle_type_text_3.setText(list.getJSONObject(2).getString("typeName"));
                                    circle_type_img_3.setTag(list.getJSONObject(2).getString("typeNo"));
                                    circle_type_text_4.setText(list.getJSONObject(3).getString("typeName"));
                                    circle_type_img_4.setTag(list.getJSONObject(3).getString("typeNo"));
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
    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext())
                    .load((String) path)
                    .into(imageView);
        }
    }

    @OnClick({R.id.new_dynamic_text, R.id.friend_dynamic_text, R.id.circle_posting_layout,
            R.id.circle_type_img_1, R.id.circle_type_img_2, R.id.circle_type_img_3, R.id.circle_type_img_4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.new_dynamic_text:
                new_dynamic_text.setTextColor(getResources().getColor(R.color.mainColor));
                friend_dynamic_text.setTextColor(Color.parseColor("#999999"));
                new_dynamic_line.setVisibility(View.VISIBLE);
                friend_dynamic_line.setVisibility(View.INVISIBLE);
                break;
            case R.id.friend_dynamic_text:
                new_dynamic_text.setTextColor(Color.parseColor("#999999"));
                friend_dynamic_text.setTextColor(getResources().getColor(R.color.mainColor));
                new_dynamic_line.setVisibility(View.INVISIBLE);
                friend_dynamic_line.setVisibility(View.VISIBLE);
                break;
            case R.id.circle_posting_layout:
                startActivity(new Intent(mContext, CirclePostingActivity.class));
                break;
            case R.id.circle_type_img_1:
                startActivity(new Intent(mContext, CircleListActivity.class).putExtra("typeNo", (String) circle_type_img_1.getTag()).putExtra("typeName", circle_type_text_1.getText()));
                break;
            case R.id.circle_type_img_2:
                startActivity(new Intent(mContext, CircleListActivity.class).putExtra("typeNo", (String) circle_type_img_2.getTag()).putExtra("typeName", circle_type_text_2.getText()));
                break;
            case R.id.circle_type_img_3:
                startActivity(new Intent(mContext, CircleListActivity.class).putExtra("typeNo", (String) circle_type_img_3.getTag()).putExtra("typeName", circle_type_text_3.getText()));
                break;
            case R.id.circle_type_img_4:
                startActivity(new Intent(mContext, CircleListActivity.class).putExtra("typeNo", (String) circle_type_img_4.getTag()).putExtra("typeName", circle_type_text_4.getText()));
                break;
        }
    }

}
