package com.example.hcz.hechangzhi;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.example.hcz.util.BaseDialog;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * 登录
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name, wang_pwd, register;
    private EditText user_name, user_pwd;
    private Button login;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private BaseDialog dialog;
    private String userInfo;
    private String message;
    private CloudPushService pushService = PushServiceFactory.getCloudPushService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("hzz_userinfo", MODE_PRIVATE);
        editor = sp.edit();
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("用户登录");
        wang_pwd = (TextView) findViewById(R.id.wang_pwd);
        user_name = (EditText) findViewById(R.id.user_name);
        user_pwd = (EditText) findViewById(R.id.user_pwd);
        register = (TextView) findViewById(R.id.register);
        login = (Button) findViewById(R.id.login);
        register.setOnClickListener(this);
        wang_pwd.setOnClickListener(this);
        login.setOnClickListener(this);
        dialog = new BaseDialog(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wang_pwd:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class).putExtra("isUpdatePsw", true));
                break;
            case R.id.register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class).putExtra("isUpdatePsw", false));
                break;
            case R.id.login:
                if (Verification()) {
                    login();
                }
                break;
            default:
                break;
        }
    }

    //验证信息
    private boolean Verification() {
        if (user_name.getText().toString().trim().equals("")) {
            Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (user_pwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                editor.putString("user_name", user_name.getText().toString());
                editor.putString("user_pwd", user_pwd.getText().toString());
                editor.putString("userInfo", userInfo);
                editor.commit();
                pushService.bindAccount(user_name.getText().toString().toLowerCase(), new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                .putExtra("loginName", user_name.getText().toString())
                                .putExtra("userInfo", userInfo));
                        finish();
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        Toast.makeText(LoginActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (msg.what == -1) {
                Toast.makeText(LoginActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    };

    //登录
    private void login() {
        dialog.show();
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        OkHttpClient okHttpClient = new OkHttpClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "河长制无法获取您的手机状态！", Toast.LENGTH_SHORT);
            dialog.dismiss();
            return;
        }
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/riverChief/login.do?loginName="
                        + user_name.getText().toString()
                        + "&password=" + user_pwd.getText().toString()
                        + "&code=" + tm.getDeviceId()).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                handler.sendEmptyMessage(-1);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String json = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(json);
                    message = (String) jsonObject.get("message");
                    Number status = (Number) jsonObject.get("status");
                    if ((int) status == 1) {
                        userInfo = (String) jsonObject.get("userInfo");
                    }
                    handler.sendEmptyMessage((int) status);
                } else
                    handler.sendEmptyMessage(-1);
            }
        });
    }
}

