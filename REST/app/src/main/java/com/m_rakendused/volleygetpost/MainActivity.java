package com.m_rakendused.volleygetpost;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private TextView get_response_text, post_response_text;
    private ImageView profile_picture;
    private EditText idInput;
    private EditText nameInput;
    private EditText jobInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button get_request_button = findViewById(R.id.get_data);
        Button post_request_button = findViewById(R.id.post_data);

        get_response_text = findViewById(R.id.get_respone_data);
        post_response_text = findViewById(R.id.post_respone_data);

        profile_picture = findViewById(R.id.imageView);

        idInput = findViewById(R.id.editTextTextID);
        nameInput = findViewById(R.id.editTextTextName);
        jobInput = findViewById(R.id.editTextTextJob);

        get_request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGetRequest();
            }
        });

        post_request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRequest();
            }
        });
    }

    private void postRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://reqres.in/api/users";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String name = jsonObject.getString("name");
                    String job = jsonObject.getString("job");
                    String id = jsonObject.getString("id");
                    String createdAt = jsonObject.getString("createdAt");
                    post_response_text.setText("Name: " + name + "\nJob: " + job + "\nID: " + id + "\nCreated At: " + createdAt);
                }
                catch (Exception e){
                    e.printStackTrace();
                    post_response_text.setText("POST DATA : unable to Parse Json");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                post_response_text.setText("Post Data : Response Failed");
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", nameInput.getText().toString());
                params.put("job", jobInput.getText().toString());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void sendGetRequest() {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://reqres.in/api/users/" + idInput.getText();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //get_response_text.setText("Data : "+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("myTag", jsonObject.getJSONObject("data").getString("avatar"));
                    Picasso.get().load(jsonObject.getJSONObject("data").getString("avatar")).into(profile_picture);
                    get_response_text.setText("Name : " + jsonObject.getJSONObject("data").getString("first_name") + " " + jsonObject.getJSONObject("data").getString("last_name") + "\nEmail: " + jsonObject.getJSONObject("data").getString("email"));
                }
                catch (Exception e){
                    e.printStackTrace();
                    get_response_text.setText("Failed to Parse Json");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                get_response_text.setText("Data : Response Failed");
            }
        });

        queue.add(stringRequest);
    }
}