package com.example.hcz.hechangzhi;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.adapter.PhotoWallAdapter;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.BottomDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.example.hcz.util.FullyGridLayoutManager;
import com.example.hcz.util.OnItemClickListener;
import com.example.hcz.util.SmallBitmapUtil;
import com.facebook.fresco.helper.photoview.PictureBrowse;
import com.facebook.fresco.helper.photoview.entity.PhotoInfo;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 评论
 */
public class AdditionalActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name, submit;
    private ImageView back;
    private RelativeLayout img_rl;
    private EditText compConn;
    private ArrayList<PhotoInfo> imgs;
    private RecyclerView gridView;
    private PhotoWallAdapter adapter;
    private FullyGridLayoutManager mLayoutManager;
    private String loginName, userInfo, type, id;
    private BaseDialog dialog;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_additional);
        init();
    }

    //绑定控件
    private void init() {
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        type = getIntent().getExtras().getString("type");
        id = getIntent().getExtras().getString("id");
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("追加评论");
        submit = (TextView) findViewById(R.id.submit);
        submit.setText("发送");
        submit.setVisibility(View.VISIBLE);
        submit.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        img_rl = (RelativeLayout) findViewById(R.id.img_rl);
        img_rl.setOnClickListener(this);
        compConn = (EditText) findViewById(R.id.text);
        imgs = new ArrayList<>();
        gridView = (RecyclerView) findViewById(R.id.gridView);
        mLayoutManager = new FullyGridLayoutManager(this, 3);
        gridView.setLayoutManager(mLayoutManager);
        adapter = new PhotoWallAdapter(imgs, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, ArrayList photos, int position) {
                PictureBrowse.newBuilder(AdditionalActivity.this)
                        .setLayoutManager(mLayoutManager)
                        .setPhotoList(photos)
                        .setCurrentPosition(position)
                        .enabledAnimation(true)
                        .toggleLongClick(false)
                        .start();
            }
        }, true);
        gridView.setAdapter(adapter);
        dialog = new BaseDialog(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                AdditionalActivity.this.setResult(1);
                finish();
            } else if (msg.what == -1) {

            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(AdditionalActivity.this, R.style.MyDialog);
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
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        if (!imgs.isEmpty()) {
            for (int i = 0; i < imgs.size(); i++) {
                String file = imgs.get(i).originalUrl;
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), new File(file));
                builder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"imgs\";filename=\"" + file + "\""), fileBody);
            }
        } else {
            builder.addPart(Headers.of(
                    "Content-Disposition",
                    "form-data; name=\"name\""),
                    RequestBody.create(null, ""));
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/comp/comment.do?loginName=" + loginName
                        + "&userInfo=" + userInfo + "&id=" + id
                        + "&compStatus=" + type + "&compResultConn=" + compConn.getText().toString())
                .post(requestBody)
                .build();
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
                    message = (String) jsonObject.get("message");
                    //status
                    Number status = (Number) jsonObject.get("status");
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
                // 先隐藏键盘
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(this
                                        .getCurrentFocus()
                                        .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                finish();
                break;
            case R.id.submit:
                if (compConn.getText().toString().trim().equals(""))
                    Toast.makeText(this, "请填写造成污染破坏的原因！", Toast.LENGTH_SHORT).show();
                else
                    submit();
                break;
            case R.id.img_rl:
                BottomDialog bottomDialog = new BottomDialog(this);
                bottomDialog.show();
                break;
            default:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String picPath = null;
        if (requestCode == 1) {// 相册
            if (data != null) {
                Uri uri = data.getData();
                String schemeStr = data.getScheme().toString();
                if (schemeStr.compareTo("file") == 0) {
                    picPath = uri.toString();
                    picPath = picPath.replace("file://", "");
                } else if (schemeStr.compareTo("content") == 0) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int columnIndex = cursor
                                .getColumnIndex(filePathColumn[0]);
                        picPath = cursor.getString(columnIndex);
                        cursor.close();

                    }
                }
                PhotoInfo photoInfo = new PhotoInfo();
                photoInfo.originalUrl = picPath;
                photoInfo.thumbnailUrl = picPath;
                imgs.add(photoInfo);
            }
        }
        if (requestCode == 2) {
            picPath = SmallBitmapUtil.getGetPath();
            if (new File(picPath).exists()) {
                SmallBitmapUtil.getSmallBitmap(picPath);
                PhotoInfo photoInfo = new PhotoInfo();
                photoInfo.originalUrl = picPath;
                photoInfo.thumbnailUrl = picPath;
                imgs.add(photoInfo);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
