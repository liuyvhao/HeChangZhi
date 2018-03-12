package com.example.hcz.hechangzhi;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.TablayoutAdapter;
import com.example.hcz.reportfragment.EntryFragment;
import com.example.hcz.reportfragment.HistoryFragment;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.example.hcz.util.SmallBitmapUtil;
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
 * 事件上报
 */
public class ReportActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {
    private TextView title_name, submit;
    private ImageView back;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String loginName, userInfo;
    private TablayoutAdapter tablayoutAdapter;
    //TabLayout标签
    private String[] titles = new String[]{"事件录入", "历史事件"};
    private List<Fragment> fragments = new ArrayList<>();
    private EntryFragment ef;
    private HistoryFragment hf;
    private BaseDialog dialog;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_report);
        init();
    }

    //绑定控件
    private void init() {
        loginName = (String) getIntent().getExtras().get("loginName");
        userInfo = (String) getIntent().getExtras().get("userInfo");
        title_name = (TextView) findViewById(R.id.title_name);
        submit = (TextView) findViewById(R.id.submit);
        back = (ImageView) findViewById(R.id.back);
        title_name.setText("事件上报");
        submit.setText("上报");
        submit.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
        tabLayout = (TabLayout) findViewById(R.id.tab_layou);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        //设置TabLayout标签的显示方式
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //循环注入标签
        for (String tab : titles) {
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }
        //设置TabLayout点击事件
        tabLayout.setOnTabSelectedListener(this);
        ef = new EntryFragment();
        hf = new HistoryFragment();
        //向Fragment传入信息
        Bundle bundle = new Bundle();
        bundle.putString("loginName", loginName);
        bundle.putString("userInfo", userInfo);
        ef.setArguments(bundle);
        hf.setArguments(bundle);
        fragments.add(ef);
        fragments.add(hf);
        tablayoutAdapter = new TablayoutAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(tablayoutAdapter);
        tabLayout.setupWithViewPager(viewPager);
        dialog = new BaseDialog(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                ef.content.setText("");
                ef.imgs.clear();
                ef.adapter.notifyDataSetChanged();
                Handler h = hf.getHandler();
                h.sendEmptyMessage(10);
                Toast.makeText(ReportActivity.this, "上报成功！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(ReportActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            }
        }
    };

    //事件上报
    private void submit() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        if (!ef.imgs.isEmpty()) {
            for (int i = 0; i < ef.imgs.size(); i++) {
                String file = ef.imgs.get(i).originalUrl;
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
                .url("http://114.55.235.16:7074/app/1/incident/incidentAdd.do?loginName=" + loginName
                        + "&userInfo=" + userInfo + "&type=" + ef.type.getText().toString()
                        + "&position=" + ef.position.getText().toString() + "&lat=" + ef.lat + "&lon=" + ef.lng
                        + "&content=" + ef.content.getText().toString() + "&riverId=" + ef.river_id)
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.submit:
                if (ef.position.getText().toString().trim().equals(""))
                    Toast.makeText(this, "地点为空！", Toast.LENGTH_SHORT).show();
                else
                    submit();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                ef.imgs.add(photoInfo);
            }
        }
        if (requestCode == 2) {
            picPath = SmallBitmapUtil.getGetPath();
            if (new File(picPath).exists()) {
                SmallBitmapUtil.getSmallBitmap(picPath);
                PhotoInfo photoInfo = new PhotoInfo();
                photoInfo.originalUrl = picPath;
                photoInfo.thumbnailUrl = picPath;
                ef.imgs.add(photoInfo);
            }
        }
        ef.adapter.notifyDataSetChanged();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            submit.setVisibility(View.VISIBLE);
        } else {
            submit.setVisibility(View.GONE);
        }
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
