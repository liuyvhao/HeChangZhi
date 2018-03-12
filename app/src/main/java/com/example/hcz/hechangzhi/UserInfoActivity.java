package com.example.hcz.hechangzhi;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.FinishLoginDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * 河长中心
 */
public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout user_center1;
    private RelativeLayout user_center2, message, about, cache, cancel;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String loginName, userInfo;
    private String avatarUrl, name, chiefType;
    private SimpleDraweeView userImage;
    private TextView name_tv, chiefType_tv;
    private CloudPushService pushService = PushServiceFactory.getCloudPushService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_user_info);
        sp = getSharedPreferences("hzz_userinfo", MODE_PRIVATE);
        editor = sp.edit();
        init();
        initData();
    }

    //绑定控件
    private void init() {
        user_center1 = (LinearLayout) findViewById(R.id.user_center1);
        user_center2 = (RelativeLayout) findViewById(R.id.user_center2);
        message = (RelativeLayout) findViewById(R.id.message);
        about = (RelativeLayout) findViewById(R.id.about);
        cache = (RelativeLayout) findViewById(R.id.cache);
        cancel = (RelativeLayout) findViewById(R.id.cancel);
        user_center1.setOnClickListener(this);
        user_center2.setOnClickListener(this);
        message.setOnClickListener(this);
        about.setOnClickListener(this);
        cache.setOnClickListener(this);
        cancel.setOnClickListener(this);
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        userImage = (SimpleDraweeView) findViewById(R.id.userimg);
        name_tv = (TextView) findViewById(R.id.name);
        chiefType_tv = (TextView) findViewById(R.id.chiefType);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                name_tv.setText(name);
                chiefType_tv.setText(chiefType);
                userImage.setImageURI(avatarUrl);
            } else if (msg.what == -1) {
                Toast.makeText(UserInfoActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(UserInfoActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            }
        }
    };

    //绑定数据
    private void initData() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/riverChief/center.do?loginName=" + loginName
                        + "&userInfo=" + userInfo).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                handler.sendEmptyMessage(-1);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(json);
                    //msg
                    String str = (String) jsonObject.get("message");
                    //status
                    Number status = (Number) jsonObject.get("status");
                    if ((int) status == 1) {
                        JSONObject value = (JSONObject) jsonObject.get("value");
                        avatarUrl = "http://114.55.235.16:7074" + value.getString("avatarUrl");
                        name = (String) value.get("name");
                        chiefType = (String) value.get("chiefType");
                    }
                    handler.sendEmptyMessage((int) status);
                } else {
                    handler.sendEmptyMessage(-1);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_center1:
                startActivityForResult(new Intent(this, UserdataActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo), 1);
                break;
            case R.id.user_center2:
                startActivityForResult(new Intent(this, UserdataActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo), 1);
                break;
            case R.id.message:
                startActivity(new Intent(UserInfoActivity.this, ProposalActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo));
                break;
            case R.id.about:
                startActivity(new Intent(UserInfoActivity.this, AboutActivity.class));
                break;
            case R.id.cache:
                startActivity(new Intent(UserInfoActivity.this, CacheActivity.class));
                break;
            case R.id.cancel:
                final Dialog dialog1 = new FinishLoginDialog(UserInfoActivity.this,
                        R.style.MyDialog, "您确定要退出登录吗？");
                dialog1.show();
                TextView no = (TextView) dialog1.findViewById(R.id.btn_finish_login_no);
                TextView yes = (TextView) dialog1.findViewById(R.id.btn_finish_login_yes);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                    }
                });
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pushService.unbindAccount(new CommonCallback() {
                            @Override
                            public void onSuccess(String s) {
                                editor.putString("user_name", "");
                                editor.putString("user_pwd", "");
                                editor.putString("userInfo", "");
                                editor.commit();
                                dialog1.dismiss();
                                finish();
                                startActivity(new Intent(UserInfoActivity.this, LoginActivity.class));
                            }

                            @Override
                            public void onFailed(String s, String s1) {
                                Toast.makeText(UserInfoActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1)
                initData();
        }
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
