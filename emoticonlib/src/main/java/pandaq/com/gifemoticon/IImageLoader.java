package pandaq.com.gifemoticon;

import android.widget.ImageView;

/**
 * Created by huxinyu on 2017/10/19 0019.
 * description ：对外提供的加载图片方法，图片加载器由使用项目自行决定
 */

public interface IImageLoader {

    void displayImage(String path, ImageView imageView);

}
