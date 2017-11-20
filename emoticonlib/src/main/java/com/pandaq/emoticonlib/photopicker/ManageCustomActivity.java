package com.pandaq.emoticonlib.photopicker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.pandaq.emoticonlib.EmoticonManager;
import com.pandaq.emoticonlib.R;
import com.pandaq.emoticonlib.base.BaseActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by huxinyu on 2017/11/9 0009.
 * description ：显示已添加自定义表情类
 */

public class ManageCustomActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final String defaultStickerPath = EmoticonManager.getStickerPath() + "/selfSticker";
    private LineGridView mGridView;
    private CheckPicAdapter mPicAdapter;
    private ArrayList<String> pics = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo);
        TextView tvSelectAlbum = (TextView) findViewById(R.id.tv_select_album);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mGridView = (LineGridView) findViewById(R.id.gv_pictures);
        setSupportActionBar(toolbar);
        tvSelectAlbum.setVisibility(View.GONE);
        mGridView.setNumColumns(4);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
}
