package com.example.challenge26;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;


/*
 * 카메라 프리뷰
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    /// SurfaceHolder란 말 그대로 하나의 display surface를 잡고 있는 추상 Interface를 말합니다.
    private SurfaceHolder mHolder;

    // 카메라 변수 선언
    private Camera camera = null;

    public static final String TAG = "CameraSurfaceView";

    // 화면 높이, 넗이
    int surfaceWidth;
    int surfaceHeight;

    // 이미지 높이, 넓이
    int bitmapWidth = 0;
    int bitmapHeight = 0;


    public CameraSurfaceView(Context context) {
        super(context);

        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    // 홀더 초기화
    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    /**
       surface Callback 정의
     */
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();

        try {
            camera.setPreviewDisplay(mHolder);

        } catch (Exception e) {
            Log.e(TAG, "Camera Preview setting failed.", e);
        }
    }

    /**
     * surfaceChanged Callback 정의
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        surfaceWidth = width;
        surfaceHeight = height;

        // 카메라가 회전 했을 시  카메라 화면을 다시 그림
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                camera.setDisplayOrientation(90);
            } else {
                Parameters parameters = camera.getParameters();
                parameters.setRotation(90);
                camera.setParameters(parameters);
            }

            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            camera.release();
        }


        camera.startPreview();
    }

    /**
     * surfaceDestroyed Callback 정의
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.setPreviewCallback(null);
        camera.release();

        camera = null;
    }

}