package com.example.szaman.ocrnote2.utils;

import android.graphics.Bitmap;

/**
 * Created by szaman on 27.12.17.
 */

public class ImageItem {

    private Bitmap image;
    private String title;
    private String path;

    public ImageItem(Bitmap image, String title, String path) {
        this.image = image;
        this.title = title;
        this.path = path;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
