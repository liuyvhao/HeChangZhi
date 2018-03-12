package com.example.hcz.dynamicfragment;

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
import com.example.hcz.adapter.NewsAdapter;
import com.example.hcz.hechangzhi.DynamicInfoActivity;
import com.example.hcz.hechangzhi.R;
import com.example.hcz.pojo.News;
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
 * 河长智库
 * Created by Administrator on 2017/11/17.
 */
public class ThinktankFragment extends Fragment implements OnRefreshListener, OnLoadmoreListener, AdapterView.OnItemClickListener {
    private View view;
    private List<News> newses;
    private List<News> listData;
    private ListView listView;
    private NewsAdapter newsAdapter;
    private String loginName, userInfo;
    private SmartRefreshLayout srl;
    private View emptyView;
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_thinktank, container, false);
        init();
        initData();
        return view;
    }

    //绑定控件
    private void init() {
        newses = new ArrayList<>();
        listData = new ArrayList<>();
        listView = view.findViewById(R.id.listView);
        newsAdapter = new NewsAdapter(view.getContext(), listData);
        listView.setAdapter(newsAdapter);
        loginName = getArguments().getString("loginName");
        userInfo = getArguments().getString("userInfo");
        srl = view.findViewById(R.id.srl);
        srl.setRefreshHeader(new ClassicsHeader(view.getContext()).setSpinnerStyle(SpinnerStyle.Translate).setEnableLastTime(false));
        srl.setOnRefreshListener(this);
        srl.setRefreshFooter(new ClassicsFooter(view.getContext()).setSpinnerStyle(SpinnerStyle.Translate));
        srl.setOnLoadmoreListener(this);
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
                listData.addAll(newses);
                newsAdapter.notifyDataSetChanged();
                if (newses.size() == 0) {
                    srl.setEnableRefresh(false);
                    srl.setEnableLoadmore(false);
                } else {
                    srl.setEnableRefresh(true);
                    srl.setEnableLoadmore(true);
                }
                if (newses.size() % 25 == 0)
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
        final Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/news/all.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
                        + "&type=3" + "&page=" + page + "&rows=25").build();
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
                        //新闻信息
                        JSONArray value = (JSONArray) jsonObject.get("value");
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject news = (JSONObject) value.get(i);
                            News news1 = new News();
                            news1.setId((Integer) news.get("id"));
                            news1.setNewsTitle((String) news.get("newsTitle"));
                            news1.setNewSource(news.getString("newSource"));
                            Number time = (Number) news.get("newsTime");
                            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
                            news1.setNewsTime(formatter.format((long) time));
                            news1.setNewsImg("http://114.55.235.16:7074" + news.getString("Con"));
                            newses.add(news1);
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
        startActivity(new Intent(view.getContext(), DynamicInfoActivity.class)
                .putExtra("loginName", loginName)
                .putExtra("id", newses.get(position).getId())
                .putExtra("userInfo", userInfo));
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        page = 1;
        newses.clear();
        srl.resetNoMoreData();
        initData();
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        page = page + 1;
        initData();
    }
}
