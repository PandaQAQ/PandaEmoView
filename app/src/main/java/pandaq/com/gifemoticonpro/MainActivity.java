package pandaq.com.gifemoticonpro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pandaq.com.gifemoticon.IEmoticonMenuClickListener;
import pandaq.com.gifemoticon.KeyBoardManager;
import pandaq.com.gifemoticon.view.PandaEmoEditText;
import pandaq.com.gifemoticon.view.PandaEmoView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_input)
    PandaEmoEditText mEtInput;
    @BindView(R.id.emoticonView)
    PandaEmoView mEmoticonView;
    @BindView(R.id.test_button)
    Button mTestButton;
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
}
