package com.pandaq.emoticonlib.utils;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    /**
     * 传入字符串参数，返回MD5加密结果（小写）
     *
     * @param value 待加密的字符串
     * @return 加密结果
     */
    public static String getMD5Result(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes("UTF-8"));
            byte[] result = md.digest();
            return getString(result);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "NoSuchAlgorithmException !";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "UnsupportedEncodingException !";
        }
    }

    private static String getString(byte[] result) {
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            int i = b & 0xff;
            if (i <= 0xf) {
                sb.append(0);
            }
            sb.append(Integer.toHexString(i));
        }
        return sb.toString().toLowerCase();
    }

}
