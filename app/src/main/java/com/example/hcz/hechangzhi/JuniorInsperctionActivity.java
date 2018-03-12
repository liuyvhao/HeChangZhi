package com.example.hcz.hechangzhi;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.JinsperctionAdapter;
import com.example.hcz.pojo.Jinsperction;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.DatePickerDialog;
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
import java.util.List;

/**
 * 下级巡检
 */
public class JuniorInsperctionActivity extends AppCompatActivity implements View.OnClickListener,
        OnRefreshListener, OnLoadmoreListener, AdapterView.OnItemClickListener {
    private TextView title_name;
    private ImageView back;
    private List<Jinsperction> jinsperctions;
    private List<Jinsperction> listData;
    private ListView listView;
    private JinsperctionAdapter adapter;
    private String loginName, userInfo;
    private SmartRefreshLayout srl;
    private RelativeLayout time;
    private TextView now_time;
    private View emptyView;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_junior_insperction);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("下级巡检");
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        srl = (SmartRefreshLayout) findViewById(R.id.srl);
        srl.setRefreshHeader(new ClassicsHeader(this).setSpinnerStyle(SpinnerStyle.Translate).setEnableLastTime(false));
        srl.setOnRefreshListener(this);
        srl.setRefreshFooter(new ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Translate));
        srl.setOnLoadmoreListener(this);
        time = (RelativeLayout) findViewById(R.id.time);
        time.setOnClickListener(this);
        now_time = (TextView) findViewById(R.id.now_time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        now_time.setText(formatter.format((long) System.currentTimeMillis()));
        jinsperctions = new ArrayList<>();
        listData = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        adapter = new JinsperctionAdapter(this, listData);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        emptyView = findViewById(R.id.emptyView);
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
                listData.addAll(jinsperctions);
                adapter.notifyDataSetChanged();
                if (jinsperctions.size() == 0) {
                    srl.setEnableRefresh(false);
                    srl.setEnableLoadmore(false);
                } else {
                    srl.setEnableRefresh(true);
                    srl.setEnableLoadmore(true);
                }
                if (jinsperctions.size() % 25 == 0)
                    srl.finishLoadmore();
                else
                    srl.finishLoadmoreWithNoMoreData();
            } else if (msg.what == -1) {
                srl.finishLoadmore(false);
                Toast.makeText(JuniorInsperctionActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(JuniorInsperctionActivity.this, R.style.MyDialog);
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
                .url("http://114.55.235.16:7074/app/1/inspection/subInspection.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
                        + "&time=" + now_time.getText()
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
                            Jinsperction jinsperction = new Jinsperction();
                            jinsperction.setId(v.getString("id"));
                            jinsperction.setChiefName(v.getString("chiefName"));
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            jinsperction.setTime(formatter.format(v.get("time")) + "巡河轨迹");
                            jinsperctions.add(jinsperction);
                        }
                    }
                    handler.sendEmptyMessage((int) status);
                }else
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
            case R.id.time:
                String time = (String) now_time.getText();
                String year = time.substring(0, 4);
                String mon = time.substring(5, time.length());
                DatePickerDialog dialog = new DatePickerDialog(this, 0, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth) {
                        now_time.setText(startYear + "-" + String.format("%02d",(startMonthOfYear + 1)));
                        page = 1;
                        initData();
                    }
                }, Integer.valueOf(year), Integer.valueOf(mon), 2);
                dialog.show();

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, HistorymapActivity.class)
                .putExtra("loginName", loginName)
                .putExtra("id", jinsperctions.get(position).getId())
                .putExtra("userInfo", userInfo));
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        page = 1;
        jinsperctions.clear();
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
