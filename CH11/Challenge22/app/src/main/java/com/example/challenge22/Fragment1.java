package com.example.challenge22;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class Fragment1 extends Fragment {

    EditText editText;
    EditText editText2;
    EditText editText3;

    OnDatabaseCallback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callback = (OnDatabaseCallback) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 인플레이션
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        // 바인딩
        editText = rootView.findViewById(R.id.editText);
        editText2 = rootView.findViewById(R.id.editText2);
        editText3 = rootView.findViewById(R.id.editText3);


        // 버튼 클릭시 테이블에 레코드를 추가함
        Button button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                String author = editText2.getText().toString();
                String contents = editText3.getText().toString();

                callback.insert(name, author, contents);
                Toast.makeText(getContext(), "책 정보를 추가했습니다.", Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }
}