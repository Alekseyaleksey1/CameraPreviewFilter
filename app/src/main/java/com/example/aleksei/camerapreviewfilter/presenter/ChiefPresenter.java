package com.example.aleksei.camerapreviewfilter.presenter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.RggbChannelVector;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;
import com.example.aleksei.camerapreviewfilter.view.CameraInterface;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ChiefPresenter {

    private static final String HANDLER_THREAD_NAME = "handlerThreadName";
    private static final int UI_HANDLER_EMPTY_MESSAGE = 1;
    private CameraInterface cameraInterfaceInstance;
    private Context context;
    private CameraManager cameraManager;
    private String workingCameraID;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder captureBuilder;
    private CameraCaptureSession cameraSession;
    private CameraSessionCallback cameraSessionCallback;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private UIChangeHandler UIHandler;

    public ChiefPresenter(CameraInterface cameraInterfaceInstance, Context context) {
        this.cameraInterfaceInstance = cameraInterfaceInstance;
        this.context = context;
    }

    public void onUIReady() {
        cameraInterfaceInstance.hideUI();
        CameraCallback cameraCallback = new CameraCallback();
        cameraSessionCallback = new CameraSessionCallback();
        UIHandler = new UIChangeHandler(this);
        int reqCameraFacingType = CameraCharacteristics.LENS_FACING_BACK;
        getCamera(reqCameraFacingType);
        openWorkingCamera(workingCameraID, cameraCallback, cameraManager);
    }

    private void getCamera(int reqCameraFacingType) {
        try {
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String[] camerasIDs = cameraManager.getCameraIdList();
            for (String cameraID : camerasIDs) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
                Integer lensFacingCameraCharacteristics = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (lensFacingCameraCharacteristics != null && lensFacingCameraCharacteristics == reqCameraFacingType) {
                    workingCameraID = cameraID;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private static class UIChangeHandler extends Handler {
        private final WeakReference<ChiefPresenter> chiefPresenterWeakReference;

        UIChangeHandler(ChiefPresenter chiefPresenter) {
            chiefPresenterWeakReference = new WeakReference<>(chiefPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            ChiefPresenter chiefPresenter = chiefPresenterWeakReference.get();
            chiefPresenter.cameraInterfaceInstance.showUI();
        }
    }

    private void openBackgroundThread() {
        backgroundThread = new HandlerThread(HANDLER_THREAD_NAME);
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void closeBackgroundThread() {
        if (backgroundHandler != null) {
            backgroundThread.quitSafely();
            backgroundThread = null;
            backgroundHandler = null;
        }
    }

    private void openWorkingCamera(String workingCameraID, CameraDevice.StateCallback cameraCallback, CameraManager cameraManager) {
        openBackgroundThread();
        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(workingCameraID, cameraCallback, backgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void informToCloseCamera() {
        closeCamera();
        closeBackgroundThread();
    }

    private void closeCamera() {
        if (cameraSession != null) {
            cameraSession.close();
            cameraSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void createPreviewSession() {
        Point size = cameraInterfaceInstance.getDisplaySize();
        SurfaceTexture surfaceTexture = cameraInterfaceInstance.getTextureView().getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(size.x, size.y);
        Surface previewSurface = new Surface(surfaceTexture);
        try {
            captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureBuilder.addTarget(previewSurface);
            ArrayList<Surface> surfaces = new ArrayList<>();
            surfaces.add(previewSurface);
            cameraDevice.createCaptureSession(surfaces, cameraSessionCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void onSeekBarsValuesChanged() {
        setupBuilder();
        try {
            cameraSession.setRepeatingRequest(captureBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setupBuilder() {
        int[] colors = cameraInterfaceInstance.getColors();
        RggbChannelVector rggbChannelVector = new RggbChannelVector((160 + colors[0])  / 255f, (80 + colors[1]) / 255f, (80 + colors[1]) / 255f, (160 + colors[2])  / 255f);
        captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_OFF);
        captureBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
        captureBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, rggbChannelVector);
    }

    private class CameraCallback extends CameraDevice.StateCallback {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
        }
    }

   private class CameraSessionCallback extends CameraCaptureSession.StateCallback {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            UIHandler.sendEmptyMessage(UI_HANDLER_EMPTY_MESSAGE);
            cameraSession = session;
            setupBuilder();
            try {
                session.setRepeatingRequest(captureBuilder.build(), null, backgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        }
    }
}

