package cn.com.test.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import org.json.JSONException;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.http.BaseCallBack;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.ToastUtils;

public class TestActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_test);
    }

    @Override
    public void initTitle() {
        title.setText("标题");
    }

    @Override
    public void init() {

    }

    @OnClick({R.id.textview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.textview:
                loadData(1, null, getString(R.string.loading_hint), RequestMethod.GET);
                break;
        }
    }

    /**
     * @param what 1.测试
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                //object.put("key", "value");
                relativeUrl = "http://api.k780.com:88/?app=life.time&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
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
                                    ToastUtils.showShort("成功");
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


}
