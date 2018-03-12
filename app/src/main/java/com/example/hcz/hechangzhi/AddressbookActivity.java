package com.example.hcz.hechangzhi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.AddressbookAdapter;
import com.example.hcz.pojo.Addressbook;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.example.hcz.util.PingYinUtil;
import com.example.hcz.util.QuickAlphabeticBar;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 通讯录
 */
public class AddressbookActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, EditText.OnEditorActionListener {
    private TextView title_name;
    private ImageView back;
    private ListView listView;
    private List<Addressbook> addressbooks;
    private AddressbookAdapter addressbookAdapter;
    private String loginName, userInfo;
    private EditText search;
    private QuickAlphabeticBar alphabeticBar; // 快速索引条
    private BaseDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_addressbook);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        back = (ImageView) findViewById(R.id.back);
        title_name.setText("通讯录");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        loginName = (String) getIntent().getExtras().get("loginName");
        userInfo = (String) getIntent().getExtras().get("userInfo");
        addressbooks = new ArrayList<>();
        alphabeticBar = (QuickAlphabeticBar) findViewById(R.id.fast_scroller);
        listView = (ListView) findViewById(R.id.listView);
        search = (EditText) findViewById(R.id.search);
        search.setOnEditorActionListener(this);
        dialog = new BaseDialog(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                addressbookAdapter = new AddressbookAdapter(AddressbookActivity.this, addressbooks, alphabeticBar);
                listView.setAdapter(addressbookAdapter);
                listView.setOnItemClickListener(AddressbookActivity.this);
                alphabeticBar.init(AddressbookActivity.this);
                alphabeticBar.setListView(listView);
                alphabeticBar.setHight(alphabeticBar.getHeight());
            } else if (msg.what == -1) {
                Toast.makeText(AddressbookActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(AddressbookActivity.this, R.style.MyDialog);
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
                .url("http://114.55.235.16:7074/app/1/riverChief/phoneNumber.do?loginName=" + loginName
                        + "&userInfo=" + userInfo
                        + "&chiefName=" + search.getText().toString().trim()).build();
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
                        addressbooks.clear();
                        JSONArray value = (JSONArray) jsonObject.get("value");
                        for (int i = 0; i < value.size(); i++) {
                            JSONObject c = (JSONObject) value.get(i);
                            Addressbook addressbook = new Addressbook();
                            addressbook.setId(i);
                            addressbook.setAvatarUrl("http://114.55.235.16:7074" + c.getString("avatarUrl"));
                            addressbook.setName((String) c.get("ChiefName"));
                            addressbook.setPin(PingYinUtil.getFirstSpell(c.getString("ChiefName")));
                            addressbook.setDescription((String) c.get("job"));
                            addressbook.setPhone((String) c.get("phone"));
                            addressbooks.add(addressbook);
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
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + addressbooks.get(position).getPhone()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            // 先隐藏键盘
            ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(this
                                    .getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
            initData();
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();

    }
}
