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

    public static final String KEY_RED_COLOR = "keyRed";
    public static final String KEY_GREEN_COLOR = "keyGreen";
    public static final String KEY_BLUE_COLOR = "keyBlue";
    public static final int PERMISSIONS_REQUEST_CODE = 1;
    private ChiefPresenter chiefPresenter;
    private TextureView textureViewPreviewHolder;
    private SeekBar sbRed;
    private SeekBar sbGreen;
    private SeekBar sbBlue;
    private TableLayout tlBarsViewGroup;
    private TextureListener surfaceTextureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initializeUI();
        if (savedInstanceState != null) {
            sbRed.setProgress(savedInstanceState.getInt(KEY_RED_COLOR));
            sbGreen.setProgress(savedInstanceState.getInt(KEY_GREEN_COLOR));
            sbBlue.setProgress(savedInstanceState.getInt(KEY_BLUE_COLOR));
        }
        chiefPresenter = new ChiefPresenter(this, this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void initializeUI() {
        SeekBarListener seekBarChangedListener = new SeekBarListener();
        surfaceTextureListener = new TextureListener();
        textureViewPreviewHolder = findViewById(R.id.activity_camera_textureview_preview_holder);
        sbRed = findViewById(R.id.activity_camera_rb_red);
        sbGreen = findViewById(R.id.activity_camera_rb_green);
        sbBlue = findViewById(R.id.activity_camera_rb_blue);
        tlBarsViewGroup = findViewById(R.id.activity_camera_tl);
        sbRed.setOnSeekBarChangeListener(seekBarChangedListener);
        sbGreen.setOnSeekBarChangeListener(seekBarChangedListener);
        sbBlue.setOnSeekBarChangeListener(seekBarChangedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (textureViewPreviewHolder.isAvailable()) {
            chiefPresenter.onUIReady();
        } else textureViewPreviewHolder.setSurfaceTextureListener(surfaceTextureListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        chiefPresenter.informToCloseCamera();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_RED_COLOR, sbRed.getProgress());
        outState.putInt(KEY_GREEN_COLOR, sbGreen.getProgress());
        outState.putInt(KEY_BLUE_COLOR, sbBlue.getProgress());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        sbRed.setProgress(savedInstanceState.getInt(KEY_RED_COLOR));
        sbGreen.setProgress(savedInstanceState.getInt(KEY_GREEN_COLOR));
        sbBlue.setProgress(savedInstanceState.getInt(KEY_BLUE_COLOR));
    }

    @Override
    public Point getDisplaySize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point displaySizePoint = new Point();
        display.getSize(displaySizePoint);
        return displaySizePoint;
    }

    @Override
    public int[] getColors() {
        return new int[]{sbRed.getProgress(), sbGreen.getProgress(), sbBlue.getProgress()};
    }

    @Override
    public TextureView getTextureView() {
        return textureViewPreviewHolder;
    }

    @Override
    public void showUI() {
        tlBarsViewGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideUI() {
        tlBarsViewGroup.setVisibility(View.INVISIBLE);
    }

    private class TextureListener implements TextureView.SurfaceTextureListener {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
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

    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            chiefPresenter.onSeekBarsValuesChanged();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}

