package pandaq.com.gifemoticon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by huxinyu on 2017/10/24 0024.
 * description ：
 */

public class EmoticonEditText extends android.support.v7.widget.AppCompatEditText {

    private IBackPressedListener mBackPressedListener;

    public EmoticonEditText(Context context) {
        super(context);
    }

    public EmoticonEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmoticonEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.KEYCODE_SOFT_LEFT) {
//            Toast.makeText(getContext(), "按下了返回键", Toast.LENGTH_SHORT).show();
            if (mBackPressedListener != null) {
                mBackPressedListener.backPressed();
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public void setBackPressedListener(IBackPressedListener backPressedListener) {
        mBackPressedListener = backPressedListener;
    }

    public interface IBackPressedListener {
        void backPressed();
    }
}
