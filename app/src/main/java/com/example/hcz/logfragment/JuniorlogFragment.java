package com.example.hcz.logfragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.LogAdapter;
import com.example.hcz.hechangzhi.LogInfoActivity;
import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.Log;
import com.example.hcz.util.DatePickerDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 下级日志
 */
public class JuniorlogFragment extends Fragment implements View.OnClickListener, OnRefreshListener, OnLoadmoreListener, AdapterView.OnItemClickListener {
    private View view;
    private RelativeLayout data;
    private String loginName, userInfo;
    private ListView listView;
    private LogAdapter logAdapter;
    private List<Log> logs;
    private List<Log> listData;
    private SmartRefreshLayout srl;
    private TextView now_time;
    private View emptyView;
    private int page = 1;
    private SimpleDateFormat formatter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_juniorlog, container, false);
        init();
        initData();
        return view;
    }

    //绑定控件
    private void init() {
        data = view.findViewById(R.id.data);
        data.setOnClickListener(this);
        logs = new ArrayList<>();
        listData = new ArrayList<>();
        srl = view.findViewById(R.id.srl);
        srl.setRefreshHeader(new ClassicsHeader(view.getContext()).setSpinnerStyle(SpinnerStyle.Translate).setEnableLastTime(false));
        srl.setOnRefreshListener(this);
        srl.setRefreshFooter(new ClassicsFooter(view.getContext()).setSpinnerStyle(SpinnerStyle.Translate));
        srl.setOnLoadmoreListener(this);
        listView = view.findViewById(R.id.listview);
        logAdapter = new LogAdapter(view.getContext(), listData);
        listView.setAdapter(logAdapter);
        loginName = getArguments().getString("loginName");
        userInfo = getArguments().getString("userInfo");
        listView.setOnItemClickListener(this);
        now_time = view.findViewById(R.id.now_time);
        formatter = new SimpleDateFormat("yyyy-MM");
        now_time.setText(formatter.format(System.currentTimeMillis()));
        emptyView = view.findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);
        page = 1;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (srl.isRefreshing())
                srl.finishRefresh();
            if (msg.what == 1) {
                listData.clear();
                listData.addAll(logs);
                logAdapter.notifyDataSetChanged();
                if (logs.size() == 0) {
                    srl.setEnableRefresh(false);
                    srl.setEnableLoadmore(false);
                } else {
                    srl.setEnableRefresh(true);
                    srl.setEnableLoadmore(true);
                }
                if (logs.size() % 25 == 0)
                    srl.finishLoadmore();
                else
                    srl.finishLoadmoreWithNoMoreData();
            } else if (msg.what == -1) {
                srl.finishLoadmore(false);
                Toast.makeText(view.getContext(), "请求失败！", Toast.LENGTH_SHORT).show();
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
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/riverChief/subLog.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
                        + "&time=" + now_time.getText().toString()
                        + "&page=" + page + "&rows=25").build();
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
                        JSONArray value = (JSONArray) jsonObject.get("value");
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject v = (JSONObject) value.get(i);
                            Log log = new Log();
                            log.setId((Integer) v.get("id"));
                            log.setLogTitle((String) v.get("chiefName"));
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            log.setLogTime(formatter.format(v.get("time")) + "河长日志");
                            logs.add(log);
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
            case R.id.data:
                String time = (String) now_time.getText();
                String year = time.substring(0, 4);
                String mon = time.substring(5, time.length());
                DatePickerDialog dialog = new com.example.hcz.util.DatePickerDialog(view.getContext(), 0, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth) {
                        now_time.setText(startYear + "-" + String.format("%02d",(startMonthOfYear + 1)));
                        page = 1;
                        logs.clear();
                        initData();
                    }
                }, Integer.valueOf(year), Integer.valueOf(mon), 2);
                dialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(view.getContext(), LogInfoActivity.class)
                .putExtra("loginName", loginName)
                .putExtra("id", logs.get(position).getId())
                .putExtra("userInfo", userInfo));
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        page = 1;
        logs.clear();
        srl.resetNoMoreData();
        initData();
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        page = page + 1;
        initData();
    }
}