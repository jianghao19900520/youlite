package cn.com.test.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.youth.banner.Banner;

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

public class CircleListActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.circle_listview)
    ListView circle_listview;

    private List<JSONObject> circleList;
    private CommAdapter<JSONObject> mAdapter;

    private String typeNo;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_circle_list);
    }

    @Override
    public void initTitle() {
        typeNo = getIntent().getStringExtra("typeNo");
        if (TextUtils.isEmpty(typeNo)) finish();
        title.setText(getIntent().getStringExtra("typeName"));
    }

    @Override
    public void init() {
        circleList = new ArrayList<>();
        mAdapter = new CommAdapter<JSONObject>(mContext, circleList, R.layout.item_circle) {
            @Override
            public void convert(final CommViewHolder holder, JSONObject item, int position) {
                try {
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
                    holder.setText(R.id.item_circle_name, item.getString("nickName"));
                    holder.setText(R.id.circle_comment_text, item.getString("commentNum"));
                    holder.setText(R.id.circle_like_text, item.getString("likeNum"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        circle_listview.setAdapter(mAdapter);
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
                object.put("page", 1);
                object.put("limit", 20);
                object.put("typeNo", typeNo);
                relativeUrl = "health/bbsArticleList";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
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
        circleList.clear();
        for (int i = 0; i < list.length(); i++) {
            circleList.add(list.getJSONObject(i));
        }
        mAdapter.notifyDataSetChanged();
    }

}
