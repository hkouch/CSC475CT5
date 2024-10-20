package com.example.ct5o1;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.os.Environment.MEDIA_MOUNTED;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static int PERMISSION_REQUEST_CODE = 100;
    RecyclerView recycler;
    ArrayList<String> images;
    GalleryAdapter adapter;
    GridLayoutManager manager;

    TextView totalimages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.gallery_recycler);
        images = new ArrayList<>();
        adapter = new GalleryAdapter(this, images);
        manager = new GridLayoutManager(this, 3);
        totalimages = findViewById(R.id.gallery_total_images);

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(manager);

        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (Scoped storage)
            if (Environment.isExternalStorageManager()) {
                loadImages();  // Permission granted, proceed to load images
            } else {
                // Request permission to manage all files
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6 - 10 (request permission for storage)
            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                loadImages();  // Permission already granted, load images
            } else {
                ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            loadImages();  // Android 5 and below, no runtime permissions required
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImages();  // Permission granted, load images
            } else {
                Toast.makeText(this, "Permission Denied. Unable to load images.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    loadImages();  // Permission granted, load images
                } else {
                    Toast.makeText(this, "Permission Denied. Unable to load images.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadImages() {
        boolean SDCard = Environment.getExternalStorageState().equals(MEDIA_MOUNTED);
        if (SDCard) {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String order = MediaStore.Images.Media.DATE_TAKEN + " DESC";

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, order);

            if (cursor != null && cursor.getCount() > 0) {
                int count = cursor.getCount();
                totalimages.setText("Total items: " + count);

                for (int i = 0; i < count; i++) {
                    cursor.moveToPosition(i);
                    int columnindex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    String imagePath = cursor.getString(columnindex);
                    images.add(imagePath);
                    Log.d("ImagePath", imagePath);  // Log image path
                }

                recycler.getAdapter().notifyDataSetChanged();
                cursor.close();
            } else {
                totalimages.setText("No images found");
                Toast.makeText(this, "No images available on the device", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "SD card not mounted", Toast.LENGTH_SHORT).show();
        }
    }
}