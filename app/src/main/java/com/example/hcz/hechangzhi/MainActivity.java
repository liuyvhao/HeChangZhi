package com.example.hcz.hechangzhi;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends TabActivity implements RadioGroup.OnCheckedChangeListener {
    private TextView title_name;
    private ImageView message;
    private TabHost mTabHost;
    private RadioGroup radioGroup;
    //点击关闭的初始时间
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        final String loginName = (String) getIntent().getExtras().get("loginName");
        final String userInfo = (String) getIntent().getExtras().get("userInfo");
        title_name = findViewById(R.id.title_name);
        message = findViewById(R.id.message);
        message.setVisibility(View.VISIBLE);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MessageActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo));
            }
        });
        radioGroup = findViewById(R.id.radiogroup);
        //实例化选项卡
        mTabHost = this.getTabHost();
        //添加选项卡
        mTabHost.addTab(mTabHost.newTabSpec("one").setIndicator("one")
                .setContent(new Intent(this, InformationActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo)));
        mTabHost.addTab(mTabHost.newTabSpec("two").setIndicator("two")
                .setContent(new Intent(this, DynamicActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo)));
        mTabHost.addTab(mTabHost.newTabSpec("three").setIndicator("three")
                .setContent(new Intent(this, UserInfoActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo)));

        radioGroup.setOnCheckedChangeListener(this);
        radioGroup.check(R.id.hedao);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.hedao:
                mTabHost.setCurrentTabByTag("one");
//                title_name.setText("蓝田河道");
                title_name.setText("河道管理");
                break;
            case R.id.dongtai:
                mTabHost.setCurrentTabByTag("two");
                title_name.setText("治水动态");
                break;
            case R.id.usercenter:
                mTabHost.setCurrentTabByTag("three");
                title_name.setText("河长中心");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            //具体的操作代码
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(MainActivity.this, "再次点击退出！", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
        return true;
    }

}
