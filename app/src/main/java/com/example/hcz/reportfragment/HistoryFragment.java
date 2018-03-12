package com.example.hcz.reportfragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.HistoryAdapter;
import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.History;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 历史事件
 */
public class HistoryFragment extends Fragment implements OnRefreshListener, OnLoadmoreListener {
    private View view;
    private SmartRefreshLayout srl;
    private List<History> histories;
    private List<History> listData;
    private ListView listView;
    private HistoryAdapter adapter;
    private String loginName, userInfo;
    private View emptyView;
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);
        init();
        initData();
        return view;
    }

    //绑定控件
    private void init() {
        loginName = getArguments().getString("loginName");
        userInfo = getArguments().getString("userInfo");
        srl = view.findViewById(R.id.srl);
        srl.setRefreshHeader(new ClassicsHeader(view.getContext()).setSpinnerStyle(SpinnerStyle.Translate).setEnableLastTime(false));
        srl.setOnRefreshListener(this);
        srl.setRefreshFooter(new ClassicsFooter(view.getContext()).setSpinnerStyle(SpinnerStyle.Translate));
        srl.setOnLoadmoreListener(this);
        histories = new ArrayList<>();
        listData = new ArrayList<>();
        listView = view.findViewById(R.id.listView);
        adapter = new HistoryAdapter(view.getContext(), listData);
        listView.setAdapter(adapter);
        emptyView = view.findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (srl.isRefreshing())
                srl.finishRefresh();
            if (msg.what == 1) {
                listData.clear();
                listData.addAll(histories);
                adapter.notifyDataSetChanged();

                if (histories.size() == 0) {
                    srl.setEnableRefresh(false);
                    srl.setEnableLoadmore(false);
                } else {
                    srl.setEnableRefresh(true);
                    srl.setEnableLoadmore(true);
                }
                if (histories.size() % 25 == 0)
                    srl.finishLoadmore();
                else
                    srl.finishLoadmoreWithNoMoreData();
            } else if (msg.what == 10) {
                histories.clear();
                initData();
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(view.getContext(), R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else if (msg.what == -1) {
                srl.finishLoadmore(false);
                Toast.makeText(view.getContext(), "请求失败！", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //绑定数据
    private void initData() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/incident/incidentHistory.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
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
                            History history = new History();
                            history.setId(v.getString("id"));
                            history.setType(v.getString("type"));
                            history.setRiverName(v.getString("riverName"));
                            history.setPosition(v.getString("position"));
                            history.setContent(v.getString("content"));
                            List<String> imgs = new ArrayList<String>();
                            JSONArray img = (JSONArray) v.get("img");
                            for (int j = 0; j < img.size(); j++) {
                                JSONObject o = (JSONObject) img.get(j);
                                imgs.add("http://114.55.235.16:7074" + o.getString("img"));
                            }
                            history.setImg(imgs);
                            histories.add(history);
                        }
                    }
                    handler.sendEmptyMessage((int) status);
                } else {
                    handler.sendEmptyMessage(-1);
                }
            }
        });
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        page = 1;
        histories.clear();
        srl.resetNoMoreData();
        initData();
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        page = page + 1;
        initData();
    }
}
