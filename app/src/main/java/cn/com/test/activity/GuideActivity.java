package cn.com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;

public class GuideActivity extends BaseActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.point_layout)
    LinearLayout pointLayout;

    private List<View> data;
    private LayoutInflater inflater;
    private int selectedPoint = 0;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_guide);
    }

    @Override
    public void initTitle() {

    }

    @Override
    public void init() {
        inflater = LayoutInflater.from(this);
        data = new ArrayList<>();
        View pager3 = inflater.inflate(R.layout.guide_pager3, null);
        pager3.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this, HomeActivity.class));
                finish();
            }
        });
        data.add(inflater.inflate(R.layout.guide_pager1, null));
        data.add(inflater.inflate(R.layout.guide_pager2, null));
        data.add(pager3);

        PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = data.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

    private void selectedPoint(int position) {
        View selectedView = pointLayout.getChildAt(selectedPoint);
        selectedView.setBackgroundResource(R.drawable.white_circle);
        View newView = pointLayout.getChildAt(position);
        newView.setBackgroundResource(R.drawable.black_circle);
        selectedPoint = position;
    }
}
