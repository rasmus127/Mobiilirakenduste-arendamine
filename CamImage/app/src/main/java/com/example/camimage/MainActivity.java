package com.example.camimage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    // initialize variables
    ImageView image;
    Button camButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign variables
        image = findViewById(R.id.iv_image);
        camButton = findViewById(R.id.btn_camera);

        // check permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA }, 100);
        }

        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }

        });
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            // get image
            Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
            Bitmap resultImage = Bitmap.createBitmap(capturedImage.getWidth(), capturedImage.getHeight(), capturedImage.getConfig());
            // image to greyscale
            for (int x = 0; x < capturedImage.getWidth(); x++) {
                for (int y = 0; y < capturedImage.getHeight(); y++) {
                    int currentPixel = capturedImage.getPixel(x, y);
                    int greyScaleValue = (Color.red(currentPixel) + Color.green(currentPixel) + Color.red(currentPixel)) / 3;
                    resultImage.setPixel(x, y, Color.rgb(greyScaleValue, greyScaleValue, greyScaleValue));
                }
            }
            // set image
            image.setImageBitmap(resultImage);
        }
    }
}