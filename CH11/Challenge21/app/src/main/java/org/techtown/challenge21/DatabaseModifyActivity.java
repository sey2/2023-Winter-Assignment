package org.techtown.challenge21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class DatabaseModifyActivity extends AppCompatActivity {

    public static void open(Context context){
        context.startActivity(new Intent(context, DatabaseModifyActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_modify);
    }
}