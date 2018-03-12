package com.example.hcz.hechangzhi;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.ReportformAdapter;
import com.example.hcz.pojo.Reportform;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.DatePickerDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.example.hcz.util.PanelView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 巡检报表
 */
public class ReportformActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name;
    private ImageView back;
    private String loginName, userInfo;
    private BaseDialog dialog;
    private RelativeLayout data;
    private TextView now_time;
    private TextView inspectionRate, inspections, inspectionNum, inspectionSum, dailyNum, dailySum;
    private String inspectionRate_str, inspections_str, inspectionNum_str, inspectionSum_str, dailyNum_str, dailySum_str;
    private List<Reportform> reportforms;
    private ReportformAdapter adapter;
    private ListView listView;
    private PanelView panelView;
    private ScrollView main;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_reportform);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        back = (ImageView) findViewById(R.id.back);
        title_name.setText("智能报表");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        dialog = new BaseDialog(this);
        data = (RelativeLayout) findViewById(R.id.data);
        data.setOnClickListener(this);
        now_time = (TextView) findViewById(R.id.now_time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        now_time.setText(formatter.format((long) System.currentTimeMillis()));
        main = (ScrollView) findViewById(R.id.main);
        emptyView = findViewById(R.id.emptyView);
        inspectionRate = (TextView) findViewById(R.id.inspectionRate);
        inspections = (TextView) findViewById(R.id.inspections);
        inspectionNum = (TextView) findViewById(R.id.inspectionNum);
        inspectionSum = (TextView) findViewById(R.id.inspectionSum);
        dailyNum = (TextView) findViewById(R.id.dailyNum);
        dailySum = (TextView) findViewById(R.id.dailySum);
        reportforms = new ArrayList<>();
        adapter = new ReportformAdapter(reportforms, this);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(ReportformActivity.this, ReportformInfoActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo)
                        .putExtra("time", now_time.getText().toString())
                        .putExtra("pidLoginName", reportforms.get(position).getPidLoginName()));
            }
        });
        panelView = (PanelView) findViewById(R.id.panelView);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                if (reportforms.size() != 0) {
                    main.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    DecimalFormat df = new DecimalFormat();
                    df.setMinimumFractionDigits(1);
                    inspectionRate.setText(df.format(Float.valueOf(inspectionRate_str) * 100) + "%");
                    inspections.setText(inspectionNum_str + "人");
                    inspectionNum.setText(inspections_str + "次");
                    inspectionSum.setText(inspectionSum_str + "次");
                    dailyNum.setText(dailyNum_str + "人");
                    dailySum.setText(dailySum_str + "次");

                    if (inspectionNum_str != null)
                        panelView.setPercent((int) (Float.valueOf(inspectionRate_str) * 100));

                    adapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(listView);
                } else {
                    main.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }

            } else if (msg.what == -1) {

            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(ReportformActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else {

            }
        }
    };

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ReportformAdapter listAdapter = (ReportformAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10); // 可删除
        listView.setLayoutParams(params);
    }

    //绑定数据
    private void initData() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/report/report.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
                        + "&time=" + now_time.getText().toString()).build();
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
                        JSONObject value = jsonObject.getJSONObject("value");
                        inspectionRate_str = value.getFloat("inspectionRate") + "";
                        inspections_str = value.getInteger("inspections") + "";
                        inspectionNum_str = value.getInteger("inspectionNum") + "";
                        inspectionSum_str = value.getInteger("inspectionSum") + "";
                        dailyNum_str = value.getInteger("dailyNum") + "";
                        dailySum_str = value.getInteger("dailySum") + "";

                        JSONArray pidList = value.getJSONArray("pidList");
                        DecimalFormat df = new DecimalFormat();
                        df.setMinimumFractionDigits(1);
                        reportforms.clear();
                        for (int i = 0; i < pidList.size(); i++) {
                            JSONObject v = pidList.getJSONObject(i);
                            Reportform reportform = new Reportform();
                            reportform.setAvatarUrl("http://114.55.235.16:7074" + v.getString("avatarUrl"));
                            reportform.setChiefName(v.getString("chiefName"));
                            reportform.setChiefTypeName(v.getString("chiefTypeName"));
                            reportform.setPidLoginName(v.getString("pidLoginName"));
                            reportform.setInspectionRate(df.format(v.getFloat("pidInspectionRate") * 100) + "%");
                            reportform.setPidInspectionCount(v.getInteger("pidInspectionCount") + "次");
                            reportform.setPidInspectionSum(v.getInteger("pidInspectionSum") + "次");
                            reportform.setPidDaily(v.getInteger("pidDaily") + "次");
                            reportforms.add(reportform);
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
            case R.id.data:
                String time = (String) now_time.getText();
                String year = time.substring(0, 4);
                String mon = time.substring(5, time.length());
                DatePickerDialog dialog = new DatePickerDialog(this, 0, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth) {
                        now_time.setText(startYear + "-" + String.format("%02d",(startMonthOfYear + 1)));
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
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
