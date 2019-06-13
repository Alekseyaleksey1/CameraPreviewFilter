package com.example.aleksei.camerapreviewfilter.view;

import android.Manifest;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TableLayout;

import com.example.aleksei.camerapreviewfilter.R;
import com.example.aleksei.camerapreviewfilter.presenter.ChiefPresenter;

public class CameraActivity extends Activity implements CameraInterface {

    public static final int PERMISSIONS_REQUEST_CODE = 1;
    private ChiefPresenter chiefPresenter;
    private TextureView textureViewPreviewHolder;
    private SeekBar sbRed;
    private SeekBar sbGreen;
    private SeekBar sbBlue;
    private TableLayout tlBarsViewGroup;

    //CameraManager cameraManager;
    //int reqCameraFacingType;
    //CameraCallback cameraCallback;
    //CameraSessionCallback cameraSessionCallback;
    //String workingCameraID;
    //public CameraDevice cameraDevice;
    //CaptureRequest.Builder captureBuilder;
    //CameraCaptureSession cameraSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initializeUI();
        chiefPresenter = new ChiefPresenter(this, this);
        //chiefPresenter.setChiefPresenterInstance(chiefPresenter);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void initializeUI() {
        SeekBarListener seekBarChangedListener = new SeekBarListener();
        TextureListener surfaceTextureListener = new TextureListener();
        textureViewPreviewHolder = findViewById(R.id.activity_camera_textureview_preview_holder);
        textureViewPreviewHolder.setSurfaceTextureListener(surfaceTextureListener);
        sbRed = findViewById(R.id.activity_camera_rb_red);
        sbGreen = findViewById(R.id.activity_camera_rb_green);
        sbBlue = findViewById(R.id.activity_camera_rb_blue);
        tlBarsViewGroup = findViewById(R.id.activity_camera_tl);
        sbRed.setOnSeekBarChangeListener(seekBarChangedListener);
        sbGreen.setOnSeekBarChangeListener(seekBarChangedListener);
        sbBlue.setOnSeekBarChangeListener(seekBarChangedListener);

        //cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        //reqCameraFacingType = CameraCharacteristics.LENS_FACING_BACK;
        //surfaceTextureListener = new TextureListener();
        //textureViewPreviewHolder.setSurfaceTextureListener(surfaceTextureListener);
        //cameraCallback = new CameraCallback();
        //cameraSessionCallback = new CameraSessionCallback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (textureViewPreviewHolder.isAvailable()) {
            chiefPresenter.onUIReady();
            // getCamera();
            // openCamera();
        } //else textureViewPreviewHolder.setSurfaceTextureListener(surfaceTextureListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        chiefPresenter.informToCloseCamera();
    }

    /*@Override
    public void getCamera() {
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

    }*/

    /*@Override
    public void openCamera(String workingCameraID, CameraDevice.StateCallback cameraCallback, CameraManager cameraManager) {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(workingCameraID, cameraCallback, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeCamera() {
        if (cameraSession != null) {
            cameraSession.close();
            cameraSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }*/

    @Override
    public Point getDisplaySize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point displaySizePoint = new Point();
        display.getSize(displaySizePoint);
        return displaySizePoint;
    }

    @Override
    public TextureView getTextureView() {
        return textureViewPreviewHolder;
    }

    @Override
    public void showUI() {
        tlBarsViewGroup.setVisibility(View.VISIBLE);
        /*/sbRed.setVisibility(View.VISIBLE);
        sbGreen.setVisibility(View.VISIBLE);
        sbBlue.setVisibility(View.VISIBLE);*/
    }

    /**
     * @Override public void hideUI() {
     * sbRed.setVisibility(View.INVISIBLE);
     * sbGreen.setVisibility(View.INVISIBLE);
     * sbBlue.setVisibility(View.INVISIBLE);
     * }
     */

    /*@Override
    public void createPreviewSession() {
        Point size = getDisplaySize();
        SurfaceTexture surfaceTexture = textureViewPreviewHolder.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(size.x, size.y);
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

    }*/

    private class TextureListener implements TextureView.SurfaceTextureListener {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //chiefPresenter.getCamera();
            //openCamera();
            chiefPresenter.onUIReady();
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

   /* class CameraCallback extends CameraDevice.StateCallback {
        @Override
        public void onOpened(CameraDevice camera) {
            //cameraDevice = camera;
            createPreviewSession();
            chiefPresenter.onCameraOpened(camera);
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            //camera.close();
            cameraDevice = null;
            chiefPresenter.onCameraDisconected(camera);
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            //camera.close();
            cameraDevice = null;
            chiefPresenter.onCameraError(camera);
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
    }*/

    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            chiefPresenter.onSeekBarsValuesChanged(sbRed.getProgress(), sbGreen.getProgress(), sbBlue.getProgress());
/*
            RggbChannelVector rggbChannelVector = new RggbChannelVector((sbRed.getProgress() / 255f), (sbGreen.getProgress() / 255f), (sbGreen.getProgress() / 255f), (sbBlue.getProgress() / 255f));

            chiefPresenter.captureBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, rggbChannelVector);
            try {
                chiefPresenter.cameraSession.setRepeatingRequest(chiefPresenter.captureBuilder.build(), null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }*/
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}

