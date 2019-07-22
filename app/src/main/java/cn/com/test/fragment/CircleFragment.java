package cn.com.test.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yanzhenjie.nohttp.RequestMethod;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.base.BaseFragment;

public class CircleFragment extends BaseFragment implements OnBannerListener {

    @BindView(R.id.banner)
    Banner banner;
    private ArrayList<String> list_path;
    private ArrayList<String> list_title;

    @Override
    public View setContent(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_circle, null);
    }

    @Override
    public void initTitle() {

    }

    @Override
    public void init() {
        list_path = new ArrayList<>();
        list_title = new ArrayList<>();
        list_path.add("http://pic.90sjimg.com/back_pic/00/00/69/40/3d07141c9523530da7b3dca9878413ec.jpg");
        list_path.add("http://aliyunzixunbucket.oss-cn-beijing.aliyuncs.com/jpg/f8f05342c765bdca7fd92ecf302c6a57.jpg?x-oss-process=image/resize,p_100/auto-orient,1/quality,q_90/format,jpg/watermark,image_eXVuY2VzaGk=,t_100");
        list_path.add("http://pic.90sjimg.com/back_pic/00/00/69/40/531ac7b7f8b61276f1ad2dd0dd02921b.jpg");
        list_title.add("图片一");
        list_title.add("图片二");
        list_title.add("图片三");
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setImageLoader(new MyLoader());
        banner.setBannerAnimation(Transformer.Default);
        banner.setBannerTitles(list_title);
        banner.setDelayTime(3000);
        banner.isAutoPlay(true);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setImages(list_path)
                .setOnBannerListener(this)
                .start();
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

}
