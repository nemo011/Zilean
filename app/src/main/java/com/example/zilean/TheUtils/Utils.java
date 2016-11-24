package com.example.zilean.TheUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 王跃_ on 2016/11/21.
 */

public class Utils {
    /**
     * 创建所有上级文件夹
     *
     * @param file 当前文件
     */
    public static void mkdirsFile(File... file) {
        for (File f : file) {
            File parent = f.getParentFile();
            if (!parent.exists()) parent.mkdirs();
        }
    }

    /**
     * 删除文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.exists()) file.delete();
    }

    /**
     * Bitmap保存到文件
     *
     * @param bitmap
     * @param file
     * @throws IOException
     */
    public static void saveBitmap2PNG(Bitmap bitmap, File file) throws IOException {
        mkdirsFile(file);
        deleteFile(file);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件移动
     *
     * @param temp
     * @param file
     */
    public static void file2File(File temp, File file) {
        if (temp.exists()) {
            mkdirsFile(file);
            deleteFile(file);
            byte[] bytes = new byte[1024];
            int i = -1;
            try {
                FileInputStream fis = new FileInputStream(temp);
                FileOutputStream fos = new FileOutputStream(file);
                while ((i = fis.read(bytes)) > 0) fos.write(bytes, 0, i);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            temp.delete();
        }
    }

    /**
     * 显示Log
     *
     * @param str1   信息1
     * @param str2   信息2
     * @param isShow 是否显示
     */
    public static void showLog(String str1, String str2, boolean isShow) {
        if (isShow) {
            Log.e(str1, str2);
        }
    }

    public static int gcd(int a, int b) {
        if (a == 0 || b == 0) return 1;
        if (a % b == 0) return b;
        else return gcd(b, a % b);
    }

    private static Toast toast;

    public static Toast getToast(Context context, String str) {
        toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        return toast;
    }
}
