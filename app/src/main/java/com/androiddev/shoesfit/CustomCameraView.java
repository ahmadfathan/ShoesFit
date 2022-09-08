package com.androiddev.shoesfit;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

import org.opencv.android.JavaCameraView;

import java.util.List;

public class CustomCameraView extends JavaCameraView {

    public CustomCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void turnOnFlashLight(){
        Camera.Parameters params = mCamera.getParameters();
        List<String> flashModes = params.getSupportedFlashModes();

        if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)){
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

            mCamera.setParameters(params);
        }
    }

    public void turnOffFlashLight(){
        Camera.Parameters params = mCamera.getParameters();
        List<String> flashModes = params.getSupportedFlashModes();

        if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)){
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

            mCamera.setParameters(params);
        }
    }
}
