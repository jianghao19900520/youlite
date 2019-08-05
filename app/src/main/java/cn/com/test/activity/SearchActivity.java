package cn.com.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yanzhenjie.nohttp.RequestMethod;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.bean.CartBean;
import cn.com.test.bean.SearchKeyBean;
import cn.com.test.utils.ToastUtils;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.search_content_text)
    EditText search_content_text;
    @BindView(R.id.search_history_listview)
    ListView search_history_listview;

    private List<SearchKeyBean> searchKeyList;
    private SearchHistoryAdapter mAdapter;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
    }

    @Override
    public void initTitle() {
        title.setText("搜索");
    }

    @Override
    public void init() {
        searchKeyList = new ArrayList<>();
        mAdapter = new SearchHistoryAdapter();
        search_history_listview.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        searchKeyList = DataSupport.findAll(SearchKeyBean.class);
        Collections.reverse(searchKeyList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

    @OnClick({R.id.search_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_btn:
                search(search_content_text.getText().toString().trim());
                break;
        }
    }

    private void search(String searchKey) {
        if (!TextUtils.isEmpty(searchKey)) {
            List<SearchKeyBean> all = DataSupport.findAll(SearchKeyBean.class);
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getSearchKey().equals(searchKey)) {
                    all.get(i).delete();
                }
            }
            SearchKeyBean bean = new SearchKeyBean(searchKey);
            bean.save();
            search_content_text.setText("");
            startActivity(new Intent(mContext, SearchGoodsActivity.class).putExtra("searchKey", searchKey));
        }
    }

    private class SearchHistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return searchKeyList.size();
        }

        @Override
        public Object getItem(int i) {
            return searchKeyList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.item_history, null);
            TextView history_text = contentView.findViewById(R.id.history_text);
            String searchKey = searchKeyList.get(i).getSearchKey();
            history_text.setText(searchKey);
            contentView.setTag(searchKey);
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String searchKey = (String) view.getTag();
                    search(searchKey);
                }
            });
            return contentView;
        }
    }

}
