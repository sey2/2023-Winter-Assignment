package com.example.challenge22;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnDatabaseCallback {
    private static final String TAG = "MainActivity";

    // 변수 선언
    Toolbar toolbar;
    Fragment1 fragment1;
    Fragment2 fragment2;
    BookDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 인플레이션
        setContentView(R.layout.activity_main);

        // 바인딩
        toolbar = findViewById(R.id.toolbar);
        // 툴바 객체 지정
        setSupportActionBar(toolbar);

        // 프래그먼트 생성
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();

        // 프래그먼트 매니저는 앱 프래그먼트에서 프래그먼트를 추가, 삭제 또는 교체하고 백 스택에 추가하는 등의 작업을 실행하는 클래스입니다.
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

        // 탭 레이아웃 바인딩
        TabLayout tabs = findViewById(R.id.tabs);
        // 입력, 조회 탭 추가 후 각각 텍스트 설정
        tabs.addTab(tabs.newTab().setText("입력"));
        tabs.addTab(tabs.newTab().setText("조회"));

        // 텝이 클릭 되었을 시 수행할 기능 설정 (리스너)
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 클릭된 탭의 위치를 int 값으로 받아옴
                int position = tab.getPosition();
                Log.d("MainActivity", "선택된 탭 : " + position);

                // 프래그먼트 객체 선언
                Fragment selected = null;

                // 각 탭이 클릭되었을 시 해당 프래그먼트에 맞게  위에서 선언한 프래그먼트를 초기화해줌
                if (position == 0) {
                    selected = fragment1;
                } else if (position == 1) {
                    selected = fragment2;
                }

                // 프래그먼트 매니저를 이용해 화면 전환
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        // open database
        if (database != null) {
            database.close();
            database = null;
        }

        database = BookDatabase.getInstance(this);
        boolean isOpen = database.open();
        if (isOpen) {
            Log.d(TAG, "Book database is open.");
        } else {
            Log.d(TAG, "Book database is not open.");
        }

    }

    protected void onDestroy() {
        // close database
        if (database != null) {
            database.close();
            database = null;
        }

        super.onDestroy();
    }

    @Override
    public void insert(String name, String author, String contents) {
        database.insertRecord(name, author, contents);
        Toast.makeText(getApplicationContext(), "책 정보를 추가했습니다.", Toast.LENGTH_LONG).show();
    }

    @Override
    public ArrayList<BookDTO> selectAll() {
        ArrayList<BookDTO> result = database.selectAll();
        Toast.makeText(getApplicationContext(), "책 정보를 조회했습니다.", Toast.LENGTH_LONG).show();

        return result;
    }
}