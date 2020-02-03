package com.example.womensecurityapp.services;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.womensecurityapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class notification_genrater extends AppCompatActivity {

    String TOPIC,NOTIFICATION_TITLE,NOTIFICATION_MESSAGE;
    String FCM_API = "https://fcm.googleapis.com/fcm/send";
    String serverKey = "key=" + "AIzaSyDo7twRdgfEnI2OVNitEEhlHyykzA22PE0";
    String contentType = "application/json";

    EditText edtTitle,edtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_genrater);

        Button btnSend=findViewById(R.id.notification_send);
         edtTitle=findViewById(R.id.notification_title);
         edtMessage=findViewById(R.id.notification_body);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TOPIC = "/topics/hello"; //topic must match with what the receiver subscribed to
                NOTIFICATION_TITLE = edtTitle.getText().toString();
                NOTIFICATION_MESSAGE = edtMessage.getText().toString();

                JSONObject notification = new JSONObject();
                JSONObject notificationBody = new JSONObject();
                try {
                    notificationBody.put("title", NOTIFICATION_TITLE);
                    notificationBody.put("message", NOTIFICATION_MESSAGE);

                    notification.put("to", TOPIC);
                    notification.put("data", notificationBody);
                } catch (JSONException e) {
                    Log.e("123", "onCreate: " + e.getMessage());
                }

                if (!NOTIFICATION_TITLE.isEmpty()) {

                    if (!NOTIFICATION_MESSAGE.isEmpty()) {

                        sendNotification(notification);
                    }
                    else
                        Toast.makeText(getApplicationContext(),"Message field can't be empty",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"Title field can't be empty",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendNotification(JSONObject notification) {

        Log.e("kc","kj");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,FCM_API,notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("123", "onResponse: " + response.toString());
                        edtTitle.setText("");
                        edtMessage.setText("");

                        Toast.makeText(getApplicationContext(),"Notification successfully send, thank you",Toast.LENGTH_LONG).show();

                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Request error", Toast.LENGTH_LONG).show();
                        Log.i("123", "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization",serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        int socketTimeout = 1000 * 60;   // 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);
    }

}
