package com.example.hcz.hechangzhi;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.hcz.adapter.PollutionAdapter;
import com.example.hcz.pojo.Pollution;
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

/**
 * 污染源
 */
public class PollutionActivity extends AppCompatActivity implements View.OnClickListener
        , SlidingDrawer.OnDrawerOpenListener, SlidingDrawer.OnDrawerCloseListener,
        AdapterView.OnItemClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter
        , RadioGroup.OnCheckedChangeListener {
    private TextView title_name, submit;
    private ImageView back;
    private String loginName, userInfo;
    private MapView mapView;
    //初始化地图控制器对象
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    private SlidingDrawer slidingDrawer;
    private ListView listView;
    private List<Pollution> pollutions;
    private PollutionAdapter pollutionAdapter;
    private BaseDialog dialog;
    private Marker marker;
    private RadioGroup radioGroup;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_pollution);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        submit = (TextView) findViewById(R.id.submit);
        back = (ImageView) findViewById(R.id.back);
        title_name.setText("污染源列表");
        submit.setText("新增");
        submit.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        myLocationStyle = new MyLocationStyle();        //初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        aMap.setMyLocationStyle(myLocationStyle);       //设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);                    // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        myLocationStyle.showMyLocation(true);               //显示定位蓝点
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
        slidingDrawer = (SlidingDrawer) findViewById(R.id.slidngdrawer);
        slidingDrawer.setOnDrawerOpenListener(this);
        slidingDrawer.setOnDrawerCloseListener(this);
        listView = (ListView) findViewById(R.id.listView);
        pollutions = new ArrayList<>();
        pollutionAdapter = new PollutionAdapter(this, pollutions);
        listView.setAdapter(pollutionAdapter);
        listView.setOnItemClickListener(this);
        dialog = new BaseDialog(this);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        img = (ImageView) findViewById(R.id.img);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                pollutionAdapter.notifyDataSetChanged();
                for (int i = pollutions.size() - 1; i >= 0; i--) {
                    LatLng latLng = new LatLng(Double.valueOf(pollutions.get(i).getLat()), Double.valueOf(pollutions.get(i).getLng()));
                    marker = aMap.addMarker(new MarkerOptions().position(latLng).title(pollutions.get(i).getName()).snippet(pollutions.get(i).getId())
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wry))));
                    marker.showInfoWindow();
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 12, 0, 0)));
                }
            } else if (msg.what == -1) {
                Toast.makeText(PollutionActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(PollutionActivity.this, R.style.MyDialog);
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
                .url("http://114.55.235.16:7074/app/1/marker/mark.do?loginName=" + loginName
                        + "&userInfo=" + userInfo).build();
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
                        pollutions.clear();
                        JSONArray value = (JSONArray) jsonObject.get("value");
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject v = (JSONObject) value.get(i);
                            Pollution pollution = new Pollution();
                            pollution.setId(v.getString("id"));
                            pollution.setName(v.getString("name"));
                            pollution.setPosition(v.getString("position"));
                            pollution.setType(v.getString("type"));
                            pollution.setLat(v.getString("Lat"));
                            pollution.setLng(v.getString("lng"));
                            pollutions.add(pollution);
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
            case R.id.submit:
                startActivityForResult(new Intent(this, InsertPollutionActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo), 1);
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
    public void onDrawerOpened() {
        img.setImageResource(R.drawable.down);
        float scale = this.getResources().getDisplayMetrics().density;
        int n = (int) (40 * scale + 0.5f);
        mapView.setPadding(0, 0, 0, slidingDrawer.getHeight() - n);
        if (marker != null)
            marker.showInfoWindow();
    }

    @Override
    public void onDrawerClosed() {
        img.setImageResource(R.drawable.up);
        mapView.setPadding(0, 0, 0, 0);
        if (marker != null)
            marker.showInfoWindow();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LatLng latLng = new LatLng(Double.valueOf(pollutions.get(position).getLat()), Double.valueOf(pollutions.get(position).getLng()));
        marker = aMap.addMarker(new MarkerOptions().position(latLng)
                .title(pollutions.get(position).getName())
                .snippet(pollutions.get(position).getId())
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wry))));
        marker.showInfoWindow();
        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 12, 0, 0)), 500, null);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        startActivity(new Intent(this, PollutionInfoActivity.class)
                .putExtra("id", marker.getSnippet())
                .putExtra("userInfo", userInfo)
                .putExtra("loginName", loginName));
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View infoContent = getLayoutInflater().inflate(
                R.layout.custom_info_contents, null);
        TextView title = infoContent.findViewById(R.id.title);
        title.setText(marker.getTitle());
        return infoContent;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View infoContent = getLayoutInflater().inflate(
                R.layout.custom_info_contents, null);
        TextView title = infoContent.findViewById(R.id.title);
        title.setText(marker.getTitle());
        return infoContent;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1)
            initData();
    }
}
