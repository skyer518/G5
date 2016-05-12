package cn.com.lightech.led_g5w.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alek on 2016/5/5.
 */
public class ImageUtil {
    /**
     * 从相册中读取图片
     *
     * @param fileName
     * @return
     */
    public static Bitmap readBitmapFormDirectoryPictures(String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
        if (file.exists() && file.isFile()) {
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(file);
                return BitmapFactory.decodeStream(stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }


    /**
     * Save Bitmap to a file.保存图片到SD卡。
     *
     * @param bitmap
     * @param fileName
     * @return error message if the saving is failed. null if the saving is
     * successful.
     * @throws IOException
     */
    public static void saveBitmapToFile(Bitmap bitmap, String fileName)
            throws IOException {
        BufferedOutputStream os = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
            if (!file.exists())
                file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
