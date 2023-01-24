package com.example.challenge26;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    FrameLayout previewFrame;
    CameraSurfaceView cameraView;

    RelativeLayout iconLayout;
    ImageView icon01Image;
    ImageView icon02Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 인플레이션
        setContentView(R.layout.activity_main);

        // 바인딩
        previewFrame = findViewById(R.id.previewFrame);
        cameraView = findViewById(R.id.cameraView);

        iconLayout = findViewById(R.id.iconLayout);
        icon01Image = findViewById(R.id.icon01Image);
        icon02Image = findViewById(R.id.icon02Image);

        // 보여주기 버튼 리스너
        Button showButton = findViewById(R.id.showButton);
        showButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                iconLayout.setVisibility(View.VISIBLE);
            }
        });

        // 숨기기 버튼 리스너
        Button hideButton = findViewById(R.id.hideButton);
        hideButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                iconLayout.setVisibility(View.INVISIBLE);
            }
        });

        // 라이브러리를 이용한 권한 설정
        AndPermission.with(this)
                .runtime()
                .permission(Permission.CAMERA,Permission.ACCESS_FINE_LOCATION)
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

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}