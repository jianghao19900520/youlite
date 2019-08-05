package cn.com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.utils.ToastUtils;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.search_content_text)
    EditText search_content_text;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
    }

    @Override
    public void initTitle() {
        title.setText("搜索");
    }

    @Override
    public void init() {

    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

    @OnClick({R.id.search_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_btn:
                if (!TextUtils.isEmpty(search_content_text.getText().toString())) {
                    ToastUtils.showShort(search_content_text.getText().toString());
                }
                break;
        }
    }
}
