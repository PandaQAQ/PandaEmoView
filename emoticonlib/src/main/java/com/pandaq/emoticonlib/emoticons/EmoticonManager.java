package com.pandaq.emoticonlib.emoticons;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.util.Xml;

import com.pandaq.emoticonlib.PandaEmoManager;
import com.pandaq.emoticonlib.emoticons.gif.AnimatedGifDrawable;
import com.pandaq.emoticonlib.utils.EmoticonUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huxinyu on 2017/11/29 0029.
 * description ：
 */

public class EmoticonManager {

    private static EmoticonManager instance;
    private final List<ImageEntry> mDefaultEntries = new ArrayList<>();
    private final Map<String, ImageEntry> mText2Entry = new HashMap<>();
    private LruCache<String, Bitmap> mDrawableCache;
    private LruCache<String, AnimatedGifDrawable> mGifDrawableCache;
    private PandaEmoManager mPandaEmoManager;

    public static EmoticonManager getInstance() {
        if (instance == null) {
            synchronized (EmoticonManager.class) {
                if (instance == null) {
                    instance = new EmoticonManager();
                }
            }
        }
        return instance;
    }

    private EmoticonManager() {
        mPandaEmoManager = PandaEmoManager.getInstance();
        loadEmoticons();
    }

    public int getGifLruSize() {
        return mGifDrawableCache.size();
    }

