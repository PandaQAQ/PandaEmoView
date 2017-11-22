package com.pandaq.emoticonlib;

import android.support.annotation.Nullable;

import com.pandaq.emoticonlib.sticker.StickerCategory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 贴图管理类
 */
public class StickerManager {

    private static StickerManager instance;
    public static String selfSticker = "selfSticker"; //用户收藏表情包文件夹名

    //数据源
    private List<StickerCategory> stickerCategories = new ArrayList<>();
    private Map<String, StickerCategory> stickerCategoryMap = new HashMap<>();
    //贴图包，顺序为服务器添加表情包顺序
    private ArrayList<String> stickers = new ArrayList<>();

    public static StickerManager getInstance() {
        if (instance == null) {
            synchronized (StickerManager.class) {
                if (instance == null) {
                    instance = new StickerManager();
                }
            }
        }
        return instance;
    }

    private StickerManager() {
        loadStickerCategory();
    }

    public void loadStickerCategory() {
        if (EmoticonManager.getInstance().getStickerPath() == null) {
            return;
        }
        stickerCategories.clear();
        stickerCategoryMap.clear();
        File stickerDir = new File(EmoticonManager.getInstance().getStickerPath());
        if (stickerDir.exists()) {
            File[] files = stickerDir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    //当前的目录下同名的有文件和文件夹，只需要其中的一个取其名
                    if (file.isDirectory()) {
                        String name = file.getName();
                        StickerCategory category;
                        if (stickers.size() > 0) { // 如果忘记设置 stickers 顺序 list 则底部栏随机显示
                            category = new StickerCategory(name, name, true, stickers.indexOf(name));
                        } else {
                            if (name.equals(selfSticker)) {
                                category = new StickerCategory(name, name, true, 0);
                            } else {
                                category = new StickerCategory(name, name, true, i + 1);
                            }
                        }
                        stickerCategories.add(category);
                        stickerCategoryMap.put(name, category);
                    }
                }

                //排序
                Collections.sort(stickerCategories, new Comparator<StickerCategory>() {
                    @Override
                    public int compare(StickerCategory o1, StickerCategory o2) {
                        return o1.getOrder() - o2.getOrder();
                    }
                });
            }
        }
    }

    public synchronized List<StickerCategory> getStickerCategories() {
        return stickerCategories;
    }

    public synchronized StickerCategory getCategory(String name) {
        return stickerCategoryMap.get(name);
    }

    public String getStickerBitmapUri(String categoryName, String stickerName) {
        String path = getStickerBitmapPath(categoryName, stickerName);
        return "file://" + path;
    }

    @Nullable
    String getStickerBitmapPath(String categoryName, String stickerName) {
        StickerManager manager = StickerManager.getInstance();
        StickerCategory category = manager.getCategory(categoryName);
        if (category == null) {
            return null;
        }
        return EmoticonManager.getInstance().getStickerPath() + File.separator + category.getName() + File.separator + stickerName;
    }

    public ArrayList<String> getStickers() {
        return stickers;
    }

    public void setStickers(ArrayList<String> stickers) {
        this.stickers = stickers;
    }

}
