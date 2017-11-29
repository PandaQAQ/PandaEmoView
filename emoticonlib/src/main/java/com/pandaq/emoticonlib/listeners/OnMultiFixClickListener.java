package com.pandaq.emoticonlib.listeners;

import android.view.View;

/**
 * Created by huxinyu on 2017/11/13 0013.
 * description ：
 */

public abstract class OnMultiFixClickListener implements View.OnClickListener {


    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 300;
    private static long lastClickTime = 0;

    public abstract void onMultiClick(View v);

    @Override
    public void onClick(View v) {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime;
            onMultiClick(v);
        }
    }
}
