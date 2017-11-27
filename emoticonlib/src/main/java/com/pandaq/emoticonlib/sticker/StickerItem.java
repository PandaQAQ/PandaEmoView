package com.pandaq.emoticonlib.sticker;

public class StickerItem {
    private String sourcePath;
    private String title; //表情包说明 Title

    public StickerItem(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public StickerItem(String sourcePath, String title) {
        this.sourcePath = sourcePath;
        this.title = title;
    }

    public String getSourcePath() {
        return "file:///" + sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof StickerItem) {
            StickerItem item = (StickerItem) obj;
            return item.getSourcePath().equals(sourcePath) && item.getTitle().equals(title);
        }
        return false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
