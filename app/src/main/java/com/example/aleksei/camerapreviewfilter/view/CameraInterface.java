package com.example.aleksei.camerapreviewfilter.view;

import android.graphics.Point;
import android.view.TextureView;

public interface CameraInterface {

    Point getDisplaySize();

    int[] getColors();

    TextureView getTextureView();

    void initializeUI();

    void showUI();

    void hideUI();
}
