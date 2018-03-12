package com.example.hcz.hechangzhi;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.WadiInfoAdapter;
import com.example.hcz.pojo.WadiInfo;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 河道信息
 */
public class InformationActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Banner banner;
    private List<Integer> imgs;
    private LinearLayout dangan_ll, rizhi_ll, xunjian_ll, wuran_ll, shangbao_ll, tousu_ll, tongxun_ll, baobiao_ll;
    private ListView listView;
    private WadiInfoAdapter wadiInfoAdapter;
    private List<WadiInfo> wadiInfos;
    private BaseDialog dialog;
    private String count, num;
    private TextView count_tv, num_tv;
    private LinearLayout xh_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_information);
        init();
        getInspection();
        initData();
    }

    private void init() {
        dangan_ll = (LinearLayout) findViewById(R.id.dangan_ll);
        rizhi_ll = (LinearLayout) findViewById(R.id.rizhi_ll);
        xunjian_ll = (LinearLayout) findViewById(R.id.xunjian_ll);
        wuran_ll = (LinearLayout) findViewById(R.id.wuran_ll);
        shangbao_ll = (LinearLayout) findViewById(R.id.shangbao_ll);
        tousu_ll = (LinearLayout) findViewById(R.id.tousu_ll);
        tongxun_ll = (LinearLayout) findViewById(R.id.tongxun_ll);
        baobiao_ll = (LinearLayout) findViewById(R.id.baobiao_ll);
        dangan_ll.setOnClickListener(this);
        rizhi_ll.setOnClickListener(this);
        xunjian_ll.setOnClickListener(this);
        wuran_ll.setOnClickListener(this);
        shangbao_ll.setOnClickListener(this);
        tousu_ll.setOnClickListener(this);
        tongxun_ll.setOnClickListener(this);
        baobiao_ll.setOnClickListener(this);
        wadiInfos = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        wadiInfoAdapter = new WadiInfoAdapter(this, wadiInfos);
        listView.setAdapter(wadiInfoAdapter);
        listView.setOnItemClickListener(this);
        dialog = new BaseDialog(this);
        count_tv = (TextView) findViewById(R.id.count);
        num_tv = (TextView) findViewById(R.id.num);
        xh_ll = (LinearLayout) findViewById(R.id.xh_ll);
        xh_ll.setOnClickListener(this);
        imgs = new ArrayList<>();
        imgs.add(R.drawable.p1);
        imgs.add(R.drawable.p2);
        imgs.add(R.drawable.p3);
//        imgs.add(R.drawable.p4);
        banner = (Banner) findViewById(R.id.banner);
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                imageView.setImageResource((Integer) path);
            }
        };
        banner.setImageLoader(imageLoader);
        banner.setImages(imgs);
        banner.setDelayTime(3000);
        banner.setBannerAnimation(Transformer.Accordion);
        banner.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                wadiInfoAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(listView);
                listView.setFocusable(false);
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(InformationActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else if (msg.what == 10) {
                count_tv.setText(count);
                num_tv.setText(num);
            }
        }
    };

    public void setListViewHeightBasedOnChildren(ListView listView) {
        WadiInfoAdapter listAdapter = (WadiInfoAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10); // 可删除
        listView.setLayoutParams(params);
    }

    private void getInspection() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/inspection/inspectionCount.do?loginName=" + getIntent().getExtras().getString("loginName")
                        + "&userInfo=" + getIntent().getExtras().getString("userInfo")).build();
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
                        JSONArray value = (JSONArray) jsonObject.get("value");
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject v = (JSONObject) value.get(i);
                            count = v.getString("inspectionShould");
                            num = v.getString("inspectionCount");
                        }
                        handler.sendEmptyMessage(10);
                    } else
                        handler.sendEmptyMessage((int) status);
                }else
                    handler.sendEmptyMessage(-1);
            }
        });
    }

    //绑定数据
    private void initData() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/river/riverCollectionDetail.do?loginName=" + getIntent().getExtras().getString("loginName")
                        + "&userInfo=" + getIntent().getExtras().getString("userInfo")).build();
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
                        wadiInfos.clear();
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject v = (JSONObject) value.get(i);
                            WadiInfo wadiInfo = new WadiInfo();
                            wadiInfo.setId(v.getInteger("riverId"));
                            wadiInfo.setName(v.getString("riverName"));
                            wadiInfo.setWadi_img("http://114.55.235.16:7074" + v.getString("path"));
                            wadiInfo.setRiver_type(v.getString("rank"));
                            wadiInfo.setType(v.getString("quality"));
                            wadiInfos.add(wadiInfo);
                        }
                    }
                    handler.sendEmptyMessage((int) status);
                } else {
                    Toast.makeText(InformationActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dangan_ll:
                startActivityForResult(new Intent(this, ChannelfileActivity.class)
                        .putExtra("loginName", (String) getIntent().getExtras().get("loginName"))
                        .putExtra("userInfo", (String) getIntent().getExtras().get("userInfo")), 10);
                break;
            case R.id.rizhi_ll:
                startActivity(new Intent(this, LogActivity.class)
                        .putExtra("loginName", (String) getIntent().getExtras().get("loginName"))
                        .putExtra("userInfo", (String) getIntent().getExtras().get("userInfo")));
                break;
            case R.id.xunjian_ll:
                startActivity(new Intent(this, InspectionActivity.class)
                        .putExtra("loginName", (String) getIntent().getExtras().get("loginName"))
                        .putExtra("userInfo", (String) getIntent().getExtras().get("userInfo")));
                break;
            case R.id.wuran_ll:
                startActivity(new Intent(this, PollutionActivity.class)
                        .putExtra("loginName", (String) getIntent().getExtras().get("loginName"))
                        .putExtra("userInfo", (String) getIntent().getExtras().get("userInfo")));
                break;
            case R.id.shangbao_ll:
                startActivity(new Intent(this, ReportActivity.class)
                        .putExtra("loginName", (String) getIntent().getExtras().get("loginName"))
                        .putExtra("userInfo", (String) getIntent().getExtras().get("userInfo")));
                break;
            case R.id.tousu_ll:
                startActivity(new Intent(this, ComplaintsActivity.class)
                        .putExtra("loginName", (String) getIntent().getExtras().get("loginName"))
                        .putExtra("userInfo", (String) getIntent().getExtras().get("userInfo")));
                break;
            case R.id.tongxun_ll:
                startActivity(new Intent(this, AddressbookActivity.class)
                        .putExtra("loginName", (String) getIntent().getExtras().get("loginName"))
                        .putExtra("userInfo", (String) getIntent().getExtras().get("userInfo")));
                break;
            case R.id.baobiao_ll:
                startActivity(new Intent(this, ReportformActivity.class)
                        .putExtra("loginName", (String) getIntent().getExtras().get("loginName"))
                        .putExtra("userInfo", (String) getIntent().getExtras().get("userInfo")));
                break;
            case R.id.xh_ll:
                startActivity(new Intent(this, InspectionActivity.class)
                        .putExtra("loginName", (String) getIntent().getExtras().get("loginName"))
                        .putExtra("userInfo", (String) getIntent().getExtras().get("userInfo")));
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
                .putExtra("loginName", getIntent().getExtras().getString("loginName"))
                .putExtra("userInfo", getIntent().getExtras().getString("userInfo")), 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10)
            initData();
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
