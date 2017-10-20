package pandaq.com.gifemoticon.sticker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import pandaq.com.gifemoticon.EmoticonManager;
import pandaq.com.gifemoticon.EmoticonUtils;
import pandaq.com.gifemoticon.EmoticonView;
import pandaq.com.gifemoticon.R;
import pandaq.com.gifemoticon.StickerManager;

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
        int emotionLayoutHeight1 = emotionLayoutHeight - EmoticonUtils.dp2px(mContext, 35 + 26 + 50);
        float perWidth = emotionLayoutWidth * 1f / EmoticonView.STICKER_COLUMN;
        mPerHeight = emotionLayoutHeight1 * 1f / EmoticonView.STICKER_ROW;

        float ivWidth = perWidth * .8f;
        float ivHeight = mPerHeight * .8f;
        mIvSize = Math.min(ivWidth, ivHeight);
    }


    @Override
    public int getCount() {
        int count = mCategory.getStickers().size() - startIndex;
        count = Math.min(count, EmoticonView.STICKER_PER_PAGE);
        return count;
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
            imageView.setImageResource(R.drawable.cute);
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
        if (index >= mCategory.getStickers().size()) {
            return convertView;
        }

        StickerItem sticker = mCategory.getStickers().get(index);
        if (sticker == null) {
            return convertView;
        }

        String stickerBitmapUri = StickerManager.getInstance().getStickerBitmapUri(sticker.getCategory(), sticker.getName());
        EmoticonManager.getIImageLoader().displayImage(mContext, stickerBitmapUri, viewHolder.mImageView);

        return convertView;
    }

    private class StickerViewHolder {
        ImageView mImageView;
    }
}
