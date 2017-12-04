package com.pandaq.pandaemoview.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.pandaq.emoticonlib.PandaEmoManager;
import com.pandaq.emoticonlib.base.SwipeBackActivity;
import com.pandaq.emoticonlib.sticker.StickerCategory;
import com.pandaq.emoticonlib.sticker.StickerManager;
import com.pandaq.pandaemoview.ItemStickerAdapter;
import com.pandaq.pandaemoview.R;
import com.pandaq.pandaemoview.StickerEntity;

import java.io.File;
import java.util.ArrayList;
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
        ArrayList<StickerEntity> entities = new ArrayList<>();
        StickerEntity entity = new StickerEntity();
        entity.downLoadUrl = PandaEmoManager.getInstance().getStickerPath() + File.separator + "soAngry";
        entity.name = "我好气啊";
        entity.isDownLoaded = true;
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
