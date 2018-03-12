package com.example.hcz.hechangzhi;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.ComplaintsInfoAdapter;
import com.example.hcz.adapter.CopyerAdapter;
import com.example.hcz.adapter.PhotoWallAdapter;
import com.example.hcz.pojo.ComplaintsInfo;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.example.hcz.util.FullyGridLayoutManager;
import com.example.hcz.util.MyListView;
import com.example.hcz.util.OnItemClickListener;
import com.facebook.fresco.helper.photoview.PictureBrowse;
import com.facebook.fresco.helper.photoview.entity.PhotoInfo;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 投诉信息详情
 */
public class ComplaintsInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name, chu, ping;
    private ImageView back;
    private LinearLayout jie, btm;
    private String loginName, userInfo, type, id, riverName;
    private BaseDialog dialog;
    private ArrayList<PhotoInfo> img_list;
    private String[] copy_list;
    private PhotoWallAdapter adapter;
    private FullyGridLayoutManager mLayoutManager;
    private GridView copyView;
    private RecyclerView gridView;
    private TextView tv_id, compUnit, compTime, compTime2, compConn, compStatus, rName;
    private TextView status1, status2, status3, status4;
    private View status1_v;
    private RelativeLayout status2_rl, status3_rl, status4_rl;
    private String sid = "", scompUnit = "", scompTime = "", scompConn = "";
    private List<ComplaintsInfo> ci2, ci3;
    private MyListView listView2, listView3;
    private ComplaintsInfoAdapter adapter2, adapter3;
    private CopyerAdapter copyerAdapter;
    private boolean copyerStatus;
    private String jie_time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_complaints_info);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        riverName = getIntent().getExtras().getString("riverName");
        type = getIntent().getExtras().getString("type");
        id = getIntent().getExtras().getString("id");
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("详情");
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        chu = (TextView) findViewById(R.id.chu);
        chu.setOnClickListener(this);
        ping = (TextView) findViewById(R.id.ping);
        ping.setOnClickListener(this);
        jie = (LinearLayout) findViewById(R.id.jie);
        btm = (LinearLayout) findViewById(R.id.btm);
        jie.setOnClickListener(this);
        dialog = new BaseDialog(this);
        gridView = (RecyclerView) findViewById(R.id.gridView);
        tv_id = (TextView) findViewById(R.id.id);
        compUnit = (TextView) findViewById(R.id.compUnit);
        compTime = (TextView) findViewById(R.id.compTime);
        compTime2 = (TextView) findViewById(R.id.compTime2);
        compConn = (TextView) findViewById(R.id.compConn);
        compStatus = (TextView) findViewById(R.id.compStatus);
        rName = (TextView) findViewById(R.id.rName);
        rName.setText(riverName);
        status1 = (TextView) findViewById(R.id.status1);
        status2 = (TextView) findViewById(R.id.status2);
        status3 = (TextView) findViewById(R.id.status3);
        status4 = (TextView) findViewById(R.id.status4);
        status1_v = (View) findViewById(R.id.status1_v);
        status2_rl = (RelativeLayout) findViewById(R.id.status2_rl);
        status3_rl = (RelativeLayout) findViewById(R.id.status3_rl);
        status4_rl = (RelativeLayout) findViewById(R.id.status4_rl);
        img_list = new ArrayList<>();
        mLayoutManager = new FullyGridLayoutManager(this, 3);
        gridView.setLayoutManager(mLayoutManager);
        adapter = new PhotoWallAdapter(img_list, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, ArrayList photos, int position) {
                PictureBrowse.newBuilder(ComplaintsInfoActivity.this)
                        .setLayoutManager(mLayoutManager)
                        .setPhotoList(photos)
                        .setCurrentPosition(position)
                        .enabledAnimation(true)
                        .toggleLongClick(false)
                        .start();
            }
        });
        gridView.setAdapter(adapter);
        ci2 = new ArrayList<>();
        listView2 = (MyListView) findViewById(R.id.listView2);
        adapter2 = new ComplaintsInfoAdapter(this, ci2);
        listView2.setAdapter(adapter2);

        ci3 = new ArrayList<>();
        listView3 = (MyListView) findViewById(R.id.listView3);
        adapter3 = new ComplaintsInfoAdapter(this, ci3);
        listView3.setAdapter(adapter3);

        copyView = (GridView) findViewById(R.id.copyView);
        copyerStatus = getIntent().getExtras().getBoolean("copyerStatus");
        updateStatus();
    }

    private void updateStatus() {
        if (type.equals("1")) {
            chu.setVisibility(View.VISIBLE);
            ping.setVisibility(View.GONE);
            jie.setVisibility(View.GONE);
            if (!copyerStatus)
                btm.setVisibility(View.GONE);
            compStatus.setText("未处理");
            compStatus.setTextColor(getResources().getColor(R.color.red));
        } else if (type.equals("2")) {
            chu.setVisibility(View.GONE);
            ping.setVisibility(View.VISIBLE);
            jie.setVisibility(View.VISIBLE);
            if (!copyerStatus)
                jie.setVisibility(View.GONE);
            compStatus.setText("处理中");
            compStatus.setTextColor(getResources().getColor(R.color.o));
        } else if (type.equals("3")) {
            chu.setVisibility(View.GONE);
            ping.setVisibility(View.VISIBLE);
            jie.setVisibility(View.GONE);
            compStatus.setText("已处理");
            compStatus.setTextColor(getResources().getColor(R.color.colorrivertxt));
        } else if (type.equals("4")) {
            compStatus.setText("已结案");
            btm.setVisibility(View.GONE);
            compStatus.setTextColor(getResources().getColor(R.color.blue));
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                tv_id.setText(sid);
                compUnit.setText(scompUnit);
                compTime.setText(scompTime);
                compTime2.setText(scompTime);
                compConn.setText(scompConn);
                adapter.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
                adapter3.notifyDataSetChanged();
                status1.setText(scompTime);
                if (ci2.size() > 0) {
                    status1_v.setVisibility(View.VISIBLE);
                    status2.setText(ci2.get(0).getCompTime());
                    status2_rl.setVisibility(View.VISIBLE);
                }
                if (ci3.size() > 0) {
                    status1_v.setVisibility(View.VISIBLE);
                    status3.setText(ci3.get(0).getCompTime());
                    status3_rl.setVisibility(View.VISIBLE);
                }
                if (!jie_time.equals("")) {
                    status4_rl.setVisibility(View.VISIBLE);
                    status4.setText(jie_time);
                }
                if (copy_list != null) {
                    copyerAdapter = new CopyerAdapter(ComplaintsInfoActivity.this, copy_list);
                    copyView.setAdapter(copyerAdapter);
                }
            } else if (msg.what == -1) {

            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(ComplaintsInfoActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            }
        }
    };

    //绑定数据
    private void initData() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/comp/detail.do?loginName="
                        + loginName
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
                        JSONObject value = (JSONObject) jsonObject.get("value");
                        if (value.size() != 0) {
                            sid = value.getString("complaintId");
                            scompUnit = value.getString("compUnit");
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            scompTime = formatter.format((long) value.get("compTime"));
                            scompConn = value.getString("cContent");
                            JSONArray compImg = (JSONArray) value.get("compImg");
                            img_list.clear();
                            for (int i = 0; i < compImg.size(); i++) {
                                JSONObject c = (JSONObject) compImg.get(i);
                                PhotoInfo photoInfo = new PhotoInfo();
                                photoInfo.originalUrl = "http://114.55.235.16:7074" + c.getString("cPath");
                                photoInfo.thumbnailUrl = "http://114.55.235.16:7074" + c.getString("cPath");
                                img_list.add(photoInfo);
                            }
                            JSONArray cfour = (JSONArray) value.get("4");
                            if (cfour != null) {
                                if (cfour.size() != 0) {
                                    JSONObject four = (JSONObject) cfour.get(0);
                                    jie_time = formatter.format((long) four.get("compTime"));
                                }
                            }
                            JSONArray cthree = (JSONArray) value.get("3");
                            ci3.clear();
                            if (cthree != null) {
                                for (int i = 0; i < cthree.size(); i++) {
                                    JSONObject three = (JSONObject) cthree.get(i);
                                    ComplaintsInfo c3 = new ComplaintsInfo();
                                    c3.setDisposeId(three.getString("disposeId"));
                                    if (three.getString("compScheduleTitle").equals(loginName))
                                        c3.setCompScheduleTitle("我");
                                    else
                                        c3.setCompScheduleTitle(three.getString("compScheduleTitle"));
                                    c3.setCompTime(formatter.format((long) three.get("compTime")));
                                    c3.setdContent(three.getString("dContent"));
                                    JSONArray dPath = (JSONArray) three.get("disposeImg");
                                    List<String> oneImage = new ArrayList<String>();
                                    if (dPath != null) {
                                        for (int j = 0; j < dPath.size(); j++) {
                                            JSONObject d1 = (JSONObject) dPath.get(j);
                                            oneImage.add("http://114.55.235.16:7074" + d1.getString("path"));
                                        }
                                    }
                                    c3.setdPath(oneImage);
                                    if (cfour != null) {
                                        if (!jie_time.equals("")) {
                                            c3.setStatus("1");
                                        } else {
                                            c3.setStatus("0");
                                        }
                                    } else
                                        c3.setStatus("0");
                                    ci3.add(c3);
                                }
                            }
                            JSONArray ctwo = (JSONArray) value.get("2");
                            ci2.clear();
                            if (ctwo != null) {
                                for (int i = 0; i < ctwo.size(); i++) {
                                    JSONObject two = (JSONObject) ctwo.get(i);
                                    ComplaintsInfo c2 = new ComplaintsInfo();
                                    c2.setDisposeId(two.getString("disposeId"));
                                    if (two.getString("compScheduleTitle").equals(loginName))
                                        c2.setCompScheduleTitle("我");
                                    else
                                        c2.setCompScheduleTitle(two.getString("compScheduleTitle"));
                                    c2.setCompTime(formatter.format((long) two.get("compdTime")));
                                    c2.setdContent(two.getString("dContent"));
                                    JSONArray dPath = (JSONArray) two.get("disposeImg");
                                    List<String> oneImage = new ArrayList<String>();
                                    if (dPath != null) {
                                        for (int j = 0; j < dPath.size(); j++) {
                                            JSONObject d1 = (JSONObject) dPath.get(j);
                                            oneImage.add("http://114.55.235.16:7074" + d1.getString("path"));
                                        }
                                    }
                                    c2.setdPath(oneImage);
                                    if (cfour != null) {
                                        if (!jie_time.equals("")) {
                                            c2.setStatus("1");
                                        } else {
                                            c2.setStatus("0");
                                        }
                                    }
                                    if (cthree != null) {
                                        if (cthree.size() != 0)
                                            c2.setStatus("1");
                                        else
                                            c2.setStatus("0");
                                    } else
                                        c2.setStatus("0");
                                    ci2.add(c2);
                                }
                            }
                            String compCopyer = value.getString("compCopyer");
                            if (!compCopyer.trim().equals("")) {
                                copy_list = compCopyer.split(",");
                                for (int i = 0; i < copy_list.length; i++) {
                                    copy_list[i] = copy_list[i].replaceAll("\\[", "");
                                    copy_list[i] = copy_list[i].replaceAll("\\]", "");
                                    copy_list[i] = copy_list[i].replaceAll("\"", "");
                                }
                            }
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
            case R.id.chu:
                startActivityForResult(new Intent(this, AdditionalActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo)
                        .putExtra("type", "2")
                        .putExtra("id", sid), 1);
                break;
            case R.id.ping:
                startActivityForResult(new Intent(this, AdditionalActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo)
                        .putExtra("type", type)
                        .putExtra("id", sid), 2);
                break;
            case R.id.jie:
                startActivityForResult(new Intent(this, AdditionalActivity.class)
                        .putExtra("loginName", loginName)
                        .putExtra("userInfo", userInfo)
                        .putExtra("type", "3")
                        .putExtra("id", sid), 3);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                initData();
                type = "2";
                ComplaintsInfoActivity.this.setResult(1);
            }
        }
        if (requestCode == 2) {
            if (resultCode == 1)
                initData();
        }
        if (requestCode == 3) {
            if (resultCode == 1) {
                initData();
                type = "3";
                ComplaintsInfoActivity.this.setResult(1);
            }
        }
        updateStatus();
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();

    }
}
