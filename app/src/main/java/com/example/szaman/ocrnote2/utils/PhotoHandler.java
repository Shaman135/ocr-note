package com.example.szaman.ocrnote2.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.example.szaman.ocrnote2.R;
import com.google.android.gms.vision.CameraSource;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by szaman on 27.12.17.
 */

public class PhotoHandler implements CameraSource.PictureCallback {

    private final Context context;
    private final Activity activity;

    public PhotoHandler(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onPictureTaken(final byte[] data) {
        final File pictureFileDir = getDir();
        Toast.makeText(context, pictureFileDir.toString(), Toast.LENGTH_LONG).show();
        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            Toast.makeText(context, context.getResources().getString(R.string.dir_failure), Toast.LENGTH_LONG).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        final String[] photoFile = {"Picture_" + date};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(context.getResources().getString(R.string.title));

        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(photoFile[0]);
        builder.setView(input);

        builder.setPositiveButton(context.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                photoFile[0] = input.getText().toString() + ".jpg";
                String filename = pictureFileDir.getPath() + File.separator + photoFile[0];
                File pictureFile = new File(filename);

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Toast.makeText(context, context.getResources().getString(R.string.saved) + " " + photoFile[0], Toast.LENGTH_LONG).show();
                } catch (Exception error) {
                    Toast.makeText(context, context.getResources().getString(R.string.save_failure), Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private File getDir(){
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "OCRNote2");
    }

}
