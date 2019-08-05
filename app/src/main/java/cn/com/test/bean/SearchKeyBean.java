package cn.com.test.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class SearchKeyBean extends DataSupport implements Serializable {

    private String searchKey;//搜索关键词

    public SearchKeyBean(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }
}
