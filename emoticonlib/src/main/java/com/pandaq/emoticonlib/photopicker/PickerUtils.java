package com.pandaq.emoticonlib.photopicker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.Toast;

import com.pandaq.emoticonlib.PandaEmoManager;
import com.pandaq.emoticonlib.utils.EmoticonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by huxinyu on 2017/11/15 0015.
 * description ：默认 PhotoPicker 的工具类
 */

class PickerUtils {

    /**
     * 文件存储默认名称为：文件顺序+_+MD5（传入图片路径）.jpeg
     */
    static String compressAndCopyToSd(String imagePath, String savePath) {
        FileOutputStream fos;
        try {
            File[] files = new File(savePath).listFiles();
            if (files == null) return null;
            if (files.length == PandaEmoManager.getInstance().getMaxCustomSticker()) {
                Toast.makeText(PandaEmoManager.getInstance().getContext(), "表情已达上限，无法添加", Toast.LENGTH_SHORT).show();
                return null;
            }
            String filename = files.length + "_" + EmoticonUtils.getMD5Result(imagePath);
            for (File file : files) {
                String[] strs = file.getName().split("_");
                if (strs.length >= 1 && strs[1].equals(EmoticonUtils.getMD5Result(imagePath))) {
                    Toast.makeText(PandaEmoManager.getInstance().getContext(), "已经添加过此表情", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }
            fos = new FileOutputStream(savePath + File.separator + filename);
            // 缩放图片，将图片大小降低
            Bitmap bitmap = getZoomImage(BitmapFactory.decodeFile(imagePath), 400);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            fos.close();
            return savePath + File.separator + filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 图片的缩放方法
     *
     * @param orgBitmap ：源图片资源
     */
    private static Bitmap getZoomImage(Bitmap orgBitmap, float maxSize) {
        if (null == orgBitmap) {
            return null;
        }
        if (orgBitmap.isRecycled()) {
            return null;
        }
        // 获取图片的宽和高
        float width = orgBitmap.getWidth();
        float height = orgBitmap.getHeight();
        float max = Math.max(width, height);
        float scale;
        if (max < maxSize) {
            scale = 1;
        } else {
            scale = maxSize / max;
        }
        // 创建操作图片的matrix对象
        Matrix matrix = new Matrix();
        // 缩放图片动作
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(orgBitmap, 0, 0, (int) width, (int) height, matrix, true);
    }

}
