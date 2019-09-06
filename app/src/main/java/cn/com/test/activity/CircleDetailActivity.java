package cn.com.test.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import cn.com.test.constant.Constant;
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
    @BindView(R.id.circle_detail_img_layout)
    LinearLayout circle_detail_img_layout;
    @BindView(R.id.circle_collection_btn)
    TextView circle_collection_btn;
    @BindView(R.id.circle_like_btn)
    TextView circle_like_btn;

    private String id;
    private String artId = "";
    private List<JSONObject> commentList;
    private CommAdapter<JSONObject> mAdapter;
    private boolean liked = false;//是否点过赞了
    private boolean collected = false;//是否点收藏了

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
        mAdapter = new CommAdapter<JSONObject>(mContext, commentList, R.layout.item_circle_comment) {
            @Override
            public void convert(final CommViewHolder holder, final JSONObject item, int position) {
                try {
                    ImageView item_comment_img = holder.getView(R.id.item_comment_img);
                    Glide.with(mContext).load(item.getString("userPic")).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(item_comment_img);
                    holder.setText(R.id.item_circle_name, item.getString("nickName"));
                    String createTime = item.getString("createTime");
                    holder.setText(R.id.item_circle_time, createTime.substring(0, 4) + "-" + createTime.substring(4, 6) + "-" + createTime.substring(6, 8) + " " + createTime.substring(8, 10) + ":" + createTime.substring(10, 12) + ":" + createTime.substring(12, 14));
                    holder.setText(R.id.item_comment_text, item.getString("content"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        comment_listview.setAdapter(mAdapter);
        loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
    }

    /**
     * @param what 1.获取帖子详情 2发表评论 3点赞 4获取收藏列表 5收藏
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
                object.put("imgList", new JSONArray().put(new JSONObject().put("toLoad", "").put("orderNum", "")));
                relativeUrl = "health/bbsComment";
            } else if (what == 3) {
                object.put("outId", value[0]);
                object.put("type", value[1]);//0=主贴 //1=一级评论 2=二级评论
                relativeUrl = "health/bbsLikeOperate";
            } else if (what == 4) {
                object.put("page", 1);
                object.put("limit", 100);
                relativeUrl = "health/collectList";
            } else if (what == 5) {
                object.put("atricleId", artId);
                relativeUrl = "health/collectAtricle";
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
                            } else if (what == 2) {
                                loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
                            } else if (what == 3) {
                                Constant.circleLikeMap.put(artId, "");
                                liked = true;
                                circle_like_btn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.circle_postlike_p), null, null);
                                loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
                            } else if (what == 4) {
                                JSONArray list = result.getJSONArray("list");
                                for (int i = 0; i < list.length(); i++) {
                                    if (artId.equals(list.getJSONObject(i).getString("atricleId"))) {
                                        circle_collection_btn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.circle_collection_p), null, null);
                                        collected = true;
                                    }
                                }
                            } else {
                                circle_collection_btn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.circle_collection_p), null, null);
                                collected = true;
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
                //主贴，而且必须是第一条
                if (i == 0) {
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
                    artId = result.getString("artId");

                    circle_detail_img_layout.removeAllViews();
                    JSONArray picList = article.getJSONArray("picList");
                    for (int j = 0; j < picList.length(); j++) {
                        ImageView imageView = new ImageView(mContext);
                        RequestOptions requestOptions = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
                        Glide.with(mContext).load(picList.getJSONObject(j).getString("netToLoad")).apply(requestOptions).into(imageView);
                        circle_detail_img_layout.addView(imageView);
                    }
                }
            } else {
                //评论
                commentList.add(article);
            }
        }
        mAdapter.notifyDataSetChanged();
        for (String key : Constant.circleLikeMap.keySet()) {
            if (key.equals(artId)) {
                liked = true;
            }
        }
        if (liked) {
            circle_like_btn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.circle_postlike_p), null, null);
        } else {
            circle_like_btn.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.circle_postlike_n), null, null);
        }
        loadData(4, null, getString(R.string.string_loading), RequestMethod.POST);
    }

    @OnClick({R.id.circle_comment_post_btn, R.id.circle_collection_btn, R.id.circle_like_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.circle_comment_post_btn:
                //评论
                if (!TextUtils.isEmpty(circle_comment_edit.getText().toString().trim())) {
                    loadData(2, null, getString(R.string.string_loading), RequestMethod.POST);
                }
                break;
            case R.id.circle_collection_btn:
                //收藏
                if (!collected) {
                    loadData(5, null, getString(R.string.string_loading), RequestMethod.POST);
                }
                break;
            case R.id.circle_like_btn:
                //点赞
                if (!liked) {
                    loadData(3, new String[]{artId, "0"}, getString(R.string.string_loading), RequestMethod.POST);
                }
                break;
        }
    }

}
