package com.pandaq.emoticonlib.photopicker;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pandaq.emoticonlib.EmoticonManager;
import com.pandaq.emoticonlib.R;
import com.pandaq.emoticonlib.base.BaseActivity;
import com.pandaq.emoticonlib.view.PandaEmoView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by huxinyu on 2017/11/9 0009.
 * description ：显示已添加自定义表情类
 */

public class ManageCustomActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String defaultStickerPath = EmoticonManager.getStickerPath() + "/selfSticker";
    private LineGridView mGridView;
    private CheckPicAdapter mPicAdapter;
    private ArrayList<String> pics = new ArrayList<>();
    private boolean showCheckBox = false;
    private RelativeLayout mBottomLayout;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo);
        TextView topAction = (TextView) findViewById(R.id.tv_action_manage);
        topAction.setOnClickListener(this);
        mBottomLayout = (RelativeLayout) findViewById(R.id.rl_bottom_layout);
        TextView tvBottomRight = (TextView) findViewById(R.id.tv_bottom_right);
        tvBottomRight.setOnClickListener(this);
        TextView tvSelectAlbum = (TextView) findViewById(R.id.tv_bottom_left);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mGridView = (LineGridView) findViewById(R.id.gv_pictures);
        setSupportActionBar(mToolbar);
        tvSelectAlbum.setVisibility(View.GONE);
        mGridView.setNumColumns(5);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageCustomActivity.this.finish();
            }
        });
        mGridView.setOnItemClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestRunTimePermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionCall() {
                @Override
                public void requestSuccess() {
                    initImages();
                }

                @Override
                public void refused() {
                    Toast.makeText(ManageCustomActivity.this, "请授予必要权限！！", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            initImages();
        }
    }

    private void initImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "未发现存储设备！", Toast.LENGTH_SHORT).show();
        }
        File parentFile = new File(defaultStickerPath);
        File[] files = parentFile.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file != null) {
                    pics.add(file.getAbsolutePath());
                }
            }
        }
        pics.add(CheckPicAdapter.IC_ACTION_ADD);
        showPics(pics);
    }

    private void showPics(ArrayList<String> value) {
        if (mPicAdapter == null) {
            String num = "(" + value.size() + "/" + EmoticonManager.MAX_CUSTON_STICKER + ")";
            mToolbar.setTitle("已添加表情" + num);
            mPicAdapter = new CheckPicAdapter(this, value);
            mGridView.setAdapter(mPicAdapter);
        } else {
            mPicAdapter.setPicPaths(value);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String path = mPicAdapter.getItem(position);
        if (CheckPicAdapter.IC_ACTION_ADD.equals(path)) {
            Intent intent = new Intent(ManageCustomActivity.this, PickImageActivity.class);
            startActivity(intent);
        }
    }

    private void manageStickers() {
        if (showCheckBox) {
            //hideBox
            mPicAdapter.showCheckBox(false);
            mBottomLayout.setVisibility(View.GONE);
        } else {
            // showBox
            mPicAdapter.showCheckBox(true);
            mBottomLayout.setVisibility(View.VISIBLE);
        }
        showCheckBox = !showCheckBox;
    }

    private void deleteSelected(ArrayList<String> selectedPaths) {
        System.out.println(selectedPaths);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_action_manage) {
            manageStickers();
        } else if (i == R.id.tv_bottom_right) {
            deleteSelected(mPicAdapter.getSelectedPath());
        }
    }
}
