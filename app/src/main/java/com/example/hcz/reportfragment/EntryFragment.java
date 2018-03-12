package com.example.hcz.reportfragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.hcz.adapter.PhotoWallAdapter;
import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.River;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.BottomDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.example.hcz.util.FullyGridLayoutManager;
import com.example.hcz.util.NumberPickerDialog;
import com.example.hcz.util.OnItemClickListener;
import com.facebook.fresco.helper.photoview.PictureBrowse;
import com.facebook.fresco.helper.photoview.entity.PhotoInfo;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 事件录入
 */
public class EntryFragment extends Fragment implements View.OnClickListener, AMapLocationListener {
    private View view;
    public ArrayList<PhotoInfo> imgs = new ArrayList<>();
    private RelativeLayout img_rl;
    private RecyclerView gridView;
    public PhotoWallAdapter adapter;
    private FullyGridLayoutManager mLayoutManager;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption;
    private RelativeLayout type_rl, river_rl;
    public TextView type, river, position;
    private String[] lx;
    private BaseDialog dialog;
    private String loginName, userInfo, message;
    private List<River> riverList;
    private String[] rivers;
    public int river_id;
    public EditText content;
    public String lat, lng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_entry, container, false);
        init();
        initData();
        return view;
    }

    //绑定控件
    private void init() {
        loginName = getArguments().getString("loginName");
        userInfo = getArguments().getString("userInfo");
        img_rl = view.findViewById(R.id.img_rl);
        img_rl.setOnClickListener(this);
        gridView = view.findViewById(R.id.gridView);
        mLayoutManager = new FullyGridLayoutManager(view.getContext(), 3);
        gridView.setLayoutManager(mLayoutManager);
        adapter = new PhotoWallAdapter(imgs, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, ArrayList photos, int position) {
                PictureBrowse.newBuilder(view.getContext())
                        .setLayoutManager(mLayoutManager)
                        .setPhotoList(photos)
                        .setCurrentPosition(position)
                        .enabledAnimation(true)
                        .toggleLongClick(false)
                        .start();
            }
        }, true);
        gridView.setAdapter(adapter);
        position = view.findViewById(R.id.position);
        type = view.findViewById(R.id.type);
        river = view.findViewById(R.id.river);
        type_rl = view.findViewById(R.id.type_rl);
        river_rl = view.findViewById(R.id.river_rl);
        type_rl.setOnClickListener(this);
        river_rl.setOnClickListener(this);
        lx = new String[]{"非法捕捞", "垃圾倾倒", "围垦河流", "非法建筑", "破坏设施"};
        type.setText(lx[0]);
        content = view.findViewById(R.id.content);
        //初始化定位
        mLocationClient = new AMapLocationClient(view.getContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        mLocationOption.setOnceLocationLatest(true);
        //开启缓存机制
        mLocationOption.setLocationCacheEnable(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        dialog = new BaseDialog(view.getContext());
        riverList = new ArrayList<>();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                if (riverList.size() != 0) {
                    rivers = new String[riverList.size()];
                    for (int i = 0; i < riverList.size(); i++) {
                        rivers[i] = riverList.get(i).getName();
                    }
                    river_id = (int) riverList.get(0).getId();
                    river.setText(rivers[0]);
                }
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(view.getContext(), R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            }
        }
    };

    //绑定数据
    private void initData() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/river/riverName.do?loginName=" + loginName
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
                    message = (String) jsonObject.get("message");
                    //status
                    Number status = (Number) jsonObject.get("status");
                    if ((int) status == 1) {
                        JSONArray value = (JSONArray) jsonObject.get("value");
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject v = (JSONObject) value.get(i);
                            River r = new River();
                            r.setId(v.getInteger("riverId"));
                            r.setName(v.getString("riverName"));
                            riverList.add(r);
                        }
                    }
                    handler.sendEmptyMessage((int) status);
                } else {
                    handler.sendEmptyMessage(-1);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_rl:
                BottomDialog bottomDialog = new BottomDialog(view.getContext());
                bottomDialog.show();
                break;
            case R.id.type_rl:
                new NumberPickerDialog(view.getContext(), new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        type.setText(lx[newVal]);
                    }
                }, lx, type.getText().toString()).show();
                break;
            case R.id.river_rl:
                if (riverList.size() != 0) {
                    new NumberPickerDialog(view.getContext(), new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            river.setText(rivers[newVal]);
                            river_id = (Integer) riverList.get(newVal).getId();
                        }
                    }, rivers, river.getText().toString()).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                position.setText(aMapLocation.getCity() + aMapLocation.getDistrict() + aMapLocation.getStreet());
                lng = String.valueOf(aMapLocation.getLongitude());
                lat = String.valueOf(aMapLocation.getLatitude());
                mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
            } else {

            }
        }
    }
}
