package com.jia.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2016/12/6.
 */
public class CreateImage {
    private static int   IMAGE_MAX_WIDTH  = 480;
    private static int   IMAGE_MAX_HEIGHT = 960;
    public static final String URL = "data/data/com.jia.znjj2/databases/";
    public static void createImage(String accountCode, byte[] bytes, String imageName){
        String urldir = URL + accountCode + "/";
        try {
            File cacheDir = new File(urldir);//设置目录参数
            cacheDir.mkdirs();//新建目录
            Log.i("copySd2phone","新建data/data目录成功");
            //文件名
            File cacheFile = new File(cacheDir,imageName);//设置参数
            cacheFile.createNewFile();//生成文件
            Log.i("createNewFile","生成文件成功"+cacheFile.getName());
            OutputStream out = new FileOutputStream(urldir+imageName);
            out.write(bytes);
            out.flush();
            out.close();
        }catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = getImageScale(url);
            return BitmapFactory.decodeFile(url, option);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int getImageScale(String imagePath) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        // set inJustDecodeBounds to true, allowing the caller to query the bitmap info without having to allocate the
        // memory for its pixels.
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, option);

        int scale = 1;
        while (option.outWidth / scale >= IMAGE_MAX_WIDTH || option.outHeight / scale >= IMAGE_MAX_HEIGHT) {
            scale *= 2;
        }
        return scale;
    }

}
