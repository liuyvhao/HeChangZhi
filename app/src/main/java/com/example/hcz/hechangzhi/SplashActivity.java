package com.example.hcz.hechangzhi;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;

/**
 * 欢迎界面
 */
public class SplashActivity extends AppCompatActivity {
    // 要申请的权限
    private String[] permissions = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS, Manifest.permission.CAMERA};
    private SharedPreferences sp;
    private String userInfo, userName, passWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String string : permissions) {
                if (ContextCompat.checkSelfPermission(SplashActivity.this, string) != PackageManager.PERMISSION_GRANTED) {
                    //Android 6.0申请权限
                    ActivityCompat.requestPermissions(this, permissions, 1);
                } else {
                }
            }
        }
        sp = getSharedPreferences("hzz_userinfo", MODE_PRIVATE);
        userName = sp.getString("user_name", "");
        passWord = sp.getString("user_pwd", "");

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //如果已经登录过了，直接进入页面
                if (!sp.getString("user_name", "").equals("") && !sp.getString("user_pwd", "").equals("") && !sp.getString("userInfo", "").equals("")) {
                    login();
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class)
                        .putExtra("loginName", sp.getString("user_name", ""))
                        .putExtra("userInfo", userInfo));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }
    };

    //登录
    private void login() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        OkHttpClient okHttpClient = new OkHttpClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "河长制无法获取您的手机状态！", Toast.LENGTH_SHORT);
            return;
        }
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/riverChief/login.do?loginName="
                        + userName
                        + "&password=" + passWord
                        + "&code=" + tm.getDeviceId()).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                handler.sendEmptyMessage(-1);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(json);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission Denied
                Toast.makeText(SplashActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
