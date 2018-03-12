package com.example.hcz.hechangzhi;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hcz.adapter.TablayoutAdapter;
import com.example.hcz.riverinfofragment.BaseinfoFragment;
import com.example.hcz.riverinfofragment.MonitorFragment;
import com.example.hcz.riverinfofragment.RiverpolicyFragment;
import com.example.hcz.util.ActiivtyStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 河道信息
 */
public class RiverinfoActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {
    private String loginName, userInfo, Rivername, type;
    private int id;
    private TextView title_name;
    private ImageView back;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TablayoutAdapter tablayoutAdapter;
    //TabLayout标签
    private String[] titles = new String[]{"基本信息", "监测信息", "一河一策"};
    private List<Fragment> fragments = new ArrayList<>();
    private BaseinfoFragment bf;
    private MonitorFragment mf;
    private RiverpolicyFragment rf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_riverinfo);
        init();
    }

    private void init() {
        loginName = (String) getIntent().getExtras().get("loginName");
        userInfo = (String) getIntent().getExtras().get("userInfo");
        Rivername = (String) getIntent().getExtras().get("Rivername");
        type = (String) getIntent().getExtras().get("type");
        id = (int) getIntent().getExtras().get("id");
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("河道信息-" + Rivername);
        back = (ImageView) findViewById(R.id.back);
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
        bf = new BaseinfoFragment();
        mf = new MonitorFragment();
        rf=new RiverpolicyFragment();
        //向Fragment传入信息
        Bundle bundle = new Bundle();
        bundle.putString("loginName", loginName);
        bundle.putString("userInfo", userInfo);
        bundle.putString("Rivername", Rivername);
        bundle.putInt("id", id);
        bundle.putString("type", type);
        bf.setArguments(bundle);
        mf.setArguments(bundle);
        rf.setArguments(bundle);
        fragments.add(bf);
        fragments.add(mf);
//        fragments.add(new WadionlineFragment());
        fragments.add(rf);
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
