package cn.com.test.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;

import java.io.File;

import cn.com.test.R;
import cn.com.test.activity.HomeActivity;
import cn.com.test.base.BaseFragment;
import cn.com.test.utils.AppUtils;
import cn.com.test.utils.LogUtils;
import cn.com.test.utils.ToastUtils;

public class MineFragment extends BaseFragment {

    private DownloadRequest downloadRequest;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
    String filename = "youlite.apk";

    @Override
    public View setContent(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_mine, null);
    }

    @Override
    public void initTitle() {
        HomeActivity activity = (HomeActivity) getActivity();
        activity.title.setText("我的");
        activity.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadRequest = NoHttp.createDownloadRequest("http://zstore.2025123.com.cn/yuejian.apk", path, filename, false, true);
                DownloadQueue downloadQueue = new DownloadQueue(1);
                downloadQueue.add(0, downloadRequest, new DownloadListener() {
                    @Override
                    public void onDownloadError(int what, Exception exception) {

                        System.out.println("onDownloadError" + exception);
                    }

                    @Override
                    public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {

                        System.out.println("onStart");
                    }

                    @Override
                    public void onProgress(int what, int progress, long fileCount, long speed) {

                        System.out.println("onProgress" + progress);
                    }

                    @Override
                    public void onFinish(int what, String filePath) {

                        System.out.println("onFinish");
                        checkPermission();
                    }

                    @Override
                    public void onCancel(int what) {

                        System.out.println("onCancel");
                    }
                });
                downloadQueue.start();
            }
        });
    }

    /**
     * 检查权限
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 26 && !mContext.getPackageManager().canRequestPackageInstalls()) {
            //8.0及以上系统，且没有获取未知来源权限，需要先申请
            ToastUtils.showLong("请开启安装权限");
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            startActivityForResult(intent, 10012);
        } else {
            installAPK();
        }
    }


    private void installAPK() {
        ToastUtils.showShort("下载完成");
        File apkFile = new File(path + "/" + filename);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    mContext
                    , "cn.com.test.fileprovider"
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    @Override
    public void init() {

    }

    @Override
    public void loadData(int what, String[] value, String msg, RequestMethod method) {

    }
}
