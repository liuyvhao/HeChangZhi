package com.example.hcz.hechangzhi;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.hcz.adapter.PhotoWallAdapter;
import com.example.hcz.pojo.River;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.BottomDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.example.hcz.util.FullyGridLayoutManager;
import com.example.hcz.util.NumberPickerDialog;
import com.example.hcz.util.OnItemClickListener;
import com.example.hcz.util.SmallBitmapUtil;
import com.facebook.fresco.helper.photoview.PictureBrowse;
import com.facebook.fresco.helper.photoview.entity.PhotoInfo;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 新增污染源
 */
public class InsertPollutionActivity extends AppCompatActivity implements View.OnClickListener, AMapLocationListener {
    private TextView title_name, submit, type, riverId, position, lng, lat;
    private ImageView back;
    private EditText name, remark;
    private RelativeLayout p_type, river, img;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption;
    private String[] types;
    private String[] rivers;
    private List<River> riverList;
    private ArrayList<PhotoInfo> imgs;
    private RecyclerView gridView;
    private PhotoWallAdapter adapter;
    private FullyGridLayoutManager mLayoutManager;
    private BaseDialog dialog;
    private String message;
    private String loginName, userInfo;
    private int river_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_insert_pollution);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("新增污染源");
        submit = (TextView) findViewById(R.id.submit);
        submit.setText("上报");
        submit.setVisibility(View.VISIBLE);
        submit.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        type = (TextView) findViewById(R.id.type);
        riverId = (TextView) findViewById(R.id.riverId);
        position = (TextView) findViewById(R.id.position);
        lng = (TextView) findViewById(R.id.lng);
        lat = (TextView) findViewById(R.id.lat);
        name = (EditText) findViewById(R.id.name);
        remark = (EditText) findViewById(R.id.remark);
        p_type = (RelativeLayout) findViewById(R.id.p_type);
        river = (RelativeLayout) findViewById(R.id.river);
        img = (RelativeLayout) findViewById(R.id.img);
        p_type.setOnClickListener(this);
        river.setOnClickListener(this);
        img.setOnClickListener(this);
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
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
        types = new String[]{"工业污染源", "农业污染源", "城镇地表径流污染源", "生活污水污染源", "河道底泥污染源"};
        type.setText(types[0]);
        river_id = 0;
        riverList = new ArrayList<>();
        imgs = new ArrayList<>();
        gridView = (RecyclerView) findViewById(R.id.gridView);
        mLayoutManager = new FullyGridLayoutManager(this, 3);
        gridView.setLayoutManager(mLayoutManager);
        adapter = new PhotoWallAdapter(imgs, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, ArrayList photos, int position) {
                PictureBrowse.newBuilder(InsertPollutionActivity.this)
                        .setLayoutManager(mLayoutManager)
                        .setPhotoList(photos)
                        .setCurrentPosition(position)
                        .enabledAnimation(true)
                        .toggleLongClick(false)
                        .start();
            }
        }, true);
        gridView.setAdapter(adapter);
        dialog = new BaseDialog(this);
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                InsertPollutionActivity.this.setResult(1);
                finish();
            } else if (msg.what == -1) {

            } else if (msg.what == 10) {
                rivers = new String[riverList.size()];
                for (int i = 0; i < riverList.size(); i++) {
                    rivers[i] = riverList.get(i).getName();
                }
                river_id = (int) riverList.get(0).getId();
                riverId.setText(rivers[0]);
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(InsertPollutionActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else {
                Toast.makeText(InsertPollutionActivity.this, message, Toast.LENGTH_SHORT).show();
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
                        handler.sendEmptyMessage(10);
                    } else
                        handler.sendEmptyMessage((int) status);
                } else
                    handler.sendEmptyMessage(-1);
            }
        });
    }

    //上报数据
    private void submit() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        if (!imgs.isEmpty()) {
            for (int i = 0; i < imgs.size(); i++) {
                String file = imgs.get(i).originalUrl;
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), new File(file));
                builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"imgs\";filename=\"" + file + "\""), fileBody);
            }
        } else {
            builder.addPart(Headers.of(
                    "Content-Disposition",
                    "form-data; name=\"name\""),
                    RequestBody.create(null, ""));
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/marker/markAdd.do?loginName=" + loginName
                        + "&userInfo=" + userInfo + "&name=" + name.getText().toString()
                        + "&position=" + position.getText().toString() + "&lat=" + lat.getText().toString() + "&lng=" + lng.getText().toString()
                        + "&riverId=" + river_id + "&type=" + type.getText().toString() + "&remark=" + remark.getText().toString())
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                handler.sendEmptyMessage(-1);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String json = response.body().string();
                JSONObject jsonObject = JSON.parseObject(json);
                //msg
                message = (String) jsonObject.get("message");
                //status
                Number status = (Number) jsonObject.get("status");
                handler.sendEmptyMessage((int) status);
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
                if (!name.getText().toString().trim().equals("")) {
                    submit();
                } else {
                    Toast.makeText(this, "污染源名称不能为空！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.p_type:
                new NumberPickerDialog(this, new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        type.setText(types[newVal]);
                    }
                }, types, type.getText().toString()).show();
                break;
            case R.id.river:
                new NumberPickerDialog(this, new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        riverId.setText(rivers[newVal]);
                        river_id = (Integer) riverList.get(newVal).getId();
                    }
                }, rivers, riverId.getText().toString()).show();
                break;
            case R.id.img:
                BottomDialog bottomDialog = new BottomDialog(this);
                bottomDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String picPath = null;
        if (requestCode == 1) {// 相册
            if (data != null) {
                Uri uri = data.getData();
                String schemeStr = data.getScheme().toString();
                if (schemeStr.compareTo("file") == 0) {
                    picPath = uri.toString();
                    picPath = picPath.replace("file://", "");
                } else if (schemeStr.compareTo("content") == 0) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int columnIndex = cursor
                                .getColumnIndex(filePathColumn[0]);
                        picPath = cursor.getString(columnIndex);
                        cursor.close();

                    }
                }
                PhotoInfo photoInfo = new PhotoInfo();
                photoInfo.originalUrl = picPath;
                photoInfo.thumbnailUrl = picPath;
                imgs.add(photoInfo);
            }
        }
        if (requestCode == 2) {
            picPath = SmallBitmapUtil.getGetPath();
            if (new File(picPath).exists()) {
                SmallBitmapUtil.getSmallBitmap(picPath);
                PhotoInfo photoInfo = new PhotoInfo();
                photoInfo.originalUrl = picPath;
                photoInfo.thumbnailUrl = picPath;
                imgs.add(photoInfo);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                position.setText(aMapLocation.getCity() + aMapLocation.getDistrict() + aMapLocation.getStreet());
                lng.setText(String.valueOf(aMapLocation.getLongitude()));
                lat.setText(String.valueOf(aMapLocation.getLatitude()));
                mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
            } else {

            }
        }
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }

}
