package com.androiddev.shoesfit.ui;

import static org.opencv.android.CameraBridgeViewBase.CAMERA_ID_BACK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.androiddev.shoesfit.databinding.ActivityMeasurementBinding;
import com.androiddev.shoesfit.util.ColorBlobDetector;
import com.androiddev.shoesfit.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MeasurementActivity extends BaseActivity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String  TAG              = "MeasurementActivity";

    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private ColorBlobDetector mDetector;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;
    
    private boolean begin = false;

    private double footW, footH;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.d(TAG, "Test");
                Log.i(TAG, "OpenCV loaded successfully");

                captured = new Mat();
                binding.cameraViewOpencv.enableView();
                binding.cameraViewOpencv.setOnTouchListener(MeasurementActivity.this);
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    public MeasurementActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    private boolean isPaperDetected = false;
    private ActivityMeasurementBinding binding;

    private boolean flashLightState = false;

    private Mat captured = null;

    private boolean paused = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        
        binding = ActivityMeasurementBinding.inflate(getLayoutInflater());
        
        setContentView(binding.getRoot());

        binding.cameraViewOpencv.setVisibility(SurfaceView.VISIBLE);
        binding.cameraViewOpencv.setCvCameraViewListener(MeasurementActivity.this);

        binding.btnFlash.setOnClickListener(view -> {
            flashLightState = !flashLightState;

            if(flashLightState){
                binding.cameraViewOpencv.turnOnFlashLight();
            }else{
                binding.cameraViewOpencv.turnOffFlashLight();
            }
        });

        binding.btnRetry.setOnClickListener(view -> {
            paused = false;
            binding.containerOption.setVisibility(View.GONE);
        });

        binding.btnNext.setOnClickListener(view -> {
            Intent intent = new Intent(MeasurementActivity.this, ResultActivity.class);
            intent.putExtra("width", footW);
            intent.putExtra("height", footH);

            startActivity(intent);
            finish();
        });

        showTutorial();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        binding.cameraViewOpencv.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.cameraViewOpencv.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        // 154.40625, 36.328125, 171.328125
        mBlobColorHsv = new Scalar(154.40625, 36.328125, 171.328125, 0);
        mDetector.setHsvColor(mBlobColorHsv);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(0,0,255,255);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (binding.cameraViewOpencv.getWidth() - cols) / 2;
        int yOffset = (binding.cameraViewOpencv.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;

        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        Toast.makeText(this, "" + mBlobColorHsv.val.length, Toast.LENGTH_SHORT).show();


        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        Log.i(TAG, "Touched hsv color: (" + mBlobColorHsv.val[0] + ", " + mBlobColorHsv.val[1] +
                ", " + mBlobColorHsv.val[2] +  ", " + mBlobColorHsv.val[3] +")");

        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        mDetector.setHsvColor(mBlobColorHsv);

        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE, 0, 0, Imgproc.INTER_LINEAR);

        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if(begin && !paused){
            mRgba = inputFrame.rgba();
            mDetector.process(mRgba);
            List<MatOfPoint> contours = mDetector.getContours();
            //Log.e(TAG, "Contours count: " + contours.size());

            if(contours.size() == 1){
                Point[] p = contours.get(0).toArray();

                Point pTopLeft = new Point(), pBottomRight = new Point();

                double maxSum = 0, minSum = mRgba.height() + mRgba.width();

                for (Point point : p) {
                    if((point.x + point.y) > maxSum){
                        maxSum = point.x + point.y;
                        pBottomRight = point;
                    }
                    if((point.x + point.y) < minSum){
                        minSum = point.x + point.y;
                        pTopLeft = point;
                    }
                }

                Point[] p_flipped = contours.get(0).toArray();

                for (Point point : p_flipped) {
                    point.set(new double[]{mRgba.width() - point.x, point.y});
                }

                Point pBottomLeft = new Point(), pTopRight = new Point();

                maxSum = 0;
                minSum = mRgba.height() + mRgba.width();

                for (Point point : p_flipped) {
                    if((point.x + point.y) > maxSum){
                        maxSum = point.x + point.y;
                        pBottomLeft.set(new double[]{mRgba.width() - point.x, point.y});
                    }
                    if((point.x + point.y) < minSum){
                        minSum = point.x + point.y;
                        pTopRight.set(new double[]{mRgba.width() - point.x, point.y});
                    }
                }

                Imgproc.circle(mRgba, pTopLeft, 8, CONTOUR_COLOR,4);
                Imgproc.circle(mRgba, pBottomRight, 8, CONTOUR_COLOR,4);
                Imgproc.circle(mRgba, pTopRight, 8, CONTOUR_COLOR,4);
                Imgproc.circle(mRgba, pBottomLeft, 8, CONTOUR_COLOR,4);

                Imgproc.putText(mRgba,"P1", pTopLeft, Core.FONT_HERSHEY_PLAIN,4, new Scalar(0, 255, 0, 255));
                Imgproc.putText(mRgba,"P2", pTopRight, Core.FONT_HERSHEY_PLAIN,4, new Scalar(0, 255, 0, 255));
                Imgproc.putText(mRgba,"P3", pBottomRight, Core.FONT_HERSHEY_PLAIN,4, new Scalar(0, 255, 0, 255));
                Imgproc.putText(mRgba,"P4", pBottomLeft, Core.FONT_HERSHEY_PLAIN,4, new Scalar(0, 255, 0, 255));

                Rect rect = Imgproc.boundingRect(contours.get(0));

                boolean allCornerAreFitted = true;

                // Top Left
                if(
                        (pTopLeft.x > rect.x) && (pTopLeft.x < rect.x + 30) &&
                        (pTopLeft.y > rect.y) && (pTopLeft.y < rect.y + 30))
                {
                    Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x + 50, rect.y + 50), new Scalar(0,255,0,50), 50);
                }else{
                    Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x + 50, rect.y + 50), new Scalar(255,0,0,50), 50);
                    allCornerAreFitted = false;
                }

                // Bottom Left
                if(
                        (pBottomLeft.x > rect.x) && (pBottomLeft.x < rect.x + 30) &&
                        (pBottomLeft.y < rect.y + rect.height) && (pBottomLeft.y > rect.y + rect.height - 30))
                {
                    Imgproc.rectangle(mRgba, new Point(rect.x, rect.y + rect.height - 50), new Point(rect.x + 50, rect.y + rect.height), new Scalar(0, 255, 0, 50), 50);
                }else{
                    Imgproc.rectangle(mRgba, new Point(rect.x, rect.y + rect.height - 50), new Point(rect.x + 50, rect.y + rect.height), new Scalar(255, 0, 0, 50), 50);
                    allCornerAreFitted = false;
                }

                // Top Right
                if(
                        (pTopRight.x > rect.x + rect.width - 30) && (pTopRight.x < rect.x + rect.width) &&
                        (pTopRight.y > rect.y) && (pTopRight.y < rect.y + 30))
                {
                    Imgproc.rectangle(mRgba, new Point(rect.x + rect.width - 50, rect.y), new Point(rect.x + rect.width, rect.y + 50), new Scalar(0, 255, 0,50), 50);
                }else{
                    Imgproc.rectangle(mRgba, new Point(rect.x + rect.width - 50, rect.y), new Point(rect.x + rect.width, rect.y + 50), new Scalar(255, 0, 0,50), 50);
                    allCornerAreFitted = false;
                }

                // Bottom Right
                if(
                        (pBottomRight.x > rect.x + rect.width - 30) && (pBottomRight.x < rect.x + rect.width) &&
                                (pBottomRight.y < rect.y + rect.height) && (pBottomRight.y > rect.y + rect.height - 30))
                {
                    Imgproc.rectangle(mRgba, new Point(rect.x + rect.width - 50, rect.y + rect.height - 50), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0,255,0,50), 50);
                }else{
                    Imgproc.rectangle(mRgba, new Point(rect.x + rect.width - 50, rect.y + rect.height - 50), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0 ,0,50), 50);
                    allCornerAreFitted = false;
                }

                if(allCornerAreFitted){
                    /*
                    List<Point> srcPoints= new ArrayList<>();
                    srcPoints.add(pTopLeft);
                    srcPoints.add(pTopRight);
                    srcPoints.add(pBottomRight);
                    srcPoints.add(pBottomLeft);

                    List<Point> dstPoints= new ArrayList<>();
                    dstPoints.add(new Point(0,0));
                    dstPoints.add(new Point(210 - 1,0));
                    dstPoints.add(new Point(210 - 1,297 - 1));
                    dstPoints.add(new Point(0, 297 - 1));

                    Mat srcMat = Converters.vector_Point2f_to_Mat(srcPoints);
                    Mat dstMat = Converters.vector_Point2f_to_Mat(dstPoints);

                    //getting the transformation matrix
                    Mat perspectiveTransformation = Imgproc.getPerspectiveTransform(srcMat,dstMat);

                    //getting the output matrix with the previously determined sizes
                    //Mat outputMat = new Mat(297 , 210, CvType.CV_8UC1);

                    //applying the transformation
                    //Imgproc.warpPerspective(mRgba, outputMat, perspectiveTransformation,new Size(210,297));

                    //return  outputMat;


                    /* Detect width and height of footpad*/

                    List<Point> p_inPaper = new ArrayList<>();
                    for (Point point : p) {
                        if(point.x > (pTopLeft.x + 32) && point.x < (pBottomRight.x - 32)
                                && point.y > (pTopLeft.y + 32) && point.y < (pBottomRight.y - 255)
                        ){
                            p_inPaper.add(point);
                        }
                    }

                    Point footYfrom = new Point();
                    Point footXfrom = new Point();
                    Point footYto = new Point();
                    Point footXto = new Point();

                    double minY = mRgba.height(), minX = mRgba.width(), maxX = 0;

                    for (Point point : p_inPaper) {
                        if(point.y < minY){
                            minY = point.y;
                            footYfrom = point;
                        }
                        if(point.x > maxX){
                            maxX = point.x;
                            footXto = point;
                        }
                        if(point.x < minX){
                            minX = point.x;
                            footXfrom = point;
                        }
                    }

                    footXfrom.set(new double[]{footXfrom.x, footXto.y});
                    footYto.set(new double[]{footYfrom.x, pBottomRight.y});

                    Imgproc.line(mRgba, footXfrom, footXto, new Scalar(255,0,0,255),3);

                    Imgproc.line(mRgba, footYfrom, footYto, new Scalar(255,0,0,255),3);

                    Imgproc.line(
                            mRgba,
                            new Point(footXfrom.x, footYfrom.y),
                            new Point(footXto.x, footYfrom.y),
                            new Scalar(255,255,0,255),3
                    );

                    Imgproc.line(
                            mRgba,
                            new Point(footXto.x, footYfrom.y),
                            new Point(footXto.x, footYto.y),
                            new Scalar(255,255,0,255),3
                    );

                    Imgproc.line(
                            mRgba,
                            new Point(footXfrom.x, footYto.y),
                            new Point(footXto.x, footYto.y),
                            new Scalar(255,255,0,255),3
                    );

                    Imgproc.line(
                            mRgba,
                            new Point(footXfrom.x, footYfrom.y),
                            new Point(footXfrom.x, footYto.y),
                            new Scalar(255,255,0,255),3
                    );

                    // 210 x 297

                    footW = ((footXto.x - footXfrom.x) * 210)/(pBottomRight.x - pTopLeft.x);
                    footH = ((footYto.y - footYfrom.y) * 297)/(pBottomRight.y - pTopLeft.y);

                    paused = true;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.containerOption.setVisibility(View.VISIBLE);
                            binding.cameraViewOpencv.turnOffFlashLight();
                        }
                    });

                    //Imgproc.putText(mRgba,"" + (int)footH + "mm", footYfrom, Core.FONT_HERSHEY_PLAIN,4, new Scalar(0, 255, 0, 255),4);
                    //Imgproc.putText(mRgba,"" + (int)footW + "mm", footXto, Core.FONT_HERSHEY_PLAIN,4, new Scalar(0, 255, 0, 255),4);

                    //Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), CONTOUR_COLOR, 4);
                }else{
                    Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255,0,0,255), 4);
                }
            }

            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

            //Mat colorLabel = mRgba.submat(4, 68, 4, 68);
            //colorLabel.setTo(mBlobColorRgba);

            //Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());

            //mSpectrum.copyTo(spectrumLabel);

            if (paused) {
                captured = mRgba.clone();
            }
        }

        if (paused) {
            return captured;
        }

        return mRgba;
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

    private void showTutorial(){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        // Setting Dialog Title
        adb.setTitle("Petunjuk Pengukuran");
        // Setting Dialog Message
        adb.setView(R.layout.dialog_tutorial);
        adb.setMessage("Posisi kaki seperti pada gambar");
        // Setting Icon to Dialog
        adb.setIcon(R.mipmap.ic_launcher);
        // Setting OK Button
        adb.setPositiveButton("Mengerti", (dialog, which) -> begin = true);

        adb.show();
    }

}
