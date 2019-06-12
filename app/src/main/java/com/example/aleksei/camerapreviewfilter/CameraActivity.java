package com.example.aleksei.camerapreviewfilter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.RggbChannelVector;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;
import android.view.TextureView;
import android.widget.SeekBar;
import java.util.ArrayList;

public class CameraActivity extends Activity {
    TextureView textureView;
    SeekBar sbRed;
    SeekBar sbGreen;
    SeekBar sbBlue;
    ListenSeekBarChange seekBarChangedListener;
    CameraManager cameraManager;
    int reqCameraFacingType;
    TextureListener surfaceTextureListener;
    CameraCallback cameraCallback;
    CameraSessionCallback cameraSessionCallback;
    String workingCameraID;
    public CameraDevice cameraDevice;
    CaptureRequest.Builder captureBuilder;
    CameraCaptureSession cameraSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        textureView = findViewById(R.id.activity_camera_textureview);
        sbRed = findViewById(R.id.activity_camera_rb_red);
        sbGreen = findViewById(R.id.activity_camera_rb_green);
        sbBlue = findViewById(R.id.activity_camera_rb_blue);
        seekBarChangedListener = new ListenSeekBarChange();
        sbRed.setOnSeekBarChangeListener(seekBarChangedListener);
        sbGreen.setOnSeekBarChangeListener(seekBarChangedListener);
        sbBlue.setOnSeekBarChangeListener(seekBarChangedListener);


        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        reqCameraFacingType = CameraCharacteristics.LENS_FACING_BACK;

        surfaceTextureListener = new TextureListener();
        cameraCallback = new CameraCallback();
        cameraSessionCallback = new CameraSessionCallback();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (textureView.isAvailable()) {
            getCamera();
            openCamera();
        } else textureView.setSurfaceTextureListener(surfaceTextureListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeCamera();
    }

    void getCamera() {
        try {
            String[] camerasIDs = cameraManager.getCameraIdList();
            for (int i = 0; i < camerasIDs.length; i++) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(camerasIDs[i]);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == reqCameraFacingType) {
                    workingCameraID = camerasIDs[i];
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(workingCameraID, cameraCallback, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    void closeCamera() {
        if (cameraSession != null) {
            cameraSession.close();
            cameraSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    void createPreviewSession() {
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(1480, 720);
        Surface previewSurface = new Surface(surfaceTexture);
        try {
            captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureBuilder.addTarget(previewSurface);
            captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_OFF);

            //captureBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
            //RggbChannelVector rggbChannelVector = new RggbChannelVector((float) (sbRed.getProgress()/0.4), (float) (sbGreen.getProgress()/0.4), (float) (sbGreen.getProgress()/0.4), (float) (sbBlue.getProgress()/0.4));
            //captureBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, rggbChannelVector);

            ArrayList<Surface> surfaces = new ArrayList<>();
            surfaces.add(previewSurface);
            cameraDevice.createCaptureSession(surfaces, cameraSessionCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    class TextureListener implements TextureView.SurfaceTextureListener {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    }

    class CameraCallback extends CameraDevice.StateCallback {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            createPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
        }
    }

    class CameraSessionCallback extends CameraCaptureSession.StateCallback {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            cameraSession = session;
            //CaptureRequest captureRequest = captureBuilder.build();
            try {
                session.setRepeatingRequest(captureBuilder.build(), null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
    }

    class ListenSeekBarChange implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            RggbChannelVector rggbChannelVector = new RggbChannelVector((float) (sbRed.getProgress() / 0.4), (float) (sbGreen.getProgress() / 0.4), (float) (sbGreen.getProgress() / 0.4), (float) (sbBlue.getProgress() / 0.4));
            captureBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, rggbChannelVector);
            try {
                cameraSession.setRepeatingRequest(captureBuilder.build(), null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}

