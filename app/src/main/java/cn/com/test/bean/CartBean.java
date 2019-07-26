package cn.com.test.bean;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

public class CartBean extends DataSupport {

    private String goodsId;//商品id
    private int goodsNum;//商品数量
    private JSONObject goodsJSON;//商品详细信息

    public CartBean(String goodsId, int goodsNum, JSONObject goodsJSON) {
        this.goodsId = goodsId;
        this.goodsNum = goodsNum;
        this.goodsJSON = goodsJSON;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public int getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(int goodsNum) {
        this.goodsNum = goodsNum;
    }

    public JSONObject getGoodsJSON() {
        return goodsJSON;
    }

    public void setGoodsJSON(JSONObject goodsJSON) {
        this.goodsJSON = goodsJSON;
    }
}
