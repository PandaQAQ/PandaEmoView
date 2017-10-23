package pandaq.com.gifemoticonpro;

import android.app.Application;

import pandaq.com.gifemoticon.EmoticonManager;

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
                .build();
    }
}
