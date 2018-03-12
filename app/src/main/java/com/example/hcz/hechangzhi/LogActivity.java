package com.example.hcz.hechangzhi;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hcz.adapter.TablayoutAdapter;
import com.example.hcz.logfragment.JuniorlogFragment;
import com.example.hcz.logfragment.XunhelogFragment;
import com.example.hcz.util.ActiivtyStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 河长日志
 */
public class LogActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {
    private TextView title_name, submit;
    private ImageView back;
    private String loginName, userInfo;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TablayoutAdapter tablayoutAdapter;
    //TabLayout标签
    private String[] titles = new String[]{"巡河日志", "下级日志"};
    private List<Fragment> fragments = new ArrayList<>();
    private XunhelogFragment xf;
    private JuniorlogFragment jf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_log);
        init();
    }

    //绑定控件
    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        submit = (TextView) findViewById(R.id.submit);
        title_name.setText("河长日志");
        submit.setText("新增");
        submit.setVisibility(View.VISIBLE);
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
        loginName = (String) getIntent().getExtras().get("loginName");
        userInfo = (String) getIntent().getExtras().get("userInfo");
        tabLayout = (TabLayout) findViewById(R.id.tab_layou);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        //设置TabLayout标签的显示方式
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //循环注入标签
        for (String tab : titles) {
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }
        //设置TabLayout点击事件
        tabLayout.setOnTabSelectedListener(this);
        xf = new XunhelogFragment();
        jf = new JuniorlogFragment();
        //向Fragment传入信息
        Bundle bundle = new Bundle();
        bundle.putString("loginName", loginName);
        bundle.putString("userInfo", userInfo);
        xf.setArguments(bundle);
        jf.setArguments(bundle);
        fragments.add(xf);
        fragments.add(jf);
        tablayoutAdapter = new TablayoutAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(tablayoutAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.submit:
                startActivityForResult(new Intent(this, InsertlogActivity.class)
                        .putExtra("loginName", (String) getIntent().getExtras().get("loginName"))
                        .putExtra("userInfo", (String) getIntent().getExtras().get("userInfo")), 1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            Handler h = xf.getHandler();
            h.sendEmptyMessage(10);
        }
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
