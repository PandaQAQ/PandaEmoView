package com.pandaq.emoticonlib;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.pandaq.emoticonlib.listeners.OnMultiFixClickListener;
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
    private PandaEmoView mEmotionView; //表情布局
    private PandaEmoEditText mEditText;
    private boolean interceptBackPress = false;
    private View lockView;
    private OnEmotionButtonOnClickListener mOnEmotionButtonOnClickListener; // 表情切换按钮点击回调
    private OnInputShowListener mOnInputShowListener; // 输入布局显示收起回调（包括系统自带和自定义表情键盘）
    @SuppressLint("StaticFieldLeak")
    private static KeyBoardManager sKeyBoardManager;
    private boolean inputViewShow;

    public static KeyBoardManager with(Activity activity) {
        if (sKeyBoardManager == null) {
            synchronized (KeyBoardManager.class) {
                if (sKeyBoardManager == null) {
                    sKeyBoardManager = new KeyBoardManager();
                }
            }
        }
        sKeyBoardManager.mActivity = activity;
        sKeyBoardManager.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        sKeyBoardManager.mSp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sKeyBoardManager;
    }

    /**
     * 绑定编辑框
     */
    private KeyBoardManager bindToEditText(final PandaEmoEditText editText) {
        mEditText = editText;
        mEditText.setKeyBoardManager(sKeyBoardManager);
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mEmotionView.isShown()) {
                        lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                        hideEmotionLayout(true);//隐藏表情布局，显示软件盘
                        //软件盘显示后，释放内容高度
                        mEditText.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                unlockContentHeightDelayed();
                            }
                        }, 200L);
                    }
                }
                return false;
            }
        });
        // 监听返回物理按键，不用 onBackpressed 因为软键盘弹出时返回键不会走 onBackpressed
        mEditText.setBackPressedListener(new PandaEmoEditText.IBackPressedListener() {
            @Override
            public void backPressed() {
                interceptBackPress = mEmotionView.isShown();
                hideEmotionLayout(false);
                unlockContentHeightDelayed();
            }
        });
        return sKeyBoardManager;
    }

    /**
     * 绑定内容view，此view用于固定bar的高度，防止跳闪
     * contentView 界面布局为 内容和表情布局分开格式，contentView 为除表情外的所有内容布局
     */
    public KeyBoardManager bindToLockContent(View lockView) {
        this.lockView = lockView;
        this.lockView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom - oldBottom == getKeyBoardHeight()) {
                    if (mOnInputShowListener != null) {
                        mOnInputShowListener.showInputView(false);
                    }
                    inputViewShow = false;
                } else if (oldBottom - bottom == getKeyBoardHeight()) {
                    if (mOnInputShowListener != null) {
                        mOnInputShowListener.showInputView(true);
                    }
                    inputViewShow = true;
                }
            }
        });
        return sKeyBoardManager;
    }

    /**
     * 绑定表情按钮（可以有多个表情按钮）
     */
    public KeyBoardManager bindToEmotionButton(View... emotionButton) {
        for (View view : emotionButton) {
            view.setOnClickListener(getOnEmotionButtonOnClickListener());
        }
        return sKeyBoardManager;
    }

    private View.OnClickListener getOnEmotionButtonOnClickListener() {
        return new OnMultiFixClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (mOnEmotionButtonOnClickListener != null) {
                    if (mOnEmotionButtonOnClickListener.onEmotionButtonOnClickListener(v)) {
                        return;
                    }
                }
                if (mEmotionView.isShown()) {
                    lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                    hideEmotionLayout(true);//隐藏表情布局，显示软件盘
                    unlockContentHeightDelayed();
                } else {
                    if (inputViewShow) {
                        lockContentHeight();
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showEmotionLayout();
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
        if (emotionView.getAttachEditText() == null) {
            try {
                throw new Exception("Please call PandaEmoView.attachEditText(PandaEmoEditText messageEditText) first");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        mEmotionView = emotionView;
        PandaEmoManager.getInstance().manage(mEmotionView);
        bindToEditText(mEmotionView.getAttachEditText());
        return sKeyBoardManager;
    }

    /**
     * 锁定内容高度，防止跳闪
     */
    private void lockContentHeight() {
        if (lockView == null) return;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lockView.getLayoutParams();
        params.height = lockView.getHeight();
        params.weight = 0F;
        lockView.setLayoutParams(params);
    }

    /**
     * 释放被锁定的内容高度
     */
    private void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) lockView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
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
        hideSoftInput();
        ViewGroup.LayoutParams params = mEmotionView.getLayoutParams();
        params.height = getKeyBoardHeight();
        mEmotionView.setVisibility(View.VISIBLE);
        mEmotionView.setLayoutParams(params);
    }

    /**
     * 隐藏表情布局
     *
     * @param showSoftInput 是否显示软件盘
     */
    private void hideEmotionLayout(final boolean showSoftInput) {
        if (showSoftInput) {
            showSoftInput();
        } else {
            hideSoftInput();
        }
        mEmotionView.setVisibility(View.GONE);
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private void showSoftInput() {
        mEditText.requestFocus();
        mInputManager.showSoftInput(mEditText, 0);
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
        // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
        softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        if (softInputHeight < 0) {
            Log.w("PandaQ", "EmotionKeyboard--Warning: value of softInputHeight is below zero!");
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
     * 获取底部输入控件的显示状态
     *
     * @return true 显示 false 关闭
     */
    public boolean isInputViewShow() {
        return inputViewShow;
    }

    /**
     * 获取软键盘高度
     *
     * @return 软键盘高度 px
     */
    public int getKeyBoardHeight() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = mSp.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, EmoticonUtils.dp2px(mActivity, 270));
        }
        return softInputHeight;
    }

    /*================== 表情按钮点击事件回调 begin ==================*/
    public interface OnEmotionButtonOnClickListener {
        /**
         * 主要是为了适用仿微信的情况，微信有一个表情按钮和一个功能按钮，这2个按钮都是控制了底部区域的显隐
         *
         * @return true:拦截切换输入法，false:让输入法正常切换
         */
        boolean onEmotionButtonOnClickListener(View view);
    }

    public KeyBoardManager setOnEmotionButtonOnClickListener(OnEmotionButtonOnClickListener onEmotionButtonOnClickListener) {
        mOnEmotionButtonOnClickListener = onEmotionButtonOnClickListener;
        return this;
    }
    /*================== 表情按钮点击事件回调 end ==================*/

    /*================== 表情按钮点击事件回调 begin ==================*/
    public interface OnInputShowListener {
        void showInputView(boolean show);
    }

    public KeyBoardManager setOnInputListener(OnInputShowListener onInputShowListener) {
        mOnInputShowListener = onInputShowListener;
        return sKeyBoardManager;
    }
    /*================== 表情按钮点击事件回调 end ==================*/

    public void hideInputLayout() {
        if (inputViewShow) {
            lockContentHeight();
            hideEmotionLayout(false);
            unlockContentHeightDelayed();
        } else {
            hideEmotionLayout(false);
        }
    }

    public void showInputLayout() {
        if (inputViewShow) {
            lockContentHeight();
            showSoftInput();
            unlockContentHeightDelayed();
        } else {
            showSoftInput();
        }
    }
}
