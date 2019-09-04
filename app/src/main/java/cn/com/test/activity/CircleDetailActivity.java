package cn.com.test.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.ToastUtils;

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

    private String id;
    private String userNo;

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
        loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
    }

    /**
     * @param what 1.获取帖子详情
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

    private void setCircleDetail(JSONObject result) throws JSONException {
        JSONObject article = result.getJSONObject("articleList").getJSONArray("list").getJSONObject(0);
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
    }

}
