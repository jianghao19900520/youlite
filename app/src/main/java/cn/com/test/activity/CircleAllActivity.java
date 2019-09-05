package cn.com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import cn.com.test.view.ListViewForScrollView;

public class CircleAllActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.circle_all_list)
    ListView circle_all_list;

    private List<JSONObject> circleList;
    private CommAdapter<JSONObject> mAdapter;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_circle_all);
    }

    @Override
    public void initTitle() {
        title.setText("全部圈子");
    }

    @Override
    public void init() {
        circleList = new ArrayList<>();
        mAdapter = new CommAdapter<JSONObject>(mContext, circleList, R.layout.item_circle_all) {
            @Override
            public void convert(final CommViewHolder holder, final JSONObject item, int position) {
                try {
                    holder.setText(R.id.item_circle_name, item.getString("typeName"));
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                startActivity(new Intent(mContext, CircleListActivity.class).putExtra("typeNo", item.getString("typeNo")).putExtra("typeName", item.getString("typeName")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        circle_all_list.setAdapter(mAdapter);
        loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
    }

    /**
     * @param what 1.获取帖子类型
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                relativeUrl = "health/bbsType";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                JSONArray list = result.getJSONArray("list");
                                circleList.clear();
                                for (int i = 0; i < list.length(); i++) {
                                    circleList.add(list.getJSONObject(i));
                                }
                                mAdapter.notifyDataSetChanged();
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

}
