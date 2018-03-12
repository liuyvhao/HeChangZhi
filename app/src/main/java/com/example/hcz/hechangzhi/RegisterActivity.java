package com.example.hcz.hechangzhi;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.util.BaseDialog;
import com.mob.MobSDK;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 注册、忘记密码
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name;
    private boolean isUpdatePsw;
    private ImageView back;
    private Button register, yanzheng_btn, submit_forget;
    private EditText user_name, user_phone, yanzheng, user_pwd, user_pwd2;
    private EventHandler eventHandler;
    private BaseDialog dialog;      //加载框
    private String message;
    private Timer sendCodeThread;// 验证码计时
    private int countDown = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isUpdatePsw = getIntent() != null && getIntent().getBooleanExtra("isUpdatePsw", false);
        if (isUpdatePsw)
            setContentView(R.layout.activity_forget);
        else
            setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        dialog = new BaseDialog(this);
        user_phone = (EditText) findViewById(R.id.user_phone);
        yanzheng = (EditText) findViewById(R.id.yanzheng);
        user_pwd = (EditText) findViewById(R.id.user_pwd);
        yanzheng_btn = (Button) findViewById(R.id.yanzheng_btn);
        yanzheng_btn.setOnClickListener(this);
        if (isUpdatePsw) {
            title_name.setText("重置密码");
            user_pwd2 = (EditText) findViewById(R.id.user_pwd2);
            submit_forget = (Button) findViewById(R.id.submit_forget);
            submit_forget.setOnClickListener(this);
        } else {
            title_name.setText("注册");
            register = (Button) findViewById(R.id.register);
            user_name = (EditText) findViewById(R.id.user_name);
            register.setOnClickListener(this);
        }
        MobSDK.init(this, "241048fdfa009", "2b02d3ca416df7d03a02e492d9df8a83");
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(final int event, final int result, Object data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            //回调完成
                            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                                //提交验证码成功
                                if (isUpdatePsw)
                                    forget();
                                else
                                    register();
                            } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                //获取验证码成功

                            } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                                //返回支持发送验证码的国家列表
                            }
                        } else {
                            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                                //提交验证码成功
                                Toast.makeText(RegisterActivity.this, "验证失败！", Toast.LENGTH_SHORT).show();
                            } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                //获取验证码成功
                                Toast.makeText(RegisterActivity.this, "发送失败！", Toast.LENGTH_SHORT).show();
                            } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                                //返回支持发送验证码的国家列表
                            }
                        }
                        dialog.dismiss();
                    }
                });
            }
        };
        //注册短信回调
        SMSSDK.registerEventHandler(eventHandler);
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                finish();
            } else if (msg.what == -1) {
                Toast.makeText(RegisterActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void handleCheckcode() {
        // 发送验证码成功
        yanzheng_btn.setClickable(false);
        yanzheng_btn.setBackgroundResource(R.drawable.bt_bottom_gray_normal);
        if (sendCodeThread == null)
            sendCodeThread = new Timer();
        sendCodeThread.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (countDown == 0) {
                            // 计时完成
                            sendCodeThread.cancel();
                            sendCodeThread = null;
                            countDown = 60;
                            yanzheng_btn.setText("获取验证码");
                            yanzheng_btn.setClickable(true);
                            yanzheng_btn.setBackgroundResource(R.drawable.bt_bottom_gray_pressed);

                        } else {
                            yanzheng_btn.setText("剩余"
                                    + countDown
                                    + "s");
                            countDown--;
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    private void register() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/riverChief/register.do?loginName="
                        + user_name.getText().toString()
                        + "&password=" + user_pwd.getText().toString()
                        + "&phoneNum=" + user_phone.getText().toString()).build();
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
                    message = (String) jsonObject.get("message");
                    Number status = (Number) jsonObject.get("status");
                    handler.sendEmptyMessage((int) status);
                } else
                    handler.sendEmptyMessage(-1);
            }
        });
    }

    private void forget() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/riverChief/forgetPsd.do?phone="
                        + user_phone.getText().toString()
                        + "&password=" + user_pwd.getText().toString()).build();
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
                    message = (String) jsonObject.get("message");
                    Number status = (Number) jsonObject.get("status");
                    handler.sendEmptyMessage((int) status);
                } else
                    handler.sendEmptyMessage(-1);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.yanzheng_btn:
                if (isPhone(user_phone.getText().toString())) {
                    //发送验证码
                    SMSSDK.getVerificationCode("86", user_phone.getText().toString());
                    handleCheckcode();
                } else
                    Toast.makeText(this, "请填写正确的手机号！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.register:
                if (Verification()) {
                    dialog.show();
                    //验证验证码
                    SMSSDK.submitVerificationCode("86", user_phone.getText().toString(), yanzheng.getText().toString());
                }
                break;
            case R.id.submit_forget:
                if (VerForget()) {
                    dialog.show();
                    //验证验证码
                    SMSSDK.submitVerificationCode("86", user_phone.getText().toString(), yanzheng.getText().toString());
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    //注册验证信息
    private boolean Verification() {
        if (user_name.getText().toString().trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请输入用户名！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (user_phone.getText().toString().trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请输入手机号！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (yanzheng.getText().toString().trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请输入验证码！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (user_pwd.getText().toString().trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请输入密码！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (user_pwd.getText().length() < 6) {
            Toast.makeText(RegisterActivity.this, "密码长度不能低于6位！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //找回验证信息
    private boolean VerForget() {
        if (user_phone.getText().toString().trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请输入手机号！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (yanzheng.getText().toString().trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请输入验证码！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (user_pwd.getText().toString().trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请输入密码！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (user_pwd.getText().length() < 6) {
            Toast.makeText(RegisterActivity.this, "密码长度不能低于6位！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (user_pwd2.getText().toString().trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "请重新输入密码！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!user_pwd2.getText().toString().equals(user_pwd.getText().toString())) {
            Toast.makeText(RegisterActivity.this, "两次密码不一致！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * 验证手机格式
     *
     * @param phone
     * @return
     */
    private boolean isPhone(String phone) {
        String regex = "^0?1[0-9]{10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
}
