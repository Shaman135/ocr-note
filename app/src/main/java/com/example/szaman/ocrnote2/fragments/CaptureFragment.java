package com.example.szaman.ocrnote2.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szaman.ocrnote2.NoteAddActivity;
import com.example.szaman.ocrnote2.R;
import com.example.szaman.ocrnote2.utils.PhotoHandler;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

/**
 * Created by szaman on 27.12.17.
 */

public class CaptureFragment extends Fragment {

    private SurfaceView surfaceView;
    private TextView textView;
    private CameraSource cameraSource;
    private FloatingActionButton add;
    private FloatingActionButton capture;
    private DisplayMetrics metrics = new DisplayMetrics();
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 112;
    private static final int CAMERA_CODE = 50;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_capture, container, false);

        surfaceView = view.findViewById(R.id.surface_view);
        textView = view.findViewById(R.id.text_value);
        add = view.findViewById(R.id.add_note);
        capture = view.findViewById(R.id.capture_picture);

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels - getStatusBarHeight();
        int width = metrics.widthPixels;

        TextRecognizer textRecognizer = new TextRecognizer.Builder(view.getContext()).build();
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
                textView.setText("");
            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if (items.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder value = new StringBuilder();
                            for (int i = 0; i < items.size(); i++) {
                                TextBlock item = items.valueAt(i);
                                value.append(item.getValue());
                                value.append("\n");
                            }
                            textView.setText(value.toString());
                        }
                    });
                }
            }
        });

        if (!textRecognizer.isOperational()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_recognizer_not_operational), Toast.LENGTH_SHORT).show();
        } else {
            cameraSource = new CameraSource.Builder(view.getContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(width, height)
                    .setRequestedFps(30.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            surfaceView.getHolder().addCallback(callback);
        }

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_CODE);
                } else {
                    if (cameraSource != null) {
                        cameraSource.takePicture(null, new PhotoHandler(getActivity().getApplicationContext(), getActivity()));
                    }
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NoteAddActivity.class);
                intent.putExtra("text", textView.getText());
                startActivity(intent);
            }
        });


        return view;
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
                } else {
                    cameraSource.start(surfaceView.getHolder());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (cameraSource != null) {
                cameraSource.stop();
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.missing_permission), Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == WRITE_EXTERNAL_STORAGE_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (cameraSource != null) {
                    cameraSource.takePicture(null, new PhotoHandler(getActivity().getApplicationContext(), getActivity()));
                }
            }
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_camera);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(cameraSource != null){
            cameraSource.release();
        }
    }

}
