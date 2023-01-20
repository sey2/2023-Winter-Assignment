package org.techtown.challenge21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DatabaseInputActivity extends AppCompatActivity {
    private static final String TAG = "DatabaseInputActivity";

    // 데이터베이스 인스턴스
    Database database;

    EditText editText;
    EditText editText2;
    EditText editText3;

    // DatabaseInputActivity를 열어주는 메소드
    public static void open(Context context){
        context.startActivity(new Intent(context, DatabaseInputActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 인플레이션
        setContentView(R.layout.activity_database_input);

        // 뷰 바인딩
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);

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

        // 책 정보 추가 버튼
        Button button = findViewById(R.id.button);

        // 버튼 클릭스 테이블에 레코드를 추가함
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                String author = editText2.getText().toString();
                String contents = editText3.getText().toString();

                database.insertRecord(name, author, contents);
                Toast.makeText(getApplicationContext(), "책 정보를 추가했습니다.", Toast.LENGTH_LONG).show();
                database.close();
                finish();
            }
        });
    }

    // 화면이 없어질때 수행되는 콜백 메소드
    protected void onDestroy() {
        // close database
        if (database != null) {
            database.close();
            database = null;
        }

        super.onDestroy();
    }
}