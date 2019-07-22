package cn.com.test.http;

public abstract class BaseCallBack<T> implements HttpListener<T> {
    public int what;
    public String msg;

    public BaseCallBack(int what, String msg) {
        this.what = what;
        this.msg = msg;
    }
}
