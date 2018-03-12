package com.example.hcz.hechangzhi;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.hcz.dynamicfragment.NewsFragment;
import com.example.hcz.dynamicfragment.PolicyFragment;
import com.example.hcz.dynamicfragment.ThinktankFragment;
import com.example.hcz.adapter.TablayoutAdapter;
import com.example.hcz.util.ActiivtyStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 治水动态
 */
public class DynamicActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    private String loginName, userInfo;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TablayoutAdapter tablayoutAdapter;
    //TabLayout标签
    private String[] titles = new String[]{"新闻动态", "政策公告", "河长智库"};
    private List<Fragment> fragments = new ArrayList<>();
    private NewsFragment nf;
    private PolicyFragment pf;
    private ThinktankFragment tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_dynamic);
        init();
    }

    private void init() {
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
        nf=new NewsFragment();
        pf=new PolicyFragment();
        tf=new ThinktankFragment();
        //向Fragment传入信息
        Bundle bundle = new Bundle();
        bundle.putString("loginName", loginName);
        bundle.putString("userInfo", userInfo);
        nf.setArguments(bundle);
        pf.setArguments(bundle);
        tf.setArguments(bundle);

        fragments.add(nf);
        fragments.add(pf);
        fragments.add(tf);
        tablayoutAdapter = new TablayoutAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(tablayoutAdapter);
        tabLayout.setupWithViewPager(viewPager);
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
