package com.example.hcz.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;

import java.io.File;

/**
 * Created by Administrator on 2017/12/12.
 */
public class BottomDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView takePhoto, choosePhoto, qx;

    public BottomDialog(Context context) {
        super(context, R.style.ActionSheetDialogStyle);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bottom);//loading的xml文件
        takePhoto = findViewById(R.id.takePhoto);
        choosePhoto = findViewById(R.id.choosePhoto);
        qx = findViewById(R.id.qx);
        takePhoto.setOnClickListener(this);
        choosePhoto.setOnClickListener(this);
        qx.setOnClickListener(this);
        getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.y = 20;
        getWindow().setAttributes(lp);
    }

    public static Uri uri;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhoto:
                Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File out = new File(SmallBitmapUtil.getPhotopath());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, out.getAbsolutePath());
                    uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                } else {
                    uri = Uri.fromFile(out);
                }
                photo.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                ((Activity) context).startActivityForResult(photo, 2);
                dismiss();
                break;
            case R.id.choosePhoto:
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                ((Activity) context).startActivityForResult(intent, 1);
                dismiss();
                break;
            case R.id.qx:
                dismiss();
                break;
            default:
                break;
        }
    }
}
