package rym.study.study_rckit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import rym.study.study_rckit.R;

public class ConversationActivity extends AppCompatActivity {

    private static final String TAG = "ConversationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        checkPushMsg();
    }

    private void checkPushMsg() {
        Log.d(TAG, "getIntent() = " + getIntent().toString());
        if (getIntent() == null || getIntent().getData() == null) {
            return;
        }

//        if (getIntent().getData().getQueryParameter("push").equals("true")) {
//            Intent intent = new Intent();
//            intent.setClass(ConversationActivity.this, MainActivity.class);
//            intent.putExtra()
//
//            String token = DemoContext.getInstance().getSharedPreferences()
//                    .getString(Constants.APP_TOKEN, Constants.DEFAULT);
//            Intent in = new Intent();
//
//            if (!token.equals(Constants.DEFAULT)) {
//                in.setClass(ConversationActivity.this, MainActivity.class);
//                in.putExtra("PUSH_TOKEN", token);
//                in.putExtra("PUSH_INTENT", intent.getData());
//            } else {
//                in.setClass(ConversationActivity.this, LoginActivity.class);
//            }
//
//            startActivity(in);
//            finish();
//        }
    }
}
