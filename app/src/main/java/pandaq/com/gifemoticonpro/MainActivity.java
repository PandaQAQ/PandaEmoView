package pandaq.com.gifemoticonpro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import pandaq.com.gifemoticon.EmoticonView;
import pandaq.com.gifemoticon.KeyBoardCoordinator;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_input)
    EditText mEtInput;
    @BindView(R.id.emoticonView)
    EmoticonView mEmoticonView;
    @BindView(R.id.llIndicator)
    LinearLayout mLlIndicator;
    @BindView(R.id.test_button)
    Button mTestButton;
    private KeyBoardCoordinator emotionKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initEmotionKeyboard();
        mEmoticonView.attachEditText(mEtInput);
        mEmoticonView.setEmotionAddVisiable(true);
    }

    private void initEmotionKeyboard() {
        emotionKeyboard = KeyBoardCoordinator.with(this);
        emotionKeyboard.bindToContent(mLlIndicator);
        emotionKeyboard.bindToEmotionButton(mTestButton);
        emotionKeyboard.bindToEditText(mEtInput);
        emotionKeyboard.setEmotionLayout(mEmoticonView);
    }

    @Override
    public void onBackPressed() {
        if (!emotionKeyboard.interceptBackPress()) {
            finish();
        }
    }
}
