package cn.com.test.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import cn.com.test.base.BaseApplication;

public class CommViewHolder {

    private final SparseArray<View> mViews;
    private  int mPosition;
    private View mConvertView;
    private Context mContext;
    private RequestManager glide;

    private CommViewHolder(Context context, ViewGroup parent, int layoutId, int position ){
        this.mContext = context;
        this.mViews = new SparseArray<View>();
        this.mPosition = position;
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }
    /**
     * 拿到一个ViewHolder对象
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static CommViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position)
    {
        if (convertView == null)
        {
            return new CommViewHolder(context, parent, layoutId,position);
        }
        CommViewHolder vHolder = (CommViewHolder) convertView.getTag();
        vHolder.setPosition(position);
        return vHolder;
    }


    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId)
    {
        View view = mViews.get(viewId);
        if (view == null)
        {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView()
    {
        return mConvertView;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public CommViewHolder setText(int viewId, String text)
    {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片(drawableId)
     */
    public CommViewHolder setImageResource(int viewId, int drawableId)
    {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * 为ImageView设置图片(Bitmap)
     */
    public CommViewHolder setImageBitmap(int viewId, Bitmap bm)
    {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * 为ImageView设置图片(url)
     */
    public CommViewHolder setImageByUrl(int viewId, String url)
    {
        if (null == glide){
            glide = Glide.with(mContext);
        }
        glide.setDefaultRequestOptions(BaseApplication.options)
                .load(url)
                .thumbnail(0.1f)
                .into((ImageView) getView(viewId));
        return this;
    }

    public void setPosition(int position){
        mPosition = position;
    }

    public int getPosition(){
        return mPosition;
    }
}
