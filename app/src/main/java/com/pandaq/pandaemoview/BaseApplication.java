package com.pandaq.pandaemoview;

import android.app.Application;
import android.widget.ImageView;

import com.pandaq.emoticonlib.EmoticonManager;
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
        new EmoticonManager.Builder()
                .with(getApplicationContext())
                .configFileName("emoji.xml")
                .sourceDir("images")
                .imageLoader(new IImageLoader() {
                    @Override
                    public void displayImage(String path, ImageView imageView) {
                        Picasso.with(getApplicationContext())
                                .load(path)
                                .fit()
                                .centerCrop()
                                .into(imageView);
                    }
                })
                .build();
    }
}
