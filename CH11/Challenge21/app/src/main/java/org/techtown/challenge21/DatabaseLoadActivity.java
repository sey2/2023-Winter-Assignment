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

    Dialog dialog;

    public static void open(Context context){
        context.startActivity(new Intent(context, DatabaseLoadActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_load);

        dialog = new Dialog(DatabaseLoadActivity.this);
        dialog.setContentView(R.layout.custom_dialog);

        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);

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

        ArrayList<BookDTO> result = database.selectAll();
        adapter.setItems(result);

        adapter.setOnItemClickListener(new OnBookItemClickListener() {
            @Override
            public void onItemClick(BookAdapter.ViewHolder holder, View view, int position) {
                BookDTO item = adapter.getItem(position);
                createDialog(item);

                // Toast.makeText(getApplicationContext(), "아이템 선택됨 : " + item.getName(), Toast.LENGTH_LONG).show();
            }
        });

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