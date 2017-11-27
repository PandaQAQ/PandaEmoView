package com.pandaq.emoticonlib.listeners;

/**
 * Created by huxinyu on 2017/11/24 0024.
 * description ：表情包 zip 下载接口，用于用户使用自己的下载框架下载 zip 包。否则使用默认 Downloader
 */

public interface IEmoticZipLoader {
    void loadEmoticonZip(String url, String zipSaveDir);
}
