package com.example.hcz.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.example.hcz.hechangzhi.LoginActivity;
import com.example.hcz.hechangzhi.R;

public class FinishLoginDialog extends Dialog {

    private String title;
    Context context;
    TextView no, yes;
    private CloudPushService pushService = PushServiceFactory.getCloudPushService();

    public FinishLoginDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.title = "您的账号已在其他设备登录，需要重新登录吗？";
        SharedPreferences sp;
        final SharedPreferences.Editor editor;
        sp = context.getSharedPreferences("hzz_userinfo", context.MODE_PRIVATE);
        editor = sp.edit();
        pushService.unbindAccount(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                editor.putString("user_name", "");
                editor.putString("user_pwd", "");
                editor.putString("userInfo", "");
                editor.commit();
            }

            @Override
            public void onFailed(String s, String s1) {

            }
        });
    }

    public FinishLoginDialog(Context context, int theme, String title) {
        super(context, theme);
        this.context = context;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.finish_login_dialog);
        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        ((TextView) findViewById(R.id.tv_dialog_body)).setText(title);
        no = (TextView) findViewById(R.id.btn_finish_login_no);
        yes = (TextView) findViewById(R.id.btn_finish_login_yes);
    }

    public void NOnclicke() {
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiivtyStack.getScreenManager().clearAllActivity();
            }
        });
    }

    public void YOnclick() {
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ActiivtyStack.getScreenManager().clearAllActivity();
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        });
    }

}