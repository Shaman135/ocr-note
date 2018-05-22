package com.example.szaman.ocrnote2.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.szaman.ocrnote2.FullImageActivity;
import com.example.szaman.ocrnote2.MainActivity;
import com.example.szaman.ocrnote2.R;
import com.example.szaman.ocrnote2.utils.GridViewAdapter;
import com.example.szaman.ocrnote2.utils.ImageItem;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by szaman on 27.12.17.
 */

public class GalleryFragment extends Fragment {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ArrayList<ImageItem> data = new ArrayList<>();
    private static final int READ_EXTERNAL_STORAGE_CODE = 55;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        gridView = view.findViewById(R.id.gridView);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        } else {
            new LoadDataAsyncTask((MainActivity) getActivity()).execute();
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), FullImageActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("path", item.getPath());
                startActivity(intent);
            }
        });

        return view;
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, ArrayList<ImageItem>> {

        private WeakReference<MainActivity> activityReference;
        private ProgressDialog dialog;
        private ArrayList<ImageItem> imageItems = new ArrayList<>();

        public LoadDataAsyncTask(MainActivity context) {
            activityReference = new WeakReference<MainActivity>(context);
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait...");
            this.dialog.show();
        }

        @Override
        protected ArrayList<ImageItem> doInBackground(Void... voids) {
            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/OCRNote2/");
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;  // 1:16
            File[] images = file.listFiles();
            if(images != null){
                for(File item : images){
                    String path = item.getAbsolutePath();
                    String name = item.getName();
                    Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                    ImageItem image = new ImageItem(bitmap, name, path);
                    imageItems.add(image);
                }
            }

            return imageItems;
        }

        @Override
        protected void onPostExecute(ArrayList<ImageItem> result) {
            MainActivity mainActivity = activityReference.get();
            if(mainActivity == null){
                return;
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            data = result;
            gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item, data);
            gridView.setAdapter(gridAdapter);
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 55) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new LoadDataAsyncTask((MainActivity) getActivity()).execute();
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.missing_permission), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_gallery);
    }

}