    /**
     * 加载表情包
     */
    private void loadEmoticons() {
        load(mPandaEmoManager.getContext(), mPandaEmoManager.getEmotDir() + File.separator + mPandaEmoManager.getConfigFile());
        mDrawableCache = new LruCache<String, Bitmap>(mDefaultEntries.size()) {
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (oldValue != newValue)
                    oldValue.recycle();
            }
        };
        mGifDrawableCache = new LruCache<String, AnimatedGifDrawable>(mDefaultEntries.size()) {
            @Override
            protected void entryRemoved(boolean evicted, String key, AnimatedGifDrawable oldValue, AnimatedGifDrawable newValue) {
                if (oldValue != newValue)
                    oldValue.setCallback(null);
            }
        };
    }

    /**
     * 加载解析配置文件
     *
     * @param context 上下文
     * @param xmlPath 配置文件路径
     */
    private void load(Context context, String xmlPath) {
        new EntryLoader().load(context, xmlPath);
        //补充最后一页少的表情,空白占位
        int tmp = mDefaultEntries.size() % mPandaEmoManager.getEmojiPerPage();
        if (tmp != 0) {
            int tmp2 = mPandaEmoManager.getEmojiPerPage() - (mDefaultEntries.size() - (mDefaultEntries.size()
                    / mPandaEmoManager.getEmojiPerPage()) * mPandaEmoManager.getEmojiPerPage());
            for (int i = 0; i < tmp2; i++) {
                mDefaultEntries.add(new ImageEntry("", ""));
            }
        }
    }

    /**
     * 表情load对象封装
     */
    private class ImageEntry {
        // 表情对应的文本内容
        private String text;
        // 表情所在的路径
        private String path;

        ImageEntry(String text, String path) {
            this.text = text;
            this.path = path;
        }
    }

    /**
     * 表情对象加载器,解析配置 xml
     */
    private class EntryLoader extends DefaultHandler {

        private String catalog = "";

        // 解析 asset 中的表情配置文件
        void load(Context context, String path) {
            InputStream is = null;
            try {
                is = context.getAssets().open(path);
                Xml.parse(is, Xml.Encoding.UTF_8, this);
            } catch (IOException | SAXException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equals("Catalog")) {
                catalog = attributes.getValue(uri, "Title");
            } else if (localName.equals("Emoticon")) {
                String tag = attributes.getValue(uri, "Tag");
                String fileName = attributes.getValue(uri, "File");
                ImageEntry entry = new ImageEntry(tag, PandaEmoManager.getInstance().getEmotDir() + File.separator + catalog + File.separator + fileName);
                mText2Entry.put(entry.text, entry);
                if (catalog.equals(PandaEmoManager.getInstance().getSourceDir())) {
                    mDefaultEntries.add(entry);
                }
            }
        }
    }

    public int getDisplayCount() {
        return mDefaultEntries.size();
    }

    public String getDisplayText(int index) {
        return index >= 0 && index < mDefaultEntries.size() ? mDefaultEntries.get(index).text : null;
    }

    public Drawable getDisplayDrawable(Context context, int index) {
        String text = (index >= 0 && index < mDefaultEntries.size() ? mDefaultEntries.get(index).text : null);
        return text == null ? null : getDrawable(context, text);
    }

    /**
     * 获取静态 Drawable，优先从缓存中读取，没有才创建新对象
     *
     * @param context 上下文
     * @param text    表情对应的文本 [微笑] [再见]
     * @return 表情静态 drawable
     */
    public Drawable getDrawable(Context context, String text) {
        ImageEntry entry = mText2Entry.get(text);
        if (entry == null || TextUtils.isEmpty(entry.text)) {
            return null;
        }
        Bitmap cache = mDrawableCache.get(entry.path);
        if (cache == null) {
            cache = loadAssetBitmap(context, entry.path);
        }
        return new BitmapDrawable(context.getResources(), cache);
    }

    /**
     * 加载asset目录下对应的静态图，无静态图时加载动态图第一帧
     * 在一个 TextView 中显示多个动态表情时为了降低内存消耗会转成静态表情，项目中默认是5个以上就显示静态图
     *
     * @param context 上下文
     * @param path    图片路径（不包含 .格式）
     * @return 静态图的 bitmap 对象
     */
    private Bitmap loadAssetBitmap(Context context, String path) {
        InputStream is = null;
        try {
            Resources resources = context.getResources();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDensity = DisplayMetrics.DENSITY_HIGH;
            options.inScreenDensity = resources.getDisplayMetrics().densityDpi;
            options.inTargetDensity = resources.getDisplayMetrics().densityDpi;
            // 显示静态图时优先显示png，无对应png时加载gif第一张
            if (isFileExists(path + ".png")) {
                is = context.getAssets().open(path + ".png");
            } else {
                is = context.getAssets().open(path + ".gif");
            }
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(), options);
            if (bitmap != null) {
                mDrawableCache.put(path, bitmap);
            }
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 去 assetPath 目录下加载动态表情
     *
     * @param context   上下文
     * @param assetPath 路径
     * @return GifDrawable 对象
     */
    private static AnimatedGifDrawable loadAssetGif(Context context, String assetPath, int bounds) {
        InputStream is;
        try {
            is = context.getResources().getAssets().open(assetPath + ".gif");
            return new AnimatedGifDrawable(is, bounds);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取 GifDrawable 对象，优先从缓存中读取，没有才创建新对象
     *
     * @param context 上下文
     * @param text    表情对应的文本 [微笑] [再见]
     * @return GifDrawable 对象
     */
    public AnimatedGifDrawable getDrawableGif(Context context, String text) {
        int size = EmoticonUtils.dp2px(context, PandaEmoManager.getInstance().getDefaultEmoBoundsDp());
        ImageEntry entry = mText2Entry.get(text);
        if (entry == null || TextUtils.isEmpty(entry.text)) {
            return null;
        }
        AnimatedGifDrawable cache = mGifDrawableCache.get(entry.path);
        if (cache == null) {
            cache = loadAssetGif(context, entry.path, size);
            mGifDrawableCache.put(entry.path, cache);
        }
        return cache;
    }

    /**
     * 判断assets文件夹下的资源文件是否存在
     *
     * @return false 不存在    true 存在
     */
    private boolean isFileExists(String filename) {
        AssetManager assetManager = mPandaEmoManager.getContext().getAssets();
        try {
            String[] names = assetManager.list(mPandaEmoManager.getEmotDir() + File.separator + mPandaEmoManager.getSourceDir());
            for (String name : names) {
                String sourceName = mPandaEmoManager.getEmotDir() + File.separator + mPandaEmoManager.getSourceDir() + File.separator + name;
                if (sourceName.equals(filename.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
