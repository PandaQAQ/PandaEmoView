package pandaq.com.gifemoticon;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 表情底部 tab 对象
 */
public class EmotionTab extends RelativeLayout {

    // 贴图表情封面图路径
    private String mStickerCoverImgPath;
    private int mIconSrc = R.drawable.ic_action_add;

    public EmotionTab(Context context, int iconSrc) {
        super(context);
        mIconSrc = iconSrc;
        init(context);
    }

    public EmotionTab(Context context, String stickerCoverImgPath) {
        super(context);
        mStickerCoverImgPath = stickerCoverImgPath;
        init(context);
    }


    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.emoticon_tab, this);
        ImageView ivIcon = (ImageView) findViewById(R.id.ivIcon);
        if (TextUtils.isEmpty(mStickerCoverImgPath)) {
            ivIcon.setImageResource(mIconSrc);
        } else {
            EmoticonManager.getIImageLoader().displayImage(context, mStickerCoverImgPath, ivIcon);
        }
    }

}
