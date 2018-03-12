package com.example.hcz.hechangzhi;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * 治水动态详情
 */
public class DynamicInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name;
    private ImageView back;
    private String loginName, userInfo;
    private int id;
    private WebView webView;
    private String html;
    private BaseDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_dynamic_info);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("详情");
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        id = getIntent().getExtras().getInt("id");
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setDomStorageEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        dialog = new BaseDialog(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                //加载HTML字符串进行显示
                if (webView != null) {
//                    html = "<html><head></head><body>" + html + "</body></html>";
                    webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
//                    webView.loadUrl(html);
//                    webView.setWebViewClient(new WebViewClient(){
//                        @Override
//                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//                            view.loadUrl(url);
//                            return true;
//                        }
//                    });
//                    webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                }
            } else if (msg.what == -1) {

            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(DynamicInfoActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else {

            }
        }
    };

    //绑定数据
    private void initData() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/news/newsHtml.do?loginName=" + loginName
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
//                    JSONObject value = (JSONObject) jsonObject.get("value");
                        html = jsonObject.getString("value");
//                    html = value.getString("Con");
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
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
