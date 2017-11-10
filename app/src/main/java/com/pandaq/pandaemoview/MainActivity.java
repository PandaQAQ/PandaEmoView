package com.pandaq.pandaemoview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pandaq.emoticonlib.KeyBoardManager;
import com.pandaq.emoticonlib.listeners.IEmoticonMenuClickListener;
import com.pandaq.emoticonlib.listeners.IStickerSelectedListener;
import com.pandaq.emoticonlib.view.PandaEmoEditText;
import com.pandaq.emoticonlib.view.PandaEmoView;
import com.pandaq.pandaemoview.photomodule.ChoosePhotoActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_input)
    PandaEmoEditText mEtInput;
    @BindView(R.id.emoticonView)
    PandaEmoView mEmoticonView;
    @BindView(R.id.test_button)
    Button mTestButton;
    @BindView(R.id.parentPanel)
    RelativeLayout mParentPanel;
    private KeyBoardManager emotionKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mEmoticonView.attachEditText(mEtInput);
        mEmoticonView.setEmotionAddVisiable(true);
        mEmoticonView.setEmoticonExtClickListener(new IEmoticonMenuClickListener() {
            @Override
            public void onTabAddClick(View view) {
                Toast.makeText(MainActivity.this, "点击添加按钮", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabSettingClick(View view) {
                Toast.makeText(MainActivity.this, "点击设置按钮", Toast.LENGTH_SHORT).show();
            }
        });
        mEmoticonView.setEmoticonSelectedListener(new IStickerSelectedListener() {
            @Override
            public void onStickerSelected(String categoryName, String stickerName, String stickerBitmapPath) {
                Log.d("PandaQ===>", "categoryName---" + categoryName + "---stickerName---" + stickerName +
                        "---stickerBitmapPath---" + stickerBitmapPath);
            }

            @Override
            public void onCustomAdd() {
                //添加按钮
                Toast.makeText(MainActivity.this, "点击添加自定义表情", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ChoosePhotoActivity.class);
                startActivity(intent);
            }
        });
        initEmotionKeyboard();
    }

    private void initEmotionKeyboard() {
        emotionKeyboard = KeyBoardManager.with(this);
        emotionKeyboard.bindToEmotionButton(mTestButton);
        emotionKeyboard.setEmotionView(mEmoticonView);
    }

    @Override
    public void onBackPressed() {
        if (!emotionKeyboard.interceptBackPress()) {
            finish();
        }
    }

    @OnClick(R.id.load_sticker)
    public void onViewClicked() {
        copyStickerToSdCard("sticker_test", getApplicationContext().getFilesDir() + "/sticker/selfSticker");
    }

    private void copyStickerToSdCard(String assetDir, String dir) {
        String[] files;
        try {
            files = this.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }
        File mWorkingPath = new File(dir);
        // if this directory does not exists, make one.
        if (!mWorkingPath.exists()) {
            mWorkingPath.mkdirs();
        }
        for (String file : files) {
            try {
                // we make sure file name not contains '.' to be a folder.
                if (!file.contains(".")) {
                    if (0 == assetDir.length()) {
                        copyStickerToSdCard(file, dir + file + "/");
                    } else {
                        copyStickerToSdCard(assetDir + "/" + file, dir + file + "/");
                    }
                    continue;
                }
                File outFile = new File(mWorkingPath, file);
                if (outFile.exists()) {
                    outFile.delete();
                }
                InputStream in;
                if (0 != assetDir.length()) {
                    in = this.getAssets().open(assetDir + "/" + file);
                } else {
                    in = this.getAssets().open(file);
                }
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                mEmoticonView.reloadEmos();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
