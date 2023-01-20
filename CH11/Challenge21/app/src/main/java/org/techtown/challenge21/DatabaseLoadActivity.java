package org.techtown.challenge21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class DatabaseLoadActivity extends AppCompatActivity {
    private static final String TAG = "DatabaseLoadActivity";

    RecyclerView recyclerView;
    BookAdapter adapter;
    Database database;

    // Custom Dialog Object
    Dialog dialog;


    // DatavaseLoadActivty 화면을 띄워즈는 함수
    public static void open(Context context){
        context.startActivity(new Intent(context, DatabaseLoadActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 인플레이션
        setContentView(R.layout.activity_database_load);

        // dialog 객체 생성 후 바인딩
        dialog = new Dialog(DatabaseLoadActivity.this);
        dialog.setContentView(R.layout.custom_dialog);

        // 리싸이클러뷰 바인딩 후 LinearLayout으로 지정
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 어댑터 객체 생성 후 리싸이클러뷰에 어댑터 등록
        adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);

        // 데이터베이스 열기
        if (database != null) {
            database.close();
            database = null;
        }

        database = Database.getInstance(this);
        boolean isOpen = database.open();
        if (isOpen) {
            Log.d(TAG, "Book database is open.");
        } else {
            Log.d(TAG, "Book database is not open.");
        }

        // 데이터베이스 select문을 수행해 모든 레코드들을 ArrayList로 가져옴
        ArrayList<BookDTO> result = database.selectAll();
        adapter.setItems(result);

        // MVP 디자인 패턴을 이용한 콜백 메소드 구현
        // 리싸이클러뷰의 아이템이 클릭될시 아래 메소드가 수행됨
        // 아이템 클릭시 커스텀 다이얼로그가 화면에 띄워짐
        adapter.setOnItemClickListener(new OnBookItemClickListener() {
            @Override
            public void onItemClick(BookAdapter.ViewHolder holder, View view, int position) {
                BookDTO item = adapter.getItem(position);
                createDialog(item);

                // Toast.makeText(getApplicationContext(), "아이템 선택됨 : " + item.getName(), Toast.LENGTH_LONG).show();
            }
        });

        // 새로고침 버튼
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<BookDTO> result = database.selectAll();
                adapter.setItems(result);
                adapter.notifyDataSetChanged();
            }
        });

    }

    // 커스텀 다이얼로그를 생성 합니다. (수정, 삭제)
    public void createDialog(BookDTO bookDTO) {
        dialog.show();

        EditText bookNameEdit = dialog.findViewById(R.id.nameEditText);
        EditText authorEdit = dialog.findViewById(R.id.authorEditText);
        EditText contentEdit = dialog.findViewById(R.id.contentEditText);

        Button modify_btn = dialog.findViewById(R.id.modifyBtn);
        modify_btn.setOnClickListener((v)->{
            String name = bookNameEdit.getText().toString();
            String author = authorEdit.getText().toString();
            String content = contentEdit.getText().toString();

            database.updateRecord(name, author, content, bookDTO);
            dialog.dismiss();
        });

        Button deleteBtn = dialog.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener((v)->{
            database.deleteRecord(bookDTO);
            dialog.dismiss();
        });

        Button canelBtn = dialog.findViewById(R.id.cancelBtn);
        canelBtn.setOnClickListener((v)->{
            dialog.dismiss();
        });
    }
}