package com.pandaq.emoticonlib.sticker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pandaq.emoticonlib.PandaEmoManager;
import com.pandaq.emoticonlib.R;
import com.pandaq.emoticonlib.utils.EmoticonUtils;
import com.pandaq.emoticonlib.view.PandaEmoView;

/**
 * 贴图适配器
 */

public class StickerAdapter extends BaseAdapter {

    private Context mContext;
    private StickerCategory mCategory;
    private int startIndex;
    private float mPerHeight;
    private float mIvSize;

    public StickerAdapter(Context context, StickerCategory category, int emotionLayoutWidth, int emotionLayoutHeight, int startIndex) {
        mContext = context;
        mCategory = category;
        this.startIndex = startIndex;
        int realPagerHeight = emotionLayoutHeight - EmoticonUtils.dp2px(mContext, 85);
        float perWidth = emotionLayoutWidth * 1f / PandaEmoManager.getInstance().getStickerColumn();
        mPerHeight = realPagerHeight * 1f / PandaEmoManager.getInstance().getStickerRow();

        float ivWidth = perWidth * .8f;
        float ivHeight = mPerHeight * .8f;
        mIvSize = Math.min(ivWidth, ivHeight);
    }


    @Override
    public int getCount() {
        return PandaEmoManager.getInstance().getStickerPerPage();
    }

    @Override
    public Object getItem(int position) {
        return mCategory.getStickers().get(startIndex + position);
    }

    @Override
    public long getItemId(int position) {
        return startIndex + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StickerViewHolder viewHolder;
        if (convertView == null) {
            RelativeLayout rl = new RelativeLayout(mContext);
            rl.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int) mPerHeight));
            ImageView imageView = new ImageView(mContext);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.width = (int) mIvSize;
            params.height = (int) mIvSize;
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            imageView.setLayoutParams(params);
            rl.addView(imageView);
            viewHolder = new StickerViewHolder();
            viewHolder.mImageView = imageView;
            convertView = rl;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (StickerViewHolder) convertView.getTag();
        }
        int index = startIndex + position;
        if (mCategory.isDefault() && index == 0) {
            viewHolder.mImageView.setImageResource(R.drawable.ic_action_add);
        } else {
            StickerItem sticker;
            if (mCategory.isDefault()) {
                sticker = mCategory.getStickers().get(index - 1);
            } else {
                sticker = mCategory.getStickers().get(index);
            }
            if (index >= mCategory.getStickers().size()) {
                return convertView;
            }
            if (sticker == null) {
                return convertView;
            }
            String stickerBitmapUri = sticker.getSourcePath();
            if (stickerBitmapUri != null) {
                PandaEmoManager.getInstance().getIImageLoader().displayImage(stickerBitmapUri, viewHolder.mImageView);
            }
        }
        return convertView;
    }

    private class StickerViewHolder {
        ImageView mImageView;
    }
}
