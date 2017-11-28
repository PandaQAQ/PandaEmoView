package com.pandaq.emoticonlib.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pandaq.emoticonlib.EmoticonManager;
import com.pandaq.emoticonlib.R;
import com.pandaq.emoticonlib.StickerManager;
import com.pandaq.emoticonlib.listeners.IEmoticonMenuClickListener;
import com.pandaq.emoticonlib.listeners.IStickerSelectedListener;
import com.pandaq.emoticonlib.sticker.StickerCategory;
import com.pandaq.emoticonlib.utils.EmoticonUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by huxinyu on 2017/10/19 0019.
 * description : 表情输入键盘视图 View
 */

public class PandaEmoView extends RelativeLayout {
    /* column of emoticon per page */
    public final static int EMOJI_COLUMN = 7;
    /* row of emoticon per page */
    public final static int EMOJI_ROW = 3;
    /* members of emoticon per page
     * the last one is delete button;
     */
    public final static int EMOJI_PER_PAGE = EMOJI_COLUMN * EMOJI_ROW - 1;
    /* column of sticker per page*/
    public final static int STICKER_COLUMN = 4;
    /* row of sticker per page*/
    public final static int STICKER_ROW = 2;
    /* members of sticker per page
     * the last one is delete button;
     */
    public final static int STICKER_PER_PAGE = STICKER_COLUMN * STICKER_ROW;
    private int mMeasuredWidth;
    private int mMeasuredHeight;

    private Context mContext;
    private ViewPager mEmoticonPager;
    private LinearLayout mIndicatorLayout;
    private LinearLayout mBottomTabLayout;
    private EmotionTab mAddTab;
    private EmotionTab mSettingTab;
    private int mTabCount;
    private ArrayList<View> mTabs = new ArrayList<>();
    private int mTabPosi = 0;
    private PandaEmoEditText mMessageEditText;
    private IStickerSelectedListener mEmoticonSelectedListener;
    private IEmoticonMenuClickListener mEmoticonExtClickListener;
    private boolean loadedResource = false;

    public PandaEmoView(Context context) {
        this(context, null);
    }

