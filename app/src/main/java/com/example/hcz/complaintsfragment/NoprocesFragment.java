package com.example.hcz.complaintsfragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.ComplaintsAdapter;
import com.example.hcz.hechangzhi.ComplaintsInfoActivity;
import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.Complaints;
import com.example.hcz.util.FinishLoginDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 未处理
 * Created by Administrator on 2017/11/17.
 */
public class NoprocesFragment extends Fragment implements OnRefreshListener, OnLoadmoreListener, AdapterView.OnItemClickListener {
    private View view;
    private List<Complaints> complaintses;
    private List<Complaints> listData;
    private ListView listView;
    private ComplaintsAdapter complaintsAdapter;
    private SmartRefreshLayout srl;
    private String loginName, userInfo;
    private View emptyView;
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_noproces, container, false);
        init();
        initData();
        return view;
    }

    //绑定控件
    private void init() {
        srl = view.findViewById(R.id.srl);
        srl.setRefreshHeader(new ClassicsHeader(view.getContext()).setSpinnerStyle(SpinnerStyle.Translate).setEnableLastTime(false));
        srl.setOnRefreshListener(this);
        srl.setRefreshFooter(new ClassicsFooter(view.getContext()).setSpinnerStyle(SpinnerStyle.Translate));
        srl.setOnLoadmoreListener(this);
        listView = view.findViewById(R.id.listView);
        complaintses = new ArrayList<>();
        listData = new ArrayList<>();
        complaintsAdapter = new ComplaintsAdapter(view.getContext(), listData);
        listView.setAdapter(complaintsAdapter);
        loginName = getArguments().getString("loginName");
        userInfo = getArguments().getString("userInfo");
        listView.setOnItemClickListener(this);
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
                listData.addAll(complaintses);
                complaintsAdapter.notifyDataSetChanged();
                if (complaintses.size() == 0) {
                    srl.setEnableRefresh(false);
                    srl.setEnableLoadmore(false);
                } else {
                    srl.setEnableRefresh(true);
                    srl.setEnableLoadmore(true);
                }
                if (complaintses.size() % 25 == 0)
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
            } else if (msg.what == 10) {
                page = 1;
                complaintses.clear();
                initData();
            } else {

            }
        }
    };

    public Handler getHandler() {
        return handler;
    }

    //绑定数据
    private void initData() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/comp/allNotType.do?loginName=" + loginName
                        + "&userInfo=" + userInfo + "&page=" + page + "&rows=25").build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
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
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject c = (JSONObject) value.get(i);
                            Complaints complaints = new Complaints();
                            complaints.setId(c.getString("Id"));
                            complaints.setRiverName((String) c.get("riverName"));
                            complaints.setCompTitle((String) c.get("compTitle"));
                            complaints.setCompStatus(c.getString("compStatus"));
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            complaints.setCompTime(formatter.format(c.get("compTime")));
                            complaints.setCompImg("http://114.55.235.16:7074" + c.getString("compImgFirst"));
                            complaints.setCopyerStatus(c.getBoolean("copyerStatus"));
                            complaintses.add(complaints);
                        }
                    }
                    handler.sendEmptyMessage((int) status);
                } else
                    handler.sendEmptyMessage(-1);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivityForResult(new Intent(view.getContext(), ComplaintsInfoActivity.class)
                .putExtra("type", complaintses.get(position).getCompStatus())
                .putExtra("id", complaintses.get(position).getId())
                .putExtra("riverName", complaintses.get(position).getRiverName())
                .putExtra("loginName", loginName)
                .putExtra("copyerStatus", complaintses.get(position).isCopyerStatus())
                .putExtra("userInfo", userInfo), 1);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        page = 1;
        complaintses.clear();
        srl.resetNoMoreData();
        initData();
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        page = page + 1;
        initData();
    }
}
