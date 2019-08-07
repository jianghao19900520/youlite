package cn.com.test.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

    public static final int EXTERNAL_STORAGE_REQ_CODE = 0;//6.0系统开始需要申请读写权限

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
        requestPermission();
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

    public void requestPermission() {
        //判断当前Activity是否已经获得了该权限
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
                Toast.makeText(mContext, "请手动在设置中打开读写权限", Toast.LENGTH_SHORT).show();
            } else {
                //进行权限请求
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_REQ_CODE);
            }
        }
    }

}
