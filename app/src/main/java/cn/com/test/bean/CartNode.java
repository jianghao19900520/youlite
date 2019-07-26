package cn.com.test.bean;

public class CartNode {

    public CartBean bean;
    public boolean isChecked;//在购物车页面是否被选中

    public CartNode(CartBean bean, boolean isChecked) {
        this.bean = bean;
        this.isChecked = isChecked;
    }

}
