package cn.com.test.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MyCareListActivity extends BaseActivity implements OnRefreshLoadmoreListener {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.title_right_text_btn)
    TextView title_right_text_btn;
    @BindView(R.id.list_view)
    ListView list_view;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private List<JSONObject> careList;
    private CommAdapter<JSONObject> mAdapter;

    private int pageIndex = 1;
    private String[] typeName;
    private String[] typeNo;
    private int width;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_list_view);
    }

    @Override
    public void initTitle() {
        title.setText("关联成员");
        title_right_text_btn.setText("添加关联");
        title_right_text_btn.setVisibility(View.VISIBLE);
        title_right_text_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData(2, null, "", RequestMethod.GET);
            }
        });
    }

    @Override
    public void init() {
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        careList = new ArrayList<>();
        mAdapter = new CommAdapter<JSONObject>(mContext, careList, R.layout.item_care) {
            @Override
            public void convert(final CommViewHolder holder, final JSONObject item, int position) {
                try {
                    TextView item_care_name = holder.getView(R.id.item_care_name);
                    TextView item_care_phone = holder.getView(R.id.item_care_phone);
                    TextView item_care_type = holder.getView(R.id.item_care_type);
                    item_care_name.setText(item.getString("nickName"));
                    item_care_phone.setText(item.getString("phone"));
                    item_care_type.setText(item.getString("typeName"));
                    holder.getView(R.id.item_del_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //删除
                            try {
                                loadData(4, new String[]{item.getString("touserNo")}, getString(R.string.string_loading), RequestMethod.POST);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    HorizontalScrollView item_care_scroll = holder.getView(R.id.item_care_scroll);
                    item_care_scroll.scrollTo(0, 0);
                    //动态设置宽度
                    LinearLayout item_care_layout = holder.getView(R.id.item_care_layout);
                    ViewGroup.LayoutParams params = item_care_layout.getLayoutParams();
                    params.width = width;
                    item_care_layout.setLayoutParams(params);
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
     * @param what 1.获取关联列表 2获取关联类型 3绑定关联 4解除绑定
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                object.put("page", pageIndex);
                object.put("limit", 20);
                relativeUrl = "health/userRelationList";
            } else if (what == 2) {
                relativeUrl = "health/relateionType";
            } else if (what == 3) {
                object.put("typeNo", value[0]);
                object.put("phone", "13597061095");
                relativeUrl = "health/setUserRelation";
            } else if (what == 4) {
                object.put("idsList", new JSONArray().put(new JSONObject().put("toUserNo", value[0])));
                relativeUrl = "health/cancelUserRelation";
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
                                setCareList(result.getJSONArray("list"));
                            } else if (what == 2) {
                                JSONArray list = result.getJSONArray("list");
                                typeName = new String[list.length()];
                                typeNo = new String[list.length()];
                                for (int i = 0; i < list.length(); i++) {
                                    typeName[i] = list.getJSONObject(i).getString("typeName");
                                    typeNo[i] = list.getJSONObject(i).getString("typeNo");
                                }
                                showListDialog();
                            } else if (what == 3 || what == 4) {
                                loadData(1, null, getString(R.string.string_loading), RequestMethod.POST);
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

    private void setCareList(JSONArray list) throws JSONException {
        if (pageIndex == 1) {
            careList.clear();
        }
        for (int i = 0; i < list.length(); i++) {
            careList.add(list.getJSONObject(i));
        }
        mAdapter.notifyDataSetChanged();
    }

    private void showListDialog() {
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(mContext);
        listDialog.setTitle("我是一个列表Dialog");
        listDialog.setItems(typeName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadData(3, new String[]{typeNo[which]}, getString(R.string.string_loading), RequestMethod.POST);
            }
        });
        listDialog.show();
    }

}
