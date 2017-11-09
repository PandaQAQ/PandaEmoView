package com.pandaq.emoticonlib;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.pandaq.emoticonlib.utils.EmoticonUtils;
import com.pandaq.emoticonlib.view.PandaEmoEditText;
import com.pandaq.emoticonlib.view.PandaEmoView;


/**
 * Created by huxinyu on 2017/10/19 0019.
 * description ：表情键盘协调者类
 */

public class KeyBoardManager {

    private static final String SHARE_PREFERENCE_NAME = "EmotionKeyBoard";
    private static final String SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "sofe_input_height";
    private Activity mActivity;
    private InputMethodManager mInputManager;//软键盘管理类
    private SharedPreferences mSp;
    private PandaEmoView mEmotionView;//表情布局
    private PandaEmoEditText mEditText;
    private boolean interceptBackPress = false;
    private OnEmotionButtonOnClickListener mOnEmotionButtonOnClickListener; // 表情切换按钮点击回调
    private OnInputShowListener mOnInputShowListener; // 输入布局显示收起回调（包括系统自带和自定义表情键盘）

    public static KeyBoardManager with(Activity activity) {
        KeyBoardManager emotionInputDetector = new KeyBoardManager();
        emotionInputDetector.mActivity = activity;
        emotionInputDetector.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        emotionInputDetector.mSp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return emotionInputDetector;
    }

    /**
     * 绑定编辑框
     */
    private KeyBoardManager bindToEditText(final PandaEmoEditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEmotionLayout(true);//隐藏表情布局，显示软件盘
            }
        });
        // 监听返回物理按键，不用 onBackpressed 因为软键盘弹出时返回键不会走 onBackpressed
        mEditText.setBackPressedListener(new PandaEmoEditText.IBackPressedListener() {
            @Override
            public void backPressed() {
                interceptBackPress = mEmotionView.isShown();
                hideEmotionLayout(false);
            }
        });
        return this;
    }

    /**
     * 绑定表情按钮（可以有多个表情按钮）
     */
    public KeyBoardManager bindToEmotionButton(View... emotionButton) {
        for (View view : emotionButton) {
            view.setOnClickListener(getOnEmotionButtonOnClickListener());
        }
        return this;
    }

    private View.OnClickListener getOnEmotionButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEmotionButtonOnClickListener != null) {
                    if (mOnEmotionButtonOnClickListener.onEmotionButtonOnClickListener(v)) {
                        return;
                    }
                }
                if (mEmotionView.isShown()) {
                    hideEmotionLayout(true);//隐藏表情布局，显示软件盘
                } else {
                    if (isSoftInputShown()) { //同上
                        showEmotionLayout();
                    } else {
                        showEmotionLayout();//两者都没显示，直接显示表情布局
                    }
                }
            }
        };
    }

    /**
     * 设置表情内容布局
     *
     * @param emotionView 表情布局
     */
    public KeyBoardManager setEmotionView(PandaEmoView emotionView) {
        mEmotionView = emotionView;
        bindToEditText(mEmotionView.getAttachEditText());
        return this;
    }

    /**
     * 是否显示软件盘
     */
    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    /**
     * 点击返回键时先隐藏表情布局
     *
     * @return 是否拦截返回键
     */
    public boolean interceptBackPress() {
        return interceptBackPress;
    }

    private void showEmotionLayout() {
        ViewGroup.LayoutParams params = mEmotionView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = getKeyBoardHeight();
        mEmotionView.setLayoutParams(params);
        hideSoftInput();
        mEmotionView.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏表情布局
     *
     * @param showSoftInput 是否显示软件盘
     */
    private void hideEmotionLayout(boolean showSoftInput) {
        ViewGroup.LayoutParams params = mEmotionView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        if (showSoftInput) {
            showSoftInput();
            params.height = getKeyBoardHeight();
        } else {
            params.height = 0;
        }
        mEmotionView.setLayoutParams(params);
        mEmotionView.setVisibility(View.INVISIBLE);
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditText, 0);
            }
        });
    }

    /**
     * 隐藏软件盘
     */
    private void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 获取软件盘的高度
     */
    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /*
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;
        /*
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
        softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        if (softInputHeight < 0) {
            Log.w("LQR", "EmotionKeyboard--Warning: value of softInputHeight is below zero!");
        }
        //存一份到本地
        if (softInputHeight > 0) {
            mSp.edit().putInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight).apply();
        }
        return softInputHeight;
    }

    /**
     * 底部虚拟按键栏的高度
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 获取软键盘高度
     *
     * @return 软键盘高度 px
     */
    private int getKeyBoardHeight() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = mSp.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, EmoticonUtils.dp2px(mActivity, 270));
        }
        return softInputHeight;
    }

    /*================== 表情按钮点击事件回调 begin ==================*/
    interface OnEmotionButtonOnClickListener {
        /**
         * 主要是为了适用仿微信的情况，微信有一个表情按钮和一个功能按钮，这2个按钮都是控制了底部区域的显隐
         *
         * @return true:拦截切换输入法，false:让输入法正常切换
         */
        boolean onEmotionButtonOnClickListener(View view);
    }

    public void setOnEmotionButtonOnClickListener(OnEmotionButtonOnClickListener onEmotionButtonOnClickListener) {
        mOnEmotionButtonOnClickListener = onEmotionButtonOnClickListener;
    }
    /*================== 表情按钮点击事件回调 end ==================*/

    /*================== 表情按钮点击事件回调 begin ==================*/
    interface OnInputShowListener {
        boolean showInputView();
    }

    public void setOnInputListener(OnInputShowListener onInputShowListener) {
        mOnInputShowListener = onInputShowListener;
    }
    /*================== 表情按钮点击事件回调 end ==================*/

}
