package com.example.challenge22;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class Fragment2 extends Fragment {
    RecyclerView recyclerView;
    BookAdapter adapter;
    OnDatabaseCallback callback;

    // Custom Dialog Object
    Dialog dialog;

    BookDatabase database;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callback = (OnDatabaseCallback) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 인플레이션
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        // dialog 객체 생성 후 바인딩
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog);

        // 리싸이클러뷰 바인딩 후 LinearLayout으로 지정
        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // 어댑터 객체 생성 후 리싸이클러뷰에 어댑터 등록
        adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);

        // 데이터베이스 select문을 수행해 모든 레코드들을 ArrayList로 가져옴
        ArrayList<BookDTO> result = callback.selectAll();
        adapter.setItems(result);

        // MVP 디자인 패턴을 이용한 콜백 메소드 구현
        // 리싸이클러뷰의 아이템이 클릭될시 아래 메소드가 수행됨
        // 아이템 클릭시 커스텀 다이얼로그가 화면에 띄워짐
        adapter.setOnItemClickListener(new OnBookItemClickListener() {
            @Override
            public void onItemClick(BookAdapter.ViewHolder holder, View view, int position) {
                BookDTO item = adapter.getItem(position);
                createDialog(item);
            }
        });


        // 새로고침 버튼
        Button button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<BookDTO> result = callback.selectAll();
                adapter.setItems(result);
                adapter.notifyDataSetChanged();
            }
        });

        database = BookDatabase.getInstance(getContext());

        return rootView;
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