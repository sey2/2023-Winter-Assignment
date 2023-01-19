package org.techtown.challenge21;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Button inputActivityBtn;
    Button loadActivityBtn;
    Button modifyActivityBtn;
    Button deleteActivityBtn;

    static Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButton();

        // open database
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
    }

    private void initButton(){
        inputActivityBtn = findViewById(R.id.InputActivtyBtn);
        loadActivityBtn = findViewById(R.id.loadActivityBtn);
        modifyActivityBtn = findViewById(R.id.modiftActivityBtn);
        deleteActivityBtn = findViewById(R.id.deleteActivityBtn);

        inputActivityBtn.setOnClickListener((v)->{
            DatabaseInputActivity.open(this);
        });

        loadActivityBtn.setOnClickListener((v)->{
            DatabaseLoadActivity.open(this);
        });

        modifyActivityBtn.setOnClickListener((v)->{
            DatabaseModifyActivity.open(this);
        });

        deleteActivityBtn.setOnClickListener((v)->{
            DatabaseDeleteActivity.open(this);
        });
    }

    public static Database getDatabaseInstance(){
        return database;
    }

    public ArrayList<BookDTO> selectAll() {
        ArrayList<BookDTO> result = database.selectAll();
        Toast.makeText(getApplicationContext(), "책 정보를 조회했습니다.", Toast.LENGTH_LONG).show();

        return result;
    }


}
