package cn.com.test.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.OnClick;
import cn.com.test.R;
import cn.com.test.base.BaseActivity;
import cn.com.test.constant.Constant;
import cn.com.test.http.HttpListener;
import cn.com.test.http.NetHelper;
import cn.com.test.utils.LoginUtils;
import cn.com.test.utils.SPUtils;
import cn.com.test.utils.ToastUtils;

public class SettingActivity extends BaseActivity {

    private DownloadRequest downloadRequest;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.youlite";
    private String filename = "youlite.apk";
    private ProgressDialog dialog;

    @Override
    public void setContent(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void initTitle() {

    }

    @Override
    public void init() {

    }

    /**
     * @param what 1.注销登录
     */
    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {
        try {
            final JSONObject object = new JSONObject();
            String relativeUrl = "";
            if (what == 1) {
                relativeUrl = "health/logout";
            }
            NetHelper.getInstance().request(mContext, what, relativeUrl, object, method, msg, new HttpListener() {
                @Override
                public void onSucceed(int what, JSONObject jsonObject) {
                    try {
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            if (what == 1) {
                                SPUtils.getInstance().put(Constant.token, "");
                                finish();
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

    @OnClick({R.id.setting_update_pwd_layout, R.id.setting_check_update_layout, R.id.setting_logout_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting_update_pwd_layout:
                if (LoginUtils.getInstance().checkLoginStatus(mContext)) {
                    startActivity(new Intent(mContext, UpdatePwdActivity.class));
                }
                break;
            case R.id.setting_check_update_layout:
                download();
                break;
            case R.id.setting_logout_layout:
                if (LoginUtils.getInstance().isLogin()) {
                    loadData(1, null, getString(R.string.string_loading), RequestMethod.GET);
                }else{
                    ToastUtils.showShort("用户未登录");
                }
                break;
        }
    }

    private void download() {
        downloadRequest = NoHttp.createDownloadRequest("http://106.52.216.72/web/youlite.apk", path, filename, false, true);
        DownloadQueue downloadQueue = new DownloadQueue(1);
        downloadQueue.add(0, downloadRequest, new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                ToastUtils.showLong("下载失败，请稍后重试");
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                if (dialog == null) {
                    dialog = ProgressDialog.show(mContext, "提示", "下载中…", true, false, null);
                } else {
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                }
            }

            @Override
            public void onProgress(int what, int progress, long fileCount, long speed) {
                dialog.setMessage("下载" + progress + "%…");
            }

            @Override
            public void onFinish(int what, String filePath) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                checkPermission();
            }

            @Override
            public void onCancel(int what) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                ToastUtils.showLong("下载取消");
            }
        });
        downloadQueue.start();
    }

    /**
     * 检查权限
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 26 && !mContext.getPackageManager().canRequestPackageInstalls()) {
            //8.0及以上系统，且没有获取未知来源权限，需要先申请
            ToastUtils.showLong("请开启安装权限");
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            startActivityForResult(intent, 0);
        } else {
            installAPK();
        }
    }

    private void installAPK() {
        File apkFile = new File(path + "/" + filename);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, "cn.com.test.fileprovider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

}
