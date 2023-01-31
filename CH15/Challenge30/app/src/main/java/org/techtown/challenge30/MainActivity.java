package org.techtown.challenge30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // 전송 메세지 EditText
    EditText messageInput;

    // POST 요청시 사용할 RequestQueue
    RequestQueue queue;

    // 아이템 목록을 나타내는 TextView
    TextView textView;

    // 리싸이클러뷰 객체 선언
    RecyclerView recyclerView;

    // 어댑터 객체 선언
    FriendAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 인플레이션
        setContentView(R.layout.activity_main);

        // 뷰 객체 바인딩
        messageInput = findViewById(R.id.messageInput);
        textView = findViewById(R.id.textView);

        // 리싸이클러뷰 바인딩
        recyclerView = findViewById(R.id.recyclerView);

        // 리싸이클러뷰를 LinearLayout으로 설정 (default: vertical)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 어댑터 객체 생성
        adapter = new FriendAdapter();
        // 전화 목록 아이템 추가
        adapter.addItem(new FriendItem("홍길동", "010-1000-1000", "eDTs00PTQN2FYrPeGmExfi:APA91bHIg_Pz6Smol2H5IFsSb8sLr_bmv-0l5J_SHC_fUOtE8t3NwtBhau5m7PtRBXvfxntqmvXgZd1uEaBUw3Q5E1-FBaM8T3F4vVrqkAep1LRQqkrFMEsFNdM2HuJcX08Uh_wTbAvQ"));
        // 어댑터 설정
        recyclerView.setAdapter(adapter);

        // 친구목록 아이템 클릭시 콜백 메소드
        adapter.setOnItemClickListener(new OnFriendItemClickListener() {
            @Override
            public void onItemClick(FriendAdapter.ViewHolder holder, View view, int position) {
                FriendItem item = adapter.getItem(position);

                Toast.makeText(getApplicationContext(), "아이템 선택됨 : " + item.getName(), Toast.LENGTH_LONG).show();
            }
        });

        // 전송버튼 클릭시 콜백 메소드
        Button sendButton = findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 전송할 메시지 문자열을 가져옴
                String input = messageInput.getText().toString();
                // FCM 전송
                send(input);
            }
        });

        // Queue 객체 초기화
        queue = Volley.newRequestQueue(getApplicationContext());


        FirebaseMessaging.getInstance().getToken() // 등록 id 확인을 위한 리스너 설정
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                            Log.d("Main", "토큰 가져오는데 실패", task.getException());
                            return;
                        }
                        String newToken = task.getResult();
                        println("등록id : " + newToken);
                    }
                });

        // 공지사항이 도착했으면 processIntent 메소드 실행
        Intent intent = getIntent();
        if (intent != null) {
            processIntent(intent);
        }

        textView.setText(adapter.getItemCount() + "명");
    }

    // FCM 전송 메소드
    public void send(String input) {
        // FCM 메세지 전송시 Json 파일을 이용하므로 JsonObject 생성
        JSONObject requestData = new JSONObject();

        try {
            // key, value add
            requestData.put("priority", "high");

            // Data Json Object 생성
            JSONObject dataObj = new JSONObject();
            // 내용 추가
            dataObj.put("contents", input);

            // requestData Json에 dataObj 추가
            requestData.put("data", dataObj);

            // JsonArray 객체에 한번에 담아 전송
            JSONArray idArray = new JSONArray();
            for (int i = 0; i < adapter.getItemCount(); i++) {
                // 친구 객체에서 정보를 가져와서 배열에 담음
                FriendItem item = adapter.getItem(i);
                String regId = item.getRegId();
                println("regId #" + i + " : " + regId);

                // RequestData에 JsonArray 담음
                idArray.put(i, regId);
            }

            requestData.put("registration_ids", idArray);

        } catch(Exception e) {
            e.printStackTrace();
        }

        // 전송 성공시 수행되는 콜백 메소드
        sendData(requestData, new SendResponseListener() {
            @Override
            public void onRequestCompleted() {
                println("onRequestCompleted() 호출됨.");

                Toast.makeText(getApplicationContext(), "공지사항 전송함", Toast.LENGTH_LONG).show();
            }

            // 전송 시작시 수행되는 콜백 메소드
            @Override
            public void onRequestStarted() {
                println("onRequestStarted() 호출됨.");
            }

            // 전송 실패시 수행되는 콜백 메소드
            @Override
            public void onRequestWithError(VolleyError error) {
                println("onRequestWithError() 호출됨.");
            }
        });

    }

    // FCM interface
    public interface SendResponseListener {
        public void onRequestStarted();
        public void onRequestCompleted();
        public void onRequestWithError(VolleyError error);
    }

    // FCM 서버에 메시지를 전송
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
                Map<String,String> params = new HashMap<String,String>();

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String,String>();
                // 두번째 파라미터에 Apikey를 입력해준다.
                headers.put("Authorization","key=AAAAvBXEC_s:APA91bFmBgs8P31sjNNsk_CSRcHiyWO8ygzWdTaxhKrb0GZNSNTUDfbVjPX0cxoehQIwi1QcV_OCjeWjQxo4CBDSBJVFzKWplNwxL4z1ab9o7ipVF4NfxHzSjsHP3nJ1HkLqeKOnNLfv");

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

    // 새로운 인텐트가 있을시 수행되는 콜백 메소드
    @Override
    protected void onNewIntent(Intent intent) {
        println("onNewIntent() called.");

        if (intent != null) {
            processIntent(intent);
        }

        super.onNewIntent(intent);
    }

    // 인텐트가 도착했을시 (FCM 알림이 왔을 시)
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