package com.example.hcz.hechangzhi;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.PhotoWallAdapter;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.example.hcz.util.FullyGridLayoutManager;
import com.example.hcz.util.OnItemClickListener;
import com.facebook.fresco.helper.photoview.PictureBrowse;
import com.facebook.fresco.helper.photoview.entity.PhotoInfo;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

public class PollutionInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name;
    private ImageView back;
    private BaseDialog dialog;
    private TextView name, type, river, position, lng, Lat, remark;
    private String sname, stype, sriver, sposition, slng, sLat, sremark;
    private String id, loginName, userInfo;
    private ArrayList<PhotoInfo> imgs;
    private RecyclerView gridView;
    private PhotoWallAdapter adapter;
    private FullyGridLayoutManager mLayoutManager;
    private LinearLayout ll_imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_pollution_info);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("详细");
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        dialog = new BaseDialog(this);
        name = (TextView) findViewById(R.id.name);
        type = (TextView) findViewById(R.id.type);
        river = (TextView) findViewById(R.id.river);
        position = (TextView) findViewById(R.id.position);
        lng = (TextView) findViewById(R.id.lng);
        Lat = (TextView) findViewById(R.id.Lat);
        remark = (TextView) findViewById(R.id.remark);
        id = getIntent().getExtras().getString("id");
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        imgs = new ArrayList<>();
        gridView = (RecyclerView) findViewById(R.id.gridView);
        mLayoutManager = new FullyGridLayoutManager(this, 3);
        gridView.setLayoutManager(mLayoutManager);
        adapter = new PhotoWallAdapter(imgs, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, ArrayList photos, int position) {
                PictureBrowse.newBuilder(PollutionInfoActivity.this)
                        .setLayoutManager(mLayoutManager)
                        .setPhotoList(photos)
                        .setCurrentPosition(position)
                        .enabledAnimation(true)
                        .toggleLongClick(false)
                        .start();
            }
        });
        gridView.setAdapter(adapter);
        ll_imgs = (LinearLayout) findViewById(R.id.ll_imgs);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                if (imgs.size() != 0)
                    ll_imgs.setVisibility(View.VISIBLE);
                else
                    ll_imgs.setVisibility(View.GONE);
                name.setText(sname);
                type.setText(stype);
                river.setText(sriver);
                position.setText(sposition);
                lng.setText(slng);
                Lat.setText(sLat);
                remark.setText(sremark);
                adapter.notifyDataSetChanged();
            } else if (msg.what == -1) {

            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(PollutionInfoActivity.this, R.style.MyDialog);
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
                .url("http://114.55.235.16:7074/app/1/marker/markDetail.do?loginName=" + loginName
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
                        JSONArray value = (JSONArray) jsonObject.get("value");
                        JSONObject v = (JSONObject) value.get(0);
                        sname = v.getString("name");
                        stype = v.getString("type");
                        sriver = v.getString("river");
                        sposition = v.getString("position");
                        slng = v.getString("lng");
                        sLat = v.getString("Lat");
                        sremark = v.getString("remark");
                        imgs.clear();
                        JSONArray img = (JSONArray) v.get("img");
                        for (int i = 0; i < img.size(); i++) {
                            JSONObject iimg = (JSONObject) img.get(i);
                            PhotoInfo photoInfo = new PhotoInfo();
                            photoInfo.originalUrl = "http://114.55.235.16:7074" + iimg.getString("img");
                            photoInfo.thumbnailUrl = "http://114.55.235.16:7074" + iimg.getString("img");
                            imgs.add(photoInfo);
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
