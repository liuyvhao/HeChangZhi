package com.example.hcz.hechangzhi;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.example.hcz.pojo.Inspection;
import com.example.hcz.pojo.River;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.example.hcz.util.TopDialog;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 开始巡检
 */
public class StartInspectionActivity extends AppCompatActivity implements View.OnClickListener
        , RadioGroup.OnCheckedChangeListener, AMapLocationListener, LocationSource {
    private TextView title_name;
    private ImageView back, del, sub;
    private MapView mapView;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption;
    private OnLocationChangedListener mListener;
    private LatLng oldLatLng;
    private RadioGroup radioGroup;
    private CheckBox checkBox;
    private List<Inspection> inspections;
    private boolean one = true;
    private String loginName, userInfo;
    private BaseDialog dialog;
    private float distance = 0;
    private TextView distance_tv, timer_tv;
    private Timer timer;
    private TimerTask timerTask;
    private ImageView shijian, wuran;
    private List<River> riverList;
    private TopDialog topDialog;
    private int riverId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_start_inspection);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            final Dialog dialog1 = new FinishLoginDialog(StartInspectionActivity.this,
                    R.style.MyDialog, "为了确保巡检精准度是否打开GPS？");
            dialog1.show();
            TextView no = (TextView) dialog1.findViewById(R.id.btn_finish_login_no);
            TextView yes = (TextView) dialog1.findViewById(R.id.btn_finish_login_yes);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                }
            });
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
        }
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("巡检轨迹");
        Drawable drawable = getResources().getDrawable(R.drawable.gj_down);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        title_name.setCompoundDrawables(null, null, drawable, null);//画在右边
        title_name.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        del = (ImageView) findViewById(R.id.del);
        del.setOnClickListener(this);
        sub = (ImageView) findViewById(R.id.sub);
        sub.setOnClickListener(this);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setLocationSource(this);
        }
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);      //设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);                // 设置为true表示启动显示定位蓝点

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        checkBox.setOnClickListener(this);
        inspections = new ArrayList<>();
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        dialog = new BaseDialog(this);
        distance_tv = (TextView) findViewById(R.id.distance);
        timer_tv = (TextView) findViewById(R.id.timer);
        shijian = (ImageView) findViewById(R.id.shijian);
        shijian.setOnClickListener(this);
        wuran = (ImageView) findViewById(R.id.wuran);
        wuran.setOnClickListener(this);
        riverList = new ArrayList<>();
        topDialog = new TopDialog(this, title_name, riverList, riverId);
    }

    private String getStringTime(int cnt) {
        int hour = cnt / 3600;
        int min = cnt % 3600 / 60;
        int second = cnt % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second);
    }

    private void initData() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/river/getRiverIdAndName.do?loginName=" + loginName
                        + "&userInfo=" + userInfo)
                .build();
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
                    //status
                    Number status = (Number) jsonObject.get("status");
                    if ((int) status == 1) {
                        JSONArray value = (JSONArray) jsonObject.get("value");
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject v = (JSONObject) value.get(i);
                            River r = new River();
                            r.setId(v.getInteger("id"));
                            r.setName(v.getString("name"));
                            riverList.add(r);
                        }
                        handler.sendEmptyMessage(10);
                    } else
                        handler.sendEmptyMessage((int) status);
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
                Toast.makeText(StartInspectionActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                checkBox.setChecked(true);
                inspections.clear();
                aMap.clear();
                if (timer != null && timerTask != null) {
                    timer.cancel();
                    timer = null;
                    timerTask.cancel();
                    timerTask = null;
                }
                timer_tv.setText("00:00:00");
                distance = 0;
                distance_tv.setText("0.0米");
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(StartInspectionActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else if (msg.what == 10) {
                if (riverList.size() != 0) {
                    title_name.setText(riverList.get(0).getName());
                    riverId = (int) riverList.get(0).getId();
                }
                topDialog = new TopDialog(StartInspectionActivity.this, title_name, riverList, riverId);
            }
        }
    };

    //上传轨迹
    private void submit() {
        dialog.show();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        for (int i = 0; i < inspections.size(); i++) {
            stringBuffer.append("{lat:" + inspections.get(i).getLat()
                    + ",lng:" + inspections.get(i).getLng()
                    + ",time:" + inspections.get(i).getTime() + "}");
            if (i != inspections.size() - 1)
                stringBuffer.append(",");
        }
        stringBuffer.append("]");
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/inspection/inspectionReport.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
                        + "&riverId=" + riverId
                        + "&latlngs=" + stringBuffer.toString()).build();
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
                if (!checkBox.isChecked()) {
                    final Dialog dialog1 = new FinishLoginDialog(StartInspectionActivity.this,
                            R.style.MyDialog, "正在巡检河道是否退出？");
                    dialog1.show();
                    TextView no = (TextView) dialog1.findViewById(R.id.btn_finish_login_no);
                    TextView yes = (TextView) dialog1.findViewById(R.id.btn_finish_login_yes);
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                        }
                    });
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                            finish();
                        }
                    });
                } else
                    finish();
                break;
            case R.id.title_name:
                topDialog.show();
                Drawable drawable = getResources().getDrawable(R.drawable.gj_top);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
                title_name.setCompoundDrawables(null, null, drawable, null);//画在右边
                break;
            case R.id.del:
                if (inspections.size() != 0) {
                    final Dialog dialog1 = new FinishLoginDialog(StartInspectionActivity.this,
                            R.style.MyDialog, "确定要删除巡检数据吗？");
                    dialog1.show();
                    TextView no = (TextView) dialog1.findViewById(R.id.btn_finish_login_no);
                    TextView yes = (TextView) dialog1.findViewById(R.id.btn_finish_login_yes);
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                        }
                    });
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                            checkBox.setChecked(true);
                            inspections.clear();
                            aMap.clear();
                            if (timer != null && timerTask != null) {
                                timer.cancel();
                                timer = null;
                                timerTask.cancel();
                                timerTask = null;
                            }
                            timer_tv.setText("00:00:00");
                            distance = 0;
                            distance_tv.setText("0.0米");
                        }
                    });
                }
                break;
            case R.id.sub:
                if (riverList.size() != 0) {
                    if (distance > 500) {
                        if (inspections.size() != 0) {
                            final Dialog dialog = new FinishLoginDialog(StartInspectionActivity.this,
                                    R.style.MyDialog, "确定要上传巡检数据吗？");
                            dialog.show();
                            TextView no1 = (TextView) dialog.findViewById(R.id.btn_finish_login_no);
                            TextView yes1 = (TextView) dialog.findViewById(R.id.btn_finish_login_yes);
                            no1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            yes1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    submit();
                                }
                            });
                        } else
                            Toast.makeText(this, "请先巡检！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "巡检范围小巡检无效！", Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(this, "获取不到您的河道！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.checkbox:
                if (!checkBox.isChecked()) {
                    if (timer == null && timerTask == null) {
                        timer = new Timer();
                        timerTask = new TimerTask() {
                            int cnt = 0;

                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!checkBox.isChecked())
                                            timer_tv.setText(getStringTime(cnt++));
                                    }
                                });
                            }
                        };
                        timer.schedule(timerTask, 0, 1000);
                    }
                }
                break;
            case R.id.shijian:
                startActivity(new Intent(this, ReportActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo));
                break;
            case R.id.wuran:
                startActivity(new Intent(this, PollutionActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
        mapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!checkBox.isChecked()) {
            final Dialog dialog1 = new FinishLoginDialog(StartInspectionActivity.this,
                    R.style.MyDialog, "正在巡检河道是否退出？");
            dialog1.show();
            TextView no = (TextView) dialog1.findViewById(R.id.btn_finish_login_no);
            TextView yes = (TextView) dialog1.findViewById(R.id.btn_finish_login_yes);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                }
            });
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                    finish();
                }
            });
        } else
            finish();
        return false;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.radio_b:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                break;
            case R.id.radio_w:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        LatLng newLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        mListener.onLocationChanged(aMapLocation);
        if (one) {
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(newLatLng, 18, 0, 0)));
            one = false;
//            oldLatLng = newLatLng;
        }
        if (!checkBox.isChecked()) {
            Inspection inspection = new Inspection();
            inspection.setLat(newLatLng.latitude + "");
            inspection.setLng(newLatLng.longitude + "");
            inspection.setTime((long) System.currentTimeMillis());
            inspections.add(inspection);
            distance = AMapUtils.calculateLineDistance(oldLatLng, newLatLng) + distance;
            DecimalFormat df = new DecimalFormat("#.0");
            if (distance != 0)
                distance_tv.setText(df.format(distance) + "米");
            if (oldLatLng != newLatLng) {
                aMap.addPolyline((new PolylineOptions())
                        .add(oldLatLng, newLatLng).width(15)
                        .geodesic(true).color(getResources().getColor(R.color.river_blue)));
//                Toast.makeText(this, oldLatLng + "-" + newLatLng, Toast.LENGTH_SHORT).show();
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(newLatLng, 18, 0, 0)));
            }
            oldLatLng = newLatLng;
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = new AMapLocationClient(this);
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
            mLocationOption.setInterval(3000);
            mLocationOption.setOnceLocation(false);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //启动定位
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
    }
}
