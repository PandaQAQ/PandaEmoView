package com.pandaq.pandaemoview.photomodule;

import java.util.ArrayList;

/**
 * Created by huxinyu on 2017/11/9 0009.
 * description ：
 */

public class ImageFileBean {
    private String topImage;

    public String getTopImage() {
        return topImage;
    }

    public void setTopImage(String topImage) {
        this.topImage = topImage;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private ArrayList<String> images;
    private String fileName;
}
