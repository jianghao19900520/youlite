package cn.com.test.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

public class UserInfoActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.user_img)
    ImageView user_img;
    @BindView(R.id.name_edit)
    TextView name_edit;
    @BindView(R.id.nick_edit)
    TextView nick_edit;
    @BindView(R.id.email_edit)
    TextView email_edit;
    @BindView(R.id.email_radio_group)
    RadioGroup email_radio_group;
    @BindView(R.id.email_radio_man)
    RadioButton email_radio_man;
    @BindView(R.id.email_radio_woman)
    RadioButton email_radio_woman;

    private int gender = 0;// 0=女 1=男

    public static final int RC_TAKE_PHOTO = 1;
    public static final int RC_CHOOSE_PHOTO = 2;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/";
    private String picPath;//当前正在拍摄的照片的路径
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private LoadingDailog dialog;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_info);
    }

    @Override
    public void initTitle() {
        title.setText("账户编辑");
    }

    @Override
    public void init() {
        loadData(1, null, getString(R.string.string_loading), RequestMethod.GET);
        email_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // 选中状态改变时被触发
                switch (checkedId) {
                    case R.id.email_radio_man:
                        gender = 1;
                        break;
                    case R.id.email_radio_woman:
                        gender = 0;
                        break;
                }
            }
        });
    }

    /**
     * @param what 1.根据token获取用户数据 2修改用户信息
     */
    @Override
    public void loadData(int what, final String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                relativeUrl = "health/userInfo";
            } else if (what == 2) {
                object.put("realName", name_edit.getText().toString().trim());
                object.put("nickName", nick_edit.getText().toString().trim());
                object.put("email", email_edit.getText().toString().trim());
                object.put("gender", gender);
                if (value != null) {
                    object.put("userPic", value[0]);
                }
                relativeUrl = "health/modifyUserInfo";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");
                            if (what == 1) {
                                String userPic = result.getString("userPic");
                                String realName = result.getString("realName");
                                String nickName = result.getString("nickName");
                                String email = result.getString("email");
                                gender = result.optInt("gender");
                                Glide.with(mContext).load(userPic).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(user_img);
                                if (!TextUtils.isEmpty(realName)) {
                                    name_edit.setText(realName);
                                }
                                if (!TextUtils.isEmpty(nickName)) {
                                    nick_edit.setText(nickName);
                                }
                                if (!TextUtils.isEmpty(email)) {
                                    email_edit.setText(email);
                                }
                                if (gender == 0) {
                                    email_radio_woman.setChecked(true);
                                } else {
                                    email_radio_man.setChecked(true);
                                }
                            } else if (what == 2) {
                                ToastUtils.showShort("修改成功");
                                loadData(1, null, "", RequestMethod.GET);
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

    @OnClick({R.id.user_info_submit, R.id.user_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_info_submit:
                loadData(2, null, getString(R.string.string_loading), RequestMethod.POST);
                break;
            case R.id.user_img:
                showBottomDialog();
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
                        if (dialog == null) {
                            dialog = new LoadingDailog.Builder(mContext)
                                    .setMessage("图片上传中...")
                                    .setCancelable(true)
                                    .setCancelOutside(true).create();
                        }
                        if (!dialog.isShowing()) {
                            dialog.show();
                        }
                    }

                    @Override
                    public void onSuccess(final File file) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String result = mulpost(Constant.BASE_URL + "health/upload", file);
                                try {
                                    final String picUrl = new JSONObject(result).getJSONObject("result").getString("path");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dialog != null) {
                                                if (dialog.isShowing()) {
                                                    dialog.dismiss();
                                                }
                                                dialog = null;
                                            }
                                            loadData(2, new String[]{picUrl}, getString(R.string.string_loading), RequestMethod.POST);
                                        }
                                    });
                                } catch (Exception e) {
                                    if (dialog != null) {
                                        if (dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                        dialog = null;
                                    }
                                    ToastUtils.showShort("上传失败，请稍后再试");
                                }

                            }
                        }).start();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch();
    }

    /**
     * 上传图片
     */
    private String mulpost(String actionUrl, File file) {
        try {
            String result = "";
            String BOUNDARY = java.util.UUID.randomUUID().toString();
            String PREFIX = "--";
            String CRLF = "\r\n";
            String MULTIPART_FROM_DATA = "multipart/form-data";

            StringBuffer headBuffer = new StringBuffer(); //构建文件头部信息
            headBuffer.append(PREFIX);
            headBuffer.append(BOUNDARY);
            headBuffer.append(CRLF);
            headBuffer.append("Content-Disposition: form-data; name=\"" + "upload_file" + "\"; filename=\"" + file.getName() + "\"" + CRLF);//模仿web上传文件提交一个form表单给服务器，表单名随意起
            headBuffer.append("Content-Type: application/octet-stream" + CRLF);//若服务器端有文件类型的校验，必须明确指定Content-Type类型
            headBuffer.append(CRLF);
            Log.i("", headBuffer.toString());
            byte[] headBytes = headBuffer.toString().getBytes();

            StringBuffer endBuffer = new StringBuffer();//构建文件结束行
            endBuffer.append(CRLF);
            endBuffer.append(PREFIX);
            endBuffer.append(BOUNDARY);
            endBuffer.append(PREFIX);
            endBuffer.append(CRLF);
            byte[] endBytes = endBuffer.toString().getBytes();

            URL uri = new URL(actionUrl);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            conn.setReadTimeout(5 * 1000);
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false);
            conn.setRequestMethod("POST"); // Post方式
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                    + ";boundary=" + BOUNDARY);

            DataOutputStream outStream = new DataOutputStream(
                    conn.getOutputStream());

            outStream.write(headBytes);//输出文件头部

            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);//输出文件内容
            }

            fileInputStream.close();

            outStream.write(endBytes);//输出结束行
            outStream.close();

            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            result = br.readLine();
            outStream.close();
            conn.disconnect();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
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

