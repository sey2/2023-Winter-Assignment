package org.techtown.challenge29;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText messageInput;

    RequestQueue queue;

    TextView textView;
    RecyclerView recyclerView;
    FriendAdapter adapter;

    private Resources resource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageInput = findViewById(R.id.messageInput);
        textView = findViewById(R.id.textView);

        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FriendAdapter();

        adapter.addItem(new FriendItem("김준수", "010-1000-1000", "dSjtt3hIRjWiKPoU2VmQn5:APA91bE6sjXL_gwz9IbphBWYLqZHT02iMKu64u2IYm8xQakkg40rX5jQYCzhzAJZ-nosveYUYtbamgyIQlpqdL9E9TY3wxuNvf9njyIe0IcjqSuMfkomcXa5bhHsfB68O6W5ZwzvoXX7"));
        // adapter.addItem(new FriendItem("홍연걸", "010-2000-2000", "dSjtt3hIRjWiKPoU2VmQn5"));

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnFriendItemClickListener() {
            @Override
            public void onItemClick(FriendAdapter.ViewHolder holder, View view, int position) {
                FriendItem item = adapter.getItem(position);

                Toast.makeText(getApplicationContext(), "아이템 선택됨 : " + item.getName(), Toast.LENGTH_LONG).show();
            }
        });

        AssetManager assetManager = getAssets();
        InputStream serviceAccount = null;
        try {
             serviceAccount = assetManager.open("service-account.json");
             FirebaseOptions options = FirebaseOptions.builder()
                     .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options, "fcmmgs");

        } catch (IOException e) {
            e.printStackTrace();
        }

        Button sendButton = findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = messageInput.getText().toString();

                  Message message = Message.builder().putData("title", "Test").putData("content", "hello")
                          .setToken("dSjtt3hIRjWiKPoU2VmQn5:APA91bE6sjXL_gwz9IbphBWYLqZHT02iMKu64u2IYm8xQakkg40rX5jQYCzhzAJZ-nosveYUYtbamgyIQlpqdL9E9TY3wxuNvf9njyIe0IcjqSuMfkomcXa5bhHsfB68O6W5ZwzvoXX7")
                                  .build();
                try {
                   String response =  FirebaseMessaging.getInstance().send(message);
                    Log.d("FMS", response);
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                }
                 //send(input);
            }
        });




        queue = Volley.newRequestQueue(getApplicationContext());


        Intent intent = getIntent();
        if (intent != null) {
            processIntent(intent);
        }
    }

    public void send(String input) {

        JSONObject requestData = new JSONObject();

        try {
            requestData.put("priority", "high");

            JSONObject dataObj = new JSONObject();
            dataObj.put("contents", input);
            requestData.put("data", dataObj);

            JSONArray idArray = new JSONArray();
            for (int i = 0; i < adapter.getItemCount(); i++) {
                FriendItem item = adapter.getItem(i);
                String regId = item.getRegId();
                println("regId #" + i + " : " + regId);

                idArray.put(i, regId);
            }

            requestData.put("registration_ids", idArray);

        } catch (Exception e) {
            e.printStackTrace();
        }

        sendData(requestData, new SendResponseListener() {
            @Override
            public void onRequestCompleted() {
                println("onRequestCompleted() 호출됨.");

                Toast.makeText(getApplicationContext(), "공지사항 전송함", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRequestStarted() {
                println("onRequestStarted() 호출됨.");
            }

            @Override
            public void onRequestWithError(VolleyError error) {
                println("onRequestWithError() 호출됨." + error);
            }
        });

    }

    public interface SendResponseListener {
        public void onRequestStarted();

        public void onRequestCompleted();

        public void onRequestWithError(VolleyError error);
    }

    public void sendData(JSONObject requestData, final SendResponseListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequestCompleted();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onRequestWithError(error);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "BEU7EfJVWm_Wyn0P_cltF2AVjhGwH_w3QoxEyLRiBFijA-vX_TLyC0tysIMjSNaXL3hdq7b33n8Fr-bHUyGJk60");

                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        queue.add(request);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        println("onNewIntent() called.");

        if (intent != null) {
            processIntent(intent);
        }

        super.onNewIntent(intent);
    }


    private void processIntent(Intent intent) {
        String from = intent.getStringExtra("from");
        if (from == null) {
            println("from is null.");
            return;
        }

        String contents = intent.getStringExtra("contents");

        println("DATA : " + from + ", " + contents);
        Toast.makeText(getApplicationContext(), "공지사항 수신함 : " + contents, Toast.LENGTH_LONG).show();

    }

    public void println(String data) {
        Log.d("FMS", data);
    }
}
