package org.techtown.challenge27;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // GoogleMap Fragment 객체 선언
    SupportMapFragment mapFragment;
    // GoogleMap 객체 선언
    GoogleMap map;

    // MapMarker 옵션 객체 선언 (나 자신)
    MarkerOptions myLocationMarker;

    // MapMarker 옵션 객체선언 (친구들)
    MarkerOptions friendMarker1;
    MarkerOptions friendMarker2;

    // Drawable 객체 선언
    private Drawable pictureDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 인플레이션
        setContentView(R.layout.activity_main);

        // 프래그먼트 초기화
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // 구글맵 Fragment가 준비 되면 Map 객체 초기화
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "GoogleMap is ready.");

                map = googleMap;

            }
        });

        // MapView 초기화
        try {
            MapsInitializer.initialize(this);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // 내 위치 확인하기 버튼 바인딩 및 리스너 설정, 버튼 클릭시 내 위치로 이동
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMyLocation();
            }
        });

        // Yanzi 자동권한 라이브러리를 이용한 권한 설정
        AndPermission.with(this)
                .runtime()
                .permission(
                        Permission.ACCESS_FINE_LOCATION,
                        Permission.ACCESS_COARSE_LOCATION)
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

    // Toast메세지를 출력해주는 메소드
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    // 내 위치를 가져오는 메소드
    private void requestMyLocation() {
        // LocationManager 객체를 사용하면 사용자의 위치를 파악할 수 있다.
        // 매니페스트에 위치 Permission을 설정해주어야 함
        LocationManager manager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            long minTime = 10000;
            float minDistance = 0;
            // requestLOcationUpdates 함수로 LocationListener를 설정합니다.
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            //위치 정보 전달 목적으로 호출

                            // 내 현재위치를 마커로 표시함
                           showCurrentLocation(location);
                           // 친구 위치를 마커로 표시함
                           addPictures(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            //provider의 상태가 변경되때마다 호출
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            //provider가 사용 가능한 상태가 되는 순간 호출
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            //provider가 사용 불가능 상황이 되는 순간 호출
                        }
                    }
            );
        } catch(SecurityException e) {
            // 에러 출력
            e.printStackTrace();
        }
    }

    private void showCurrentLocation(Location location) {
        // 위도 경도를 담는 객체
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());

        // 현재 위치 위도, 경도를 log창에 출력
        Log.d("Location", "showCurrentLocation: " +location.getLongitude() + " " +location.getLongitude());

        try {
            // 현재 위치로 카메라 이동
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

            // 내 위치를 마커로 표시
            showMyLocationMarker(location);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void showMyLocationMarker(Location location) {
        // 마커 객체가 초기화 되어 있지 않을 때
        if (myLocationMarker == null) {
            // 마커 객체 초기화
            myLocationMarker = new MarkerOptions();
            // 내 위치의 위도, 경도를 로그창에 출력
            Log.d("Location", "내위치: " +location.getLongitude() + " " +location.getLongitude());
            // 마커에 위치를 추가해줌 (마커가 어디에 표시될지)
            myLocationMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
            // 마커 타이틀 설정
            myLocationMarker.title("● 내 위치\n");
            // 마커 설명 추가 
            myLocationMarker.snippet("● GPS로 확인한 위치");
            // 마커 아이콘 그림 추가
            myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
            // 위에서 설정한 마커 객체를 맵에 추가시킴 (맵에 보이게 끔)
            map.addMarker(myLocationMarker);
        } else {
            // 마커 객체가 초기화 되어 있으면 위치만 업데이트 시켜줌
            myLocationMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    // 친구 위치를 마커로 표시해주는 메소드
    private void addPictures(Location location) {
        // 친구 위치를 표시할 이미지를 가져옴
        int pictureResId = R.drawable.friend01;
        // 친구 정보
        String msg = "● 김성수\n"
                + "● 010-7788-1234";
        // 마커 객체가 초기화 되어 있지 않으면
        if (friendMarker1 == null) {
            // 마커 객체 초기화
            friendMarker1 = new MarkerOptions();
            // 마커에 위치를 추가해 줌 (마커가 어디에 표시될지)
            friendMarker1.position(new LatLng(location.getLatitude(), location.getLongitude()-0.005));
            // 친구 위치 위도, 경도를 로그창에 출력
            Log.d("Location", "친구1: " + location.getLongitude() + " " +(location.getLongitude()-0.005));
            // 친구 마커의 제목 설정
            friendMarker1.title("● 친구 1\n");
            // 친구 마커의 설명 추가
            friendMarker1.snippet(msg);
            // 친구 마커의 이미지 설정
            friendMarker1.icon(BitmapDescriptorFactory.fromResource(pictureResId));
            // 친구 마커를 맵에 추가 (맵에 보이게 끔)
            map.addMarker(friendMarker1);
        } else {
            // 마커가 이미 초기화 되어 있으면 위치만 업데이트 시켜줌
            friendMarker1.position(new LatLng(location.getLatitude(), location.getLongitude()-0.005));
        }

        // 친구 위치를 표시할 이미지를 가져옴
        pictureResId = R.drawable.friend04;
        // 친구 정보
        msg = "● 이현수\n"
                + "● 010-5512-4321";

        // 마커 객체가 초기화 되어 있지 않으면
        if (friendMarker2 == null) {
            // 마커 객체 초기화
            friendMarker2 = new MarkerOptions();
            // 마커에 위치를 추가해 줌 (마커가 어디에 표시될지)
            friendMarker2.position(new LatLng(location.getLatitude(), location.getLongitude()+0.005));
            // 친구 마커의 제목 설정
            friendMarker2.title("● 친구 2\n");
            // 친구 마커의 설명 추가
            friendMarker2.snippet(msg);
            // 친구 마커의 이미지 설정
            friendMarker2.icon(BitmapDescriptorFactory.fromResource(pictureResId));
            // 친구 마커를 맵에 추가 (맵에 보이게 끔)
            map.addMarker(friendMarker2);
        } else {
            // 마커가 이미 초기화 되어 있으면 위치만 업데이트 시켜줌
            friendMarker2.position(new LatLng(location.getLatitude()+0.005, location.getLongitude()+0.005));
        }

    }

}