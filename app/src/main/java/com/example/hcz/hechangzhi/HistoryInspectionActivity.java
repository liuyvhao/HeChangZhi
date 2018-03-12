package com.example.hcz.hechangzhi;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.HistoryInspectionAdapter;
import com.example.hcz.pojo.HistoryInspection;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.FinishLoginDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 历史巡检
 */
public class HistoryInspectionActivity extends AppCompatActivity implements View.OnClickListener
        , OnRefreshListener, OnLoadmoreListener, AdapterView.OnItemClickListener {
    private TextView title_name;
    private ImageView back;
    private List<HistoryInspection> historyInspections;
    private List<HistoryInspection> listData;
    private ListView listView;
    private HistoryInspectionAdapter historyAdapter;
    private SmartRefreshLayout srl;
    private String loginName, userInfo;
    private View emptyView;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_history_inspection);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("历史轨迹");
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        historyInspections = new ArrayList<>();
        listData = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        historyAdapter = new HistoryInspectionAdapter(this, listData);
        listView.setAdapter(historyAdapter);
        listView.setOnItemClickListener(this);
        srl = (SmartRefreshLayout) findViewById(R.id.srl);
        srl.setRefreshHeader(new ClassicsHeader(this).setSpinnerStyle(SpinnerStyle.Translate).setEnableLastTime(false));
        srl.setOnRefreshListener(this);
        srl.setRefreshFooter(new ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Translate));
        srl.setOnLoadmoreListener(this);
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        emptyView = (View) findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);
        page = 1;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (srl.isRefreshing())
                srl.finishRefresh();
            if (msg.what == 1) {
                listData.clear();
                listData.addAll(historyInspections);
                historyAdapter.notifyDataSetChanged();
                if (historyInspections.size() == 0) {
                    srl.setEnableRefresh(false);
                    srl.setEnableLoadmore(false);
                } else {
                    srl.setEnableRefresh(true);
                    srl.setEnableLoadmore(true);
                }
                if (historyInspections.size() % 25 == 0)
                    srl.finishLoadmore();
                else
                    srl.finishLoadmoreWithNoMoreData();
            } else if (msg.what == -1) {
                srl.finishLoadmore(false);
                Toast.makeText(HistoryInspectionActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(HistoryInspectionActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else {

            }
        }
    };

    //绑定数据
    private void initData() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/inspection/inspectionHistory.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
                        + "&page=" + page + "&rows=25").build();
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
                    //msg
                    String str = (String) jsonObject.get("message");
                    //status
                    Number status = (Number) jsonObject.get("status");
                    if ((int) status == 1) {
                        JSONArray value = (JSONArray) jsonObject.get("value");
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject v = (JSONObject) value.get(i);
                            HistoryInspection hi = new HistoryInspection();
                            hi.setId(v.getString("id"));
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd HH:mm:ss");
                            hi.setBeginTime(formatter.format(Long.valueOf(v.getString("beginTime"))));
                            hi.setEndTime(formatter.format(Long.valueOf(v.getString("endTime"))));
                            SimpleDateFormat fmt = new SimpleDateFormat("yyyy年MM月");
                            hi.setTime(fmt.format(Long.valueOf(v.getString("beginTime"))));
                            historyInspections.add(hi);
                        }
                    }
                    handler.sendEmptyMessage((int) status);
                } else
                    handler.sendEmptyMessage(-1);
            }
        });

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, HistorymapActivity.class)
                .putExtra("loginName", loginName)
                .putExtra("id", historyInspections.get(position).getId())
                .putExtra("userInfo", userInfo));
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        page = 1;
        historyInspections.clear();
        srl.resetNoMoreData();
        initData();
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        page = page + 1;
        initData();
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}