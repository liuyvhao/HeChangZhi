package com.example.hcz.hechangzhi;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReportformInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name;
    private ImageView back;
    private String loginName, userInfo, time, pidLoginName, chiefName = "";
    private RelativeLayout gj_rl, rz_rl;
    private TextView name, time_tv;
    private BaseDialog dialog;
    private PieChart pieChart1, pieChart2;
    private int inspectionSum, inspectionCount, dailySum, dailyCount;
    private Float inspectionRate, dailyRate;
    private TextView inspection_yi, inspection_wei, inspection_rate, daily_yi, daily_wei, daily_rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_reportform_info);
        initView();
        initData();
    }

    private void initView() {
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("智能报表");
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        time = getIntent().getExtras().getString("time");
        pidLoginName = getIntent().getExtras().getString("pidLoginName");
        gj_rl = (RelativeLayout) findViewById(R.id.gj_rl);
        rz_rl = (RelativeLayout) findViewById(R.id.rz_rl);
        gj_rl.setOnClickListener(this);
        rz_rl.setOnClickListener(this);
        name = (TextView) findViewById(R.id.name);
        time_tv = (TextView) findViewById(R.id.time);
        time_tv.setText(time + "月");
        dialog = new BaseDialog(this);
        pieChart1 = (PieChart) findViewById(R.id.pieChart1);
        pieChart2 = (PieChart) findViewById(R.id.pieChart2);
        showChart(pieChart1);
        showChart(pieChart2);
        inspection_yi = (TextView) findViewById(R.id.inspection_yi);
        inspection_wei = (TextView) findViewById(R.id.inspection_wei);
        inspection_rate = (TextView) findViewById(R.id.inspection_rate);
        daily_yi = (TextView) findViewById(R.id.daily_yi);
        daily_wei = (TextView) findViewById(R.id.daily_wei);
        daily_rate = (TextView) findViewById(R.id.daily_rate);
    }

    private void initData() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/report/reportDetail.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
                        + "&time=" + time
                        + "&pidLoginName=" + pidLoginName).build();
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
                        JSONArray value = jsonObject.getJSONArray("value");
                        JSONObject v1 = value.getJSONObject(0);
                        inspectionSum = v1.getInteger("inspectionSum");
                        inspectionRate = v1.getFloat("inspectionRate");
                        inspectionCount = v1.getInteger("inspectionCount");
                        chiefName = v1.getString("chiefName");

                        JSONObject v2 = value.getJSONObject(1);
                        dailyCount = v2.getInteger("dailyCount");
                        dailyRate = v2.getFloat("dailyRate");
                        dailySum = v2.getInteger("dailySum");
                    }
                    handler.sendEmptyMessage((Integer) status);
                } else
                    handler.sendEmptyMessage(-1);
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                name.setText(chiefName);
                inspection_yi.setText(inspectionSum + "次");
                int wei1 = inspectionCount - inspectionSum;
                if (inspectionCount - inspectionSum < 0)
                    wei1 = 0;
                inspection_wei.setText(wei1 + "次");
                DecimalFormat df = new DecimalFormat();
                df.setMinimumFractionDigits(1);
                inspection_rate.setText(df.format(inspectionRate * 100) + "%");
                pieChart1.clear();
                ArrayList<PieEntry> yValues = new ArrayList<>();
                yValues.add(new PieEntry(Float.valueOf(inspectionSum), "已巡检"));
                yValues.add(new PieEntry(Float.valueOf(wei1), "未巡检"));
                //y轴的集合
                PieDataSet pieDataSet = new PieDataSet(yValues, "");
                pieDataSet.setSliceSpace(3f); //设置个饼状图之间的距离
                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(getResources().getColor(R.color.colorPrimary));
                colors.add(getResources().getColor(android.R.color.holo_orange_dark));
                pieDataSet.setColors(colors);
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                float px = 3 * (metrics.densityDpi / 160f);
                pieDataSet.setSelectionShift(px); // 选中态多出的长度
                PieData pieData = new PieData(pieDataSet);
                pieData.setDrawValues(false);
                pieChart1.setData(pieData);
                pieChart1.animateXY(1000, 1000); //设置动画

                daily_yi.setText(dailySum + "次");
                int wei2 = dailyCount - dailySum;
                if (dailyCount - dailySum < 0)
                    wei2 = 0;
                daily_wei.setText(wei2 + "次");
                daily_rate.setText(df.format(dailyRate * 100) + "%");
                pieChart2.clear();
                ArrayList<PieEntry> yValues2 = new ArrayList<>();
                yValues2.add(new PieEntry(Float.valueOf(dailySum), "已提交"));
                yValues2.add(new PieEntry(Float.valueOf(wei2), "未提交"));
                //y轴的集合
                PieDataSet pieDataSet2 = new PieDataSet(yValues2, "");
                pieDataSet2.setSliceSpace(3f); //设置个饼状图之间的距离
                ArrayList<Integer> colors2 = new ArrayList<>();
                colors2.add(getResources().getColor(android.R.color.holo_blue_light));
                colors2.add(getResources().getColor(android.R.color.holo_red_light));
                pieDataSet2.setColors(colors2);
                DisplayMetrics metrics2 = getResources().getDisplayMetrics();
                float px2 = 3 * (metrics2.densityDpi / 160f);
                pieDataSet2.setSelectionShift(px2); // 选中态多出的长度
                PieData pieData2 = new PieData(pieDataSet2);
                pieData2.setDrawValues(false);
                pieChart2.setData(pieData2);
                pieChart2.animateXY(1000, 1000); //设置动画
            } else if (msg.what == -1) {
                Toast.makeText(ReportformInfoActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(ReportformInfoActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else {

            }
        }
    };

    private void showChart(PieChart pieChart) {
//        pieChart.setHoleRadius(40f); //半径
        pieChart.setTransparentCircleRadius(0); // 半透明圈
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        pieChart.setHoleRadius(0); //实心圆
//        pieChart.setDrawCenterText(true); //饼状图中间可以添加文字
//        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationAngle(45); // 初始旋转角度
        pieChart.setRotationEnabled(false); // 可以手动旋转
        pieChart.setUsePercentValues(true); //显示成百分比
        pieChart.setDrawCenterText(true);
        pieChart.setDrawEntryLabels(true);
        pieChart.setDrawMarkers(true);
//        pieChart.setCenterText(string); //饼状图中间的文字
//        //设置数据
//        pieChart.setData(pieData);
        Legend mLegend = pieChart.getLegend(); //设置比例图
        mLegend.setEnabled(false);
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART); //最右边显示
        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.gj_rl:
                startActivity(new Intent(ReportformInfoActivity.this, InspectionActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo));
                break;
            case R.id.rz_rl:
                startActivity(new Intent(ReportformInfoActivity.this, LogActivity.class)
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
