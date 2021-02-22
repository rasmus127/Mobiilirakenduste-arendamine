package com.example.decathloncalculator;

import java.lang.Math;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // our code
        Button calcButton = (Button) findViewById(R.id.calculate_button);
        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                // editable text
                EditText event1Text = (EditText) findViewById(R.id.event1);
                EditText event2Text = (EditText) findViewById(R.id.event2);
                EditText event3Text = (EditText) findViewById(R.id.event3);
                EditText event4Text = (EditText) findViewById(R.id.event4);
                EditText event5Text = (EditText) findViewById(R.id.event5);
                EditText event6Text = (EditText) findViewById(R.id.event6);
                EditText event7Text = (EditText) findViewById(R.id.event7);
                EditText event8Text = (EditText) findViewById(R.id.event8);
                EditText event9Text = (EditText) findViewById(R.id.event9);
                EditText event10Text = (EditText) findViewById(R.id.event10);

                // display text
                TextView score1 = (TextView) findViewById(R.id.score1);
                TextView score2 = (TextView) findViewById(R.id.score);
                TextView score3 = (TextView) findViewById(R.id.score2);
                TextView score4 = (TextView) findViewById(R.id.score3);
                TextView score5 = (TextView) findViewById(R.id.score4);
                TextView score6 = (TextView) findViewById(R.id.score5);
                TextView score7 = (TextView) findViewById(R.id.score6);
                TextView score8 = (TextView) findViewById(R.id.score7);
                TextView score9 = (TextView) findViewById(R.id.score8);
                TextView score10 = (TextView) findViewById(R.id.score9);
                TextView resultText = (TextView) findViewById(R.id.score10);

                // calculating score
                try {
                    int event1 = (int)(25.4347 * Math.pow((18 - Float.parseFloat(event1Text.getText().toString())), 1.81));
                    int event2 = (int)(0.14354 * Math.pow((Float.parseFloat(event2Text.getText().toString()) - 220), 1.4));
                    int event3 = (int)(51.39 * Math.pow((Float.parseFloat(event3Text.getText().toString()) - 1.5), 1.05));
                    int event4 = (int)(0.8465 * Math.pow((Float.parseFloat(event4Text.getText().toString()) - 75), 1.42));
                    int event5 = (int)(1.53775 * Math.pow((82 - Float.parseFloat(event5Text.getText().toString())), 1.81));
                    int event6 = (int)(5.74352 * Math.pow((28.5 - Float.parseFloat(event6Text.getText().toString())), 1.92));
                    int event7 = (int)(12.91 * Math.pow((Float.parseFloat(event7Text.getText().toString()) - 4), 1.1));
                    int event8 = (int)(0.2797 * Math.pow((Float.parseFloat(event8Text.getText().toString()) - 100), 1.35));
                    int event9 = (int)(10.14 * Math.pow((Float.parseFloat(event9Text.getText().toString()) - 7), 1.08));
                    int event10 = (int)(0.03768 * Math.pow((480 - Float.parseFloat(event10Text.getText().toString())), 1.85));
                    score1.setText(event1 + "p");
                    score2.setText(event2 + "p");
                    score3.setText(event3 + "p");
                    score4.setText(event4 + "p");
                    score5.setText(event5 + "p");
                    score6.setText(event6 + "p");
                    score7.setText(event7 + "p");
                    score8.setText(event8 + "p");
                    score9.setText(event9 + "p");
                    score10.setText(event10 + "p");
                    resultText.setText("Score: " + (event1 + event2 + event3 + event4 + event5 + event6 + event7 + event8 + event9 + event10) + "p");
                }
                catch (Exception e) {
                    resultText.setText("Incorrect values!");
                }
            }
        });
    }
}