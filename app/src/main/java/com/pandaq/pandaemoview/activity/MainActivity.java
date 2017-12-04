package com.pandaq.pandaemoview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pandaq.emoticonlib.KeyBoardManager;
import com.pandaq.emoticonlib.PandaEmoManager;
import com.pandaq.emoticonlib.PandaEmoTranslator;
import com.pandaq.emoticonlib.emoticons.EmoticonManager;
import com.pandaq.emoticonlib.emoticons.gif.AnimatedGifDrawable;
import com.pandaq.emoticonlib.listeners.IEmoticonMenuClickListener;
import com.pandaq.emoticonlib.listeners.IStickerSelectedListener;
import com.pandaq.emoticonlib.photopicker.ManageCustomActivity;
import com.pandaq.emoticonlib.view.PandaEmoEditText;
import com.pandaq.emoticonlib.view.PandaEmoView;
import com.pandaq.pandaemoview.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.toptitle)
    TextView mToptitle;
    @BindView(R.id.iv_call_menu)
    ImageView mIvCallMenu;
    @BindView(R.id.iv_call_emoticon)
    ImageView mIvCallEmoticon;
    @BindView(R.id.et_input)
    PandaEmoEditText mEtInput;
    @BindView(R.id.text_title)
    TextView mTextTitle;
    @BindView(R.id.tv_input_content)
    TextView mTvInputContent;
    @BindView(R.id.img_title)
    TextView mImgTitle;
    @BindView(R.id.iv_img_pic)
    ImageView mIvImgPic;
    @BindView(R.id.rl_content)
    RelativeLayout mRlContent;
    @BindView(R.id.emoticonView)
    PandaEmoView mEmoticonView;
    @BindView(R.id.rl_bottom_layout)
    RelativeLayout mRlBottomLayout;
    @BindView(R.id.tv_send)
    TextView mTvSend;
    @BindView(R.id.tv_lru_size)
    TextView mTvLruSize;
    private KeyBoardManager emotionKeyboard;
    private boolean inPutLayoutShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mEmoticonView.attachEditText(mEtInput);
        // Tab 菜单按钮监听
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
        // 表情贴图选中监听
        mEmoticonView.setEmoticonSelectedListener(new IStickerSelectedListener() {
            @Override
            public void onStickerSelected(String title, String stickerBitmapPath) {
                PandaEmoManager.getInstance().getIImageLoader().displayImage(stickerBitmapPath, mIvImgPic);
            }

            @Override
            public void onCustomAdd() {
                //添加按钮
                Toast.makeText(MainActivity.this, "点击添加自定义表情", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ManageCustomActivity.class);
                startActivity(intent);
            }
        });
        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(mEtInput.getText())) {
                    mTvSend.setVisibility(View.VISIBLE);
                    mIvCallMenu.setVisibility(View.GONE);
                } else {
                    mTvSend.setVisibility(View.GONE);
                    mIvCallMenu.setVisibility(View.VISIBLE);
                }
            }
        });
        mTvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String send = mEtInput.getText().toString();
                mTvInputContent.setText(PandaEmoTranslator
                        .getInstance()
                        .makeGifSpannable(getLocalClassName(), send, new AnimatedGifDrawable.RunGifCallBack() {
                            @Override
                            public void run() {
                                mTvInputContent.postInvalidate();
                            }
                        }));
                mEtInput.setText("");
                mTvLruSize.setText(String.valueOf(EmoticonManager.getInstance().getGifLruSize()));
            }
        });
        initEmotionKeyboard();
        // 将测试表情包压缩文件copy到SD卡
        copyStickerAndUnZip("ziptest", getApplicationContext().getFilesDir().getAbsolutePath());
    }

    private void initEmotionKeyboard() {
        emotionKeyboard = KeyBoardManager.with(this)
                .bindToEmotionButton(mIvCallEmoticon, mIvCallMenu)
                .setEmotionView(mEmoticonView)
                .bindToLockContent(mRlContent)
                .setOnInputListener(new KeyBoardManager.OnInputShowListener() {
                    @Override
                    public void showInputView(boolean show) {
                        inPutLayoutShow = show;
                    }
                });
        emotionKeyboard.setOnEmotionButtonOnClickListener(new KeyBoardManager.OnEmotionButtonOnClickListener() {
            @Override
            public boolean onEmotionButtonOnClickListener(View view) {
                if (view.getId() == R.id.iv_call_menu) {
                    if (mRlBottomLayout.isShown()) {
                        mRlBottomLayout.setVisibility(View.GONE);
                        emotionKeyboard.showInputLayout();
                    } else {
                        if (inPutLayoutShow) {
                            emotionKeyboard.hideInputLayout(true);
                        } else {
                            emotionKeyboard.hideInputLayout(false);
                        }
                        ViewGroup.LayoutParams params = mRlBottomLayout.getLayoutParams();
                        params.height = KeyBoardManager.with(MainActivity.this)
                                .getKeyBoardHeight();
                        mRlBottomLayout.setLayoutParams(params);
                        mRlBottomLayout.setVisibility(View.VISIBLE);
                    }
                    // 重写逻辑时一定要返回 ture 拦截 KeyBoardManager 中的默认逻辑
                    return true;
                } else if (view.getId() == R.id.iv_call_emoticon) {
                    mRlBottomLayout.setVisibility(View.GONE);
                    return false;// 不破坏表情按钮的处理逻辑，只是隐藏显示的菜单页
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mRlBottomLayout.isShown()) {
            if (!emotionKeyboard.interceptBackPress()) {
                finish();
            }
        } else {
            mRlBottomLayout.setVisibility(View.GONE);
        }
    }

    private void copyStickerAndUnZip(String assetDir, String dir) {
        copyStickerToSdCard(assetDir, dir);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PandaEmoTranslator.getInstance().startGif(getLocalClassName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        PandaEmoTranslator.getInstance().pauseGif();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PandaEmoTranslator.getInstance().clearGif(getLocalClassName());
    }
}
