package com.example.hcz.hechangzhi;

import android.app.Dialog;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hcz.util.BaseDialog;
import com.example.hcz.util.FinishLoginDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import java.io.File;
import java.text.DecimalFormat;

public class CacheActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title_name, availableMemory, photo_Memory, file_Memory;
    private ImageView back;
    private RelativeLayout availableMemory_rl, photo_Memory_rl, file_Memory_rl;
    private BaseDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        init();
        initData();
    }

    //绑定控件
    private void init() {
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("清除缓存");
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        availableMemory = (TextView) findViewById(R.id.availableMemory);
        availableMemory_rl = (RelativeLayout) findViewById(R.id.availableMemory_rl);
        availableMemory_rl.setOnClickListener(this);
        photo_Memory_rl = (RelativeLayout) findViewById(R.id.photo_Memory_rl);
        photo_Memory_rl.setOnClickListener(this);
        file_Memory_rl = (RelativeLayout) findViewById(R.id.file_Memory_rl);
        file_Memory_rl.setOnClickListener(this);
        photo_Memory = (TextView) findViewById(R.id.photo_Memory);
        file_Memory = (TextView) findViewById(R.id.file_Memory);
        dialog = new BaseDialog(this);
    }

    //绑定数据
    private void initData() {
        // 获得sd卡的内存状态
        File sdcardFileDir = Environment.getExternalStorageDirectory();
        // 获得一个磁盘状态对象
        StatFs stat = new StatFs(sdcardFileDir.getPath());
        long blockSize = stat.getBlockSize();   // 获得一个扇区的大小
        long availableBlocks = stat.getAvailableBlocks();   // 获得可用的扇区数量
        availableMemory.setText(Formatter.formatFileSize(this, availableBlocks * blockSize));


        long cacheSize = Fresco.getImagePipelineFactory().getMainFileCache().getSize();
        photo_Memory.setText(FormetFileSize(cacheSize));


        File file = new File("/sdcard/LianTian/");
        if (!file.exists()) {
            Log.e("TAG", "第一次创建文件夹");
            file.mkdirs();// 如果文件夹不存在，则创建文件夹
        }
        file_Memory.setText(FormetFileSize(getFolderSize(file)));
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long 单位为M
     * @throws Exception
     */
    private long getFolderSize(java.io.File file) {
        long size = 0;
        java.io.File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }
        return size;
    }

    private String FormetFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS == 0) {
            fileSizeString = "0.0KB";
        } else if (fileS == -1) {
            fileSizeString = "0.0KB";
        } else if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @return
     */
    public void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.isDirectory()) {// 处理目录
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFolderFile(files[i].getAbsolutePath(), true);
                }
            }
            if (deleteThisPath) {
                if (!file.isDirectory()) {// 如果是文件，删除
                    file.delete();
                } else {// 目录
                    if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                        file.delete();
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.photo_Memory_rl:
                final Dialog photo = new FinishLoginDialog(CacheActivity.this,
                        R.style.MyDialog, "确认要清空图片缓存吗？");
                photo.show();
                TextView photo_no = photo.findViewById(R.id.btn_finish_login_no);
                TextView photo_yes = photo.findViewById(R.id.btn_finish_login_yes);
                photo_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photo.dismiss();
                    }
                });
                photo_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.show();
                        Fresco.getImagePipeline().clearCaches();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                initData();
                                dialog.dismiss();
                                Toast.makeText(CacheActivity.this, "清除成功！", Toast.LENGTH_SHORT).show();
                            }
                        }, 2000);
                        photo.dismiss();
                    }
                });
                break;
            case R.id.file_Memory_rl:
                final Dialog file = new FinishLoginDialog(CacheActivity.this,
                        R.style.MyDialog, "确认要清空文件缓存吗？");
                file.show();
                TextView no = file.findViewById(R.id.btn_finish_login_no);
                TextView yes = file.findViewById(R.id.btn_finish_login_yes);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        file.dismiss();
                    }
                });
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.show();
                        deleteFolderFile("/sdcard/LianTian/", true);
                        initData();
                        dialog.dismiss();
                        Toast.makeText(CacheActivity.this, "清除成功！", Toast.LENGTH_SHORT).show();
                        file.dismiss();
                    }
                });
                break;
            case R.id.availableMemory_rl:
                break;
            default:
                break;
        }
    }
}
