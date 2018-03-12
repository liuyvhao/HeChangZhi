package com.example.hcz.hechangzhi;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HistorymapActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private TextView title_name;
    private ImageView back;
    private RadioGroup radioGroup;
    private MapView mapView;
    private AMap aMap;
    private BaseDialog dialog;
    private MyLocationStyle myLocationStyle;
    private String loginName, userInfo, id;
    private List<LatLng> latLngs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_historymap);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        id = getIntent().getExtras().getString("id");
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("巡检轨迹");
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        dialog = new BaseDialog(this);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.interval(2000);         //定位间隔
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);      //设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);                // 设置为true表示启动显示定位蓝点
        latLngs = new ArrayList<>();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                if (latLngs.size() != 0) {
                    aMap.addMarker(new MarkerOptions().position(latLngs.get(0)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.drawable.map_start))));
                    aMap.addMarker(new MarkerOptions().position(latLngs.get(latLngs.size() - 1)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.drawable.map_stop))));
                    aMap.addPolyline(new PolylineOptions()
                            .addAll(latLngs).width(15).color(getResources().getColor(R.color.river_blue)));
                    LatLngBounds.Builder newbounds = new LatLngBounds.Builder();
                    for (int i = 0; i < latLngs.size(); i++) {
                        newbounds.include(latLngs.get(i));
                    }
                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(newbounds.build(),
                            105));//第二个参数为四周留宽度
                }
            } else if (msg.what == -1) {

            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(HistorymapActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else {

            }
        }
    };

    //绑定数据
    private void initData() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/inspection/inspectionTrajectory.do?loginName=" + loginName
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
                        latLngs.clear();
                        JSONArray value = (JSONArray) jsonObject.get("value");
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject v = (JSONObject) value.get(i);
                            LatLng latLng = new LatLng(Double.valueOf(v.getString("lat")), Double.valueOf(v.getString("lng")));
                            latLngs.add(latLng);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
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
}
