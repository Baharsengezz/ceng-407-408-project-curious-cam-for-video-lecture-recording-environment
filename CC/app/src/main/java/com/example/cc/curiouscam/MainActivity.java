package com.example.cc.curiouscam;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "MainActivity";

    private File mCascadeFile;
    private FaceDetection mfaceDetector;
    private Mat mRgba;
    private Mat mGray;
    private Mat edgesMat;

    double centerFaceX;
    double centerFaceY;
    double resX;
    double resY;
    long sendTimeX = 0;
    long sendTimeY = 0;
    long startTime = 0;

    //Buttons
    private Button mRecordButton;
    private Button mSelfControl;
    private Button mProfile;
    private Button mUp;
    private Button mDown;
    private Button mRight;
    private Button mLeft;

    //Socket Client Server
    private Client mClient;
    private String directoryName = "Curious Cam";

    //Booleans
    private boolean clicked = true;
    private boolean checkWiFi = false;
    private boolean selfcontrol = false;
    boolean recording = false;
    volatile boolean runAudioThread = true;

    //Camera
    private CameraBridgeViewBase mOpenCvCameraView;

    //video path
    private String videoPath;

    //Frame
    private Frame videoImage = null;
    //Frame Width, Height and rate
    private int imageWidth = 1280;
    private int imageHeight = 720;
    private int frameRate = 30;

    //Create recorder and audio thread
    private FFmpegFrameRecorder recorder;
    private Thread audioThread;
    private AudioRecord audioRecord;
    private AudioRecordRunnable audioRecordRunnable;
    private int sampleAudioRateInHz = 44100;


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

        //Camera Views
        mOpenCvCameraView = findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setMaxFrameSize(imageWidth,imageHeight);
        mOpenCvCameraView.enableFpsMeter();

        //Buttons
        mRecordButton = findViewById(R.id.button_capture);
        mSelfControl = findViewById(R.id.button_selfcontrol);
        mProfile = findViewById(R.id.button_profile);
        mRight = findViewById(R.id.button_right);
        mLeft = findViewById(R.id.button_left);
        mUp = findViewById(R.id.button_up);
        mDown = findViewById(R.id.button_down);



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
                resX = point.x;
                resY = point.y;
                Log.d(TAG,"H:"+ resX+" W:"+resY);
                sendClientMsg("RESOLUTION:"+resX+":"+resY);
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
                        startRecording();
                    } else if (!clicked) {
                        mRecordButton.setBackground(getDrawable(R.drawable.ic_fiber_manual_record_black_24dp));
                        clicked = true;
                        stopRecording();
                    }
                }else{
                    if(clicked)
                    {
                        mRecordButton.setBackgroundResource(R.drawable.stop_24dp);
                        clicked = false;
                        startRecording();
                    }
                    else if(!clicked)
                    {
                        mRecordButton.setBackgroundResource(R.drawable.ic_fiber_manual_record_black_24dp);
                        clicked = true;
                        stopRecording();
                    }
                }
                sendClientMsg("PUSHED");
            }
        });

        /////////////////////////////SELF CONTROL BUTTON////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        mSelfControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selfcontrol)
                {
                    mRecordButton.setVisibility(View.INVISIBLE);
                    mProfile.setVisibility(View.INVISIBLE);
                    mUp.setVisibility(View.VISIBLE);
                    mDown.setVisibility(View.VISIBLE);
                    mRight.setVisibility(View.VISIBLE);
                    mLeft.setVisibility(View.VISIBLE);
                    selfcontrol = true;
                }
                else if(selfcontrol)
                {
                    mRecordButton.setVisibility(View.VISIBLE);
                    mProfile.setVisibility(View.VISIBLE);
                    mUp.setVisibility(View.INVISIBLE);
                    mDown.setVisibility(View.INVISIBLE);
                    mRight.setVisibility(View.INVISIBLE);
                    mLeft.setVisibility(View.INVISIBLE);
                    selfcontrol = false;
                }
            }
        });

        //////////////////////////////////////////UP BUTTON/////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        mUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendClientMsg("UP");
                Log.d(TAG,"UP");
            }
        });
        //////////////////////////////////////////DOWN BUTTON/////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        mDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendClientMsg("DOWN");
                Log.d(TAG,"DOWN");
            }
        });
        //////////////////////////////////////////RIGHT BUTTON/////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        mRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendClientMsg("RIGHT");
                Log.d(TAG,"RIGHT");
            }
        });
        //////////////////////////////////////////LEFT BUTTON/////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        mLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendClientMsg("LEFT");
                Log.d(TAG,"LEFT");
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
        edgesMat = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
        if (edgesMat != null)
            edgesMat.release();

        edgesMat = null;
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if(recording)
        {
            byte[] byteFrame = new byte[(int) (mRgba.total()*mRgba.channels())];
            mRgba.get(0,0,byteFrame);
            onFrame(byteFrame);
        }

        MatOfRect faces = new MatOfRect();

        //Detect faces
        if (mfaceDetector != null) mfaceDetector.detect(mGray, faces);

        if(!selfcontrol)
        {
            Rect[] facesArray = faces.toArray();
            for (int i = 0; i < facesArray.length; i++) {
                Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar(0,0,255,255), 5);

                centerFaceX = facesArray[i].tl().x + facesArray[i].width/2;
                centerFaceY = facesArray[i].tl().y + facesArray[i].height/2;


                if(centerFaceX > resX/4 && centerFaceX < resX/4*3)
                {
                    //safe zone
                    Log.d(TAG, "Safe X zone");
                }
                else
                {
                    Log.d(TAG, "X="+String.valueOf(centerFaceX));

                    if((System.nanoTime() - sendTimeX)/1000000 >1500)
                    {
                        sendTimeX = System.nanoTime();
                        sendClientMsg("FACEX:"+String.valueOf(centerFaceX));
                    }
                }
                if(centerFaceY > resY/3 && centerFaceY < resY/3*2)
                {
                    //safe zone
                    Log.d(TAG, "Safe Y zone");
                }
                else
                {
                    Log.d(TAG, "Y="+String.valueOf(centerFaceY));

                    if((System.nanoTime() - sendTimeY)/1000000 >1500)
                    {
                        sendTimeY = System.nanoTime();
                        sendClientMsg("FACEY:"+String.valueOf(centerFaceY));
                    }
                }
            }
        }
        return mRgba;
    }
    private void onFrame(byte[] data)
    {
        if (audioRecord == null || audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            startTime = System.currentTimeMillis();
            return;
        }
            /* get video data */
        if (videoImage != null && recording) {
            try {
                ((ByteBuffer)videoImage.image[0].position(0)).put(data);
            }catch (BufferOverflowException e)
            {
                Log.d(TAG,"ByteBuffer Overflow");
            }

            try {
                long t = 1000 * (System.currentTimeMillis() - startTime);
                if (t > recorder.getTimestamp()) {
                    recorder.setTimestamp(t);
                }
                recorder.record(videoImage);

            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void startRecording()
    {
        initRecorder();

        try {
            recorder.start();
            startTime = System.currentTimeMillis();
            recording = true;
            audioThread.start();
        } catch (FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }
    public void stopRecording()
    {
        runAudioThread = false;
        try {
            audioThread.join();
        } catch (InterruptedException e) {
            // reset interrupt to be nice
            Thread.currentThread().interrupt();
            return;
        }
        audioRecordRunnable = null;
        audioThread = null;

        if (recorder != null && recording) {
            recording = false;
            Log.d(TAG,"recorder finished!");
            try {
                recorder.stop();
                recorder.release();
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
            recorder = null;

        }
        launchUploadActivity();
    }
    private void launchUploadActivity(){
        Intent i = new Intent(MainActivity.this, UploadActivity.class);
        i.putExtra("filePath", videoPath);
        startActivity(i);
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

    private void initRecorder()
    {
        Log.d(TAG, "initialize recorder");
        if (videoImage == null) {
            videoImage = new Frame(imageWidth, imageHeight, Frame.DEPTH_UBYTE, 4);
            Log.d(TAG, "create Video Image");
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        File mediaDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),directoryName);

        if(!mediaDir.exists())
        {
            if(!mediaDir.mkdir())
            {
                Log.d(TAG,"Creating directory failed!");
            }
        }

        File videoFile = new File(mediaDir.getPath()+File.separator+"CC_"+timeStamp+".mp4");

        Log.d(TAG,"Video file = "+videoFile.getPath());

        videoPath = videoFile.getPath();
        recorder = new FFmpegFrameRecorder(videoPath,imageWidth,imageHeight,1);
        recorder.setFormat("mp4");
        recorder.setSampleRate(sampleAudioRateInHz);
        recorder.setFrameRate(frameRate);

        Log.d(TAG,"recoreder initialized");

        audioRecordRunnable = new AudioRecordRunnable();
        audioThread = new Thread(audioRecordRunnable);
        runAudioThread = true;

    }

    class AudioRecordRunnable implements Runnable {

        @Override
        public void run() {
            //Thread
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

            // Audio
            int bufferSize;
            ShortBuffer audioData;
            int bufferReadResult;

            bufferSize = AudioRecord.getMinBufferSize(sampleAudioRateInHz, AudioFormat.CHANNEL_IN_MONO
                    ,AudioFormat.ENCODING_PCM_16BIT);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleAudioRateInHz
                    ,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,bufferSize);

            audioData = ShortBuffer.allocate(bufferSize);


            Log.d(TAG, "audioRecord start!");
            audioRecord.startRecording();

            while(runAudioThread)
            {
                bufferReadResult = audioRecord.read(audioData.array(), 0, audioData.capacity());
                audioData.limit(bufferReadResult);
                if (bufferReadResult > 0) {
                    if (recording) {
                        try {
                            recorder.recordSamples(audioData);
                            //Log.v(LOG_TAG,"recording " + 1024*i + " to " + 1024*i+1024);
                        } catch (FFmpegFrameRecorder.Exception e) {
                            Log.v(TAG,e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
            Log.d(TAG,"AudioThread Finished!");

            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
                Log.d(TAG,"audioRecord released!");

            }
        }

    }

}