    public PandaEmoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PandaEmoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getHeight() != 0 && !loadedResource) {
            init();
            initListener();
            loadedResource = true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = measureWidth(widthMeasureSpec);
        mMeasuredHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(mMeasuredWidth, mMeasuredHeight);
    }

    //计算控件布局宽度
    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) { // 精确模式直接显示真实 Size
            result = specSize;
        } else { //非精确模式时显示默认 Size 如果是限制类型则显示默认值和限制值中较小的一个
            result = EmoticonUtils.dp2px(mContext, 200);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    //计算控件布局高度
    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) { // 精确模式直接显示真实 Size
            result = specSize;
        } else { //非精确模式时显示默认 Size 如果是限制类型则显示默认值和限制值中较小的一个
            result = EmoticonUtils.dp2px(mContext, 200);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.emoticon_layout, this);
        }
        mEmoticonPager = (ViewPager) findViewById(R.id.vpEmoticon);
        mIndicatorLayout = (LinearLayout) findViewById(R.id.llIndicator);
        mBottomTabLayout = (LinearLayout) findViewById(R.id.llTabContainer);
        mAddTab = (EmotionTab) findViewById(R.id.tabAdd);
        if (EmoticonManager.getInstance().isShowAddButton()) {
            mAddTab.setVisibility(VISIBLE);
        } else {
            mAddTab.setVisibility(GONE);
        }
        initTabs();
    }

    /**
     * 初始化底部按钮
     */
    private void initTabs() {
        if (mBottomTabLayout == null) return;
        mTabs.clear();
        mBottomTabLayout.removeAllViews();
        //添加默认表情 Tab
        EmotionTab emojiTab = new EmotionTab(mContext, EmoticonManager.getInstance().getDefaultIconRes());
        mBottomTabLayout.addView(emojiTab);
        mTabs.add(emojiTab);
        //添加所有的贴图tab
        if (EmoticonManager.getInstance().isShowStickers()) {  // 是否显示
            List<StickerCategory> stickerCategories = StickerManager.getInstance().getStickerCategories();
            for (int i = 0; i < stickerCategories.size(); i++) {
                StickerCategory category = stickerCategories.get(i);
                EmotionTab tab;
                if (category.getName().equals(StickerManager.selfSticker)) {
                    tab = new EmotionTab(mContext, R.drawable.icon_self);
                    mBottomTabLayout.addView(tab);
                    mTabs.add(tab);
                } else {
                    tab = new EmotionTab(mContext, category.getCoverPath());
                    mBottomTabLayout.addView(tab);
                    mTabs.add(tab);
                }
            }
        }
        //最后添加一个表情设置Tab
        if (EmoticonManager.getInstance().isShowSetButton()) {
            mSettingTab = new EmotionTab(mContext, R.drawable.ic_emotion_setting);
            StateListDrawable drawable = new StateListDrawable();
            Drawable unSelected = mContext.getResources().getDrawable(R.color.white);
            drawable.addState(new int[]{-android.R.attr.state_pressed}, unSelected);
            Drawable selected = mContext.getResources().getDrawable(R.color.gray_text);
            drawable.addState(new int[]{android.R.attr.state_pressed}, selected);
            mSettingTab.setBackground(drawable);
            mBottomTabLayout.addView(mSettingTab);
            mTabs.add(mSettingTab);
        }
        selectTab(0); //默认底一个被选中
    }

    /**
     * 选择选中的 Item
     */
    private void selectTab(int tabPosi) {
        if (EmoticonManager.getInstance().isShowSetButton()) {
            if (tabPosi == mTabs.size() - 1)
                return;
        }
        for (int i = 0; i < mTabCount; i++) {
            View tab = mTabs.get(i);
            tab.setBackgroundResource(R.drawable.shape_tab_normal);
        }
        mTabs.get(tabPosi).setBackgroundResource(R.drawable.shape_tab_press);
        //显示表情内容
        fillVpEmotioin(tabPosi);
    }

    private void fillVpEmotioin(int tabPosi) {
        EmotionViewPagerAdapter adapter = new EmotionViewPagerAdapter(mContext, mMeasuredWidth, mMeasuredHeight, tabPosi, mEmoticonSelectedListener);
        mEmoticonPager.setAdapter(adapter);
        mIndicatorLayout.removeAllViews();
        setCurPageCommon(0);
        if (tabPosi == 0) {
            adapter.attachEditText(mMessageEditText);
        }
    }

    private void initListener() {
        if (mBottomTabLayout == null) return;
        if (EmoticonManager.getInstance().isShowSetButton()) {
            mTabCount = mBottomTabLayout.getChildCount() - 1;//不包含最后的设置按钮
        } else {
            mTabCount = mBottomTabLayout.getChildCount();
        }
        for (int position = 0; position < mTabCount; position++) {
            View tab = mBottomTabLayout.getChildAt(position);
            tab.setTag(position);
            tab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTabPosi = (int) v.getTag();
                    selectTab(mTabPosi);
                }
            });
        }
        mEmoticonPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setCurPageCommon(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (mAddTab != null) {
            mAddTab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEmoticonExtClickListener != null) {
                        mEmoticonExtClickListener.onTabAddClick(v);
                    }
                }
            });
        }
        if (mSettingTab != null) {
            mSettingTab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEmoticonExtClickListener != null) {
                        mEmoticonExtClickListener.onTabSettingClick(v);
                    }
                }
            });
        }
    }


    private void setCurPageCommon(int position) {
        if (mTabPosi == 0) {
            setCurPage(position, (int) Math.ceil(EmoticonManager.getInstance().getDisplayCount() / (float) PandaEmoView.EMOJI_PER_PAGE));
        } else {
            StickerCategory category = StickerManager.getInstance().getStickerCategories().get(mTabPosi - 1);
            setCurPage(position, (int) Math.ceil(category.getStickers().size() / (float) PandaEmoView.STICKER_PER_PAGE));
        }
    }

    private void setCurPage(int page, int pageCount) {
        int hasCount = mIndicatorLayout.getChildCount();
        int forMax = Math.max(hasCount, pageCount);
        ImageView ivCur;
        for (int i = 0; i < forMax; i++) {
            if (pageCount <= hasCount) {
                if (i >= pageCount) {
                    mIndicatorLayout.getChildAt(i).setVisibility(View.GONE);
                    continue;
                } else {
                    ivCur = (ImageView) mIndicatorLayout.getChildAt(i);
                }
            } else {
                if (i < hasCount) {
                    ivCur = (ImageView) mIndicatorLayout.getChildAt(i);
                } else {
                    ivCur = new ImageView(mContext);
                    ivCur.setBackgroundResource(R.drawable.selector_view_pager_indicator);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(EmoticonUtils.dp2px(mContext, 8), EmoticonUtils.dp2px(mContext, 8));
                    ivCur.setLayoutParams(params);
                    params.leftMargin = EmoticonUtils.dp2px(mContext, 3);
                    params.rightMargin = EmoticonUtils.dp2px(mContext, 3);
                    mIndicatorLayout.addView(ivCur);
                }
            }
            ivCur.setId(i);
            ivCur.setSelected(i == page);
            ivCur.setVisibility(View.VISIBLE);
        }
    }

    public void attachEditText(PandaEmoEditText messageEditText) {
        mMessageEditText = messageEditText;
    }

    public PandaEmoEditText getAttachEditText() {
        return mMessageEditText;
    }

    public void setEmoticonSelectedListener(IStickerSelectedListener emotionSelectedListener) {
        mEmoticonSelectedListener = emotionSelectedListener;
    }

    public void setEmoticonExtClickListener(IEmoticonMenuClickListener emotionExtClickListener) {
        mEmoticonExtClickListener = emotionExtClickListener;
    }

    /**
     * 新增了表情库后调用
     */
    public void reloadEmos(int position) {
        StickerManager.getInstance().loadStickerCategory();
        initTabs();
        initListener();
        invalidate();
        if (0 <= position && position < mTabs.size()) {
            selectTab(position);
        }
    }

}
