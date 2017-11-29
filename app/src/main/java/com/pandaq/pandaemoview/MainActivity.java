package com.pandaq.pandaemoview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pandaq.emoticonlib.KeyBoardManager;
import com.pandaq.emoticonlib.sticker.StickerManager;
import com.pandaq.emoticonlib.listeners.IEmoticonMenuClickListener;
import com.pandaq.emoticonlib.listeners.IStickerSelectedListener;
import com.pandaq.emoticonlib.photopicker.ManageCustomActivity;
import com.pandaq.emoticonlib.view.PandaEmoEditText;
import com.pandaq.emoticonlib.view.PandaEmoView;

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
    LinearLayout mParentPanel;
    @BindView(R.id.tv_bottom_test)
    TextView mTvBottomTest;
    @BindView(R.id.toptitle)
    TextView mToptitle;
    @BindView(R.id.load_sticker)
    Button mLoadSticker;
    @BindView(R.id.llIndicator)
    RelativeLayout mLlIndicator;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.rl_content)
    RelativeLayout mRlContent;
    @BindView(R.id.test_image)
    ImageView mTestImage;
    private KeyBoardManager emotionKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mEmoticonView.attachEditText(mEtInput);
        mEmoticonView.setEmoticonMenuClickListener(new IEmoticonMenuClickListener() {
            @Override
            public void onTabAddClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEmoActivity.class);
                startActivity(intent);
            }

            @Override
            public void onTabSettingClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        mEmoticonView.setEmoticonSelectedListener(new IStickerSelectedListener() {
            @Override
            public void onStickerSelected(String title, String stickerBitmapPath) {
                Log.d("PandaQ===>", "Title---" + title + "---stickerBitmapPath---" + stickerBitmapPath);
            }

            @Override
            public void onCustomAdd() {
                //添加按钮
                Toast.makeText(MainActivity.this, "点击添加自定义表情", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ManageCustomActivity.class);
                startActivity(intent);
            }
        });
        initEmotionKeyboard();
    }

    private void initEmotionKeyboard() {
        emotionKeyboard = KeyBoardManager.with(this)
                .bindToEmotionButton(mTestButton)
                .setEmotionView(mEmoticonView)
                .bindToLockContent(mRlContent)
                .setOnInputListener(new KeyBoardManager.OnInputShowListener() {
                    @Override
                    public void showInputView(boolean show) {
                        System.out.println("showInputLayout-------->" + show);
                    }
                });
        emotionKeyboard.setOnEmotionButtonOnClickListener(new KeyBoardManager.OnEmotionButtonOnClickListener() {
            @Override
            public boolean onEmotionButtonOnClickListener(View view) {
                if (view.getId() == R.id.load_sticker) {
                    if (mTvBottomTest.isShown()) {
                        mTvBottomTest.setVisibility(View.GONE);
                        emotionKeyboard.showInputLayout();
                    } else {
                        emotionKeyboard.hideInputLayout();
                        mTvBottomTest.setVisibility(View.VISIBLE);
                    }
                    // 重写逻辑时一定要返回 ture 拦截 KeyBoardManager 中的默认逻辑
                    return true;
                } else if (view.getId() == R.id.test_button) {
                    mTvBottomTest.setVisibility(View.GONE);
                    return false;// 不破坏表情按钮的处理逻辑，只是隐藏显示的菜单页
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mTvBottomTest.isShown()) {
            if (!emotionKeyboard.interceptBackPress()) {
                finish();
            }
        } else {
            mTvBottomTest.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.load_sticker)
    public void onViewClicked() {
        copyStickerToSdCard("sticker_test", getApplicationContext().getFilesDir() + "/sticker/selfSticker");
        copyStickerAndUnZip("ziptest", getApplicationContext().getFilesDir().getAbsolutePath());
    }

    private void copyStickerAndUnZip(String assetDir, String dir) {
        copyStickerToSdCard(assetDir, dir);
        StickerManager.getInstance().addZipResource(dir + "/soAngry.zip");
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
                mEmoticonView.reloadEmos(1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
