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
import android.support.v4.app.ActivityCompat;
import android.telecom.VideoProfile;
import android.view.Surface;

import com.example.aleksei.camerapreviewfilter.view.CameraInterface;

import java.util.ArrayList;

public class ChiefPresenter {
    //private ChiefPresenter chiefPresenterInstance;
    private CameraInterface cameraInterfaceInstance;
    private Context context;
    private CameraManager cameraManager;
    private String workingCameraID;
    private int reqCameraFacingType;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder captureBuilder;
    private CameraCaptureSession cameraSession;
    private CameraSessionCallback cameraSessionCallback;


    public ChiefPresenter(CameraInterface cameraInterfaceInstance, Context context) {
        this.cameraInterfaceInstance = cameraInterfaceInstance;
        this.context = context;
    }

   /* public void setChiefPresenterInstance(ChiefPresenter chiefPresenterInstance) {
        this.chiefPresenterInstance = chiefPresenterInstance;
    }*/

    public void onUIReady() {
        CameraCallback cameraCallback = new CameraCallback();
        cameraSessionCallback = new CameraSessionCallback();
        reqCameraFacingType = CameraCharacteristics.LENS_FACING_BACK;
        getCamera();
        openWorkingCamera(workingCameraID, cameraCallback, cameraManager);
    }

    private void getCamera() {
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

    private void openWorkingCamera(String workingCameraID, CameraDevice.StateCallback cameraCallback, CameraManager cameraManager) {
        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(workingCameraID, cameraCallback, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void informToCloseCamera() {
        closeCamera();
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
            // captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_OFF);
            // captureBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
            ArrayList<Surface> surfaces = new ArrayList<>();
            surfaces.add(previewSurface);
            cameraDevice.createCaptureSession(surfaces, cameraSessionCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void onSeekBarsValuesChanged(int red, int green, int blue) {

        // RggbChannelVector rggbChannelVector = new RggbChannelVector( (red / 255f),   (green / 255f),   (green / 255f),  (blue / 255f));
        //RggbChannelVector rggbChannelVector = new RggbChannelVector(1-(255 - red)/255f, 1-(255 - green)/255f, 1-(255 - green)/255f, 1-(255 - blue)/255f);
        //RggbChannelVector rggbChannelVector = new RggbChannelVector((80+ red)/255f, (80+ green)/255f, (80 + green)/255f, (80 + blue)/255f);
        RggbChannelVector rggbChannelVector = new RggbChannelVector((80 + red) / 255f, (80 + green) / 255f, (80 + green) / 255f, (80 + blue) / 255f);
        //RggbChannelVector rggbChannelVector = new RggbChannelVector((150+ red)/405f, (150+ green)/405f, (150 + green)/405f, (150 + blue)/405f);
        captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_OFF);

        ///captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
        ///captureBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 200);


        captureBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
        captureBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, rggbChannelVector);
        try {
            cameraSession.setRepeatingRequest(captureBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    //  public void startPre


    /*public void onTextureViewAvailable() {
        onUIReady();
        //getCamera();
        //openWorkingCamera(workingCameraID, cameraCallback, cameraManager);
    }*/

    /*public void onCameraOpened(CameraDevice camera) {
        cameraDevice = camera;
        cameraInterfaceInstance.showUI();
        createPreviewSession();
    }*/

    /*public void onCameraDisconected(CameraDevice camera) {
        camera.close();
        cameraDevice = null;
    }*/

    /*public void onCameraError(CameraDevice camera) {
        camera.close();
        cameraDevice = null;
    }*/


    class CameraCallback extends CameraDevice.StateCallback {
        @Override
        public void onOpened(CameraDevice camera) {
            //cameraDevice = camera;
            //createPreviewSession();
            //
            // onCameraOpened(camera);

            cameraDevice = camera;
            cameraInterfaceInstance.showUI();
            createPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            //camera.close();
            //cameraDevice = null;
            // onCameraDisconected(camera);
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            //camera.close();
            //cameraDevice = null;
            //onCameraError(camera);
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
}

