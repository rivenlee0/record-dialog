package com.example.rivenlee.recorddialog.utils;

import android.os.Environment;
import android.text.TextUtils;

import com.example.rivenlee.recorddialog.RecodeApp;

import java.io.File;

/**
 * author: rivenlee
 * date: 2018/11/1
 * email: rivenlee0@gmail.com
 */
public class FileUtils {
    private static String filePath;
    private static File cacheDir = !isExternalStorageWritable()? RecodeApp.getInstance()
            .getFilesDir(): RecodeApp.getInstance().getExternalCacheDir();

    public static boolean deleteFile(String filename) {
        if (TextUtils.isEmpty(filename)){
            return false;
        }
        File file = new File(filename);
        return  file.exists() ? file.delete() : false;
    }

    /**
     * 获取缓存文件地址
     */
    public static String getCacheFilePath(String fileName){
        filePath = cacheDir.getAbsolutePath() + "/record/" + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    public static String getFilePath(){
        return filePath;
    }

    /**
     * 判断外部存储是否可用
     *
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
