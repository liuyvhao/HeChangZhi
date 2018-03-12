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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InsertlogActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name, submit, now_time, river;
    private ImageView back;
    private EditText logName, other;
    private String loginName, userInfo;
    private BaseDialog dialog;
    private RadioGroup radioGroup1, radioGroup2, radioGroup3, radioGroup4, radioGroup5;
    private String inspection1, inspection2, inspection3, inspection4, inspection5;
    private ArrayList<PhotoInfo> imgs;
    private String message;
    private RelativeLayout img_tl, riverRl;
    private RecyclerView gridView;
    private PhotoWallAdapter adapter;
    private FullyGridLayoutManager mLayoutManager;
    private ScrollView scrollView;
    private List<River> riverList;
    private String[] rivers;
    private int river_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_insertlog);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        title_name = (TextView) findViewById(R.id.title_name);
        submit = (TextView) findViewById(R.id.submit);
        back = (ImageView) findViewById(R.id.back);
        title_name.setText("新增日志");
        submit.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        submit.setText("上报");
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
        now_time = (TextView) findViewById(R.id.now_time);
        river = (TextView) findViewById(R.id.river);
        logName = (EditText) findViewById(R.id.logName);
        other = (EditText) findViewById(R.id.other);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        now_time.setText(formatter.format((long) System.currentTimeMillis()));
        logName.setText(loginName + formatter.format((long) System.currentTimeMillis()) + "河长日志");
        dialog = new BaseDialog(this);
        radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
        radioGroup3 = (RadioGroup) findViewById(R.id.radioGroup3);
        radioGroup4 = (RadioGroup) findViewById(R.id.radioGroup4);
        radioGroup5 = (RadioGroup) findViewById(R.id.radioGroup5);
        imgs = new ArrayList<>();
        img_tl = (RelativeLayout) findViewById(R.id.img_tl);
        img_tl.setOnClickListener(this);
        riverRl = (RelativeLayout) findViewById(R.id.riverRl);
        riverRl.setOnClickListener(this);
        gridView = (RecyclerView) findViewById(R.id.gridView);
        mLayoutManager = new FullyGridLayoutManager(this, 3);
        gridView.setLayoutManager(mLayoutManager);
        adapter = new PhotoWallAdapter(imgs, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, ArrayList photos, int position) {
                PictureBrowse.newBuilder(InsertlogActivity.this)
                        .setLayoutManager(mLayoutManager)
                        .setPhotoList(photos)
                        .setCurrentPosition(position)
                        .enabledAnimation(true)
                        .toggleLongClick(false)
                        .start();
            }
        }, true);
        gridView.setAdapter(adapter);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        riverList = new ArrayList<>();
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
                    message = (String) jsonObject.get("message");
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
                InsertlogActivity.this.setResult(1);
                finish();
            } else if (msg.what == -1) {
                Toast.makeText(InsertlogActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(InsertlogActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else if (msg.what == 10) {
                if (riverList.size() != 0) {
                    rivers = new String[riverList.size()];
                    for (int i = 0; i < riverList.size(); i++) {
                        rivers[i] = riverList.get(i).getName();
                    }
                    river_id = (int) riverList.get(0).getId();
                    river.setText(rivers[0]);
                }
            } else {
                Toast.makeText(InsertlogActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    };

    //上报数据
    private void submit() {
        dialog.show();
        getInspection();
        String others;
        if (other.getText().toString().trim().equals("")) {
            others = "无";
        } else {
            others = other.getText().toString();
        }
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
                .url("http://114.55.235.16:7074/app/1/riverChief/addLog.do?loginName=" + loginName
                        + "&userInfo=" + userInfo + "&title=" + logName.getText().toString()
                        + "&inspection1=" + inspection1 + "&inspection2=" + inspection2 + "&inspection3=" + inspection3
                        + "&inspection4=" + inspection4 + "&inspection5=" + inspection5 + "&other=" + others + "&riverId=" + river_id)
                .post(requestBody)
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
                    handler.sendEmptyMessage((int) status);
                } else
                    handler.sendEmptyMessage(-1);
            }
        });
    }

    //获取状态信息
    private void getInspection() {
        if (radioGroup1.getCheckedRadioButtonId() == R.id.radio11)
            inspection1 = "1";
        else if (radioGroup1.getCheckedRadioButtonId() == R.id.radio12)
            inspection1 = "0";
        if (radioGroup2.getCheckedRadioButtonId() == R.id.radio21)
            inspection2 = "1";
        else if (radioGroup2.getCheckedRadioButtonId() == R.id.radio22)
            inspection2 = "0";
        if (radioGroup3.getCheckedRadioButtonId() == R.id.radio31)
            inspection3 = "1";
        else if (radioGroup3.getCheckedRadioButtonId() == R.id.radio32)
            inspection3 = "0";
        if (radioGroup4.getCheckedRadioButtonId() == R.id.radio41)
            inspection4 = "1";
        else if (radioGroup4.getCheckedRadioButtonId() == R.id.radio42)
            inspection4 = "0";
        if (radioGroup5.getCheckedRadioButtonId() == R.id.radio51)
            inspection5 = "1";
        else if (radioGroup5.getCheckedRadioButtonId() == R.id.radio52)
            inspection5 = "0";
    }

    //验证
    private boolean Verification() {
        if (logName.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请填写日志名！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (radioGroup1.getCheckedRadioButtonId() == -1 || radioGroup2.getCheckedRadioButtonId() == -1
                || radioGroup3.getCheckedRadioButtonId() == -1 || radioGroup4.getCheckedRadioButtonId() == -1
                || radioGroup5.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "请选择河道状态！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (river.getText().toString().trim().equals("")) {
            Toast.makeText(this, "请选择河道！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.submit:
                if (Verification())
                    submit();
                break;
            case R.id.img_tl:
                BottomDialog bottomDialog = new BottomDialog(this);
                bottomDialog.show();
                break;
            case R.id.riverRl:
                if (riverList.size() != 0) {
                    new NumberPickerDialog(this, new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            river.setText(rivers[newVal]);
                            river_id = (Integer) riverList.get(newVal).getId();
                        }
                    }, rivers, river.getText().toString()).show();
                } else
                    Toast.makeText(this, "获取不到您的河道！", Toast.LENGTH_SHORT).show();
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
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
