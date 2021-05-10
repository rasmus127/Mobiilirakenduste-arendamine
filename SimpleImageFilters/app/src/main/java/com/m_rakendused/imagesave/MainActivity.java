package com.m_rakendused.imagesave;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String currentImagePath = null;
    String selectedImage = null;
    private static final int IMAGE_REQUEST = 1;
    private boolean imageTaken = false;
    private boolean imageSelected = false;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void captureImage(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // ensure that there's a camera activity to handle the intent
        // create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, IMAGE_REQUEST);
        }
    }

    public void openImage(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                // permission not granted, request it
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                // permission already granted
                pickImageFromGallery();
            }
        } else {
            // old system
            pickImageFromGallery();
        }
    }

    public void displayImage(View view) {
        if (!imageTaken && !imageSelected) {
            Toast.makeText(this, "No images selected or taken!", Toast.LENGTH_SHORT).show();
            return;
        }
        // creates a new intent with extras and starts the new activity
        Intent intent = new Intent(this, DisplayImage.class);
        if (imageTaken)
            intent.putExtra("image_path", currentImagePath);
        if (imageSelected)
            intent.putExtra("selected_image", selectedImage);
        startActivity(intent);
    }

    private File createImageFile() throws IOException {
        // creates an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageName, ".jpg", storageDir);

        // saves a file: path for use with ACTION_VIEW intents
        currentImagePath = image.getAbsolutePath();
        return image;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    // following method is from https://stackoverflow.com/questions/36336498/get-real-path-from-android-uri-after-selecting-image-from-gallery
    private String getRealPathFromURI(Uri contentURI) {
        String thePath = "no-path-found";
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentURI, filePathColumn, null, null, null);
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            thePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return  thePath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if image captured
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageTaken = true;
            imageSelected = false;
            Toast.makeText(this, "Image taken.", Toast.LENGTH_SHORT).show();
        // if image selected
        } else if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageSelected = true;
            imageTaken = false;
            selectedImage = getRealPathFromURI(data.getData());
            Toast.makeText(this, "Image imported." + selectedImage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void openGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
        startActivity(intent);
    }
}