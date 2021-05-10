package com.m_rakendused.imagesave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class DisplayImage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageView imageView;
    Button saveBitmapButton;
    // save prefs
    private String image_type_index = "";
    private Spinner spinnerImage;
    // save prefs
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SELECTED_IMAGE = "image";
    // filters
    Matrix gaussianBlur3x3 = new Matrix(3, 3);
    Matrix gaussianBlur7x7 = new Matrix(7, 7);
    Matrix motionBlur = new Matrix(7, 7);
    Matrix sharpen = new Matrix(3, 3);
    Matrix edges = new Matrix(3, 3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // setting filters
        gaussianBlur3x3.data = new int[][] { { 1, 2, 1}, { 2, 4, 2}, {1, 2, 1} };
        sharpen.data = new int[][] { { -1, -1, -1}, { -1, 9, -1}, {-1, -1, -1} };
        edges.data = new int[][] { { -1, -1, -1}, { -1, 8, -1}, {-1, -1, -1} };
        gaussianBlur7x7.data = new int[][] { { 1, 6, 15, 20, 15, 6, 1 }, { 6, 36, 90, 120, 90, 36, 6 }, { 15, 90, 225, 300, 225, 90, 15 }, { 20, 120, 300, 400, 300, 120, 20 }, { 15, 90, 225, 300, 225, 90, 15 }, { 6, 36, 90, 120, 90, 36, 6 }, { 1, 6, 15, 20, 15, 6, 1 } };
        motionBlur.data = new int[][] { { 1, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0, 0, 0 }, { 0, 0, 1, 0, 0, 0, 0 }, { 0, 0, 0, 1, 0, 0, 0 }, { 0, 0, 0, 0, 1, 0, 0 }, { 0, 0, 0, 0, 0, 1, 0 }, { 0, 0, 0, 0, 0, 0, 1 } };

        // setting up buttons, images and spinners
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        imageView = findViewById(R.id.mimageView);

        saveBitmapButton = findViewById(R.id.button4);
        saveBitmapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tryToSave();
            }
        });

        spinnerImage = findViewById(R.id.spinner);
        spinnerImage.setOnItemSelectedListener(this);
        String[] imageType = getResources().getStringArray(R.array.image_type);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, imageType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerImage.setAdapter(adapter);

        displayImage();
    }

    private void tryToSave () {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());
            saveBitmap(bitmap, "Image_" + currentDateandTime);
            Toast.makeText(this, "Image saved.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Saving failed!. " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayImage() {
        image_type_index = loadData();
        Log.d("myTag", image_type_index);
        Bitmap bitmap = null;
        // getting image from extras
        if (getIntent().hasExtra("image_path")) {
            bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        } else if (getIntent().hasExtra("selected_image")) {
            bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("selected_image"));
        }
        // making image smaller if needed
        double factor = 1;
        if (bitmap.getWidth() - 727 > 0)
            factor = (3648 - 727) * 0.16 / (bitmap.getWidth() - 727);
        if (factor > 1)
            factor = 1;
        Log.d("myTag", String.valueOf(bitmap.getWidth()));
        Log.d("myTag", String.valueOf(factor));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*factor), (int)(bitmap.getHeight()*factor), true);

        // applying filters
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
        } else if (image_type_index.equals("Gaussian Blur")) {
            resultBitmap = applyGaussianBlur(resizedBitmap);
            spinnerImage.setSelection(4);
        } else if (image_type_index.equals("Motion Blur")) {
            resultBitmap = applyMotionBlur(resizedBitmap);
            spinnerImage.setSelection(5);
        } else if (image_type_index.equals("Sharpen")) {
            resultBitmap = applySharpen(resizedBitmap);
            spinnerImage.setSelection(6);
        } else if (image_type_index.equals("Canny Edges")) {
            resultBitmap = applyCanny(resizedBitmap);
            spinnerImage.setSelection(7);
        } else {
            spinnerImage.setSelection(0);
        }
        imageView.setImageBitmap(resultBitmap);
    }

    private Matrix imageToMatrix (Bitmap image, int channel)
    {
        Matrix result = new Matrix(image.getHeight(), image.getWidth());
        int newValue = 0;

        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                int currentPixel = image.getPixel(x, y);
                if (channel == 0) {
                    newValue = Color.red(currentPixel);
                } else if (channel == 1) {
                    newValue = Color.green(currentPixel);
                } else {
                    newValue = Color.blue(currentPixel);
                }
                result.data[y][x] = newValue;
            }
        }

        return result;
    }

    private Bitmap matrixToImage (Matrix matrixRed, Matrix matrixGreen, Matrix matrixBlue)
    {
        Bitmap result = Bitmap.createBitmap(matrixRed.columns, matrixRed.rows, Bitmap.Config.ARGB_8888);

        for (int y = 0; y < result.getHeight(); y++)
        {
            for (int x = 0; x < result.getWidth(); x++)
            {
                if (matrixRed.data[y][x] > 255)
                    matrixRed.data[y][x] = 255;
                else if (matrixRed.data[y][x] < 0)
                    matrixRed.data[y][x] = 0;
                if (matrixGreen.data[y][x] > 255)
                    matrixGreen.data[y][x] = 255;
                else if (matrixGreen.data[y][x] < 0)
                    matrixGreen.data[y][x] = 0;
                if (matrixBlue.data[y][x] > 255)
                    matrixBlue.data[y][x] = 255;
                else if (matrixBlue.data[y][x] < 0)
                    matrixBlue.data[y][x] = 0;

                result.setPixel(x, y, Color.rgb(matrixRed.data[y][x], matrixGreen.data[y][x], matrixBlue.data[y][x]));
            }
        }

        return result;
    }

    // to do: get filter's width/height and create pixelsToView matrix accordingly/automatically
    private Matrix applyFilter(Matrix matrix, Matrix filter) {
        Matrix result = new Matrix(matrix.rows-1, matrix.columns-1);

        if (filter.columns == 3) {
            for (int y = 1; y < matrix.rows-1; y++)
            {
                for (int x = 1; x < matrix.columns - 1; x++)
                {
                    Matrix pixelsToView = new Matrix(3, 3);
                    pixelsToView.data = new int[][] {
                            { matrix.data[y - 1][x - 1], matrix.data[y - 1][x], matrix.data[y - 1][x + 1] },
                            { matrix.data[y][x - 1], matrix.data[y][x], matrix.data[y][x + 1] },
                            { matrix.data[y + 1][x - 1], matrix.data[y + 1][x], matrix.data[y + 1][x + 1] }
                    };
                    pixelsToView.Multiply(filter);
                    result.data[y][x] = (int)(pixelsToView.MatrixSum() / filter.MatrixSum());
                }
            }
        } else if (filter.columns == 7) {
            for (int y = 3; y < matrix.rows-3; y++)
            {
                for (int x = 3; x < matrix.columns - 3; x++)
                {
                    Matrix pixelsToView = new Matrix(5, 5);
                    pixelsToView.data = new int[][] {
                            { matrix.data[y - 3][x - 3], matrix.data[y - 3][x - 2], matrix.data[y - 3][x - 1], matrix.data[y - 3][x], matrix.data[y - 3][x + 1], matrix.data[y - 3][x + 2], matrix.data[y - 3][x + 3] },
                            { matrix.data[y - 2][x - 3], matrix.data[y - 2][x - 2], matrix.data[y - 2][x - 1], matrix.data[y - 2][x], matrix.data[y - 2][x + 1], matrix.data[y - 2][x + 2], matrix.data[y - 2][x + 3] },
                            { matrix.data[y - 1][x - 3], matrix.data[y - 1][x - 2], matrix.data[y - 1][x - 1], matrix.data[y - 1][x], matrix.data[y - 1][x + 1], matrix.data[y - 1][x + 2], matrix.data[y - 1][x + 3] },
                            { matrix.data[y][x - 3], matrix.data[y][x - 2], matrix.data[y][x - 1], matrix.data[y][x], matrix.data[y][x + 1], matrix.data[y][x + 2], matrix.data[y][x + 3] },
                            { matrix.data[y + 1][x - 3], matrix.data[y + 1][x - 2], matrix.data[y + 1][x - 1], matrix.data[y + 1][x], matrix.data[y + 1][x + 1], matrix.data[y + 1][x + 2], matrix.data[y + 1][x + 3] },
                            { matrix.data[y + 2][x - 3], matrix.data[y + 2][x - 2], matrix.data[y + 2][x - 1], matrix.data[y + 2][x], matrix.data[y + 2][x + 1], matrix.data[y + 2][x + 2], matrix.data[y + 2][x + 3] },
                            { matrix.data[y + 3][x - 3], matrix.data[y + 3][x - 2], matrix.data[y + 3][x - 1], matrix.data[y + 3][x], matrix.data[y + 3][x + 1], matrix.data[y + 3][x + 2], matrix.data[y + 3][x + 3] }

                    };
                    pixelsToView.Multiply(filter);
                    result.data[y][x] = (int)(pixelsToView.MatrixSum() / filter.MatrixSum());
                }
            }
        }

        return result;
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

    private Bitmap applyGaussianBlur(Bitmap img) {
        Bitmap resultImg;

        Matrix matrixRed = imageToMatrix(img, 0);
        Matrix matrixGreen = imageToMatrix(img, 1);
        Matrix matrixBlue = imageToMatrix(img, 2);

        matrixRed = applyFilter(matrixRed, gaussianBlur7x7);
        matrixGreen = applyFilter(matrixGreen, gaussianBlur7x7);
        matrixBlue = applyFilter(matrixBlue, gaussianBlur7x7);

        resultImg = matrixToImage(matrixRed, matrixGreen, matrixBlue);

        return resultImg;
    }

    private Bitmap applyCanny(Bitmap img) {
        Canny canny = new Canny();
        Bitmap resultImg;

        resultImg = imageToGreyscale(img);
        Matrix matrix = imageToMatrix(img, 0);
        matrix = applyFilter(matrix, gaussianBlur3x3);

        Pair<Matrix, Matrix> sobelOperation = canny.ApplySobelOperator(matrix);
        matrix = canny.ApplyNonMaximumSupression(sobelOperation.first, sobelOperation.second);
        matrix = canny.ApplyDoubleTresholding(matrix);
        matrix = canny.ApplyEdgeTracking(matrix);
        matrix = canny.ApplyCleaning(matrix);

        resultImg = matrixToImage(matrix, matrix, matrix);

        return resultImg;
    }

    private Bitmap applyMotionBlur(Bitmap img) {
        Bitmap resultImg;

        Matrix matrixRed = imageToMatrix(img, 0);
        Matrix matrixGreen = imageToMatrix(img, 1);
        Matrix matrixBlue = imageToMatrix(img, 2);

        matrixRed = applyFilter(matrixRed, motionBlur);
        matrixGreen = applyFilter(matrixGreen, motionBlur);
        matrixBlue = applyFilter(matrixBlue, motionBlur);

        resultImg = matrixToImage(matrixRed, matrixGreen, matrixBlue);

        return resultImg;
    }

    private Bitmap applySharpen(Bitmap img) {
        Bitmap resultImg;

        Matrix matrixRed = imageToMatrix(img, 0);
        Matrix matrixGreen = imageToMatrix(img, 1);
        Matrix matrixBlue = imageToMatrix(img, 2);

        matrixRed = applyFilter(matrixRed, sharpen);
        matrixGreen = applyFilter(matrixGreen, sharpen);
        matrixBlue = applyFilter(matrixBlue, sharpen);

        resultImg = matrixToImage(matrixRed, matrixGreen, matrixBlue);

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

    // following method is taken from https://stackoverflow.com/questions/63776744/save-bitmap-image-to-specific-location-of-gallery-android-10
    private void saveBitmap(Bitmap bitmap, @NonNull String name) throws IOException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getApplicationContext().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "SimpleImageEditor");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + "SimpleImageEditor";

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, name + ".png");
            fos = new FileOutputStream(image);

        }

        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}