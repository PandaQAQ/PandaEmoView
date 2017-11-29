package com.pandaq.emoticonlib.listeners;

/**
 * Created by huxinyu on 2017/10/19 0019.
 * description ：sticker 图片点击回调
 */

public interface IStickerSelectedListener {

    void onStickerSelected(String title, String sourcePath);

    void onCustomAdd();
}
