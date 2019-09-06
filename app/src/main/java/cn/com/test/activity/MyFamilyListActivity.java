package cn.com.test.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
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

public class MyFamilyListActivity extends BaseActivity implements OnRefreshLoadmoreListener {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.list_view)
    ListView list_view;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private List<JSONObject> familyList;
    private CommAdapter<JSONObject> mAdapter;

    private int pageIndex = 1;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_list_view);
    }

    @Override
    public void initTitle() {
        title.setText("家庭成员");
    }

    @Override
    public void init() {
        familyList = new ArrayList<>();
        mAdapter = new CommAdapter<JSONObject>(mContext, familyList, R.layout.item_circle) {
            @Override
            public void convert(final CommViewHolder holder, final JSONObject item, int position) {
                try {
                    item.getString("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        list_view.setAdapter(mAdapter);
        refreshLayout.setOnRefreshLoadmoreListener(this);
        refreshLayout.setEnableLoadmore(true);
        loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
    }


    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        pageIndex++;
        loadData(1, null, "", RequestMethod.POST);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        pageIndex = 1;
        loadData(1, null, "", RequestMethod.POST);
    }

    /**
     * @param what 1.获取帖子列表 2取消收藏
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("page", pageIndex);
                object.put("limit", 20);
                relativeUrl = "health/collectList";
            } else if (what == 2) {
                object.put("idsList", new JSONArray().put(new JSONObject().put("atricleId", value[0])));
                relativeUrl = "health/cancelCollect";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                if (pageIndex == 1) {
                                    refreshLayout.finishRefresh();
                                } else {
                                    refreshLayout.finishLoadmore();
                                }
                                setFamilyList(result.getJSONArray("list"));
                            } else {
                                pageIndex = 1;
                                loadData(1, null, "", RequestMethod.POST);
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

    private void setFamilyList(JSONArray list) throws JSONException {
        if (pageIndex == 1) {
            familyList.clear();
        }
        for (int i = 0; i < list.length(); i++) {
            familyList.add(list.getJSONObject(i));
        }
        mAdapter.notifyDataSetChanged();
    }

}
