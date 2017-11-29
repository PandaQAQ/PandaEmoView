package com.pandaq.emoticonlib.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huxinyu on 2017/10/12 0012.
 * description ：设置高亮文本可点击
 */

public class ClickTextSpan extends ClickableSpan {
    private int mHighLightColor = Color.parseColor("#fdc300");
    private boolean mUnderLine = false;
    private View.OnClickListener mClickListener;

    public ClickTextSpan(View.OnClickListener listener) {
        this.mClickListener = listener;
    }

    public ClickTextSpan(int color, View.OnClickListener listener) {
        this.mHighLightColor = color;
        this.mClickListener = listener;
    }

    public ClickTextSpan(int color, boolean underline, View.OnClickListener listener) {
        this.mHighLightColor = color;
        this.mUnderLine = underline;
        this.mClickListener = listener;
    }

    @Override
    public void onClick(View widget) {
        if (mClickListener != null)
            mClickListener.onClick(widget);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(mHighLightColor);
        ds.setUnderlineText(mUnderLine);
    }

    public static void setTextHighLightWithClick(TextView tv, String text
            , String keyWord, int color, View.OnClickListener listener) {
        tv.setClickable(true);
        tv.setHighlightColor(Color.TRANSPARENT);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableString s = new SpannableString(text);
        Pattern p = Pattern.compile(keyWord);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            ClickTextSpan clickTextSpan;
            if (color == -1) {  //不设置颜色默认为黄色
                clickTextSpan = new ClickTextSpan(listener);
            } else {
                clickTextSpan = new ClickTextSpan(color, listener);
            }
            s.setSpan(clickTextSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        tv.setText(s);
    }
}
