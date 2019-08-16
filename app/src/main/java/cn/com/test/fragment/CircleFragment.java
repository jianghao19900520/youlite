package cn.com.test.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yanzhenjie.nohttp.RequestMethod;
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
import cn.com.test.activity.CirclePostingActivity;
import cn.com.test.activity.HomeActivity;
import cn.com.test.activity.OrderDetailActivity;
import cn.com.test.adapter.CommAdapter;
import cn.com.test.adapter.CommViewHolder;
import cn.com.test.base.BaseFragment;
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
                    View item_circle_content = holder.getView(R.id.item_circle_content);
                    View item_circle_pic = holder.getView(R.id.item_circle_pic);
                    switch (item.getInt("type")) {
                        case 1:
                            holder.setText(R.id.item_circle_name, "匿名用户1");
                            item_circle_content.setVisibility(View.VISIBLE);
                            item_circle_pic.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            holder.setText(R.id.item_circle_name, "匿名用户2");
                            item_circle_content.setVisibility(View.GONE);
                            item_circle_pic.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            holder.setText(R.id.item_circle_name, "匿名用户3");
                            item_circle_content.setVisibility(View.VISIBLE);
                            item_circle_pic.setVisibility(View.GONE);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        circle_listview.setAdapter(mAdapter);
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

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

    @OnClick({R.id.new_dynamic_text, R.id.friend_dynamic_text, R.id.circle_posting_layout})
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
        }
    }

}
