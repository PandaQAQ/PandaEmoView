package com.pandaq.pandaemoview;

import android.app.Application;
import android.widget.ImageView;

import com.pandaq.emoticonlib.PandaEmoManager;
import com.pandaq.emoticonlib.listeners.IImageLoader;
import com.squareup.picasso.Picasso;

/**
 * Created by huxinyu on 2017/10/23 0023.
 * description ：
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        configPandaEmoView();
    }

    private void configPandaEmoView() {
        new PandaEmoManager.Builder()
                .with(getApplicationContext()) // 传递 Context
                .configFileName("emoji.xml")// 配置文件名称
                .emoticonDir("face") // asset 下存放表情的目录路径（asset——> configFileName 之间的路径,结尾不带斜杠）
                .sourceDir("images") // 存放 emoji 表情资源文件夹路径（emoticonDir 图片资源之间的路径,结尾不带斜杠）
                .showAddTab(true)//tab栏是否显示添加按钮
                .showStickers(true)//tab栏是否显示贴图切换按键
                .showSetTab(true)//tab栏是否显示设置按钮
                .defaultBounds(30)//emoji 表情显示出来的宽高
                .cacheSize(50)//加载资源到内存时 LruCache 缓存大小，最小必须大于表情总数或者两页表情的数（否则会在显示时前面资源被回收）
                .defaultTabIcon(R.drawable.ic_default)//emoji表情Tab栏图标
                .emojiColumn(7)//单页显示表情的列数
                .emojiRow(3)//单页显示表情的行数
                .stickerRow(2)//单页显示贴图表情的行数
                .stickerColumn(4)//单页显示贴图表情的列数
                .maxCustomStickers(30)//允许添加的收藏表情数
                .imageLoader(new IImageLoader() {
                        @Override
                    public void displayImage(String path, ImageView imageView) { // 加载贴图表情的图片加载接口
                        Picasso.with(getApplicationContext())
                                .load(path)
                                .fit()
                                .centerCrop()
                                .into(imageView);
                    }
                })
                .build(); //构建 PandaEmoManager 单利
    }
}
