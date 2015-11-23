package rym.study.study_rckit.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import rym.study.study_rckit.R;
import rym.study.study_rckit.common.Const;
import rym.study.study_rckit.utils.HttpRequestUtil;

public class LoginActivity extends AppCompatActivity implements Handler.Callback {

    private static final String TAG = "LoginActivity";
    private Handler mHandler = new Handler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = ((EditText) findViewById(R.id.edit_user_id)).getText().toString();
                Log.d(TAG, "Login click: userId = " + userId);

                if (userId.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "UserID can not be empty.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (userId.compareTo(getLoginInfo(Const.LOGIN_USERID)) == 0) {
                    connectIMServer(getLoginInfo(Const.LOGIN_TOKEN));
                    return;
                }

                try {
                    String content = "userId=" + userId + "&name=" + userId + "&portraitUri=";
                    HttpRequestUtil request = new HttpRequestUtil(mHandler, HttpRequestUtil.METHOD_POST, Const.GetToken, content);
                    request.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button btnExit = (Button) findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((EditText) findViewById(R.id.edit_user_id)).setText(getLoginInfo(Const.LOGIN_USERID));
    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.d(TAG, "msg = " + msg.obj);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse((String) msg.obj).getAsJsonObject();
        int code = jsonObject.get("code").getAsInt();
        if (code == 200) {
            String userId = ((EditText) findViewById(R.id.edit_user_id)).getText().toString();
            String token = jsonObject.get("token").getAsString();
            setLoginInfo(userId, token);
            connectIMServer(token);
        } else {
            Log.e(TAG, "Http response code = " + code);
            Toast.makeText(getApplicationContext(), "Error! Http response code = " + code, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void connectIMServer(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e(TAG, "connectIMServer onTokenIncorrect.");
                Toast.makeText(getApplicationContext(), "Get token incorrect.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String userId) {
                Log.d(TAG, "connectIMServer onSuccess. userId = " + userId);
                Intent intent = new Intent();
                intent.putExtra("UserID", userId);
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.d(TAG, "connectIMServer onError. errorCode = " + errorCode);
            }
        });
    }

    private void setLoginInfo(String userId, String token) {
        SharedPreferences pre = getSharedPreferences(Const.SharedPrefenrences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString(Const.LOGIN_USERID, userId);
        editor.putString(Const.LOGIN_TOKEN, token);
        editor.commit();
    }

    private String getLoginInfo(String key) {
        SharedPreferences pre = getSharedPreferences(Const.SharedPrefenrences, Context.MODE_PRIVATE);
        return pre.getString(key, "");
    }
}
