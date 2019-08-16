package cn.com.test.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.com.test.R;
import cn.com.test.adapter.CommAdapter;
import cn.com.test.adapter.CommViewHolder;
import cn.com.test.base.BaseActivity;
import cn.com.test.utils.ToastUtils;
import cn.com.test.view.ListViewForScrollView;

public class CirclePostingActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.circle_posting_listview)
    ListViewForScrollView circle_posting_listview;

    private List<JSONObject> circleList;
    private CommAdapter<JSONObject> mAdapter;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_circle_posting);
    }

    @Override
    public void initTitle() {
        title.setText("发布动态");
    }

    @Override
    public void init() {
        circleList = new ArrayList<>();
        try {
            circleList.add(new JSONObject().put("url", "http://pic.90sjimg.com/back_pic/00/00/69/40/3d07141c9523530da7b3dca9878413ec.jpg"));
            circleList.add(new JSONObject().put("url", "http://aliyunzixunbucket.oss-cn-beijing.aliyuncs.com/jpg/f8f05342c765bdca7fd92ecf302c6a57.jpg?x-oss-process=image/resize,p_100/auto-orient,1/quality,q_90/format,jpg/watermark,image_eXVuY2VzaGk=,t_100"));
            circleList.add(new JSONObject().put("url", "http://pic.90sjimg.com/back_pic/00/00/69/40/531ac7b7f8b61276f1ad2dd0dd02921b.jpg"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter = new CommAdapter<JSONObject>(mContext, circleList, R.layout.item_posting) {
            @Override
            public void convert(final CommViewHolder holder, JSONObject item, int position) {
                try {
//                    holder.setImageByUrl(R.id.item_posting_pic, item.getString("url"));
                    holder.setImageResource(R.id.item_posting_pic, R.mipmap.mine_background);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        circle_posting_listview.setAdapter(mAdapter);
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

}
