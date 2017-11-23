package com.pandaq.emoticonlib.photopicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pandaq.emoticonlib.EmoticonManager;
import com.pandaq.emoticonlib.R;
import com.pandaq.emoticonlib.base.SwipeBackActivity;
import com.pandaq.emoticonlib.utils.Constant;

import static com.pandaq.emoticonlib.utils.Constant.SOURCE_PATH;
import static com.pandaq.emoticonlib.utils.Constant.TARGET_PATH;

/**
 * Created by huxinyu on 2017/11/15 0015.
 * description ：添加自定义贴图表情预览界面
 */

public class StickerAddPreviewActivity extends SwipeBackActivity implements View.OnClickListener {

    private ImageView mImageView;
    private String sourcePath;
    private String targetPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_preview);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("表情预览");
        TextView actionManage = (TextView) findViewById(R.id.tv_action_manage);
        mImageView = (ImageView) findViewById(R.id.iv_pic);
        actionManage.setOnClickListener(this);
    }

    private void initData() {
        sourcePath = getIntent().getStringExtra(SOURCE_PATH);
        targetPath = getIntent().getStringExtra(TARGET_PATH);
        EmoticonManager.getInstance().getIImageLoader().displayImage("file://" + sourcePath, mImageView);
    }

    @Override
    public void onClick(View v) {
        PickerUtils.compressAndCopyToSd(sourcePath, targetPath);
        Intent intent = new Intent(this, ManageCustomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        EmoticonManager.getInstance().getManagedView().reloadEmos(1);
        Toast.makeText(this, "已添加", Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
