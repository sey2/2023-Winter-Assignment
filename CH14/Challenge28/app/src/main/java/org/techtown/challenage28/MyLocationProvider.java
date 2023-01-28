package org.techtown.challenage28;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.List;


/*
    해당 프로젝트는 SMS를 보내는 프로젝트이기에 실제 기기에 USIM 칩이 꽂혀 있어야 동작 과정을 확인할 수 있습니다.
    에뮬레이터는 SMS를 보낼 떄 소켓 통신으로 다른 에뮬레이터로 보내는 형식으로 테스트 가능하나 따로 설정이 필요합니다.
 */

public class MyLocationProvider extends AppWidgetProvider {

    // 메세지 보낼 전화 번호
    static String receiver = "010-2024-2978";
    static String info = "";

    // PendingIntent는 가지고 있는 Intent 를 당장 수행하진 않고 특정 시점에 수행하도록 하는 특징을 갖고 있다.
    // 이 '특정 시점'이라 함은, 보통 해당 앱이 구동되고 있지 않을 때이다.
    static PendingIntent sentIntent;
    static PendingIntent deliveredIntent;

    // 위젯이 삭제 되었을 때
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // intent가 null이면 새로운 인텐트를 만들어 PendingIntent에 담는다
        // SMS 가 보내질 때 인텐트 동작하도록 설정
        if (sentIntent == null) {
            sentIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT_ACTION"), 0);
        }

        // intent가 null이면 새로운 인텐트를 만들어 PendingIntent에 담는다
        // 문자 메시지가 보내졌을 때 인텐트가 동작하도록 설정
        if (deliveredIntent == null) {
            deliveredIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_DELIVERED_ACTION"), 0);
        }

        Log.d("MyLocationProvider", "onUpdate() called.");

        final int size = appWidgetIds.length;

        for (int i = 0; i < size; i++) {
            int appWidgetId = appWidgetIds[i];

            // GPSLocationService 클래스를 담은 인텐트를 생성
            Intent sendIntent = new Intent(context, GPSLocationService.class);

            // 인텐트에 값을 담아준다.
            sendIntent.putExtra("command", "send");
            sendIntent.putExtra("receiver", receiver);

            // PendingIntent에 Service를 시작하는 인텐트를 담음
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, sendIntent, 0);

