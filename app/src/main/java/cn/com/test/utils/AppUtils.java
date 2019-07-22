package cn.com.test.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {
    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    static WeakReference<Activity> sTopActivityWeakRef;
    static List<Activity> sActivityList = new LinkedList<>();

    private static Application.ActivityLifecycleCallbacks mCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            sActivityList.add(activity);
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            sActivityList.remove(activity);
        }
    };

    private AppUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param app 应用
     */
    public static void init(@NonNull final Application app) {
        AppUtils.sApplication = app;
        app.registerActivityLifecycleCallbacks(mCallbacks);
    }

    /**
     * 获取Application
     *
     * @return Application
     */
    public static Application getApp() {
        if (sApplication != null) return sApplication;
        throw new NullPointerException("u should init first");
    }

    private static void setTopActivityWeakRef(Activity activity) {
        if (sTopActivityWeakRef == null || !activity.equals(sTopActivityWeakRef.get())) {
            sTopActivityWeakRef = new WeakReference<>(activity);
        }
    }

    /**中文转Unicode编码
     * @param str 字符串
     * @return 如果是中文则返回对应的Unicode编码字符串
     */
    public static String chinaToUnicode(String str){
        String result="";

        if (isContainsChinese(str)){

            for (int i = 0; i < str.length(); i++){
                int chr1 = (char) str.charAt(i);
                if(chr1 >= 19968 && chr1 <= 171941){//汉字范围 \u4e00-\u9fa5 (中文)
                    result+="\\u" + Integer.toHexString(chr1);
                }else{
                    result+=str.charAt(i);
                }
            }
        }else {
            result = str;
        }
        return result;
    }

    /**
     * 判断字符串中是否含有中文
     * @param str 字符串
     * @return true 有 false 无
     */

    public static boolean isContainsChinese(String str)
    {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(str);
        boolean flg = false;
        if (matcher.find())    {
            flg = true;
        }
        return flg;
    }
}
