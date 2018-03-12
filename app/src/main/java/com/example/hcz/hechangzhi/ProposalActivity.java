package com.example.hcz.hechangzhi;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
 * 我有建议
 */
public class ProposalActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name, submit;
    private ImageView back;
    private EditText text;
    private BaseDialog dialog;
    private String loginName, userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_proposal);
        init();
    }

    //绑定控件
    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        submit = (TextView) findViewById(R.id.submit);
        back = (ImageView) findViewById(R.id.back);
        submit.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        title_name.setText("意见反馈");
        submit.setText("提交");
        submit.setOnClickListener(this);
        back.setOnClickListener(this);
        text = (EditText) findViewById(R.id.text);
        dialog = new BaseDialog(this);
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                Toast.makeText(ProposalActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                finish();
            }else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(ProposalActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else {

            }
        }
    };

    //提交
    private void submit() {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/riverChief/idea.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
                        + "&suggest=" + text.getText().toString()).build();
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
            case R.id.submit:
                if (text.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "说点什么吧！", Toast.LENGTH_SHORT).show();
                } else {
                    submit();
                }
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
