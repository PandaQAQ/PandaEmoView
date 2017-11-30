package com.pandaq.emoticonlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import com.pandaq.emoticonlib.listeners.IImageLoader;
import com.pandaq.emoticonlib.view.PandaEmoView;

import java.io.File;
import java.util.regex.Pattern;


/**
 * Created by huxinyu on 2017/10/19 0019.
 * description ：emoji 表情管理类
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
public class PandaEmoManager {

    @SuppressLint("StaticFieldLeak")
    private static PandaEmoManager sPandaEmoManager;

    private String EMOT_DIR = "face"; // assets 中默认表情文件夹
    private String SOURCE_DIR = "source_default"; // assets 中默认图片资源文件夹名称，父目录为 EMOT_DIR
    private String STICKER_PATH = null; //默认路径在 /data/data/包名/files/sticker 下
    private int CACHE_MAX_SIZE = 1024;
    private int DEFAULT_EMO_BOUNDS_DP = 30; //默认的 emoji 表情图文混排大小
    private int defaultIcon = R.drawable.ic_default;
    private Context mContext;
    private String mConfigName = "emoji_default.xml";
    private IImageLoader mIImageLoader;
    private int MAX_CUSTOM_STICKER = 30;
    private int EMOJI_ROW = 3;
    private int EMOJI_COLUMN = 7;
    private int STICKER_ROW = 2;
    private int STICKER_COLUMN = 4;
    private boolean showAddButton = true;
    private boolean showSetButton = true;
    private boolean showStickers = true;
    private int backGroundColor = Color.WHITE;

    private PandaEmoView sPandaEmoView;
    private Pattern mPattern;

    private void init() {
        if (STICKER_PATH == null) {
            STICKER_PATH = new File(mContext.getFilesDir(), "sticker").getAbsolutePath();
        }
        mPattern = makePattern();
        File mWorkingPath = new File(STICKER_PATH + "/selfSticker");
        // if this directory does not exists, make one.
        if (!mWorkingPath.exists()) {
            mWorkingPath.mkdirs();
        }
    }

    public int getDefaultEmoBoundsDp() {
        return DEFAULT_EMO_BOUNDS_DP;
    }

    public static PandaEmoManager getInstance() {
        return sPandaEmoManager;
    }

    public Context getContext() {
        return mContext;
    }

    public int getDefaultIconRes() {
        return defaultIcon;
    }

    public String getStickerPath() {
        return STICKER_PATH;
    }

    public IImageLoader getIImageLoader() {
        return mIImageLoader;
    }

    Pattern getPattern() {
        return mPattern;
    }

    private Pattern makePattern() {
        return Pattern.compile("\\[[^\\[]{1,10}\\]");
    }

    public boolean isShowAddButton() {
        return showAddButton;
    }

    public boolean isShowSetButton() {
        return showSetButton;
    }

    public boolean isShowStickers() {
        return showStickers;
    }

    public int getEmojiRow() {
        return EMOJI_ROW;
    }

    public int getEmojiColumn() {
        return EMOJI_COLUMN;
    }

    public int getStickerRow() {
        return STICKER_ROW;
    }

    public int getStickerColumn() {
        return STICKER_COLUMN;
    }

    public String getEmotDir() {
        return EMOT_DIR;
    }

    public String getSourceDir() {
        return SOURCE_DIR;
    }

    public int getCacheMaxSize() {
        return CACHE_MAX_SIZE;
    }

    public int getDefaultIcon() {
        return defaultIcon;
    }

    public String getConfigName() {
        return mConfigName;
    }

    public int getMaxCustomSticker() {
        return MAX_CUSTOM_STICKER;
    }

    public int getEmojiPerPage() {
        return EMOJI_COLUMN * EMOJI_ROW - 1;
    }

    public int getStickerPerPage() {
        return STICKER_COLUMN * STICKER_ROW;
    }

    public PandaEmoView getManagedView() {
        return sPandaEmoView;
    }

    public int getBackGroundColor() {
        return backGroundColor;
    }

    public static class Builder {
        // 表情包在 assets 文件夹下的目录
        private String EMOT_DIR;
        // 表情包图片资源文件夹名称,EMOT_DIR为其父目录
        private String SOUCRE_DIR;
        //配置文件名称
        private String mConfigName;
        // 加载表情时内存缓存的最大值
        private int CACHE_MAX_SIZE;
        // 默认的表情 bounds 大小 单位为 dp
        private int DEFAULT_EMO_BOUNDS_DP;
        // 上下文
        private Context mContext;
        // 贴图表情加载
        private IImageLoader mIImageLoader;
        // 贴图表情目录路径
        private String STICKER_PATH;
        // 默认表情的菜单栏 icon 资源 ID
        private int defaultIcon;
        // 自定义贴图表情最大张数
        private int MAX_CUSTON_STICKER;
        // 是否显示 addTab
        private boolean showAddButton = true;
        // 是否显示 setTab
        private boolean showSetButton = true;
        // 是否显示 stickers Tabs
        private boolean showStickers = true;
        // 表情显示行数
        private int EMOJI_ROW = 3;
        // 表情显示列数
        private int EMOJI_COLUMN = 7;
        // 贴图表情行数
        private int STICKER_ROW = 2;
        // 贴图表情列数
        private int STICKER_COLUMN = 4;
        // 控件背景颜色
        private int backgroundColor = Color.WHITE;

        public Builder defaultTabIcon(int defaultIconRes) {
            this.defaultIcon = defaultIconRes;
            return this;
        }

        public Builder emoticonDir(String EMOT_DIR) {
            this.EMOT_DIR = EMOT_DIR;
            return this;
        }

        public Builder cacheSize(int CACHE_MAX_SIZE) {
            this.CACHE_MAX_SIZE = CACHE_MAX_SIZE;
            return this;
        }

        public Builder defaultBounds(int boundsDp) {
            this.DEFAULT_EMO_BOUNDS_DP = boundsDp;
            return this;
        }

        public Builder with(Context context) {
            mContext = context;
            return this;
        }

        public Builder imageLoader(IImageLoader IImageLoader) {
            mIImageLoader = IImageLoader;
            return this;
        }

        public Builder configFileName(String configName) {
            mConfigName = configName;
            return this;
        }

        public Builder sourceDir(String sourceDir) {
            this.SOUCRE_DIR = sourceDir;
            return this;
        }

        public Builder showAddTab(boolean showAddButton) {
            this.showAddButton = showAddButton;
            return this;
        }

        public Builder showStickers(boolean showStickers) {
            this.showStickers = showStickers;
            return this;
        }

        public Builder showSetTab(boolean showSetButton) {
            this.showSetButton = showSetButton;
            return this;
        }

        public Builder stickerPath(String STICKER_PATH) {
            this.STICKER_PATH = STICKER_PATH;
            return this;
        }

        public Builder maxCustomStickers(int maxCustonSticker) {
            this.MAX_CUSTON_STICKER = maxCustonSticker;
            return this;
        }

        public Builder emojiRow(int emojiRow) {
            this.EMOJI_ROW = emojiRow;
            return this;
        }

        public Builder emojiColumn(int emojiColumn) {
            this.EMOJI_COLUMN = emojiColumn;
            return this;
        }

        public Builder stickerRow(int stickerRow) {
            this.STICKER_ROW = stickerRow;
            return this;
        }

        public Builder stickerColumn(int stickerColumn) {
            this.STICKER_COLUMN = stickerColumn;
            return this;
        }

        public Builder backgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public void build() {
            if (sPandaEmoManager == null) {
                synchronized (PandaEmoManager.class) {
                    if (sPandaEmoManager == null) {
                        sPandaEmoManager = new PandaEmoManager();
                    }
                }
            }

            if (this.CACHE_MAX_SIZE != 0) {
                sPandaEmoManager.CACHE_MAX_SIZE = this.CACHE_MAX_SIZE;
            }

            if (this.DEFAULT_EMO_BOUNDS_DP != 0) {
                sPandaEmoManager.DEFAULT_EMO_BOUNDS_DP = this.DEFAULT_EMO_BOUNDS_DP;
            }

            if (this.EMOT_DIR != null) {
                sPandaEmoManager.EMOT_DIR = this.EMOT_DIR;
            }

            if (this.mContext != null) {
                sPandaEmoManager.mContext = this.mContext;
            }

            if (this.mIImageLoader != null) {
                sPandaEmoManager.mIImageLoader = this.mIImageLoader;
            }

            if (this.mConfigName != null) {
                sPandaEmoManager.mConfigName = this.mConfigName;
            }

            if (this.SOUCRE_DIR != null) {
                sPandaEmoManager.SOURCE_DIR = this.SOUCRE_DIR;
            }
            if (this.STICKER_PATH != null) {
                sPandaEmoManager.STICKER_PATH = this.STICKER_PATH;
            }

            if (this.defaultIcon > 0) {
                sPandaEmoManager.defaultIcon = this.defaultIcon;
            }

            if (this.MAX_CUSTON_STICKER != 0) {
                sPandaEmoManager.MAX_CUSTOM_STICKER = this.MAX_CUSTON_STICKER;
            }

            if (this.EMOJI_ROW != 0) {
                sPandaEmoManager.EMOJI_ROW = this.EMOJI_ROW;
            }

            if (this.EMOJI_COLUMN != 0) {
                sPandaEmoManager.EMOJI_COLUMN = this.EMOJI_COLUMN;
            }

            if (this.STICKER_ROW != 0) {
                sPandaEmoManager.STICKER_ROW = this.STICKER_ROW;
            }

            if (this.STICKER_COLUMN != 0) {
                sPandaEmoManager.STICKER_COLUMN = this.STICKER_COLUMN;
            }

            sPandaEmoManager.backGroundColor = this.backgroundColor;

            sPandaEmoManager.showAddButton = this.showAddButton;

            sPandaEmoManager.showStickers = this.showStickers;

            sPandaEmoManager.showSetButton = this.showSetButton;

            sPandaEmoManager.init();
        }
    }

    void manage(PandaEmoView emoView) {
        sPandaEmoView = emoView;
    }
}
