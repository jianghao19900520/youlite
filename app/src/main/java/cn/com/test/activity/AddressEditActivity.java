package cn.com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.bean.CartBean;
import cn.com.test.constant.Constant;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;

public class AddressEditActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.address_name)
    EditText address_name;//名称
    @BindView(R.id.address_phone)
    EditText address_phone;//手机
    @BindView(R.id.address_simple)
    TextView address_simple;//省市区
    @BindView(R.id.address_detailed)
    EditText address_detailed;//详细地址

    private String addressId;
    private String province;//省
    private String city;//市
    private String area;//区

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_address_edit);
    }

    @Override
    public void initTitle() {
        addressId = getIntent().getStringExtra("addressId");
        if (TextUtils.isEmpty(addressId)) {
            title.setText("添加地址");
        } else {
            title.setText("修改地址");
        }
    }

    @Override
    public void init() {

    }

    /**
     * @param what 1.保存地址
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                if (TextUtils.isEmpty(addressId)) {
                    //添加地址
                    object.put("operate", "1");
                } else {
                    //修改地址
                    object.put("id", addressId);
                    object.put("operate", "2");
                }
                object.put("province", province);
                object.put("city", city);
                object.put("area", area);
                object.put("address", address_detailed.getText().toString().trim());
                object.put("linkMan", address_name.getText().toString().trim());
                object.put("linkPhone", address_phone.getText().toString().trim());
                object.put("isDefault", "0");//默认地址 0=否 1=是
                relativeUrl = "health/modifyAddress";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {

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

    @OnClick({R.id.address_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.address_save:
                if (TextUtils.isEmpty(address_name.getText().toString().trim())) {
                    ToastUtils.showShort("请填写收件人姓名");
                    return;
                }
                if (TextUtils.isEmpty(address_phone.getText().toString().trim())) {
                    ToastUtils.showShort("请填写收件人手机号");
                    return;
                }
                if (TextUtils.isEmpty(address_simple.getText().toString().trim())) {
                    ToastUtils.showShort("请选择选择省市区");
                    return;
                }
                if (TextUtils.isEmpty(address_detailed.getText().toString().trim())) {
                    ToastUtils.showShort("请填写详细地址");
                    return;
                }
                loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
                break;
        }
    }

}