            /* 무엇보다도 AppWidget 은 실재 어플리케이션과 서로 다른 프로세스에서 동작하는 만큼,
            직접적으로 화면상에 그림을 그릴 수 없습니다. 자신이 원하는 형태로 화면을 그리도록 AppWidgetService 를 통해 AppWidget Host 에게 부탁해야합니다.
             그리고 이 때 사용되는 클래스가 바로 RemoteViews 입니다.
             */
            // RemoteView 객체 생성 후 인플레이션
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mylocation);

            // 위젯의 버튼 클릭시 pendingIntent가 실행
            views.setOnClickPendingIntent(R.id.button, pendingIntent);

            /**
             * 위젯의 형태를 업데이트합니다.
             *
             * @param context 컨텍스트
             * @param appWidgetManager 위젯 메니저
             * @param appWidgetId 업데이트할 위젯 아이디
             */
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        // GPSLocationSevice를 담는 인텐트 객체 생성
        Intent startIntent = new Intent(context, GPSLocationService.class);
        // 인텐트에 value 값 추가
        startIntent.putExtra("command", "start");

        // 버전에 따라
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 포그라운드에서 서비스 시작
            context.startForegroundService(startIntent);
        } else {
            // 서비스 시작
            context.startService(startIntent);
        }

    }


    public static class GPSLocationService extends Service {
        public static final String TAG = "GPSLocationService";

        // 위치 좌표를 담는 객체
        public static double ycoord = 0.0D;
        public static double xcoord = 0.0D;

        // location 객체 선언
        private LocationManager manager = null;

        // locatoinListener 설정
        private LocationListener listener = new LocationListener() {

            public void onStatusChanged(String provider, int status, Bundle extras) {
                //provider의 상태가 변경되때마다 호출
            }

            public void onProviderEnabled(String provider) {
                //provider가 사용 가능한 상태가 되는 순간 호출
            }

            public void onProviderDisabled(String provider) {
                //provider가 사용 불가능 상황이 되는 순간 호출
            }

            public void onLocationChanged(Location location) {
                //위치 정보 전달 목적으로 호출
                Log.d(TAG, "onLocationChanged() called.");

                updateCoordinates(location.getLatitude(), location.getLongitude());

                stopSelf();
            }
        };

        // BroadCastReciver 리스너 설정 메세지 전송시
        BroadcastReceiver sentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode()){
                    case Activity.RESULT_OK:
                        // 전송 성공시
                        Toast.makeText(context, "전송 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        // 전송 실패시
                        Toast.makeText(context, "전송 실패", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // 서비스 지역 아닐시
                        Toast.makeText(context, "서비스 지역 아님", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        // 무선 설정이 꺼져 있을 시
                        Toast.makeText(context, "무선(Radio)이 꺼져 있음", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        // PDU 실패시
                        Toast.makeText(context, "PDU 실패", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        // BroadcastReceiver 리스너 설정 (메세지 전송 성공시)
        BroadcastReceiver deliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        // 도착 완료시
                        Toast.makeText(context, "SMS 도착 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // 도착 안했을 시
                        Toast.makeText(context, "SMS 도착 안됨", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        // Service 클래스 상속시 구현 해야하는 메소드
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        // 서비스 시작시
        public void onCreate() {
            super.onCreate();

            Log.d(TAG, "onCreate() called.");

            // LocationManager 객체 초기화
            manager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // 앱이 Broadcast를 받으려면 먼저 Receiver를 등록해야 합니다.
            // BroadcastReceiver 객체를 생성하고 이것을 Manifest에 등록하거나 registerReceiver()로 등록해야 합니다
            registerReceiver(sentReceiver, new IntentFilter("SMS_SENT_ACTION"));
            registerReceiver(deliveredReceiver, new IntentFilter("SMS_DELIVERED_ACTION"));

            // Android O(API 26)에서 Notification Channel 개념이 추가되었습니다.
            // 그래서 Android O이상의 디바이스에 Notification을 띄우려먼 먼저 Channel을 생성해야 합니다.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                final String strId = "LocatonProvider";
                final String strTitle = "LocatonProvider";

                // NotificationManager 객체 초기화
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // NotificationChannel 객체 초기화
                NotificationChannel channel = notificationManager.getNotificationChannel(strId);

                // NotificationChannel이 null 값일 시 초기화
                if (channel == null) {
                    channel = new NotificationChannel(strId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                }

                // Notification 알림창 띄우기
                Notification notification = new NotificationCompat.Builder(this, strId).build();
                startForeground(1, notification);
            }

        }

        /*
        service가 생성되고 다음으로 실행되는 함수는 onstart()함수이나, onStartCommand()함수를 사용할 것을 권장합니다.
        또 서비스가 실행되고 있는 상태에서 또 서비스 시작을 할 경우 onStartCommand()함수를 탑니다
        */
        public int onStartCommand(Intent intent, int flags, int startId) {
            // intent가 도착 했을시
            if (intent != null) {
                String command = intent.getStringExtra("command");
                if (command != null) {
                    if (command.equals("start")) {
                        // 기기 상황에 따른 옵션 설정
                        startListening();
                    } else if (command.equals("send")) {
                        // 문자 전송
                        String receiver = intent.getStringExtra("receiver");
                        String contents = "내 위치 : " + xcoord + ", " + ycoord ;
                        sendSMS(receiver, contents);
                    }
                }
            }

            return super.onStartCommand(intent, flags, startId);
        }

        // 서비스 종료시
        public void onDestroy() {
            stopListening();

            // Receiver 해제
            unregisterReceiver(sentReceiver);
            unregisterReceiver(deliveredReceiver);

            Log.d(TAG, "onDestroy() called.");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopForeground(true);
            }

            super.onDestroy();
        }

        // sms 보내기 메소드
        private void sendSMS(String receiver, String contents) {
            SmsManager mSmsManager = SmsManager.getDefault();
            mSmsManager.sendTextMessage(receiver, null, contents, sentIntent, deliveredIntent);
        }

        private void startListening() {
            Log.d(TAG, "startListening() called.");

            final Criteria criteria = new Criteria();

            // 정확도에 대한 조건
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // 고도 제공 여부
            criteria.setAltitudeRequired(false);
            // 방향 제공 여부
            criteria.setBearingRequired(false);
            // 비용이 드는 것을 허용할 것인지
            criteria.setCostAllowed(true);
            // 전원 소비량 조건
            criteria.setPowerRequirement(Criteria.POWER_LOW);

            // 위에서 설정한 조건을 가져옴
            final String bestProvider = manager.getBestProvider(criteria, true);

            try {
                if (bestProvider != null && bestProvider.length() > 0) {
                    // 위치가 변경 될때마다 이벤트를 받음
                    manager.requestLocationUpdates(bestProvider, 500, 10, listener);
                } else {
                    final List<String> providers = manager.getProviders(true);

                    for (final String provider : providers) {
                        manager.requestLocationUpdates(provider, 500, 10, listener);
                    }
                }
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        }

        private void stopListening() {
            try {
                if (manager != null && listener != null) {
                    manager.removeUpdates(listener);
                }

                manager = null;
            } catch (final Exception ex) {

            }
        }

        // 위치 정보 업데이트
        private void updateCoordinates(double latitude, double longitude) {
            // 위도 경도 객체 선언 후 초기화
            Geocoder coder = new Geocoder(this);

            // 주소를 담을 arrayList
            List<Address> addresses = null;
            // 위치 정보를 담을 Strinf 객체
            info = "";

            Log.d(TAG, "updateCoordinates() called.");

            try {
                // 현재 위치를 가져옴
                addresses = coder.getFromLocation(latitude, longitude, 2);

                // 현재 위치 Parsing
                if (null != addresses && addresses.size() > 0) {
                    int addressCount = addresses.get(0).getMaxAddressLineIndex();

                    if (-1 != addressCount) {
                        for (int index = 0; index <= addressCount; ++index) {
                            info += addresses.get(0).getAddressLine(index);

                            if (index < addressCount)
                                info += ", ";
                        }
                    } else {
                        info += addresses.get(0).getFeatureName() + ", "
                                + addresses.get(0).getSubAdminArea() + ", "
                                + addresses.get(0).getAdminArea();
                    }
                }

                Log.d(TAG, "Address : " + addresses.get(0).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            coder = null;
            addresses = null;

            if (info.length() <= 0) {
                info = "[내 위치] " + latitude + ", " + longitude;
            } else {
                info += ("\n" + "[내 위치] " + latitude + ", " + longitude + ")");
            }

            // 위젯 뷰에 sms 보낸 위치를 표시
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.mylocation);
            views.setTextViewText(R.id.txtInfo, info);
            ComponentName thisWidget = new ComponentName(this, MyLocationProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, views);

            // 위도 경도 설정
            xcoord = longitude;
            ycoord = latitude;
            Log.d(TAG, "coordinates : " + latitude + ", " + longitude);

        }

    }

}
