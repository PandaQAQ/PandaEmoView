package com.pandaq.pandaemoview.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.pandaq.emoticonlib.base.SwipeBackActivity;
import com.pandaq.pandaemoview.ItemStickerAdapter;
import com.pandaq.pandaemoview.R;
import com.pandaq.pandaemoview.StickerEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huxinyu on 2017/11/27 0027.
 * description ：
 */

public class AddEmoActivity extends SwipeBackActivity {

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
        mToptitle.setText("添加表情");
        init();
    }

    private void init() {
        ArrayList<StickerEntity> entities = new ArrayList<>();
        StickerEntity entity = new StickerEntity();
        entity.downLoadUrl = getApplicationContext().getFilesDir().getAbsolutePath() + "/soAngry.zip";
        entity.name = "我好气啊";
        entity.isDownLoaded = false;
        entity.picCover = "https://b-ssl.duitang.com/uploads/item/201607/24/20160724220001_reK4C.jpeg";
        entities.add(entity);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ItemStickerAdapter itemStickerAdapter = new ItemStickerAdapter(this, entities);
        mRvEmoticonList.setLayoutManager(new LinearLayoutManager(this));
        mRvEmoticonList.setAdapter(itemStickerAdapter);
    }
}
