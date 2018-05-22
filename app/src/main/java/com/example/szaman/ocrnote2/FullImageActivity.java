package com.example.szaman.ocrnote2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class FullImageActivity extends Activity {

    private ImageView imageView;
    private DisplayMetrics metrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        Intent intent = getIntent();
        String path = intent.getStringExtra("path");

        imageView = findViewById(R.id.imageView);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int targetW = metrics.widthPixels;
        int targetH = metrics.heightPixels;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        BitmapFactory.decodeFile(path);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        bmOptions.inSampleSize = Math.min(photoW/targetW, photoH/targetH);
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        imageView.setImageBitmap(bitmap);

    }
}
