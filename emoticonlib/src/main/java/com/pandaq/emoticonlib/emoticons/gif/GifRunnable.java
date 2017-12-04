package com.pandaq.emoticonlib.emoticons.gif;

import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huxinyu on 2017/10/11 0011.
 * description ：gif 运行类
 */

public class GifRunnable implements Runnable {
    private Map<String, List<AnimatedGifDrawable>> mGifDrawableMap = new HashMap<>();// 用来存储每个 Activity 显示的Drawable
    private Map<String, List<AnimatedGifDrawable.RunGifCallBack>> listenersMap = new HashMap<>();
    private Handler mHandler;
    private boolean isRunning = false;
    private String currentActivity = null;
    private long frameDuration = 200;

    public GifRunnable(AnimatedGifDrawable gifDrawable, Handler handler) {
        addGifDrawable(gifDrawable);
        mHandler = handler;
    }

    @Override
    public void run() {
        isRunning = true;
        if (currentActivity != null) {
            List<AnimatedGifDrawable> runningDrawables = mGifDrawableMap.get(currentActivity);
            if (runningDrawables != null) {
                for (AnimatedGifDrawable gifDrawable : runningDrawables) {
                    AnimatedGifDrawable.RunGifCallBack listener = gifDrawable.getUpdateListener();
                    List<AnimatedGifDrawable.RunGifCallBack> runningListener = listenersMap.get(currentActivity);
                    if (runningListener != null) {
                        if (!runningListener.contains(listener)) {
                            runningListener.add(listener);
                        }
                    } else {
                        // 为空时肯定不存在直接添加
                        runningListener = new ArrayList<>();
                        runningListener.add(listener);
                        listenersMap.put(currentActivity, runningListener);
                    }
                    gifDrawable.nextFrame();
                }
                for (AnimatedGifDrawable.RunGifCallBack callBack : listenersMap.get(currentActivity)) {
                    if (callBack != null) {
                        callBack.run();
                    }
                }
                frameDuration = runningDrawables.get(0).getFrameDuration();
            }
        }
        mHandler.postDelayed(this, frameDuration);
    }

    public void addGifDrawable(AnimatedGifDrawable gifDrawable) {
        List<AnimatedGifDrawable> runningDrawables = mGifDrawableMap.get(gifDrawable.getContainerTag());
        if (runningDrawables != null) {
            if (!runningDrawables.contains(gifDrawable)) {
                runningDrawables.add(gifDrawable);
            }
        } else {
            // 为空时肯定不存在直接添加
            runningDrawables = new ArrayList<>();
            runningDrawables.add(gifDrawable);
            mGifDrawableMap.put(gifDrawable.getContainerTag(), runningDrawables);
        }
    }

    /**
     * 使用了表情转换的界面退出时调用，停止动态图handler
     */
    public void pauseHandler() {
        //暂停时空执行
        currentActivity = null;
    }

    /**
     * 使用了表情转换的界面退出时调用，停止动态图handler
     */
    public void clearHandler(String activityName) {
        currentActivity = null;
        //清除当前页的数据
        mGifDrawableMap.remove(activityName);
        // 当退出当前Activity后没表情显示时停止 Runable 清除所有动态表情数据
        listenersMap.remove(activityName);
        if (mGifDrawableMap.size() == 0) {
            clearAll();
        }
    }

    private void clearAll() {
        mHandler.removeCallbacks(this);
        mHandler.removeCallbacksAndMessages(null);
        mGifDrawableMap.clear();
        isRunning = false;
    }

    /**
     * 启动运行
     */
    public void startHandler(String activityName) {
        currentActivity = activityName;
        if (mGifDrawableMap != null && mGifDrawableMap.size() > 0 && !isRunning) {
            run();
        }
    }

    boolean isRunning() {
        return isRunning;
    }
}
