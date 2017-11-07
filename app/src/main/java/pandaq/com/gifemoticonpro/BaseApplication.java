package pandaq.com.gifemoticonpro;

import android.app.Application;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import pandaq.com.gifemoticon.EmoticonManager;
import pandaq.com.gifemoticon.IImageLoader;

/**
 * Created by huxinyu on 2017/10/23 0023.
 * description ：
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new EmoticonManager.Builder()
                .setContext(getApplicationContext())
                .setConfigName("emoji.xml")
                .setSOUCRE_DIR("images")
                .setIImageLoader(new IImageLoader() {
                    @Override
                    public void displayImage(String path, ImageView imageView) {
                        Picasso.with(getApplicationContext())
                                .load(path)
                                .into(imageView);
                    }
                })
                .build();
    }
}
