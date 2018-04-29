package com.example.cc.curiouscam;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
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

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameFilter;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "MainActivity";

    private File mCascadeFile;
    private FaceDetection mfaceDetector;
    private Mat mRgba;
    private Mat mGray;

    double centerFaceX;
    double centerFaceY;
    double resX;
    double resY;
    long sendTimeX = 0;
    long sendTimeY = 0;

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

    //Booleans
    private boolean clicked = true;
    private boolean checkWiFi = false;
    private boolean selfcontrol = false;

    //Camera
    private CameraBridgeViewBase mOpenCvCameraView;

    private opencv_core.IplImage videoImage = null;

    private String ffmpeg_link;
    //private volatile FFmpegFrameRecorder recorder;
    private AudioRecordRunnable audioRecordRunnable;

    boolean recording = false;
    private Mat edgesMat;
    long startTime = 0;

    //Yeni
    final int RECORD_LENGTH = 10;
    Frame[] images;
    long[] timestamps;
    ShortBuffer[] samples;
    int imagesIndex, samplesIndex;
    private Frame yuvImage = null;
    private int imageWidth = 320;
    private int imageHeight = 240;
    private int frameRate = 30;
    private int sampleAudioRateInHz = 44100;
    private FFmpegFrameRecorder recorder;
    private Thread audioThread;
    private AudioRecord audioRecord;
    volatile boolean runAudioThread = true;




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
    int frames = 0;
    private void onFrame(byte[] data)
    {
        /*OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
        if(videoImage != null && recording)
        {
            long videoTimeStamp = 1000 * (System.currentTimeMillis() - startTime);
            ((ByteBuffer)videoImage.createBuffer()).put(data);

            try {
                recorder.setTimestamp(videoTimeStamp);
                recorder.record(converterToMat.convert(videoImage));
                frames++;
            }catch (FFmpegFrameRecorder.Exception e){
                e.printStackTrace();
            }
        }*/
        if (audioRecord == null || audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            startTime = System.currentTimeMillis();
            return;
        }
        if (RECORD_LENGTH > 0) {
            int i = imagesIndex++ % images.length;
            yuvImage = images[i];
            timestamps[i] = 1000 * (System.currentTimeMillis() - startTime);

            /* get video data */
            if (yuvImage != null && recording) {
                ((ByteBuffer)yuvImage.image[0].position(0)).put(data);

                if (RECORD_LENGTH <= 0) try {
                    Log.v(TAG,"Writing Frame");
                    long t = 1000 * (System.currentTimeMillis() - startTime);
                    if (t > recorder.getTimestamp()) {
                        recorder.setTimestamp(t);
                    }


                    recorder.record(yuvImage);

                } catch (FFmpegFrameRecorder.Exception e) {
                    Log.v(TAG,e.getMessage());
                    e.printStackTrace();
                }
            }
        }

    }

    public void startRecording()
    {
        /*

        if(recording)
        {
            startTime = System.currentTimeMillis();

            try {
                recorder.start();
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }else stopRecording();*/
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
        /*runAudioThread = false;

        if(recorder != null)
        {
            try {
                recorder.stop();
                recorder.release();
            }catch (FFmpegFrameRecorder.Exception e)
            {
                e.printStackTrace();
            }
            recorder = null;
        }
        MediaScannerConnection.scanFile(MainActivity.this,new String[]{ffmpeg_link},null,null);*/
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
            if (RECORD_LENGTH > 0) {
                Log.v(TAG,"Writing frames");
                try {
                    int firstIndex = imagesIndex % samples.length;
                    int lastIndex = (imagesIndex - 1) % images.length;
                    if (imagesIndex <= images.length) {
                        firstIndex = 0;
                        lastIndex = imagesIndex - 1;
                    }
                    if ((startTime = timestamps[lastIndex] - RECORD_LENGTH * 1000000L) < 0) {
                        startTime = 0;
                    }
                    if (lastIndex < firstIndex) {
                        lastIndex += images.length;
                    }
                    for (int i = firstIndex; i <= lastIndex; i++) {
                        long t = timestamps[i % timestamps.length] - startTime;
                        if (t >= 0) {
                            if (t > recorder.getTimestamp()) {
                                recorder.setTimestamp(t);
                            }
                            recorder.record(images[i % images.length]);
                        }
                    }

                    firstIndex = samplesIndex % samples.length;
                    lastIndex = (samplesIndex - 1) % samples.length;
                    if (samplesIndex <= samples.length) {
                        firstIndex = 0;
                        lastIndex = samplesIndex - 1;
                    }
                    if (lastIndex < firstIndex) {
                        lastIndex += samples.length;
                    }
                    for (int i = firstIndex; i <= lastIndex; i++) {
                        recorder.recordSamples(samples[i % samples.length]);
                    }
                } catch (FFmpegFrameRecorder.Exception e) {
                    Log.v(TAG,e.getMessage());
                    e.printStackTrace();
                }
            }

            recording = false;
            Log.v(TAG,"Finishing recording, calling stop and release on recorder");
            try {
                recorder.stop();
                recorder.release();
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
            recorder = null;

        }
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
        /*Log.d(TAG,"initRecorder");

        int depth = opencv_core.IPL_DEPTH_8U;
        int channels = 2;
        videoImage = opencv_core.IplImage.create(imageWidth,imageHeight,depth,channels);


        File videoFile = new File(getExternalFilesDir(null), "VideoTest/images/video.mp4");
        boolean mk = videoFile.getParentFile().mkdirs();

        boolean del = videoFile.delete();

        try {
            boolean create = videoFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ffmpeg_link = videoFile.getAbsolutePath();

        recorder = new FFmpegFrameRecorder(ffmpeg_link,imageWidth,imageHeight,1);
        recorder.setFormat("mp4");
        recorder.setSampleRate(44100);
        recorder.setFrameRate(frameRate);

        audioRecordRunnable = new AudioRecordRunnable();
        audioThread = new Thread(audioRecordRunnable);*/
        Log.d(TAG, "init recorder");
        if (RECORD_LENGTH > 0) {
            imagesIndex = 0;
            images = new Frame[RECORD_LENGTH * frameRate];
            timestamps = new long[images.length];
            for (int i = 0; i < images.length; i++) {
                images[i] = new Frame(imageWidth, imageHeight, Frame.DEPTH_UBYTE, 2);
                timestamps[i] = -1;
            }
        } else if (yuvImage == null) {
            yuvImage = new Frame(imageWidth, imageHeight, Frame.DEPTH_UBYTE, 2);
            Log.d(TAG, "create yuvImage");
        }
        ffmpeg_link = "/mnt/sdcard/stream.flv";
        recorder = new FFmpegFrameRecorder(ffmpeg_link,imageWidth,imageHeight,1);
        recorder.setFormat("flv");
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

            bufferSize = AudioRecord.getMinBufferSize(sampleAudioRateInHz, AudioFormat.CHANNEL_OUT_MONO
                    ,AudioFormat.ENCODING_PCM_16BIT);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleAudioRateInHz
                    ,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT,bufferSize);

            if (RECORD_LENGTH > 0) {
                samplesIndex = 0;
                samples = new ShortBuffer[RECORD_LENGTH * sampleAudioRateInHz * 2 / bufferSize + 1];
                for (int i = 0; i < samples.length; i++) {
                    samples[i] = ShortBuffer.allocate(bufferSize);
                }
            } else {
                audioData = ShortBuffer.allocate(bufferSize);
            }

            Log.d(TAG, "audioRecord.startRecording()");
            audioRecord.startRecording();

            while(runAudioThread)
            {
                if (RECORD_LENGTH > 0) {
                    audioData = samples[samplesIndex++ % samples.length];
                    audioData.position(0).limit(0);
                }
                bufferReadResult = audioRecord.read(audioData.array(), 0, audioData.capacity());
                audioData.limit(bufferReadResult);
                if (bufferReadResult > 0) {
                    Log.v(TAG,"bufferReadResult: " + bufferReadResult);
                    // If "recording" isn't true when start this thread, it never get's set according to this if statement...!!!
                    // Why?  Good question...
                    if (recording) {
                        if (RECORD_LENGTH <= 0) try {
                            recorder.recordSamples(audioData);
                            //Log.v(LOG_TAG,"recording " + 1024*i + " to " + 1024*i+1024);
                        } catch (FFmpegFrameRecorder.Exception e) {
                            Log.v(TAG,e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
            Log.d(TAG,"AudioThread Finished, release audioRecord");

            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
                Log.d(TAG,"audioRecord released");

            }
        }

    }

}

