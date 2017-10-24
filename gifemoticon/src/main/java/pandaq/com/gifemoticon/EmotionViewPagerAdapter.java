package pandaq.com.gifemoticon;

import android.content.Context;
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

import java.util.List;

import pandaq.com.gifemoticon.gif.EmojiAdapter;
import pandaq.com.gifemoticon.sticker.StickerAdapter;
import pandaq.com.gifemoticon.sticker.StickerCategory;
import pandaq.com.gifemoticon.sticker.StickerItem;
import pandaq.com.gifemoticon.view.EmoticonView;


/**
 * 表情控件的ViewPager适配器(emoji + 贴图)
 */

public class EmotionViewPagerAdapter extends PagerAdapter {

    private int mPageCount = 0;
    private int mTabPosi = 0;

    private int mEmotionLayoutWidth;
    private int mEmotionLayoutHeight;

    private IEmoticonSelectedListener listener;
    private EditText mMessageEditText;

    public void attachEditText(EditText messageEditText) {
        mMessageEditText = messageEditText;
    }

    public EmotionViewPagerAdapter(int emotionLayoutWidth, int emotionLayoutHeight, int tabPosi, IEmoticonSelectedListener listener) {
        mEmotionLayoutWidth = emotionLayoutWidth;
        mEmotionLayoutHeight = emotionLayoutHeight;
        mTabPosi = tabPosi;
        if (mTabPosi == 0) { // 默认的 emoji 或者 gif emoji
            mPageCount = (int) Math.ceil(EmoticonManager.getDisplayCount() / (float) EmoticonView.EMOJI_PER_PAGE);
        } else { //贴图表情
            mPageCount = (int) Math.ceil(StickerManager.getInstance().getStickerCategories().get(mTabPosi - 1).getStickers().size() / (float) EmoticonView.STICKER_PER_PAGE);
        }
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return mPageCount == 0 ? 1 : mPageCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
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
            gridView.setAdapter(new EmojiAdapter(context, mEmotionLayoutWidth, mEmotionLayoutHeight, position * EmoticonView.EMOJI_PER_PAGE));
            gridView.setNumColumns(EmoticonView.EMOJI_COLUMN);
        } else {
            StickerCategory category = StickerManager.getInstance().getCategory(StickerManager.getInstance().getStickerCategories().get(mTabPosi - 1).getName());
            gridView.setOnItemClickListener(stickerListener);
            gridView.setAdapter(new StickerAdapter(context, category, mEmotionLayoutWidth, mEmotionLayoutHeight, position * EmoticonView.STICKER_PER_PAGE));
            gridView.setNumColumns(EmoticonView.STICKER_COLUMN);
        }

        rl.addView(gridView);
        container.addView(rl);
        return rl;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public AdapterView.OnItemClickListener emojiListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            int index = position + (Integer) parent.getTag() * EmoticonView.EMOJI_PER_PAGE;
            int count = EmoticonManager.getDisplayCount();
            if (position == EmoticonView.EMOJI_PER_PAGE || index >= count) {
                if (listener != null) {
                    listener.onEmojiSelected("/DEL");
                }
                onEmojiSelected("/DEL");
            } else {
                String text = EmoticonManager.getDisplayText((int) id);
                if (!TextUtils.isEmpty(text)) {
                    if (listener != null) {
                        listener.onEmojiSelected(text);
                    }
                    onEmojiSelected(text);
                }
            }
        }
    };
    public AdapterView.OnItemClickListener stickerListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            StickerCategory category = StickerManager.getInstance().getStickerCategories().get(mTabPosi - 1);
            List<StickerItem> stickers = category.getStickers();
            int index = position + (Integer) parent.getTag() * EmoticonView.STICKER_PER_PAGE;

            if (index >= stickers.size()) {
                Log.i("CSDN_LQR", "index " + index + " larger than size " + stickers.size());
                return;
            }

            if (listener != null) {
                StickerItem sticker = stickers.get(index);
                StickerCategory real = StickerManager.getInstance().getCategory(sticker.getCategory());

                if (real == null) {
                    return;
                }

                listener.onStickerSelected(sticker.getCategory(), sticker.getName(), StickerManager.getInstance().getStickerBitmapPath(sticker.getCategory(), sticker.getName()));
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
            // TODO: 2017/10/19 0019  
//            MoonUtils.getInstance().replaceEmoticons(LQREmotionKit.getContext(), editable, 0, editable.toString().length());
            mMessageEditText.setSelection(editEnd);
        }
    }
}
