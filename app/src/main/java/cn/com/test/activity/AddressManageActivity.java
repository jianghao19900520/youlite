package cn.com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;

public class AddressManageActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.title_right_text_btn)
    TextView title_right_text_btn;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_address_manage);
    }

    @Override
    public void initTitle() {
        title.setText("地址管理");
        title_right_text_btn.setText("添加");
        title_right_text_btn.setVisibility(View.VISIBLE);
        title_right_text_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, AddressEditActivity.class).putExtra("addressId", ""));
            }
        });
    }

    @Override
    public void init() {

    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

}
