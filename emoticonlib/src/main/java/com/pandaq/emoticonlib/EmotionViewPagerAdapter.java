package com.pandaq.emoticonlib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.pandaq.emoticonlib.gif.EmojiAdapter;
import com.pandaq.emoticonlib.listeners.IStickerSelectedListener;
import com.pandaq.emoticonlib.sticker.StickerAdapter;
import com.pandaq.emoticonlib.sticker.StickerCategory;
import com.pandaq.emoticonlib.sticker.StickerItem;
import com.pandaq.emoticonlib.view.PandaEmoView;

import java.util.List;


/**
 * 表情控件的ViewPager适配器(emoji + 贴图)
 */

public class EmotionViewPagerAdapter extends PagerAdapter {

    private int mPageCount = 0;
    private int mTabPosi = 0;

    private int mEmotionLayoutWidth;
    private int mEmotionLayoutHeight;

    private IStickerSelectedListener listener;
    private EditText mMessageEditText;

    private Context mContext;

    public void attachEditText(EditText messageEditText) {
        mMessageEditText = messageEditText;
    }

    public EmotionViewPagerAdapter(Context context, int emotionLayoutWidth, int emotionLayoutHeight, int tabPosi, IStickerSelectedListener listener) {
        mContext = context;
        mEmotionLayoutWidth = emotionLayoutWidth;
        mEmotionLayoutHeight = emotionLayoutHeight;
        mTabPosi = tabPosi;
        if (mTabPosi == 0) { // 默认的 emoji 或者 gif emoji
            mPageCount = (int) Math.ceil(EmoticonManager.getInstance().getDisplayCount() / (float) PandaEmoView.EMOJI_PER_PAGE);
        } else { //贴图表情
            mPageCount = (int) Math.ceil(StickerManager.getInstance().getStickerCategories().get(mTabPosi - 1).getStickers().size() / (float) PandaEmoView.STICKER_PER_PAGE);
        }
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return mPageCount == 0 ? 1 : mPageCount;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Context context = container.getContext();
        RelativeLayout rl = new RelativeLayout(context);
        rl.setGravity(Gravity.CENTER);
        GridView gridView = new GridView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        gridView.setLayoutParams(params);
        gridView.setGravity(Gravity.CENTER);

        gridView.setTag(position);//标记自己是第几页
        if (mTabPosi == 0) {
            gridView.setOnItemClickListener(emojiListener);
            gridView.setAdapter(new EmojiAdapter(context, mEmotionLayoutWidth, mEmotionLayoutHeight, position * PandaEmoView.EMOJI_PER_PAGE));
            gridView.setNumColumns(PandaEmoView.EMOJI_COLUMN);
        } else {
            StickerCategory category = StickerManager.getInstance().getCategory(StickerManager.getInstance().getStickerCategories().get(mTabPosi - 1).getName());
            gridView.setOnItemClickListener(stickerListener);
            gridView.setAdapter(new StickerAdapter(context, category, mEmotionLayoutWidth, mEmotionLayoutHeight, position * PandaEmoView.STICKER_PER_PAGE));
            gridView.setNumColumns(PandaEmoView.STICKER_COLUMN);
        }

        rl.addView(gridView);
        container.addView(rl);
        return rl;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private AdapterView.OnItemClickListener emojiListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            int index = position + (Integer) parent.getTag() * PandaEmoView.EMOJI_PER_PAGE;
            int count = EmoticonManager.getInstance().getDisplayCount();
            if (position == PandaEmoView.EMOJI_PER_PAGE || index >= count) {
                onEmojiSelected("/DEL");
            } else {
                String text = EmoticonManager.getInstance().getDisplayText((int) id);
                if (!TextUtils.isEmpty(text)) {
                    onEmojiSelected(text);
                }
            }
        }
    };
    private AdapterView.OnItemClickListener stickerListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            StickerCategory category = StickerManager.getInstance().getStickerCategories().get(mTabPosi - 1);
            List<StickerItem> stickers = category.getStickers();
            int index = position + (Integer) parent.getTag() * PandaEmoView.STICKER_PER_PAGE - 1;
            if (index >= stickers.size()) {
                Log.i("CSDN_LQR", "index " + index + " larger than size " + stickers.size());
                return;
            }
            if (listener != null) {
                if (index < 0) {
                    listener.onCustomAdd();
                } else {
                    StickerItem sticker = stickers.get(index);
                    StickerCategory real = StickerManager.getInstance().getCategory(sticker.getCategory());
                    if (real == null) {
                        return;
                    }
                    listener.onStickerSelected(sticker.getCategory(), sticker.getName(),
                            StickerManager.getInstance().getStickerBitmapPath(sticker.getCategory(), sticker.getName()));
                }
            }
        }
    };

    private void onEmojiSelected(String key) {
        if (mMessageEditText == null)
            return;
        Editable editable = mMessageEditText.getText();
        if (key.equals("/DEL")) {
            mMessageEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        } else {
            int start = mMessageEditText.getSelectionStart();
            int end = mMessageEditText.getSelectionEnd();
            start = (start < 0 ? 0 : start);
            end = (start < 0 ? 0 : end);
            editable.replace(start, end, key);
            int editEnd = mMessageEditText.getSelectionEnd();
            PandaEmoTranslator.getInstance().replaceEmoticons(mContext, editable, 0, editable.toString().length());
            mMessageEditText.setSelection(editEnd);
        }
    }
}
