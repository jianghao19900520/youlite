package cn.com.test.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yanzhenjie.nohttp.RequestMethod;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.utils.FileUtils;

public class CirclePostingActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.circle_posting_img_layout)
    LinearLayout circle_posting_img_layout;

    public static final int RC_TAKE_PHOTO = 1;
    public static final int RC_CHOOSE_PHOTO = 2;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";
    private Uri saveUri;//当前正在拍摄的照片的uri

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

    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }

    @OnClick({R.id.circle_posting_camera_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.circle_posting_camera_btn:
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    //未授权，申请授权(从相册选择图片需要读取存储卡的权限)
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_CHOOSE_PHOTO);
//                } else {
//                    //已授权，获取照片
//                    choosePhoto();
//                }
                saveUri = Uri.fromFile(new File(path, System.currentTimeMillis() + ".jpg"));
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
                startActivityForResult(intent, RC_TAKE_PHOTO);
                break;
        }
    }

    /**
     * 权限申请结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_TAKE_PHOTO:   //拍照权限申请返回
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    takePhoto();
                }
                break;
            case RC_CHOOSE_PHOTO:   //相册选择照片权限申请返回
                choosePhoto();
                break;
        }
    }

    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, RC_CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_CHOOSE_PHOTO:
                Uri uri = data.getData();
                String filePath = FileUtils.getFilePathByUri(this, uri);
                if (!TextUtils.isEmpty(filePath)) {
                    ImageView imageView = new ImageView(mContext);
                    RequestOptions requestOptions = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
                    Glide.with(mContext).load(filePath).apply(requestOptions).into(imageView);
                    circle_posting_img_layout.addView(imageView);
                    imageView.setTag(uri);
                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(final View view) {
                            AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
                                    .setMessage("确定要删除该张照片？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            circle_posting_img_layout.removeView(view);
                                        }
                                    }).setNegativeButton("取消", null).create();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                            return true;
                        }
                    });
                }
                break;
            case RC_TAKE_PHOTO:
                ImageView imageView = new ImageView(mContext);
                RequestOptions requestOptions = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
                Glide.with(mContext).load(FileUtils.getFilePathByUri(this, saveUri)).apply(requestOptions).into(imageView);
                circle_posting_img_layout.addView(imageView);
                imageView.setTag(saveUri);
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View view) {
                        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
                                .setMessage("确定要删除该张照片？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        circle_posting_img_layout.removeView(view);
                                    }
                                }).setNegativeButton("取消", null).create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        return true;
                    }
                });
                break;
        }
    }
}


