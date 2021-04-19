package com.m_rakendused.imagesave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

public class DisplayImage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageView imageView;
    // save prefs
    private String image_type_index = "";
    private Spinner spinnerImage;
    // save prefs
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SELECTED_IMAGE = "image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        imageView = findViewById(R.id.mimageView);

        spinnerImage = findViewById(R.id.spinner);
        spinnerImage.setOnItemSelectedListener(this);
        String[] imageType = getResources().getStringArray(R.array.image_type);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, imageType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImage.setAdapter(adapter);

        displayImage();
    }

    private void displayImage() {
        image_type_index = loadData();
        Log.d("myTag", image_type_index);
        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*0.3f), (int)(bitmap.getHeight()*0.3f), true);
        Bitmap resultBitmap = bitmap;
        if (image_type_index.equals("Negative")) {
            resultBitmap = imageToNegative(resizedBitmap);
            spinnerImage.setSelection(3);
        } else if (image_type_index.equals("Greyscale")) {
            resultBitmap = imageToGreyscale(resizedBitmap);
            spinnerImage.setSelection(1);
        } else if (image_type_index.equals("Sepia")) {
            resultBitmap = imageToSepia(resizedBitmap);
            spinnerImage.setSelection(2);
        } else {
            spinnerImage.setSelection(0);
        }
        imageView.setImageBitmap(resultBitmap);
    }

    private Bitmap imageToGreyscale(Bitmap img) {
        Bitmap resultImg = Bitmap.createBitmap(img.getWidth(), img.getHeight(), img.getConfig());
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int currentPixel = img.getPixel(x, y);
                int greyScaleValue = (Color.red(currentPixel) + Color.green(currentPixel) + Color.red(currentPixel)) / 3;
                resultImg.setPixel(x, y, Color.rgb(greyScaleValue, greyScaleValue, greyScaleValue));
            }
        }
        return resultImg;
    }

    private Bitmap imageToSepia(Bitmap img) {
        Bitmap resultImg = Bitmap.createBitmap(img.getWidth(), img.getHeight(), img.getConfig());
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int currentPixel = img.getPixel(x, y);
                int redValue = (int)(Color.red(currentPixel) * 0.393f + Color.green(currentPixel) * 0.769f + Color.blue(currentPixel) * 0.189f);
                int greenValue = (int)(Color.red(currentPixel) * 0.349f + Color.green(currentPixel) * 0.686f + Color.blue(currentPixel) * 0.168f);
                int blueValue = (int)(Color.red(currentPixel) * 0.272f + Color.green(currentPixel) * 0.534f + Color.blue(currentPixel) * 0.131f);
                if (redValue > 255) { redValue = 255; }
                if (greenValue > 255) { greenValue = 255; }
                if (blueValue > 255) { blueValue = 255; }
                resultImg.setPixel(x, y, Color.rgb(redValue, greenValue, blueValue));
            }
        }
        return resultImg;
    }
    private Bitmap imageToNegative(Bitmap img) {
        Bitmap resultImg = Bitmap.createBitmap(img.getWidth(), img.getHeight(), img.getConfig());
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int currentPixel = img.getPixel(x, y);
                int redValue = 255 - Color.red(currentPixel);
                int greenValue = 255 - Color.green(currentPixel);
                int blueValue = 255 - Color.blue(currentPixel);
                resultImg.setPixel(x, y, Color.rgb(redValue, greenValue, blueValue));
            }
        }
        return resultImg;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner) {
            //Log.d("myTag", parent.getItemAtPosition(position).toString());
            saveData(parent.getItemAtPosition(position).toString());
            displayImage();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void saveData(String textToSave) {
        //Log.d("myTag", textToSave);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SELECTED_IMAGE, textToSave);
        editor.apply();
    }

    public String loadData() {
        String result = "Color";
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        result = sharedPreferences.getString(SELECTED_IMAGE, "Empty");
        return result;
    }
}