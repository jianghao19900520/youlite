package cn.com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.fragment.CheckFragment;
import cn.com.test.fragment.CircleFragment;
import cn.com.test.fragment.MineFragment;
import cn.com.test.fragment.StoreFragment;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.title)
    public TextView title;
    @BindView(R.id.title_back_btn)
    ImageView title_back_btn;
    @BindView(R.id.tabs_rg)
    RadioGroup mTabRadioGroup;

    private SparseArray<Fragment> mFragmentSparseArray;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
    }

    @Override
    public void initTitle() {
        title.setText("标题");
        title_back_btn.setVisibility(View.GONE);
    }

    @Override
    public void init() {
        mFragmentSparseArray = new SparseArray<>();
        mFragmentSparseArray.append(R.id.today_tab, new CheckFragment());
        mFragmentSparseArray.append(R.id.record_tab, new CircleFragment());
        mFragmentSparseArray.append(R.id.contact_tab, new StoreFragment());
        mFragmentSparseArray.append(R.id.settings_tab, new MineFragment());
        mTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 具体的fragment切换逻辑可以根据应用调整，例如使用show()/hide()
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        mFragmentSparseArray.get(checkedId)).commit();
            }
        });
        // 默认显示第一个
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                mFragmentSparseArray.get(R.id.today_tab)).commit();
        findViewById(R.id.sign_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里是中间按钮的点击事件
            }
        });
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }
}
