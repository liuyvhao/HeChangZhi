package com.example.hcz.hechangzhi;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.FullyGridLayoutManager;
import com.example.hcz.util.OnItemClickListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.PhotoWallAdapter;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.facebook.fresco.helper.photoview.PictureBrowse;
import com.facebook.fresco.helper.photoview.entity.PhotoInfo;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 巡河日志详情
 */
public class LogInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name;
    private ImageView back;
    private String loginName, userInfo;
    private int id;
    private BaseDialog dialog;
    private String riverName, inspectionName, inspectionTime, inspection1,
            inspection2, inspection3, inspection4, inspection5, other;
    private TextView riverName_tv, inspectionName_tv, inspectionTime_tv, inspection1_tv,
            inspection2_tv, inspection3_tv, inspection4_tv, inspection5_tv, other_tv;
    private RecyclerView gridView;
    private ArrayList<PhotoInfo> data = new ArrayList<>();
    private PhotoWallAdapter mPhotoWallAdapter;
    private FullyGridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_log_info);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("河长日志");
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        id = getIntent().getExtras().getInt("id");
        dialog = new BaseDialog(this);
        riverName_tv = (TextView) findViewById(R.id.riverName);
        inspectionName_tv = (TextView) findViewById(R.id.inspectionName);
        inspectionTime_tv = (TextView) findViewById(R.id.inspectionTime);
        inspection1_tv = (TextView) findViewById(R.id.inspection1);
        inspection2_tv = (TextView) findViewById(R.id.inspection2);
        inspection3_tv = (TextView) findViewById(R.id.inspection3);
        inspection4_tv = (TextView) findViewById(R.id.inspection4);
        inspection5_tv = (TextView) findViewById(R.id.inspection5);
        other_tv = (TextView) findViewById(R.id.other);
        gridView = (RecyclerView) findViewById(R.id.gridView);
        mLayoutManager = new FullyGridLayoutManager(this, 3);
        gridView.setLayoutManager(mLayoutManager);
        mPhotoWallAdapter = new PhotoWallAdapter(data, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, ArrayList photos, int position) {
                PictureBrowse.newBuilder(LogInfoActivity.this)
                        .setLayoutManager(mLayoutManager)
                        .setPhotoList(photos)
                        .setCurrentPosition(position)
                        .enabledAnimation(true)
                        .toggleLongClick(false)
                        .start();
            }
        });
        gridView.setAdapter(mPhotoWallAdapter);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                riverName_tv.setText(riverName);
                inspectionName_tv.setText(inspectionName);
                inspectionTime_tv.setText(inspectionTime);
                inspection1_tv.setText(inspection1);
                inspection2_tv.setText(inspection2);
                inspection3_tv.setText(inspection3);
                inspection4_tv.setText(inspection4);
                inspection5_tv.setText(inspection5);
                other_tv.setText(other);
                mPhotoWallAdapter.notifyDataSetChanged();
            } else if (msg.what == -1) {

            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(LogInfoActivity.this, R.style.MyDialog);
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
                .url("http://114.55.235.16:7074/app/1/riverChief/logDetail.do?loginName="
                        + loginName
                        + "&userInfo=" + userInfo
                        + "&logId=" + id).build();
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
                    String str = (String) jsonObject.get("message");
                    Number status = (Number) jsonObject.get("status");
                    if ((int) status == 1) {
                        JSONObject value = (JSONObject) jsonObject.get("value");
                        riverName = value.getString("riverName");
                        inspectionName = value.getString("inspectionName");
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        inspectionTime = formatter.format(Long.valueOf(value.getString("inspectionTime")));
                        if (value.getString("inspection1").equals("1")) {
                            inspection1 = "是";
                        } else if (value.getString("inspection1").equals("0")) {
                            inspection1 = "否";
                        }
                        if (value.getString("inspection2").equals("1")) {
                            inspection2 = "是";
                        } else if (value.getString("inspection2").equals("0")) {
                            inspection2 = "否";
                        }
                        if (value.getString("inspection3").equals("1")) {
                            inspection3 = "是";
                        } else if (value.getString("inspection3").equals("0")) {
                            inspection3 = "否";
                        }
                        if (value.getString("inspection4").equals("1")) {
                            inspection4 = "是";
                        } else if (value.getString("inspection4").equals("0")) {
                            inspection4 = "否";
                        }
                        if (value.getString("inspection5").equals("1")) {
                            inspection5 = "是";
                        } else if (value.getString("inspection5").equals("0")) {
                            inspection5 = "否";
                        }
                        other = value.getString("other");
                        data.clear();
                        JSONArray img = (JSONArray) value.get("inspectionImg");
                        for (int i = 0; i < img.size(); i++) {
                            JSONObject iimg = (JSONObject) img.get(i);
                            PhotoInfo photoInfo = new PhotoInfo();
                            photoInfo.originalUrl = "http://114.55.235.16:7074" + iimg.getString("inspectionImg");
                            photoInfo.thumbnailUrl = "http://114.55.235.16:7074" + iimg.getString("inspectionImg");
                            data.add(photoInfo);
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
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
