package cn.com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.OptionsPickerView;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.bean.AddressBean;
import cn.com.test.bean.CartBean;
import cn.com.test.constant.Constant;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.GetAssetsUtils;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;

public class AddressEditActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.address_name)
    EditText address_name;//名称
    @BindView(R.id.address_phone)
    EditText address_phone;//手机
    @BindView(R.id.address_text)
    TextView address_text;//省市区
    @BindView(R.id.address_detailed_text)
    EditText address_detailed_text;//详细地址

    private String addressId;
    private List<AddressBean> options1Items = new ArrayList<>();//省
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();//市
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();//区县
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
        initAddress();
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
                object.put("address", address_detailed_text.getText().toString().trim());
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
                                finish();
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

    @OnClick({R.id.address_text, R.id.address_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.address_text:
                ShowPickerView();
                break;
            case R.id.address_save:
                if (TextUtils.isEmpty(address_name.getText().toString().trim())) {
                    ToastUtils.showShort("请填写收件人姓名");
                    return;
                }
                if (TextUtils.isEmpty(address_phone.getText().toString().trim())) {
                    ToastUtils.showShort("请填写收件人手机号");
                    return;
                }
                if (TextUtils.isEmpty(address_text.getText().toString().trim())) {
                    ToastUtils.showShort("请选择选择省市区");
                    return;
                }
                if (TextUtils.isEmpty(address_detailed_text.getText().toString().trim())) {
                    ToastUtils.showShort("请填写详细地址");
                    return;
                }
                loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
                break;
        }
    }

    /*
    初始化省市县数据
     */
    private void initAddress() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //在子线程中解析省市区数据
                String jsonData = new GetAssetsUtils().getJson(mContext, "address.json");
                ArrayList<AddressBean> detail = new ArrayList<>();
                try {
                    JSONArray data = new JSONArray(jsonData);
                    for (int i = 0; i < data.length(); i++) {
                        AddressBean entity = JSON.parseObject(data.optJSONObject(i).toString(), AddressBean.class);
                        detail.add(entity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtils.showShort("数据解析失败");
                }
                //添加省份数据
                options1Items = detail;
                for (int i = 0; i < detail.size(); i++) {//遍历省份
                    ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
                    ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

                    for (int c = 0; c < detail.get(i).getCity().size(); c++) {//遍历该省份的所有城市
                        String CityName = detail.get(i).getCity().get(c).getName();
                        CityList.add(CityName);//添加城市
                        ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                        //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                        if (detail.get(i).getCity().get(c).getArea() == null
                                || detail.get(i).getCity().get(c).getArea().size() == 0) {
                            City_AreaList.add("");
                        } else {
                            for (int d = 0; d < detail.get(i).getCity().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                                String AreaName = detail.get(i).getCity().get(c).getArea().get(d);
                                City_AreaList.add(AreaName);//添加该城市所有地区数据
                            }
                        }
                        Province_AreaList.add(City_AreaList);//添加该省所有地区数据
                    }
                    //添加城市数据
                    options2Items.add(CityList);
                    //添加地区数据
                    options3Items.add(Province_AreaList);
                }
            }
        });
        thread.start();
    }

    /*
    省市县弹框
     */
    private void ShowPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                province = options1Items.get(options1).getPickerViewText();
                city = options2Items.get(options1).get(options2);
                area = options3Items.get(options1).get(options2).get(options3);
                address_text.setText(province + city + area);
            }
        })
                .setTitleText("城市选择")
                .setDividerColor(mContext.getResources().getColor(R.color.mainColor))
                .setTextColorCenter(mContext.getResources().getColor(R.color.mainColor))
                .setContentTextSize(25)
                .build();
        //pvOptions.setPicker(options1Items);//一级选择器
        //pvOptions.setPicker(options1Items, options2Items);//二级选择器
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

}
