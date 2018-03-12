package com.example.hcz.hechangzhi;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.hcz.util.ActiivtyStack;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.BottomDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.example.hcz.util.SmallBitmapUtil;
import com.facebook.drawee.view.SimpleDraweeView;
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

/**
 * 河长资料
 */
public class UserdataActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name;
    private ImageView back;
    private RelativeLayout uplod_img;
    private SimpleDraweeView userImg;
    private TextView chiefName, loginName_tv, chiefType, depPhone, phone, superior;
    private String avatarUrl, schiefName, sloginName_tv, schiefType, sdepPhone, sphone, ssuperior;
    private String loginName, userInfo;
    private BaseDialog dialog;
    private File outputImage = new File("/sdcard/LianTian/user_image.jpg");
    private Uri imageUri = Uri.fromFile(outputImage);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiivtyStack.getScreenManager().pushActivity(this);
        setContentView(R.layout.activity_userdata);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("河长资料");
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        uplod_img = (RelativeLayout) findViewById(R.id.uplod_img);
        uplod_img.setOnClickListener(this);
        userImg = (SimpleDraweeView) findViewById(R.id.userImg);
        chiefName = (TextView) findViewById(R.id.chiefName);
        loginName_tv = (TextView) findViewById(R.id.loginName);
        chiefType = (TextView) findViewById(R.id.chiefType);
        depPhone = (TextView) findViewById(R.id.depPhone);
        phone = (TextView) findViewById(R.id.phone);
        superior = (TextView) findViewById(R.id.superior);
        loginName = getIntent().getExtras().getString("loginName");
        userInfo = getIntent().getExtras().getString("userInfo");
        dialog = new BaseDialog(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            if (msg.what == 1) {
                userImg.setImageURI(avatarUrl);
                chiefName.setText(schiefName);
                loginName_tv.setText(sloginName_tv);
                chiefType.setText(schiefType);
                depPhone.setText(sdepPhone);
                phone.setText(sphone);
                superior.setText(ssuperior);
            } else if (msg.what == 10) {
                initData();
            } else if (msg.what == -1) {
                Toast.makeText(UserdataActivity.this, "请求失败！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                FinishLoginDialog dialog1 = new FinishLoginDialog(UserdataActivity.this, R.style.MyDialog);
                dialog1.show();
                dialog1.NOnclicke();
                dialog1.YOnclick();
            } else {

            }
        }
    };

    //绑定数据
    private void initData() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://114.55.235.16:7074/app/1/riverChief/personal.do?loginName=" + loginName
                        + "&userInfo=" + userInfo).build();
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
                        avatarUrl = "http://114.55.235.16:7074" + value.getString("avatarUrl");
                        schiefName = value.getString("chiefName");
                        sloginName_tv = value.getString("loginName");
                        schiefType = value.getString("chiefType");
                        sdepPhone = value.getString("depPhone");
                        sphone = value.getString("phone");
                        ssuperior = value.getString("superior");
                    }
                    handler.sendEmptyMessage((int) status);
                } else {
                    handler.sendEmptyMessage(-1);
                }
            }
        });
    }

    //上传图片
    private void uplodImg(String pic) {
        dialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), new File(pic));
        builder.addPart(Headers.of(
                "Content-Disposition",
                "form-data; name=\"imgs\";filename=\"" + pic + "\""), fileBody);
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder().url("http://114.55.235.16:7074/app/1/riverChief/updateAvatar.do?loginName=" + loginName
                + "&userInfo=" + userInfo).post(requestBody).build();
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
                    if ((int) status == 1)
                        handler.sendEmptyMessage(10);
                } else {
                    handler.sendEmptyMessage(-1);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.uplod_img:
                BottomDialog bottomDialog = new BottomDialog(this);
                bottomDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String picPath = null;
        File lt = new File("/sdcard/LianTian/");
        if (!lt.exists()) {
            Log.e("TAG", "第一次创建文件夹");
            lt.mkdirs();// 如果文件夹不存在，则创建文件夹
        }
        if (requestCode == 1) {// 相册
            if (data != null) {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(data.getData(), "image/*");
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 10);
            }
        }
        if (requestCode == 10) {
            if (data != null) {
                picPath = imageUri.toString();
                picPath = picPath.replace("file://", "");
                uplodImg(picPath);
                UserdataActivity.this.setResult(1);
            }
        }
        if (requestCode == 2) {
            picPath = SmallBitmapUtil.getGetPath();
            File file = new File(picPath);
            if (file.exists()) {
                SmallBitmapUtil.getSmallBitmap(picPath);
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(BottomDialog.uri, "image/*");
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 10);
            }
        }
    }

    @Override
    protected void onDestroy() {
        // 退出时弹出stack
        ActiivtyStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }
}
