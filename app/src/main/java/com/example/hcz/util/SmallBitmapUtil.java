package com.example.hcz.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2017/12/12.
 */

public class SmallBitmapUtil {
    public static String getPath;

    public static String getGetPath() {
        return getPath;
    }

    public static String getPhotopath() {
        DateFormat df = new DateFormat();
        String name = df.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA))
                + ".png";
        String fileName = "/sdcard/LianTian/" + name;
        getPath = fileName;
        File file = new File("/sdcard/LianTian/");
        if (!file.exists()) {
            Log.e("TAG", "第一次创建文件夹");
            file.mkdirs();// 如果文件夹不存在，则创建文件夹
        }
        return fileName;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//设置为ture,只读取图片的大小，不把它加载到内存中去
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;//处理完后，同时需要记得设置为false
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        return compressImage(bitmap, filePath);
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // 质量压缩法：
    public static Bitmap compressImage(Bitmap image, String filepath) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 2048) {
                baos.reset();//重置baos即清空baos
                options = options - 5;//每次都减少10
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            }
            //压缩好后写入文件中
            FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
