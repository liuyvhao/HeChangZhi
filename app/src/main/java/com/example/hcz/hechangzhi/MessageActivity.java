package com.example.hcz.hechangzhi;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.MessageAdapter;
import com.example.hcz.pojo.Message;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.example.hcz.util.TypeDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshListener {
    private TextView title_name;
    private ImageView back, sx;
    private ListView listView;
    private List<Message> messages = new ArrayList<>();
    private List<Message> listData = new ArrayList<>();
    private MessageAdapter adapter;
    private View emptyView;
    private BaseDialog dialog;
    private String loginName, userInfo;
    private SmartRefreshLayout srl;
    private int page = 1;
    private int type = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_message);
        initView();
        initData();
    }

    private void initView() {
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("消息中心");
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        sx = (ImageView) findViewById(R.id.sx);
        sx.setVisibility(View.VISIBLE);
        sx.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
        emptyView = findViewById(R.id.emptyView);
        adapter = new MessageAdapter(this, listData, loginName, userInfo);
        listView.setAdapter(adapter);
        listView.setEmptyView(emptyView);
        dialog = new BaseDialog(this);
        srl = (SmartRefreshLayout) findViewById(R.id.srl);
        srl.setRefreshHeader(new ClassicsHeader(this).setSpinnerStyle(SpinnerStyle.Translate).setEnableLastTime(false));
        srl.setOnRefreshListener(this);
        dialog.show();
        page = 1;
        type = 1;
    }

    private void initData() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/message/message.do?loginName=" + loginName
                        + "&userInfo=" + userInfo + "&type=" + type
                        + "&page=" + page + "&rows=15").build();
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
                        JSONArray value = jsonObject.getJSONArray("value");
//                    messages.clear();
                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm");
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject v = value.getJSONObject(i);
                            Message message = new Message();
                            message.setMessageId(v.getString("messageId"));
                            message.setType(v.getString("type"));
                            message.setComplaintRiver(v.getString("complaintRiver"));
                            message.setResponeRiver(v.getString("responRiver"));
                            message.setCompStatus(v.getString("compStatus"));
                            message.setCompCon(v.getString("complaintCont"));
                            message.setRecipient(v.getString("recipient"));
                            message.setUninspection(v.getString("uninspection"));
                            message.setTime(formatter.format(v.getLong("time")));
                            messages.add(message);
                        }
                        ListSort(messages);
                    }

                    handler.sendEmptyMessage((Integer) status);
                } else {
                    handler.sendEmptyMessage(-1);
                }
            }
        });
    }

    private void ListSort(List<Message> list) {
        Collections.sort(list, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm");
                try {
                    Date dt1 = format.parse(o1.getTime());
                    Date dt2 = format.parse(o2.getTime());
                    if (dt1.getTime() > dt2.getTime()) {
                        return 1;
                    } else if (dt1.getTime() < dt2.getTime()) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                int selection = messages.size() - listData.size();
                listData.clear();
                listData.addAll(messages);
                if (srl.isRefreshing()) {
                    srl.finishRefresh();
                    listView.setSelection(selection);
                } else
                    adapter.notifyDataSetChanged();
            } else if (msg.what == -1) {
                if (srl.isRefreshing())
                    srl.finishRefresh();
                Toast.makeText(MessageActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                if (srl.isRefreshing())
                    srl.finishRefresh();
                FinishLoginDialog dialog1 = new FinishLoginDialog(MessageActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.sx:
                final TypeDialog dialog = new TypeDialog(MessageActivity.this, R.style.MyDialog);
                dialog.show();
                dialog.all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messages.clear();
                        page = 1;
                        type = 1;
                        initData();
                        dialog.dismiss();
                    }
                });
                dialog.xj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messages.clear();
                        page = 1;
                        type = 4;
                        initData();
                        dialog.dismiss();
                    }
                });
                dialog.ts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messages.clear();
                        page = 1;
                        type = 2;
                        initData();
                        dialog.dismiss();
                    }
                });
                dialog.cs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messages.clear();
                        page = 1;
                        type = 3;
                        initData();
                        dialog.dismiss();
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        page = page + 1;
        initData();
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }

}
