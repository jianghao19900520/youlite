package cn.com.test.bean;

import java.io.Serializable;

public class CartBean implements Serializable {

    private String goodsNo;//商品id
    private String goodsName;//商品名称
    private int num;//商品数量
    private String newPrice;//商品现价
    private String oldPrice;//商品原价
    private boolean isChecked = false;//是否被选中

    public CartBean() {
    }

    public CartBean(String goodsNo, int num, String goodsName, String newPrice, String oldPrice) {
        this.goodsNo = goodsNo;
        this.num = num;
        this.goodsName = goodsName;
        this.newPrice = newPrice;
        this.oldPrice = oldPrice;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
