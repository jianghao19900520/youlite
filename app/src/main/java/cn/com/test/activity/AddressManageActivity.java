package cn.com.test.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.adapter.CommAdapter;
import cn.com.test.adapter.CommViewHolder;
import cn.com.test.base.BaseActivity;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.ToastUtils;

public class AddressManageActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.title_right_text_btn)
    TextView title_right_text_btn;
    @BindView(R.id.address_list)
    ListView address_list;

    private List<JSONObject> addressList;
    private CommAdapter<JSONObject> mAdapter;
    private int width;

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
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        addressList = new ArrayList<>();
        mAdapter = new CommAdapter<JSONObject>(mContext, addressList, R.layout.item_address) {

            @Override
            public void convert(final CommViewHolder holder, final JSONObject item, int position) {
                try {
                    TextView item_address_name = holder.getView(R.id.item_address_name);
                    TextView item_address_phone = holder.getView(R.id.item_address_phone);
                    TextView item_address_text = holder.getView(R.id.item_address_text);
                    item_address_name.setText(item.getString("linkMan"));
                    item_address_phone.setText(item.getString("linkPhone"));
                    item_address_text.setText(item.getString("province") + item.getString("city") + item.getString("area") + item.getString("address"));
                    int isDefault = item.getInt("isDefault");
                    if (isDefault == 1) {
                        item_address_name.setTextColor(Color.parseColor("#3ea0e0"));
                        item_address_phone.setTextColor(Color.parseColor("#3ea0e0"));
                        item_address_text.setTextColor(Color.parseColor("#3ea0e0"));
                    } else {
                        item_address_name.setTextColor(Color.parseColor("#333333"));
                        item_address_phone.setTextColor(Color.parseColor("#333333"));
                        item_address_text.setTextColor(Color.parseColor("#666666"));
                    }
                    holder.getView(R.id.item_default_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //设为默认
                            try {
                                loadData(2, new String[]{item.getString("id")}, getString(R.string.string_loading), RequestMethod.POST);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    holder.getView(R.id.item_del_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //删除
                            try {
                                loadData(3, new String[]{item.getString("id")}, getString(R.string.string_loading), RequestMethod.POST);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    HorizontalScrollView item_address_scroll = holder.getView(R.id.item_address_scroll);
                    item_address_scroll.scrollTo(0, 0);
                    //动态设置宽度
                    LinearLayout item_address_layout = holder.getView(R.id.item_address_layout);
                    ViewGroup.LayoutParams params = item_address_layout.getLayoutParams();
                    params.width = width;
                    item_address_layout.setLayoutParams(params);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        address_list.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(1, null, getString(R.string.string_loading), RequestMethod.GET);
    }

    /**
     * @param what 1.保存地址 2.设为默认 3.删除
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                relativeUrl = "health/addressList";
            } else if (what == 2) {
                object.put("id", value[0]);
                object.put("operate", 2);//1=新增，2=修改，3=删除
                object.put("isDefault", 1);//默认地址 0=否 1=是
                relativeUrl = "health/modifyAddress";
            } else if (what == 3) {
                object.put("id", value[0]);
                object.put("operate", 3);//1=新增，2=修改，3=删除
                relativeUrl = "health/modifyAddress";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1 | what == 2 | what == 3) {
                                setAddressList(result.getJSONArray("addressList"));
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

    private void setAddressList(JSONArray jsonArray) throws JSONException {
        addressList.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            addressList.add(jsonArray.getJSONObject(i));
        }
        mAdapter.notifyDataSetChanged();
    }

}
