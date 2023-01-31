package org.techtown.challenge29;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
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

    // Dialog 객체 선언
    Dialog dialog;

    // FCM Device Token
    private final String myDeviceId = "dSjtt3hIRjWiKPoU2VmQn5:APA91bE6sjXL_gwz9IbphBWYLqZHT02iMKu64u2IYm8xQakkg40rX5jQYCzhzAJZ-nosveYUYtbamgyIQlpqdL9E9TY3wxuNvf9njyIe0IcjqSuMfkomcXa5bhHsfB68O6W5ZwzvoXX7";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 인플레이션
        setContentView(R.layout.activity_main);

        // 뷰 객체 바인딩
        messageInput = findViewById(R.id.messageInput);
        textView = findViewById(R.id.textView);

        // dialog 객체 생성 후 바인딩
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);

        // yanzi 라이브러리를 이용한 자동 권한 설정
        // 연락처 쓰기, 읽기 권한
        AndPermission.with(this)
                .runtime()
                .permission(
                        Permission.WRITE_CONTACTS,
                        Permission.READ_CONTACTS  )
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Log.d("Intro", "허용된 권한 갯수 : " + permissions.size());
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Log.d("Intro", "거부된 권한 갯수 : " + permissions.size());
                    }
                })
                .start();

        // 리싸이클러뷰 바인딩
        recyclerView = findViewById(R.id.recyclerView);

        // 리싸이클러뷰를 LinearLayout으로 설정 (default: vertical)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 어댑터 객체 생성
        adapter = new FriendAdapter();
        // 전화 목록 아이템 추가
        adapter.addItem(new FriendItem("김준수", "010-1000-1000", myDeviceId, "-1"));
        // 어댑터 설정
        recyclerView.setAdapter(adapter);

        // 친구목록 아이템 클릭시 콜백 메소드
        adapter.setOnItemClickListener(new OnFriendItemClickListener() {
            @Override
            public void onItemClick(FriendAdapter.ViewHolder holder, View view, int position) {
                FriendItem item = adapter.getItem(position);
                // CustomDialog 띄우기
                createDialog(item);
            }
        });

        // 전송버튼 클릭시 콜백 메소드
        Button sendButton = findViewById(R.id.sendBtn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 전송할 메시지 문자열을 가져옴
                String input = messageInput.getText().toString();

                // FCM 전송
                send(input);
            }
        });

        // 전화목록 불러오기 버튼 콜백 메소드
        Button loadButton = findViewById(R.id.loadBtn);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 전화 번호 목록에서 아이템을 가져와 리싸이클러뷰에 적용
                loadItem();
            }
        });

        // 전화 목록 추가 버튼
        Button addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent 객체 만들어서 전화목록 앱 띄우기
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                startActivity(intent);
            }
        });

        // Queue 객체 초기화
        queue = Volley.newRequestQueue(getApplicationContext());

        // 공지사항이 도착했으면 processIntent 메소드 실행
        Intent intent = getIntent();
        if (intent != null) {
            processIntent(intent);
        }

    }

    // 전화목록에서 아이템 가져오는 메소드
    public void loadItem(){
        if (adapter.getItemCount() > 1){
            adapter.removeAllItem();
            adapter.addItem(new FriendItem("김준수", "010-1000-1000", myDeviceId, "-1"));
        }
        getContacts(getApplicationContext());
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

                idArray.put(i, regId);
            }

            // RequestData에 JsonArray 담음
            requestData.put("registration_ids", idArray);

        } catch (Exception e) {
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
                println("onRequestWithError() 호출됨." + error);
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
        // 서버에 POST 요청
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
                // 두번째 파라미터에 Apikey를 입력해준다.
                headers.put("Authorization", "key=AAAAvBXEC_s:APA91bFmBgs8P31sjNNsk_CSRcHiyWO8ygzWdTaxhKrb0GZNSNTUDfbVjPX0cxoehQIwi1QcV_OCjeWjQxo4CBDSBJVFzKWplNwxL4z1ab9o7ipVF4NfxHzSjsHP3nJ1HkLqeKOnNLfv");

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

    public void getContacts(Context context){
        // 1. Resolver 가져오기(데이터베이스 열어주기)
        // 전화번호부에 이미 만들어져 있는 ContentProvider 를 통해 데이터를 가져올 수 있음
        // 다른 앱에 데이터를 제공할 수 있도록 하고 싶으면 ContentProvider 를 설정
        // 핸드폰 기본 앱 들 중 데이터가 존재하는 앱들은 Content Provider 를 갖는다
        // ContentResolver 는 ContentProvider 를 가져오는 통신 수단
        ContentResolver resolver = context.getContentResolver();

        // 2. 전화번호가 저장되어 있는 테이블 주소값(Uri)을 가져오기
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        // 3. 테이블에 정의된 칼럼 가져오기
        // ContactsContract.CommonDataKinds.Phone 이 경로에 상수로 칼럼이 정의
        String[] projection = { ContactsContract.CommonDataKinds.Phone.CONTACT_ID // 인덱스 값, 중복될 수 있음 -- 한 사람 번호가 여러개인 경우
                ,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                ,  ContactsContract.CommonDataKinds.Phone.NUMBER};

        // 4. ContentResolver로 쿼리를 날림 -> resolver 가 provider 에게 쿼리하겠다고 요청
        Cursor cursor = resolver.query(phoneUri, projection, null, null, null);

        // 4. 커서로 리턴된다. 반복문을 돌면서 cursor 에 담긴 데이터를 하나씩 추출
        if(cursor != null){
            while(cursor.moveToNext()){
                // 4.1 이름으로 인덱스를 찾아준다
                int idIndex = cursor.getColumnIndex(projection[0]); // 이름을 넣어주면 그 칼럼을 가져와준다.
                int nameIndex = cursor.getColumnIndex(projection[1]);
                int numberIndex = cursor.getColumnIndex(projection[2]);
                // 4.2 해당 index 를 사용해서 실제 값을 가져온다.
                String id = cursor.getString(idIndex);
                String name = cursor.getString(nameIndex);
                String number = cursor.getString(numberIndex);

                Log.d("FMS", cursor.getColumnName(idIndex) + " " + cursor.getColumnName(nameIndex) + " " +  cursor.getColumnName(numberIndex));

                println(id);

                FriendItem friendItem = new FriendItem(name, number,myDeviceId, id);
                adapter.addItem(friendItem);
            }
        }
        adapter.notifyDataSetChanged();

        // 데이터 계열은 반드시 닫아줘야 한다.
        cursor.close();
    }

    // 커스텀 다이얼로그를 생성 합니다. (수정, 삭제)
    public void createDialog(FriendItem friendItem) {
        // 다이얼로그 띄우기
        dialog.show();

        // 바인딩
        EditText bookNameEdit = dialog.findViewById(R.id.nameEditText);
        EditText phoneEdit = dialog.findViewById(R.id.phoneEditText);

        // 콘턴트 리솔버 객체 초기화
        ContentResolver cr = getApplicationContext().getContentResolver();

        // 수정 버튼 클릭시 수행되는 콜백 메소드
        Button modify_btn = dialog.findViewById(R.id.modifyBtn);
        modify_btn.setOnClickListener((v)->{

            // 임의로 추가한 데이터면
            if(friendItem.contactId.equals("-1")) {
                Toast.makeText(getApplicationContext(), "전화번호에 없는 아이템 입니다.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                return;
            }

            // 전화목록에 있는 아이템이면 입력한 이름, 전화번호를 토대로 수정
            String name = bookNameEdit.getText().toString();
            String phone = phoneEdit.getText().toString();

            // intent 객체에 전화 번호 목록을 띄우겠다고 알림
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            // 애니메이션 설정 x
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            // intent에 수정하고 싶은 이름, 전화번호 데이터를 담음
            intent.putExtra(ContactsContract.Intents.Insert.NAME, name) //TODO 이름
                    .putExtra(ContactsContract.Intents.Insert.PHONE, phone); //TODO 전화번호
            // 목록 띄위기
            startActivity(intent);

            // 기존에 있는 전화번호는 삭제
            cr.delete(ContactsContract.RawContacts.CONTENT_URI,
                    ContactsContract.RawContacts.CONTACT_ID + " = " + friendItem.contactId, null);

            // 리싸이클러뷰 업데이트
            loadItem();

            // 친구 목록 수 업데이트
            textView.setText(adapter.getItemCount());

            // 다이얼로그 닫기
            dialog.dismiss();
        });

        // 삭제 버튼 클릭시 수행되는 메소드
        Button deleteBtn = dialog.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener((v)->{
            // 전화번호 삭제
            cr.delete(ContactsContract.RawContacts.CONTENT_URI,
                    ContactsContract.RawContacts.CONTACT_ID + " = " + friendItem.contactId, null);

            // 리싸이클러뷰 업데이트
            loadItem();
            Toast.makeText(getApplicationContext(), "연락처가 삭제 되었습니다.",Toast.LENGTH_LONG).show();

            // 친구 수 업데이트
            textView.setText(adapter.getItemCount());

            // 다이얼로그 닫기
            dialog.dismiss();
        });

        // 취소 버튼 클릭시 수행되는 메소드
        Button canelBtn = dialog.findViewById(R.id.cancelBtn);
        canelBtn.setOnClickListener((v)->{
            // 다이얼로그 닫기
            dialog.dismiss();
        });
    }
}
