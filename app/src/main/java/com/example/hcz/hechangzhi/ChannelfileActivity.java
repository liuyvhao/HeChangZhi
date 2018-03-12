package com.example.hcz.hechangzhi;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.DivisionAdapter;
import com.example.hcz.adapter.WadiInfoAdapter;
import com.example.hcz.pojo.Division;
import com.example.hcz.pojo.WadiInfo;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.example.hcz.util.MyGridView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 河道档案
 */
public class ChannelfileActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, EditText.OnEditorActionListener {
    private String loginName, userInfo;
    private TextView title_name;
    private ImageView back;
    private ListView list_view;
    private WadiInfoAdapter wadiInfoAdapter;
    private List<WadiInfo> wadiInfos;
//    private List<Division> divisions;
//    private MyGridView gridView;
//    private DivisionAdapter adapter;
    private String districkName, riverName;
    private EditText search;
    private BaseDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_channelfile);
        init();
//        getDivision();
        initData();
    }

    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("河道档案");
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        loginName = (String) getIntent().getExtras().get("loginName");
        userInfo = (String) getIntent().getExtras().get("userInfo");
        list_view = (ListView) findViewById(R.id.listview);
        wadiInfos = new ArrayList<>();
//        divisions = new ArrayList<>();
//        divisions.add(new Division("全部", true));
//        gridView = (MyGridView) findViewById(R.id.gridView);
//        adapter = new DivisionAdapter(this, divisions);
//        gridView.setAdapter(adapter);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                divisions.get(position).setCheck(true);
//                for (int i = 0; i < divisions.size(); i++) {
//                    if (position != i)
//                        divisions.get(i).setCheck(false);
//                }
//                adapter.notifyDataSetChanged();
//                if (position != 0)
//                    districkName = divisions.get(position).getName();
//                else
//                    districkName = "";
//                riverName = "";
//                initData();
//            }
//        });
        districkName = "";
        riverName = "";
        search = (EditText) findViewById(R.id.search);
        search.setOnEditorActionListener(this);
        dialog = new BaseDialog(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                wadiInfoAdapter = new WadiInfoAdapter(ChannelfileActivity.this, wadiInfos);
                list_view.setAdapter(wadiInfoAdapter);
                list_view.setOnItemClickListener(ChannelfileActivity.this);
            } else if (msg.what == -1) {
                Toast.makeText(ChannelfileActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(ChannelfileActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else if (msg.what == 10) {
//                adapter.notifyDataSetChanged();
            }
        }
    };

    private void getDivision() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/river/districk.do?loginName=" + loginName
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
                        //河道信息
                        JSONArray value = (JSONArray) jsonObject.get("value");
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject v = (JSONObject) value.get(i);
//                            divisions.add(new Division(v.getString("districkName"), false));
                        }
                        handler.sendEmptyMessage(10);
                    } else
                        handler.sendEmptyMessage((int) status);
                } else
                    handler.sendEmptyMessage(-1);
            }
        });
    }

    //绑定数据
    private void initData() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/river/riverAll.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
                        + "&districkName=" + districkName
                        + "&riverName=" + riverName).build();
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
                String str = (String) jsonObject.get("message");
                //status
                Number status = (Number) jsonObject.get("status");
                if ((int) status == 1) {
                    wadiInfos.clear();
                    //河道信息
                    JSONArray value = (JSONArray) jsonObject.get("value");
                    for (int i = 0; i < value.size(); i++) {
                        JSONObject v = (JSONObject) value.get(i);
                        WadiInfo wadiInfo = new WadiInfo();
                        wadiInfo.setId((Integer) (v.get("id")));
                        wadiInfo.setName((String) (v.get("riverName")));
                        wadiInfo.setType(v.getString("quality"));
                        wadiInfo.setRiver_type((String) v.get("rank"));
                        wadiInfo.setRz(v.getString("level"));
                        wadiInfo.setWadi_img("http://114.55.235.16:7074" + v.getString("path"));
                        wadiInfos.add(wadiInfo);
                    }
                }
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
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivityForResult(new Intent(this, RiverinfoActivity.class)
                .putExtra("id", wadiInfos.get(position).getId())
                .putExtra("Rivername", wadiInfos.get(position).getName())
                .putExtra("type", wadiInfos.get(position).getType())
                .putExtra("loginName", loginName)
                .putExtra("userInfo", userInfo), 10);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            // 先隐藏键盘
            ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(this
                                    .getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
            riverName = search.getText().toString();
            districkName = "";
            initData();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 10)
            ChannelfileActivity.this.setResult(10);
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
