package com.example.hcz.hechangzhi;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hcz.complaintsfragment.AllFragment;
import com.example.hcz.complaintsfragment.DealtwithFragment;
import com.example.hcz.complaintsfragment.NoprocesFragment;
import com.example.hcz.adapter.TablayoutAdapter;
import com.example.hcz.util.ActiivtyStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 河道投诉
 */
public class ComplaintsActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {
    private TextView title_name;
    private ImageView back;
    private String loginName, userInfo;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TablayoutAdapter tablayoutAdapter;
    //TabLayout标签
    private String[] titles = new String[]{"全部", "未结案", "已结案"};
    private List<Fragment> fragments = new ArrayList<>();
    private AllFragment af;
    private DealtwithFragment df;
    private NoprocesFragment nf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_complaints);
        init();
    }

    private void init() {
        loginName = (String) getIntent().getExtras().get("loginName");
        userInfo = (String) getIntent().getExtras().get("userInfo");
        title_name = (TextView) findViewById(R.id.title_name);
        back = (ImageView) findViewById(R.id.back);
        title_name.setText("我的投诉");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
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
        af = new AllFragment();
        df = new DealtwithFragment();
        nf = new NoprocesFragment();
        //向Fragment传入信息
        Bundle bundle = new Bundle();
        bundle.putString("loginName", loginName);
        bundle.putString("userInfo", userInfo);
        af.setArguments(bundle);
        df.setArguments(bundle);
        nf.setArguments(bundle);
        fragments.add(af);
        fragments.add(nf);
        fragments.add(df);
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
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            af.getHandler().sendEmptyMessage(10);
            nf.getHandler().sendEmptyMessage(10);
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
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
