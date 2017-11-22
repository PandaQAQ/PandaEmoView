package com.pandaq.emoticonlib.base;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;

import com.pandaq.emoticonlib.R;

/**
 * Created by huxinyu on 2017/11/22 0022.
 * description ：带侧滑返回功能的 Activity
 */

public class SwipeBackActivity extends BaseActivity implements SwipeBackLayout.SwipeListener {
    protected SwipeBackLayout layout;
    private ArgbEvaluator argbEvaluator;
    private int currentStatusColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
                R.layout.swipeback_base, null);
        layout.attachToActivity(this);
        argbEvaluator = new ArgbEvaluator();
        layout.addSwipeListener(this);
        if (Build.VERSION.SDK_INT >= 23) {
            currentStatusColor = getResources().getColor(R.color.colorPrimaryDark, null);
        } else {
            currentStatusColor = getResources().getColor(R.color.colorPrimaryDark);
        }
    }

    public void addViewPager(ViewPager pager) {
        layout.addViewPager(pager);
    }

    @Override
    public void swipeValue(double value) {
        int statusColor = (int) argbEvaluator.evaluate((float) value, currentStatusColor,
                Color.parseColor("#00ffffff"));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(statusColor);
//        }
    }
}
