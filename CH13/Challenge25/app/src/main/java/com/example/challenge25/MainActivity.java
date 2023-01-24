package com.example.challenge25;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    // 리싸이클러뷰 변수 선언
    RecyclerView recyclerView;
    // 리싸이클러뷰 어댑터 변수 선언
    PictureAdapter adapter;

    // 날짜 포맷 변수 선언
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    int pictureCount = 0;

    // 사진 정보가 담기는 ArrayList 선언
    ArrayList<PictureInfo> pictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 인플레이션
        setContentView(R.layout.activity_main);

        // 바인딩
        textView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recyclerView);

        // LinearLayout 변수 선언 후 리싸이클러뷰에 적용
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 어댑터 객체 선언
        adapter = new PictureAdapter();
        // 리싸이클러뷰 어댑터 설정
        recyclerView.setAdapter(adapter);

        // 리싸이클러뷰 아이템 클릭 이벤트 설정
        adapter.setOnItemClickListener(new OnPictureItemClickListener() {
            @Override
            public void onItemClick(PictureAdapter.ViewHolder holder, View view, int position) {
                // 선택한 아이템의 오브젝트를 가져옴
                PictureInfo item = adapter.getItem(position);

                Toast.makeText(getApplicationContext(), "아이템 선택됨 : " + item.getDisplayName(), Toast.LENGTH_LONG).show();
            }
        });

        // 버튼 클릭 이벤트 설정
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 모든 사진 정보를 가져와 ArrayList에 담음
                ArrayList<PictureInfo> result = queryAllPictures();

                // 어댑터 아이템 설정
                adapter.setItems(result);
                // 어댑터에게 데이터 셋이 변경되었다고 알림
                adapter.notifyDataSetChanged();
            }
        });

        // 사진 정보 오브젝트를 모두 가져와 ArrayLists에 담음
        ArrayList<PictureInfo> result = queryAllPictures();
        //어댑터에 아이템 설정
        adapter.setItems(result);
        // 어댑터에게 데이터셋이 변경되었다고 알림
        adapter.notifyDataSetChanged();

        // 오픈소스 라이브러리를 이용해 권한 설정
        AndPermission.with(this)
                .runtime()
                .permission(Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        showToast("허용된 권한 갯수 : " + permissions.size());
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        showToast("거부된 권한 갯수 : " + permissions.size());
                    }
                })
                .start();

    }

    // Toast 메세지를 출력하게 해주는 함수
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



    private ArrayList<PictureInfo> queryAllPictures() {
        // 모든 사진 정보를 가져와 ArrayList에 담음
        ArrayList<PictureInfo> result = new ArrayList<>();
        // 외부 사진 URI 주소를 가져와 uri 변수에 담음
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // MediaStore는 안드로이드 시스템에서 제공하는 미디어 데이터 DB입니다.
        // 시스템이 제공하는 Provider를 이용해 미디어 파일(오디오, 이미지, 비디오)를 쿼리할 수 있다.
        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_ADDED };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        int columnDataIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int columnNameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        int columnDateIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED);

        // 사진 정보를 가져와 오브젝트에 저장
        pictureCount = 0;
        while (cursor.moveToNext()) {
            String path = cursor.getString(columnDataIndex);
            String displayName = cursor.getString(columnNameIndex);
            String outDate = cursor.getString(columnDateIndex);
            String addedDate = dateFormat.format(new Date(new Long(outDate).longValue() * 1000L));

            if (!TextUtils.isEmpty(path)) {
                PictureInfo info = new PictureInfo(path, displayName, addedDate);
                result.add(info);
            }

            pictureCount++;
        }

        textView.setText(pictureCount + " 개");
        Log.d("MainActivity", "Picture count : " + pictureCount);

        for (PictureInfo info : result) {
            Log.d("MainActivity", info.toString());
        }

        return result;
    }

}