package cn.com.test.bean;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class CartBean extends DataSupport implements Serializable{

    private String goodsId;//商品id
    private String goodsName;//商品名称
    private int goodsNum;//商品数量
    private String goodsPriceNew;//商品现价
    private String goodsPriceOld;//商品原价
    private boolean isChecked = false;//是否被选中

    public CartBean(String goodsId, int goodsNum, String goodsName, String goodsPriceNew, String goodsPriceOld) {
        this.goodsId = goodsId;
        this.goodsNum = goodsNum;
        this.goodsName = goodsName;
        this.goodsPriceNew = goodsPriceNew;
        this.goodsPriceOld = goodsPriceOld;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(int goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getGoodsPriceNew() {
        return goodsPriceNew;
    }

    public void setGoodsPriceNew(String goodsPriceNew) {
        this.goodsPriceNew = goodsPriceNew;
    }

    public String getGoodsPriceOld() {
        return goodsPriceOld;
    }

    public void setGoodsPriceOld(String goodsPriceOld) {
        this.goodsPriceOld = goodsPriceOld;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
