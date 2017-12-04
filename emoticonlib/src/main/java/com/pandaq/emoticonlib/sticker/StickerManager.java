package com.pandaq.emoticonlib.sticker;

import android.util.Xml;

import com.pandaq.emoticonlib.PandaEmoManager;
import com.pandaq.emoticonlib.utils.ZipUtils;
import com.pandaq.emoticonlib.view.PandaEmoView;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private ArrayList<String> stickerOrderNames = new ArrayList<>();

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
        if (PandaEmoManager.getInstance().getStickerPath() == null) {
            return;
        }
        stickerCategories.clear();
        stickerCategoryMap.clear();
        File stickerDir = new File(PandaEmoManager.getInstance().getStickerPath());
        if (stickerDir.exists()) {
            File[] files = stickerDir.listFiles();// 列出 stickers 目录下的所有文件夹（每一个文件夹是一组表情包）
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.isDirectory()) {
                        String name = file.getName();
                        StickerCategory category;
                        if (stickerOrderNames.size() > 0) { // 如果忘记设置 stickers 顺序 list 则底部栏随机显示
                            category = new StickerCategory(name, stickerOrderNames.indexOf(name));
                        } else {
                            if (name.equals(selfSticker)) {
                                category = new StickerCategory(name, 0);
                                category.setDefault(true);
                            } else {
                                category = new StickerCategory(name, i + 1);
                                category.setDefault(false);
                            }
                        }
                        // 为每一个贴图包装载贴图数据
                        loadStickerData(category);
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

    public void addZipResource(String zipPath) {
        ZipUtils.unZipFiles(zipPath, PandaEmoManager.getInstance().getStickerPath());
        PandaEmoManager.getInstance().getManagedView().reloadEmos(0);
    }

    public void setStickerOrderNames(ArrayList<String> stickers) {
        this.stickerOrderNames = stickers;
    }

    private void loadStickerData(StickerCategory category) {
        List<StickerItem> stickers = new ArrayList<>();
        File stickerDir = new File(PandaEmoManager.getInstance().getStickerPath(), category.getName());
        if (stickerDir.exists()) {
            // 如果是默认 sticker 直接便利文件加载数据
            if (selfSticker.equals(stickerDir.getName())) {
                File[] files = stickerDir.listFiles();
                for (File file : files) {
                    stickers.add(new StickerItem(file.getAbsolutePath()));
                }
            } else {
                // 非自定义贴图表情包
                category.setFilePath(stickerDir.getAbsolutePath());
                stickers.addAll(new EntryLoader().load(category));
            }
        }
        //补充最后一页缺少的贴图
        int stickerPerPage = PandaEmoManager.getInstance().getStickerPerPage();
        int tmp = stickers.size() % stickerPerPage;
        if (tmp != 0) {
            int tmp2 = PandaEmoManager.getInstance().getStickerPerPage() - (stickers.size() - (stickers.size() / stickerPerPage) * stickerPerPage);
            for (int i = 0; i < tmp2; i++) {
                stickers.add(new StickerItem(""));
            }
        }
        category.setStickers(stickers);
    }

    /**
     * 表情对象加载器,解析配置 xml
     */
    private class EntryLoader extends DefaultHandler {
        private String configFileName = "config.xml";
        private List<StickerItem> mStickerItems = new ArrayList<>();
        private String stickerPath;
        private StickerCategory mCategory;

        // 解析 asset 中的表情配置文件
        List<StickerItem> load(StickerCategory category) {
            mCategory = category;
            stickerPath = new File(PandaEmoManager.getInstance().getStickerPath(), category.getName()).getAbsolutePath();
            InputStream is = null;
            try {
                is = new FileInputStream(stickerPath + File.separator + configFileName);
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
            return mStickerItems;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equals("Catalog")) {
                String title = attributes.getValue(uri, "Title");
                mCategory.setTitle(title);
            } else if (localName.equals("Emoticon")) {
                String tag = attributes.getValue(uri, "Tag");
                String filePath = attributes.getValue(uri, "File");
                String path = stickerPath + filePath;
                if ("TabCover".equals(tag)) {
                    mCategory.setCoverPath(path);
                } else {
                    mStickerItems.add(new StickerItem(path, tag));
                }
            }
        }
    }

}
