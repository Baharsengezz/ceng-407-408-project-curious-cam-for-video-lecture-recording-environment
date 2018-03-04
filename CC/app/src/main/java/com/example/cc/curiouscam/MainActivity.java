package com.example.cc.curiouscam;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "MainActivity";

    private File mCascadeFile;
    private FaceDetection mfaceDetector;
    private Mat mRgba;
    private Mat mGray;

    private Button mRecordButton;

    //Socket Client Server
    private Client mClient;

    private boolean clicked = true;
    private boolean checkWiFi = false;
    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV LOADED !");

                    //Native loading
                    System.loadLibrary("detection_based_tracker");

                    loadCascadeFiles();

                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Camera Vies
        mOpenCvCameraView = findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        //Buttons
        mRecordButton = findViewById(R.id.button_capture);

        ////////////////////////////////////CHECK WiFi CONNECTION///////////////////////////////////
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (info.isConnected()) {
            WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifi = wifiManager.getConnectionInfo();

            if(wifi.getSSID().equals("\"CuriousCam\""))
            {
                checkWiFi = true;
                Toast.makeText(getApplicationContext(),"Connection established!",Toast.LENGTH_SHORT).show();
                ///////////////////////////////GET RESOLUTION OF SCREEN/////////////////////////////
                Display display = getWindowManager().getDefaultDisplay();
                android.graphics.Point point = new android.graphics.Point();
                display.getSize(point);
                Log.d(TAG,"H:"+ point.x+" W:"+point.y);
                sendClientMsg("RESOLUTION:"+point.x+":"+point.y);
            }
            else{
                Toast.makeText(getApplicationContext(),"Please connect to CuriousCam",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Please connect to CuriousCam over Wi-Fi",Toast.LENGTH_LONG).show();
        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////RECORD BUTTON//////////////////////////////////////////
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 21) {
                    if (clicked) {
                        mRecordButton.setBackground(getDrawable(R.drawable.stop_24dp));
                        clicked = false;
                    } else if (!clicked) {
                        mRecordButton.setBackground(getDrawable(R.drawable.ic_fiber_manual_record_black_24dp));
                        clicked = true;
                    }
                }else{
                    if(clicked)
                    {
                        mRecordButton.setBackgroundResource(R.drawable.stop_24dp);
                        clicked = false;
                    }
                    else if(!clicked)
                    {
                        mRecordButton.setBackgroundResource(R.drawable.ic_fiber_manual_record_black_24dp);
                        clicked = true;
                    }
                }
                sendClientMsg("PUSHED");
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV NOT FOUND ON THE PROJECT FILE!");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV FOUND!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        MatOfRect faces = new MatOfRect();

        //Detect faces
        if (mfaceDetector != null) mfaceDetector.detect(mGray, faces);


        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar(0,0,255,255), 5);

            Log.d(TAG, facesArray[i].tl().toString());
            sendClientMsg("FACE:"+String.valueOf(facesArray[i].tl().x)+":"+String.valueOf(facesArray[i].tl().y));
        }

        return mRgba;
    }

    ///////////////////////////////LOAD PROFILE AND FRONTAL LBP CASCADES///////////////////////////
    public void loadCascadeFiles()
    {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.lbpcascade_profileface);
            File cascade = getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascade,"lbpcascade_profileface.xml");

            FileOutputStream fileOutputStream = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            mfaceDetector = new FaceDetection(mCascadeFile.getAbsolutePath(), 0);

            inputStream = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            mCascadeFile = new File(cascade,"lbpcascade_frontalface.xml");
            fileOutputStream = new FileOutputStream(mCascadeFile);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            mfaceDetector = new FaceDetection(mCascadeFile.getAbsolutePath(), 0);

            inputStream.close();
            fileOutputStream.close();

            cascade.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Cascades loading failed :" + e);
        }
    }

    public void sendClientMsg(String cmd)
    {
        if(checkWiFi)
        {
            mClient = new Client();
            mClient.setCommand(cmd);
            mClient.execute();
        }
    }

}
