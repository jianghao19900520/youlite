package cn.com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.adapter.CommAdapter;
import cn.com.test.adapter.CommViewHolder;
import cn.com.test.base.BaseActivity;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.ToastUtils;
import cn.com.test.view.ListViewForScrollView;

public class CircleDetailActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.item_circle_img)
    ImageView item_circle_img;
    @BindView(R.id.item_circle_name)
    TextView item_circle_name;
    @BindView(R.id.item_circle_time)
    TextView item_circle_time;
    @BindView(R.id.item_circle_num)
    TextView item_circle_num;
    @BindView(R.id.item_circle_title)
    TextView item_circle_title;
    @BindView(R.id.item_circle_content)
    TextView item_circle_content;
    @BindView(R.id.circle_comment_edit)
    EditText circle_comment_edit;
    @BindView(R.id.comment_listview)
    ListViewForScrollView comment_listview;

    private String id;
    private String userNo;
    private String artId;
    private List<JSONObject> commentList;
    private CommAdapter<JSONObject> mAdapter;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_circle_detail);
    }

    @Override
    public void initTitle() {
        title.setText("帖子详情");
    }

    @Override
    public void init() {
        id = getIntent().getStringExtra("id");
        if (TextUtils.isEmpty(id)) finish();
        commentList = new ArrayList<>();
        mAdapter = new CommAdapter<JSONObject>(mContext, commentList, R.layout.item_circle) {
            @Override
            public void convert(final CommViewHolder holder, final JSONObject item, int position) {
                try {
                    Glide.with(mContext).load(item.getString("userPic")).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(item_circle_img);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        comment_listview.setAdapter(mAdapter);
        loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
    }

    /**
     * @param what 1.获取帖子详情 2发表评论
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("id", id);
                object.put("order", 0);
                object.put("page", 1);
                object.put("limit", 100);
                relativeUrl = "health/bbsArticleDetail";
            } else if (what == 2) {
                object.put("artId", artId);
                object.put("content", circle_comment_edit.getText().toString().trim());
                relativeUrl = "health/bbsComment";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                setCircleDetail(result);
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

    /**
     * 展示帖子详情和评论详情
     */
    private void setCircleDetail(JSONObject result) throws JSONException {
        commentList.clear();
        JSONArray list = result.getJSONObject("articleList").getJSONArray("list");
        for (int i = 0; i < list.length(); i++) {
            JSONObject article = list.getJSONObject(i);
            if (article.getInt("type") == 0) {
                //主贴
                Glide.with(mContext).load(article.getString("userPic")).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(item_circle_img);
                item_circle_name.setText(article.getString("nickName"));
                String createTime = article.getString("createTime");
                item_circle_time.setText(createTime.substring(0, 4) + "-" + createTime.substring(4, 6) + "-" + createTime.substring(6, 8) + " " + createTime.substring(8, 10) + ":" + createTime.substring(10, 12) + ":" + createTime.substring(12, 14));
                item_circle_num.setText("评论" + result.getString("commentNum") + " | 点赞" + result.getString("likeNum"));
                String title = result.getString("title");
                if (TextUtils.isEmpty(title)) {
                    item_circle_title.setVisibility(View.GONE);
                } else {
                    item_circle_title.setText(title);
                    item_circle_title.setVisibility(View.VISIBLE);
                }
                String content = article.getString("content");
                if (TextUtils.isEmpty(content)) {
                    item_circle_content.setVisibility(View.GONE);
                } else {
                    item_circle_content.setText(content);
                    item_circle_content.setVisibility(View.VISIBLE);
                }
                userNo = article.getString("userNo");
                artId = result.getString("artId");
            } else {
                //评论
                commentList.add(article);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.circle_comment_post_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.circle_comment_post_btn:
                if (!TextUtils.isEmpty(circle_comment_edit.getText().toString().trim())) {
                    loadData(2, null, getString(R.string.string_loading), RequestMethod.POST);
                }
                break;
        }
    }

}
