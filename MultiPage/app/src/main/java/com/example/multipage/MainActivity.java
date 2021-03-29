package com.example.multipage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public Button buttonHistory;
    public Button buttonUsage;
    public Button buttonLicense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonHistory = (Button) findViewById(R.id.history);
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intenet = new Intent(MainActivity.this, Activity2.class);
                startActivity(intenet);
            }
        });

        buttonUsage = (Button) findViewById(R.id.usage);
        buttonUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intenet = new Intent(MainActivity.this, Activity3.class);
                startActivity(intenet);
            }
        });

        buttonLicense = (Button) findViewById(R.id.license);
        buttonLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intenet = new Intent(MainActivity.this, Activity4.class);
                startActivity(intenet);
            }
        });
    }
}