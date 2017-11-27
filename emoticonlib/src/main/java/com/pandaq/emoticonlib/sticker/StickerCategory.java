package com.pandaq.emoticonlib.sticker;

import java.util.List;

/**
 * Created by huxinyu on 2017/10/19 0019.
 * description ：贴图文件对象
 */

public class StickerCategory {

    private String name; //贴图包文件夹名
    private String title; //贴图标签名称
    private int order = 0; //默认顺序
    private String coverPath; //tab栏图标
    private String filePath; // 文件路径
    private boolean isDefault; //是否是自定义sticker（带添加按钮）
    private transient List<StickerItem> stickers;

    public StickerCategory(String name, int order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<StickerItem> getStickers() {
        return stickers;
    }

    public void setStickers(List<StickerItem> stickers) {
        this.stickers = stickers;
    }


    public boolean hasStickers() {
        return stickers != null && stickers.size() > 0;
    }

    public int getCount() {
        if (stickers == null || stickers.isEmpty()) {
            return 0;
        }
        return stickers.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof StickerCategory)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        StickerCategory r = (StickerCategory) obj;
        return r.getName().equals(getName());
    }

    public String getCoverPath() {
        return "file://" + coverPath;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
