package com.example.hcz.hechangzhi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hcz.util.ActiivtyStack;

/**
 * 河道巡检
 */
public class InspectionActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name;
    private ImageView back;
    private RelativeLayout start, history, other;
    private String loginName, userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_inspection);
        init();
    }

    //绑定控件
    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        title_name.setText("巡检管理");
        back.setOnClickListener(this);
        start = (RelativeLayout) findViewById(R.id.start);
        history = (RelativeLayout) findViewById(R.id.history);
        other = (RelativeLayout) findViewById(R.id.other);
        start.setOnClickListener(this);
        history.setOnClickListener(this);
        other.setOnClickListener(this);
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.start:
                startActivity(new Intent(this, StartInspectionActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo));
                break;
            case R.id.history:
                startActivity(new Intent(this, HistoryInspectionActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo));
                break;
            case R.id.other:
                startActivity(new Intent(this, JuniorInsperctionActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
