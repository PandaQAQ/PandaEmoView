package com.pandaq.emoticonlib.photopicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.pandaq.emoticonlib.R;
import com.pandaq.emoticonlib.base.SwipeBackActivity;

/**
 * Created by huxinyu on 2017/11/15 0015.
 * description ：添加自定义贴图表情预览界面
 */

public class StickerAddPreviewActivity extends SwipeBackActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

    }
}
