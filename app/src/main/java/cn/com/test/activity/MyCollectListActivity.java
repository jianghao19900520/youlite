package cn.com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
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

public class MyCollectListActivity extends BaseActivity implements OnRefreshLoadmoreListener {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.circle_listview)
    ListView circle_listview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private List<JSONObject> circleList;
    private CommAdapter<JSONObject> mAdapter;

    private int pageIndex = 1;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_circle_list);
    }

    @Override
    public void initTitle() {
        title.setText("我的收藏");
    }

    @Override
    public void init() {
        circleList = new ArrayList<>();
        mAdapter = new CommAdapter<JSONObject>(mContext, circleList, R.layout.item_circle) {
            @Override
            public void convert(final CommViewHolder holder, final JSONObject item, int position) {
                try {
                    ImageView item_circle_img = holder.getView(R.id.item_circle_img);
                    TextView item_circle_title = holder.getView(R.id.item_circle_title);
                    ImageView item_circle_pic = holder.getView(R.id.item_circle_pic);
                    item_circle_pic.setVisibility(View.GONE);
                    String title = item.getString("title");
                    if (!TextUtils.isEmpty(title)) {
                        item_circle_title.setText(title);
                        item_circle_title.setVisibility(View.VISIBLE);
                    } else {
                        item_circle_title.setVisibility(View.GONE);
                    }
                    Glide.with(mContext).load(item.getString("userPic")).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(item_circle_img);
                    holder.setText(R.id.item_circle_name, item.getString("nickName"));
                    holder.setText(R.id.circle_comment_text, item.getString("commentNum"));
                    holder.setText(R.id.circle_like_text, item.getString("likeNum"));
                    String createTime = item.getString("createTime");
                    holder.setText(R.id.item_circle_time, createTime.substring(0, 4) + "-" + createTime.substring(4, 6) + "-" + createTime.substring(6, 8) + " " + createTime.substring(8, 10) + ":" + createTime.substring(10, 12) + ":" + createTime.substring(12, 14));
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                startActivity(new Intent(mContext, CircleDetailActivity.class).putExtra("id", item.getString("id")));
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
        circle_listview.setAdapter(mAdapter);
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
     * @param what 1.获取帖子列表
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
                                setCircleList(result.getJSONArray("list"));
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

    private void setCircleList(JSONArray list) throws JSONException {
        if (pageIndex == 1) {
            circleList.clear();
        }
        for (int i = 0; i < list.length(); i++) {
            circleList.add(list.getJSONObject(i));
        }
        mAdapter.notifyDataSetChanged();
    }

}
