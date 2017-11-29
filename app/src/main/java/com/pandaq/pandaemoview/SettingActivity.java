package com.pandaq.pandaemoview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.pandaq.emoticonlib.sticker.StickerManager;
import com.pandaq.emoticonlib.base.SwipeBackActivity;
import com.pandaq.emoticonlib.sticker.StickerCategory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huxinyu on 2017/11/27 0027.
 * description ：
 */

public class SettingActivity extends SwipeBackActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_emoticon_list)
    RecyclerView mRvEmoticonList;
    @BindView(R.id.toptitle)
    TextView mToptitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToptitle.setText("设置");
        init();
    }

    private void init() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        List<StickerCategory> stickerCategories = StickerManager.getInstance().getStickerCategories();

    }
}
