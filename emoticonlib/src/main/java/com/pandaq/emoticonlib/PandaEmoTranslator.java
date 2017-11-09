package com.pandaq.emoticonlib;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.pandaq.emoticonlib.gif.AnimatedGifDrawable;
import com.pandaq.emoticonlib.gif.AnimatedImageSpan;
import com.pandaq.emoticonlib.gif.GifRunnable;
import com.pandaq.emoticonlib.utils.EmoticonUtils;

import java.util.HashSet;
import java.util.regex.Matcher;

/**
 * Created by huxinyu on 2017/11/2 0002.
 * description ：表情文字转表情图的转换类
 */

public class PandaEmoTranslator {

    private static PandaEmoTranslator sEmoTranslator;
    private int MAX_PER_VIEW = 5;
    private int FACE_BOUNDS = 30;// 单位 DP
    private GifRunnable mGifRunnable;
    private Handler mHandler = new Handler();
    /* faceInfo 用于存放表情的起始位置和对应文字
     * 每一个表情在字符串中的起始位置不同因此用位置数组作为 key 避免相同表情时覆盖信息
     */
    private HashSet<int[]> faceInfo = new HashSet<>();

    public static PandaEmoTranslator getInstance() {
        if (sEmoTranslator == null) {
            synchronized (PandaEmoTranslator.class) {
                if (sEmoTranslator == null) {
                    sEmoTranslator = new PandaEmoTranslator();
                }
            }
        }

        return sEmoTranslator;
    }

    /**
     * 设置单个 TextView 最多显示的动态图个数
     * 超过这个个数则所有表情显示为静态图
     *
     * @param maxPerView 阈值
     */
    public void setMAX_PER_VIEW(int maxPerView) {
        MAX_PER_VIEW = maxPerView;
    }

    public int getMAX_PER_VIEW() {
        return MAX_PER_VIEW;
    }

    public void setFACE_BOUNDS(int faceBounds) {
        FACE_BOUNDS = faceBounds;
    }

    public int getFACE_BOUNDS() {
        return FACE_BOUNDS;
    }

    /**
     * 图文混排工具方法
     *
     * @param context  环境
     * @param classTag 当前显示界面的 Tag（一般是Activity），
     *                 用于在退出界面时将界面内的动图对象从 Runable 中移除
     * @param value    要转换的文字
     * @param callBack 动图刷新回调
     * @return 图文混排后显示的内容
     */
    public SpannableString makeGifSpannable(Context context, String classTag, String value,
                                            AnimatedGifDrawable.RunGifCallBack callBack) {
        if (TextUtils.isEmpty(value)) {
            value = "";
        }
        faceInfo.clear();
        int start, end;
        SpannableString spannableString = new SpannableString(value);
        Matcher matcher = EmoticonManager.getPattern().matcher(value);
        /*
         单个 TextView 中显示动态图太多刷新绘制比较消耗内存，
         因此做类似QQ动态表情的限制，
         超过 MAX_PER_VIEW 个就显示静态表情
        */
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            faceInfo.add(new int[]{start, end});
        }
        int faces = faceInfo.size();
        for (int[] faceIndex : faceInfo) {
            if (faces > MAX_PER_VIEW) {
                Drawable drawable = getEmotDrawable(context, value.substring(faceIndex[0], faceIndex[1]));
                if (drawable != null) {
                    ImageSpan span = new ImageSpan(drawable);
                    spannableString.setSpan(span, faceIndex[0], faceIndex[1], Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
            } else {
                AnimatedGifDrawable gifDrawable = EmoticonManager.getDrawableGif(context,
                        value.substring(faceIndex[0], faceIndex[1]),
                        EmoticonUtils.dp2px(context, FACE_BOUNDS));
                if (gifDrawable != null) {
                    gifDrawable.setRunCallBack(callBack);
                    gifDrawable.setContainerTag(classTag);
                    // 如果动态图执行类不存在，则创建一个新的执行类。存在则将此次转化的表情加入执行类中
                    if (mGifRunnable == null) {
                        mGifRunnable = new GifRunnable(gifDrawable, mHandler);
                    } else {
                        mGifRunnable.addGifDrawable(gifDrawable);
                    }
                    AnimatedImageSpan span = new AnimatedImageSpan(gifDrawable, mGifRunnable);
                    spannableString.setSpan(span, faceIndex[0], faceIndex[1], Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                } else { //不存在动态图资源时用静态图代替
                    Drawable drawable = getEmotDrawable(context, value.substring(faceIndex[0], faceIndex[1]));
                    if (drawable != null) {
                        ImageSpan span = new ImageSpan(drawable);
                        spannableString.setSpan(span, faceIndex[0], faceIndex[1], Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                }
            }
        }
        return spannableString;
    }

    /**
     * 将 EditText 文本替换为静态表情图
     *
     * @param context 上下文环境
     */
    public void replaceEmoticons(Context context, Editable editable, int start, int count) {
        if (count <= 0 || editable.length() < start + count)
            return;
        CharSequence s = editable.subSequence(start, start + count);
        Matcher matcher = EmoticonManager.getPattern().matcher(s);
        while (matcher.find()) {
            int from = start + matcher.start();
            int to = start + matcher.end();
            String emot = editable.subSequence(from, to).toString();
            Drawable d = getEmotDrawable(context, emot);
            if (d != null) {
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
                editable.setSpan(span, from, to, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * 获取静态表情 Drawable 对象
     *
     * @param context 上下文
     * @param text    表情对应的文本信息
     * @return 静态表情 Drawable对象
     */
    private Drawable getEmotDrawable(Context context, String text) {
        Drawable drawable = EmoticonManager.getDrawable(context, text);
        int size = EmoticonUtils.dp2px(context, FACE_BOUNDS);
        if (drawable != null) {
            drawable.setBounds(10, 0, size, size);
        }
        return drawable;
    }

}
