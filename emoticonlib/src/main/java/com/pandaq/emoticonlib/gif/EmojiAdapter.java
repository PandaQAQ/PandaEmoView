package com.pandaq.emoticonlib.gif;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pandaq.emoticonlib.EmoticonManager;
import com.pandaq.emoticonlib.R;
import com.pandaq.emoticonlib.utils.EmoticonUtils;
import com.pandaq.emoticonlib.view.PandaEmoView;

public class EmojiAdapter extends BaseAdapter {

    private Context mContext;
    private int mStartIndex;
    private final float mPerHeight;
    private final float mIvSize;

    public EmojiAdapter(Context context, int emotionLayoutWidth, int emotionLayoutHeight, int startIndex) {
        mContext = context;
        mStartIndex = startIndex;
        if (emotionLayoutHeight <= 0) {
            emotionLayoutHeight = EmoticonUtils.dp2px(context, 270);
        }
        int mEmotionLayoutHeight = emotionLayoutHeight - EmoticonUtils.dp2px(mContext, 35 + 26 + 50);
        float perWidth = emotionLayoutWidth * 1f / PandaEmoView.EMOJI_COLUMN;
        mPerHeight = mEmotionLayoutHeight * 1f / PandaEmoView.EMOJI_ROW;
        float ivWidth = perWidth * .6f;
        float ivHeight = mPerHeight * .6f;
        mIvSize = Math.min(ivWidth, ivHeight);
    }

    @Override
    public int getCount() {
        int count = EmoticonManager.getDisplayCount() - mStartIndex + 1;
        count = Math.min(count, PandaEmoView.EMOJI_PER_PAGE + 1);
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return mStartIndex + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout rl = new RelativeLayout(mContext);
        rl.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int) mPerHeight));
        ImageView emojiThumb = new ImageView(mContext);
        int count = EmoticonManager.getDisplayCount();
        int index = mStartIndex + position;
        if (position == PandaEmoView.EMOJI_PER_PAGE || index == count) {
            emojiThumb.setBackgroundResource(R.drawable.ic_emoji_del);
        } else if (index < count) {
            emojiThumb.setBackground(EmoticonManager.getDisplayDrawable(mContext, index));
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.width = (int) mIvSize;
        layoutParams.height = (int) mIvSize;
        emojiThumb.setLayoutParams(layoutParams);
        rl.setGravity(Gravity.CENTER);
        rl.addView(emojiThumb);
        return rl;
    }
}
