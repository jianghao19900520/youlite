package cn.com.test.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.constant.Constant;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.FileUtils;
import cn.com.test.utils.ToastUtils;
import pub.devrel.easypermissions.EasyPermissions;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class CirclePostingActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.circle_posting_img_layout)
    LinearLayout circle_posting_img_layout;

    public static final int RC_TAKE_PHOTO = 1;
    public static final int RC_CHOOSE_PHOTO = 2;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/";
    private String picPath;//当前正在拍摄的照片的路径
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
        loadData(1, null, "", RequestMethod.POST);
    }

    /**
     * @param what 1.获取帖子类型 2.提交帖子
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                relativeUrl = "health/bbsType";
            } else if (what == 2) {
                object.put("title", "title");
                object.put("typeNo", "1");
                object.put("content", "content");
                relativeUrl = "health/postArticle";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                JSONArray list = result.getJSONArray("list");
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

    @OnClick({R.id.circle_posting_camera_btn, R.id.circle_posting_submit_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.circle_posting_camera_btn:
                showBottomDialog();
                break;
            case R.id.circle_posting_submit_btn:
                for (int i = 0; i < circle_posting_img_layout.getChildCount(); i++) {
                    ImageView imageView = (ImageView) circle_posting_img_layout.getChildAt(i);
                    String path = (String) imageView.getTag(R.id.indexTag);//要上传的图片路径
                    System.out.println("@@@" + path);
                }
                break;
        }
    }

    private void showBottomDialog() {
        if (EasyPermissions.hasPermissions(mContext, permissions)) {
            //已经打开权限
            final Dialog dialog = new Dialog(this, R.style.DialogTheme);
            View view = View.inflate(this, R.layout.dialog_custom_layout, null);
            dialog.setContentView(view);
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            dialog.findViewById(R.id.tv_take_photo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar calendar = Calendar.getInstance();  //获取当前时间，作为图标的名字
                    int yearInt = calendar.get(Calendar.YEAR);
                    String year = String.valueOf(yearInt);
                    int monthInt = calendar.get(Calendar.MONTH) + 1;
                    String month = String.valueOf(monthInt > 9 ? monthInt : ("0" + monthInt));
                    int dayInt = calendar.get(Calendar.DAY_OF_MONTH);
                    String day = String.valueOf(dayInt > 9 ? dayInt : ("0" + dayInt));
                    int hourInt = calendar.get(Calendar.HOUR_OF_DAY);
                    String hour = String.valueOf(hourInt > 9 ? hourInt : ("0" + hourInt));
                    int minuteInt = calendar.get(Calendar.MINUTE);
                    String minute = String.valueOf(minuteInt > 9 ? minuteInt : ("0" + minuteInt));
                    int secondInt = calendar.get(Calendar.SECOND);
                    String second = String.valueOf(secondInt > 9 ? secondInt : ("0" + secondInt));
                    picPath = path + "IMG" + "_" + year + month + day + "_" + hour + minute + second + ".jpg";
                    File picFile = new File(picPath);
                    Uri picUri = Uri.fromFile(picFile);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        picUri = FileProvider.getUriForFile(mContext, "cn.com.test.fileprovider", picFile);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                    startActivityForResult(intent, RC_TAKE_PHOTO);
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_take_pic).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choosePhoto();
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的相册、照相使用权限", 1, permissions);
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
            case RC_TAKE_PHOTO:
                //拍照
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + picPath)));
                pressPicture(picPath);
                break;
            case RC_CHOOSE_PHOTO:
                if (data == null) return;
                //相册
                pressPicture(FileUtils.getFilePathByUri(this, data.getData()));
                break;
        }
    }

    /**
     * 压缩本地图片，新图片保存到.youlite目录下
     */
    private void pressPicture(String path) {
        String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.youlite";
        if (!new File(newPath).exists()) {
            new File(newPath).mkdirs();
        }
        Luban.with(mContext)
                .load(path)                                   // 传人要压缩的图片列表
                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                .setTargetDir(newPath)                        // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(final File file) {
                        ImageView imageView = new ImageView(mContext);
                        RequestOptions requestOptions = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
                        Glide.with(mContext).load(file).apply(requestOptions).into(imageView);
                        circle_posting_img_layout.addView(imageView);
                        imageView.setTag(R.id.indexTag, file.getAbsolutePath());
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

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort("图片压缩失败，请重新拍摄");
                    }
                }).launch();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //框架要求必须这么写
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //成功打开权限
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    //用户未同意权限
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}


