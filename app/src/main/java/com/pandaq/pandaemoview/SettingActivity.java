package com.pandaq.pandaemoview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.pandaq.emoticonlib.base.SwipeBackActivity;
import com.pandaq.emoticonlib.sticker.StickerCategory;
import com.pandaq.emoticonlib.sticker.StickerManager;

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

    private class MyWebViewClient extends WebViewClient {
        boolean overLoad = true;

        public MyWebViewClient(boolean overLoad) {
            this.overLoad = overLoad;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return overLoad;
        }

        /**
         * 网页开始加载
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        /**
         * 网页加载完成
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            super.onReceivedSslError(webView, sslErrorHandler, sslError);
            sslErrorHandler.proceed();
            System.out.println("?????????");
        }
    }
}
