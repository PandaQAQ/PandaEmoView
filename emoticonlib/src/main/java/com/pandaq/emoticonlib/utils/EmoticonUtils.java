package com.pandaq.emoticonlib.utils;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.io.File;

/**
 * Created by huxinyu on 2017/10/19 0019.
 * description ：表情库中使用到的一些公用工具方法
 */

public class EmoticonUtils {

    /**
     * dp 转 px
     *
     * @param context 上下文
     * @param dpValue dp 值
     * @return px 值
     */
    public static int dp2px(Context context, float dpValue) {
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        return (int) (dpValue * displayMetrics.density + 0.5f);
    }

    /**
     * px 转 dp
     *
     * @param context 上下文
     * @param pxValue px 值
     * @return dp 值
     */
    public static int px2dp(Context context, float pxValue) {
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        return (int) (pxValue / displayMetrics.density + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        return (int) (spValue * displayMetrics.scaledDensity + 0.5f);
    }

    /**
     * 获取本应用在系统的存储目录
     */
    public static String getAppFile(Context context, String uniqueName) {
        String cachePath = null;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getParent();
        } else {
            cachePath = context.getCacheDir().getParent();
        }
        return cachePath + File.separator + uniqueName;
    }


}
