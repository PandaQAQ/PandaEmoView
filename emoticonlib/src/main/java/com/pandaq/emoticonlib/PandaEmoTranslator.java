package com.pandaq.emoticonlib;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.pandaq.emoticonlib.emoticons.EmoticonManager;
import com.pandaq.emoticonlib.emoticons.gif.AnimatedGifDrawable;
import com.pandaq.emoticonlib.emoticons.gif.AnimatedImageSpan;
import com.pandaq.emoticonlib.emoticons.gif.GifRunnable;
import com.pandaq.emoticonlib.utils.ClickTextSpan;
import com.pandaq.emoticonlib.utils.EmoticonUtils;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huxinyu on 2017/11/2 0002.
 * description ：表情文字转表情图的转换类
 */

public class PandaEmoTranslator {

    private static PandaEmoTranslator sEmoTranslator;
    private int MAX_PER_VIEW = 5;
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
     * @param maxGifPerView 阈值
     */
    public void setMaxGifPerView(int maxGifPerView) {
        MAX_PER_VIEW = maxGifPerView;
    }

    public int getMaxGifPerView() {
        return MAX_PER_VIEW;
    }

    /**
     * 图文混排工具方法
     *
     * @param classTag 当前显示界面的 Tag（一般是Activity），
     *                 用于在退出界面时将界面内的动图对象从 Runable 中移除
     * @param value    要转换的文字
     * @param callBack 动图刷新回调
     * @return 图文混排后显示的内容
     */
    public SpannableString makeGifSpannable(String classTag, String value,
                                            AnimatedGifDrawable.RunGifCallBack callBack) {
        if (TextUtils.isEmpty(value)) {
            value = "";
        }
        faceInfo.clear();
        int start, end;
        SpannableString spannableString = new SpannableString(value);
        Matcher matcher = PandaEmoManager.getInstance().getPattern().matcher(value);
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
                Drawable drawable = getEmotDrawable(PandaEmoManager.getInstance().getContext(), value.substring(faceIndex[0], faceIndex[1]));
                if (drawable != null) {
                    ImageSpan span = new ImageSpan(drawable);
                    spannableString.setSpan(span, faceIndex[0], faceIndex[1], Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
            } else {
                AnimatedGifDrawable gifDrawable = EmoticonManager.getInstance().getDrawableGif(PandaEmoManager.getInstance().getContext(),
                        value.substring(faceIndex[0], faceIndex[1]));
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
                    Drawable drawable = getEmotDrawable(PandaEmoManager.getInstance().getContext(), value.substring(faceIndex[0], faceIndex[1]));
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
     * 将带表情的文本转换成图文混排的文本（动态图也转换为静态图）
     *
     * @param value   待转换文本
     * @return 转换结果
     */
    public SpannableString makeEmojiSpannable(String value) {
        if (TextUtils.isEmpty(value)) {
            value = "";
        }
        int start;
        int end;
        SpannableString mSpannableString = new SpannableString(value);
        Matcher matcher = PandaEmoManager.getInstance().getPattern().matcher(value);
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            String emot = value.substring(start, end);
            Drawable drawable = getEmotDrawable(PandaEmoManager.getInstance().getContext(), emot);
            if (drawable != null) {
                ImageSpan span = new ImageSpan(drawable);
                mSpannableString.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        return mSpannableString;
    }

    /**
     * 将 EditText 文本替换为静态表情图
     *
     */
    public void replaceEmoticons( Editable editable, int start, int count) {
        if (count <= 0 || editable.length() < start + count)
            return;
        CharSequence s = editable.subSequence(start, start + count);
        Matcher matcher = PandaEmoManager.getInstance().getPattern().matcher(s);
        while (matcher.find()) {
            int from = start + matcher.start();
            int to = start + matcher.end();
            String emot = editable.subSequence(from, to).toString();
            Drawable d = getEmotDrawable(PandaEmoManager.getInstance().getContext(), emot);
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
        Drawable drawable = EmoticonManager.getInstance().getDrawable(context, text);
        int size = EmoticonUtils.dp2px(context, PandaEmoManager.getInstance().getDefaultEmoBoundsDp());
        if (drawable != null) {
            drawable.setBounds(10, 0, size, size);
        }
        return drawable;
    }

    /**
     * 开始执行某一个界面的动态表情显示
     *
     * @param activityTag 停止界面 activity 的 Tag
     */
    public void startGif(String activityTag) {
        if (mGifRunnable != null) {
            mGifRunnable.startHandler(activityTag);
        }
    }

    /**
     * 暂停当前界面的动态表情执行
     */
    public void pauseGif() {
        if (mGifRunnable != null) {
            mGifRunnable.pauseHandler();
        }
    }

    /**
     * 停止某个界面的动态表情执行,将任务移除
     *
     * @param activityTag 停止界面 activity 的 Tag
     */
    public void stopGif(String activityTag) {
        if (mGifRunnable != null) {
            mGifRunnable.stopHandler(activityTag);
        }
    }

}
