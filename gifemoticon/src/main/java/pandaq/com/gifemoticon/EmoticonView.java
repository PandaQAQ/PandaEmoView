package pandaq.com.gifemoticon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import pandaq.com.gifemoticon.sticker.StickerCategory;

/**
 * Created by huxinyu on 2017/10/19 0019.
 * description : 表情输入键盘视图 View
 */

public class EmoticonView extends RelativeLayout {
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
    private EditText mMessageEditText;
    private boolean mEmoticonAddVisiable = true;
    private IEmotionSelectedListener mEmotionSelectedListener;
    private IEmotionExtClickListener mEmotionExtClickListener;

    public EmoticonView(Context context) {
        this(context, null);
    }

    public EmoticonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmoticonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
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
        inflater.inflate(R.layout.emoticon_layout, this);
        mEmoticonPager = (ViewPager) findViewById(R.id.vpEmoticon);
        mIndicatorLayout = (LinearLayout) findViewById(R.id.llIndicator);
        mBottomTabLayout = (LinearLayout) findViewById(R.id.llTabContainer);
        mAddTab = (EmotionTab) findViewById(R.id.tabAdd);
        setEmotionAddVisiable(mEmoticonAddVisiable);
        initTabs();
    }

    /**
     * 初始化底部按钮
     */
    private void initTabs() {
        //添加默认表情 Tab
        EmotionTab emojiTab = new EmotionTab(mContext, R.drawable.cute);
        mBottomTabLayout.addView(emojiTab);
        mTabs.add(emojiTab);
        //添加所有的贴图tab
        List<StickerCategory> stickerCategories = StickerManager.getInstance().getStickerCategories();
        for (int i = 0; i < stickerCategories.size(); i++) {
            StickerCategory category = stickerCategories.get(i);
            EmotionTab tab = new EmotionTab(mContext, category.getCoverImgPath());
            mBottomTabLayout.addView(tab);
            mTabs.add(tab);
        }
        //最后添加一个表情设置Tab
        mSettingTab = new EmotionTab(mContext, R.drawable.ic_emotion_setting);
        StateListDrawable drawable = new StateListDrawable();
        Drawable unSelected = mContext.getResources().getDrawable(R.color.white);
        drawable.addState(new int[]{-android.R.attr.state_pressed}, unSelected);
        Drawable selected = mContext.getResources().getDrawable(R.color.gray_text);
        drawable.addState(new int[]{android.R.attr.state_pressed}, selected);
        mSettingTab.setBackground(drawable);
        mBottomTabLayout.addView(mSettingTab);
        mTabs.add(mSettingTab);
        selectTab(1); //默认底一个被选中
    }

    /**
     * 选择选中的 Item
     *
     * @param tabPosi
     */
    private void selectTab(int tabPosi) {
        if (tabPosi == mTabs.size() - 1)
            return;
        for (int i = 1; i < mTabCount; i++) {
            View tab = mTabs.get(i);
            tab.setBackgroundResource(R.drawable.shape_tab_normal);
        }
        mTabs.get(tabPosi).setBackgroundResource(R.drawable.shape_tab_press);
        //显示表情内容
        fillVpEmotioin(tabPosi);
    }

    private void fillVpEmotioin(int tabPosi) {
        EmotionViewPagerAdapter adapter = new EmotionViewPagerAdapter(mMeasuredWidth, mMeasuredHeight, tabPosi, mEmotionSelectedListener);
        mEmoticonPager.setAdapter(adapter);
        mIndicatorLayout.removeAllViews();
        setCurPageCommon(0);
        if (tabPosi == 0) {
            adapter.attachEditText(mMessageEditText);
        }
    }

    private void setCurPageCommon(int position) {
        if (mTabPosi == 0) {
            setCurPage(position, (int) Math.ceil(EmoticonManager.getDisplayCount() / (float) EmoticonView.EMOJI_PER_PAGE));
        } else {
            StickerCategory category = StickerManager.getInstance().getStickerCategories().get(mTabPosi - 1);
            setCurPage(position, (int) Math.ceil(category.getStickers().size() / (float) EmoticonView.STICKER_PER_PAGE));
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

    public void attachEditText(EditText messageEditText) {
        mMessageEditText = messageEditText;
    }

    /**
     * 设置表情添加按钮的显隐
     */
    public void setEmotionAddVisiable(boolean visiable) {
        mEmoticonAddVisiable = visiable;
        if (mAddTab != null) {
            mAddTab.setVisibility(mEmoticonAddVisiable ? View.VISIBLE : View.GONE);
        }
    }
}
