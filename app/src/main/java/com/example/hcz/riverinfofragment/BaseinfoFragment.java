package com.example.hcz.riverinfofragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.ChiefListAdapter;
import com.example.hcz.adapter.LowRiverAdapter;
import com.example.hcz.hechangzhi.R;
import com.example.hcz.hechangzhi.RiverinfoActivity;
import com.example.hcz.hechangzhi.RivermapActivity;
import com.example.hcz.pojo.ChiefList;
import com.example.hcz.pojo.WadiInfo;
import com.example.hcz.util.FinishLoginDialog;
import com.example.hcz.util.MyListView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基本信息
 */
public class BaseinfoFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private View view;
    private String loginName, userInfo, Rivername;
    private int id, riverCollection;
    private TextView name1, name2, riverCode, riverLength, area,
            riverStart, riverEnd, riverDuty;
    private String str_riverCode, str_riverLength, str_area, str_riverStart, str_riverEnd,
            str_riverDuty, str_riverLevel, img;
    private SimpleDraweeView imageView;
    private List<WadiInfo> wadiInfos;
    private List<ChiefList> chiefLists;
    private GridView gridview;
    private LowRiverAdapter lowRiverAdapter;
    private ChiefListAdapter chiefListAdapter;
    private LinearLayout heDao_map;
    private CheckBox check_col;
    private MyListView listView;
    private LinearLayout river_fs, river_low;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_baseinfo, container, false);
        init();
        initData();
        return view;
    }

    private void init() {
        loginName = getArguments().getString("loginName");
        userInfo = getArguments().getString("userInfo");
        Rivername = getArguments().getString("Rivername");
        id = getArguments().getInt("id");
        name1 = (TextView) view.findViewById(R.id.name1);
        name2 = (TextView) view.findViewById(R.id.name2);
        riverCode = (TextView) view.findViewById(R.id.riverCode);
        riverLength = (TextView) view.findViewById(R.id.riverLength);
        area = (TextView) view.findViewById(R.id.area);
        riverStart = (TextView) view.findViewById(R.id.riverStart);
        riverEnd = (TextView) view.findViewById(R.id.riverEnd);
        riverDuty = (TextView) view.findViewById(R.id.riverDuty);
        imageView = view.findViewById(R.id.img);
        wadiInfos = new ArrayList<>();
        chiefLists = new ArrayList<>();
        gridview = (GridView) view.findViewById(R.id.gridView);
        lowRiverAdapter = new LowRiverAdapter(view.getContext(), wadiInfos);
        gridview.setAdapter(lowRiverAdapter);
        gridview.setOnItemClickListener(this);
        listView = view.findViewById(R.id.listView);
        chiefListAdapter = new ChiefListAdapter(view.getContext(), chiefLists);
        listView.setAdapter(chiefListAdapter);
        heDao_map = (LinearLayout) view.findViewById(R.id.heDao_map);
        heDao_map.setOnClickListener(this);
        name1.setText(Rivername);
        name2.setText(Rivername);
        check_col = (CheckBox) view.findViewById(R.id.check_col);
        check_col.setOnClickListener(this);
        river_fs = view.findViewById(R.id.river_fs);
        river_low = view.findViewById(R.id.river_low);
    }

    //自适应gridview高度
    private void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        LowRiverAdapter listAdapter = (LowRiverAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }
        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        listView.setLayoutParams(params);
    }

    //更新ui
    private Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (chiefLists.size() != 0)
                    river_fs.setVisibility(View.VISIBLE);
                else
                    river_fs.setVisibility(View.GONE);
                if (wadiInfos.size() != 0)
                    river_low.setVisibility(View.VISIBLE);
                else
                    river_low.setVisibility(View.GONE);
                area.setText(str_area);
                imageView.setImageURI(img);
                riverCode.setText(str_riverCode);
                riverLength.setText(str_riverLength);
                riverStart.setText(str_riverStart);
                riverEnd.setText(str_riverEnd);
                riverDuty.setText(str_riverDuty);
                setListViewHeightBasedOnChildren(gridview);
                lowRiverAdapter.notifyDataSetChanged();
                if (riverCollection == 0)
                    check_col.setChecked(false);
                else
                    check_col.setChecked(true);
            } else if (msg.what == -1) {
                Toast.makeText(view.getContext(), "请求失败！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(view.getContext(), R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else if (msg.what == 10) {
                ((RiverinfoActivity) view.getContext()).setResult(10);
            }
        }
    };

    //绑定数据
    private void initData() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/river/riverBasic.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
                        + "&id=" + id).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                hander.sendEmptyMessage(-1);
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
                        wadiInfos.clear();
                        //河道信息
                        JSONObject value = (JSONObject) jsonObject.get("value");

                        img = "http://114.55.235.16:7074" + value.getString("img");
                        str_riverCode = (String) value.get("riverCode");
                        str_riverLevel = (String) value.get("riverPosition");
                        str_riverLength = (String) value.get("riverLength");
                        str_riverStart = (String) value.get("riverStart");
                        str_riverDuty = (String) value.get("riverDuty");
                        str_riverEnd = (String) value.get("riverEnd");
                        str_area = (String) value.get("riverResponsible");
                        riverCollection = value.getInteger("riverCollection");

                        //下级河道信息
                        JSONArray lowRiver = (JSONArray) value.get("pid");
                        for (int i = 0; i < lowRiver.size(); i++) {
                            JSONObject pid = (JSONObject) lowRiver.get(i);
                            WadiInfo wadiInfo = new WadiInfo();
                            wadiInfo.setId(Integer.parseInt(pid.getString("pidId")));
                            wadiInfo.setName(pid.getString("pName"));
                            wadiInfo.setRiver_type(pid.getString("pidRank"));
                            wadiInfo.setType(pid.getString("pidQuality"));
                            wadiInfos.add(wadiInfo);
                        }

                        chiefLists.clear();
                        //河长联系方式
                        JSONArray chiefList = (JSONArray) value.get("chiefList");
                        for (int i = 0; i < chiefList.size(); i++) {
                            JSONObject cl = (JSONObject) chiefList.get(i);
                            ChiefList chiefList1 = new ChiefList();
                            chiefList1.setId(cl.getInteger("id"));
                            chiefList1.setChiefName(cl.getString("chiefName"));
                            chiefList1.setPhone(cl.getString("phone"));
                            chiefList1.setJob(cl.getString("job"));
                            chiefList1.setLinkman(cl.getString("linkman"));
                            chiefList1.setLinkmanPhone(cl.getString("linkmanPhone"));
                            chiefList1.setDepartment(cl.getString("department"));
                            chiefList1.setDepartmentphone(cl.getString("departmenterphone"));
                            chiefLists.add(chiefList1);
                        }
                    }
                    hander.sendEmptyMessage((int) status);
                } else
                    hander.sendEmptyMessage(-1);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(view.getContext(), RiverinfoActivity.class)
                .putExtra("id", wadiInfos.get(position).getId())
                .putExtra("Rivername", wadiInfos.get(position).getName())
                .putExtra("loginName", loginName)
                .putExtra("type", wadiInfos.get(position).getType())
                .putExtra("userInfo", userInfo));
    }

    private int collecting;

    private void check_col() {
        if (check_col.isChecked())
            collecting = 1;
        else
            collecting = 0;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/river/riverCollection.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
                        + "&id=" + id
                        + "&collecting=" + collecting).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                hander.sendEmptyMessage(-1);
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
                    hander.sendEmptyMessage(10);
                } else {
                    hander.sendEmptyMessage((int) status);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.heDao_map:
                startActivity(new Intent(view.getContext(), RivermapActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("Rivername", Rivername)
                        .putExtra("userInfo", userInfo)
                        .putExtra("RiverId", id));
                break;
            case R.id.check_col:
                check_col();
                break;
            default:
                break;
        }
    }
}
