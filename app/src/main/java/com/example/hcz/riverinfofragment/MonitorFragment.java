package com.example.hcz.riverinfofragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.hechangzhi.R;
import com.example.hcz.util.FinishLoginDialog;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 监测信息
 */
public class MonitorFragment extends Fragment {
    private View view;
    private ImageView type;
    private TextView DO, COD, NH3_N, TP, RZ, warningRZ, floodRZ;
    private String str_DO, str_COD, str_NH3_N, str_TP, str_RZ, str_warningRZ, str_floodRZ;
    private int id, stype;
    private String loginName, userInfo;
    private LineChart lineView1, lineView2;
    private ArrayList<String> time1, time2;
    private ArrayList<Float> doxs, cods, nh3s, tps, rzs;
    private LimitLine limitLine1, limitLine2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_monitor, container, false);
        init();
        initData();
        return view;
    }

    //绑定控件
    private void init() {
        time1 = new ArrayList<String>();
        time2 = new ArrayList<>();
        doxs = new ArrayList<>();
        cods = new ArrayList<>();
        nh3s = new ArrayList<>();
        tps = new ArrayList<>();
        rzs = new ArrayList<>();
        type = (ImageView) view.findViewById(R.id.type);
        DO = (TextView) view.findViewById(R.id.DO);
        COD = (TextView) view.findViewById(R.id.COD);
        NH3_N = (TextView) view.findViewById(R.id.NH3_N);
        TP = (TextView) view.findViewById(R.id.TP);
        RZ = (TextView) view.findViewById(R.id.RZ);
        warningRZ = (TextView) view.findViewById(R.id.warningRZ);
        floodRZ = (TextView) view.findViewById(R.id.floodRZ);
        loginName = getArguments().getString("loginName");
        userInfo = getArguments().getString("userInfo");
        id = getArguments().getInt("id");
        stype = getArguments().getInt("type");
        lineView1 = (LineChart) view.findViewById(R.id.lineView1);
        lineView2 = (LineChart) view.findViewById(R.id.lineView2);
        switch (stype) {
            case 1:
                type.setImageResource(R.drawable.water1);
                break;
            case 2:
                type.setImageResource(R.drawable.water2);
                break;
            case 3:
                type.setImageResource(R.drawable.water3);
                break;
            case 4:
                type.setImageResource(R.drawable.water4);
                break;
            case 5:
                type.setImageResource(R.drawable.water5);
                break;
            case 6:
                type.setImageResource(R.drawable.water6);
                break;
            default:
                break;
        }
        showChart(lineView1);
        showChart(lineView2);
    }

    //更新UI
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                DO.setText(str_DO + "");
                COD.setText(str_COD + "");
                NH3_N.setText(str_NH3_N + "");
                TP.setText(str_TP + "");
                RZ.setText(str_RZ + "");
                warningRZ.setText(str_warningRZ + "");
                floodRZ.setText(str_floodRZ + "");

                XAxis xAxis1 = lineView1.getXAxis();
                xAxis1.setValueFormatter(null);
                lineView1.clear();
                LineData lineData = new LineData();
                if (doxs.size() != 0) {
                    // y轴的数据集合
                    LineDataSet doxLineDataSet = new LineDataSet(getEntrys(doxs), "溶解氧" /*显示在比例图上*/);
                    setLineData(doxLineDataSet, getResources().getColor(R.color.colorPrimary));
                    lineData.addDataSet(doxLineDataSet);
                }
                if (cods.size() != 0) {
                    LineDataSet codLineDataSet = new LineDataSet(getEntrys(cods), "高锰酸钾指数" /*显示在比例图上*/);
                    setLineData(codLineDataSet, getResources().getColor(R.color.cod));
                    lineData.addDataSet(codLineDataSet);
                }
                if (nh3s.size() != 0) {
                    LineDataSet nh3LineDataSet = new LineDataSet(getEntrys(nh3s), "氨基酸" /*显示在比例图上*/);
                    setLineData(nh3LineDataSet, getResources().getColor(R.color.o));
                    lineData.addDataSet(nh3LineDataSet);
                }
                if (tps.size() != 0) {
                    LineDataSet tpLineDataSet = new LineDataSet(getEntrys(tps), "总磷" /*显示在比例图上*/);
                    setLineData(tpLineDataSet, getResources().getColor(R.color.river_blue));
                    lineData.addDataSet(tpLineDataSet);
                }
                if (lineData.getDataSetCount() != 0) {
                    lineView1.setData(lineData);// 设置数据
                    setAxisValue(time1, lineView1);
                    lineView1.invalidate();
                    lineView1.animateY(500, Easing.EasingOption.EaseInOutSine); // 立即执行的动画
                }

                XAxis xAxis2 = lineView2.getXAxis();
                xAxis2.setValueFormatter(null);
                lineView2.clear();
                LineData lineData1 = new LineData();
                if (rzs.size() != 0) {
                    LineDataSet rzLineDataSet = new LineDataSet(getEntrys(rzs), "当前水位");
                    setLineData(rzLineDataSet, getResources().getColor(R.color.river_blue));
                    lineData1.addDataSet(rzLineDataSet);
                }
                YAxis leftAxis = lineView2.getAxisLeft();
                if (str_warningRZ != null) {
                    limitLine1 = new LimitLine(Float.valueOf(str_warningRZ), "警戒水位");
                    limitLine1.setLineWidth(2f);
                    limitLine1.setTextSize(10f);
                    leftAxis.addLimitLine(limitLine1);
                }
                if (str_floodRZ != null) {
                    limitLine2 = new LimitLine(Float.valueOf(str_floodRZ), "洪水水位");
                    limitLine2.setLineWidth(1f);
                    limitLine2.setTextSize(10f);
                    leftAxis.addLimitLine(limitLine2);
                }
                if (lineData1.getDataSetCount() != 0) {
                    lineView2.setData(lineData1);
                    setAxisValue(time2, lineView2);
                    lineView2.invalidate();
                    lineView2.animateY(500, Easing.EasingOption.EaseInOutSine); // 立即执行的动画
                }

            } else if (msg.what == -1) {
                Toast.makeText(view.getContext(), "请求失败！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(view.getContext(), R.style.MyDialog);
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
                .url("http://114.55.235.16:7074/app/1/river/riverMonitor.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
                        + "&id=" + id).build();
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
                        doxs.clear();
                        cods.clear();
                        nh3s.clear();
                        tps.clear();
                        rzs.clear();
                        time1.clear();
                        time2.clear();
                        //河道信息
                        JSONArray value = (JSONArray) jsonObject.get("value");
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject v = (JSONObject) value.get(i);
                            str_DO = (String) v.get("dox");
                            str_COD = (String) v.get("cod");
                            str_NH3_N = (String) v.get("nh3");
                            str_TP = (String) v.get("tp");
                            str_RZ = (String) v.get("rz");
                            str_warningRZ = (String) v.get("warningRz");
                            str_floodRZ = (String) v.get("floodRz");
                            JSONArray quality = (JSONArray) v.get("quality12");
                            for (int j = 0; j < quality.size(); j++) {
                                JSONObject qv = (JSONObject) quality.get(j);
                                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
                                time1.add(formatter.format(Long.valueOf(qv.getString("time"))));
                                doxs.add(Float.valueOf(qv.getString("dox")));
                                cods.add(Float.valueOf(qv.getString("cod")));
                                nh3s.add(Float.valueOf(qv.getString("nh3")));
                                tps.add(Float.valueOf(qv.getString("tp")));
                            }
                            JSONArray rz12 = (JSONArray) v.get("rz12");
                            for (int j = 0; j < rz12.size(); j++) {
                                JSONObject rv = (JSONObject) rz12.get(j);
                                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
                                time2.add(formatter.format(Long.valueOf(rv.getString("time"))));
                                rzs.add(Float.valueOf(rv.getString("rz")));
                            }
                        }
                    }
                    handler.sendEmptyMessage((int) status);
                } else
                    handler.sendEmptyMessage(-1);
            }
        });
    }

    //设置x轴
    private void setAxisValue(final ArrayList<String> strings, LineChart lineChart) {
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                float percent = value / axis.mAxisRange;
                return strings.get((int) ((strings.size() - 1) * percent));
            }
        };
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
    }

    //获取坐标组
    private ArrayList<Entry> getEntrys(ArrayList<Float> floats) {
        ArrayList<Entry> yValues = new ArrayList<>();
        for (int i = 0; i < floats.size(); i++) {
            yValues.add(new Entry(i, floats.get(i)));
        }
        return yValues;
    }

    //设置LineData
    private void setLineData(LineDataSet lineDataSet, int color) {
        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(1.75f); // 线宽
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCircleSize(4f);// 显示的圆形大小
        // 设置坐标点为空心环状
        lineDataSet.setDrawCircleHole(true);
        // 设置平滑曲线
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.1f);
        lineDataSet.setColor(color);// 显示颜色
        lineDataSet.setCircleColor(color);// 圆形的颜色
        //lineDataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色
    }

    // 设置显示的样式
    private void showChart(LineChart lineChart) {
        lineChart.setDrawBorders(false);  //是否在折线图上添加边框
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);// 数据描述
        XAxis xAxis = lineChart.getXAxis();
        // 设置x轴数据的位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setLabelRotationAngle(-60);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8);
        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setEnabled(false);
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        lineChart.setNoDataText("暂无数据");
        lineChart.setNoDataTextColor(getResources().getColor(R.color.black));
        lineChart.setTouchEnabled(true); // 设置是否可以触摸
        lineChart.setDragEnabled(false);// 是否可以拖拽
        lineChart.setScaleEnabled(false);// 是否可以缩放
        lineChart.setPinchZoom(false);
        lineChart.setBackgroundColor(getResources().getColor(android.R.color.white));// 设置背景
        Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的
        // mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(getResources().getColor(R.color.black));// 颜色
        lineChart.animateY(500, Easing.EasingOption.EaseInOutSine); // 立即执行的动画
    }
}
