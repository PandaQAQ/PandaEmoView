package pandaq.com.gifemoticon;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by huxinyu on 2017/10/19 0019.
 * description ：表情库中使用到的一些公用工具方法
 */

public class EmoticonUtils {

    /**
     * dp 转 px
     *
     * @param context 上下文
     * @param dpValue dp 值
     * @return px 值
     */
    public static int dp2px(Context context, float dpValue) {
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        return (int) (dpValue * displayMetrics.density + 0.5f);
    }

    /**
     * px 转 dp
     *
     * @param context 上下文
     * @param pxValue px 值
     * @return dp 值
     */
    public static int px2dp(Context context, float pxValue) {
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        return (int) (pxValue / displayMetrics.density + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        return (int) (spValue * displayMetrics.scaledDensity + 0.5f);
    }
}
