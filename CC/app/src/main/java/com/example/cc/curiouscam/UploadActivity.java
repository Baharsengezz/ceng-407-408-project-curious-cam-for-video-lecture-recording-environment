package com.example.cc.curiouscam;

import android.app.Activity;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * Created by Alihan on 7.05.2018.
 */

public class UploadActivity extends Activity {
    // LogCat tag
    private static final String TAG = "UploadActivity";

    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private VideoView videoPreview;
    private EditText videoTitle;
    private EditText email;
    private EditText password;
    private Button btnUpload;
    long totalSize = 0;

    //Server URL
    private String uploadURL = "http://192.168.4.3/VideoUpload/videoUpload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        txtPercentage = findViewById(R.id.txtPercentage);
        btnUpload = findViewById(R.id.btnUpload);
        progressBar = findViewById(R.id.progressBar);
        videoPreview = findViewById(R.id.videoPreview);
        videoTitle = findViewById(R.id.videoTitle);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        // take the file path data from MainActivity
        Intent i = getIntent();

        // take video file path
        filePath = i.getStringExtra("filePath");

        if (filePath != null) {
            previewMedia();
        } else {
            //No file path found!
            Toast.makeText(getApplicationContext(),"Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
                new UploadFileToServer().execute();
            }
        });

    }
    private void previewMedia() {
            videoPreview.setVisibility(View.VISIBLE);
            videoPreview.setVideoPath(filePath);
            videoPreview.start();
    }

    // Upload the video file to wamp server
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress[0]);
            txtPercentage.setText("% " + String.valueOf(progress[0]));
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uploadURL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Parameters
                entity.addPart("email", new StringBody(email.getText().toString()));
                entity.addPart("password", new StringBody(password.getText().toString()));
                entity.addPart("title", new StringBody(videoTitle.getText().toString()));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // take server response
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity responseEntity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // file uploaded server correctly
                    Log.d(TAG,"Response from server : " + EntityUtils.toString(responseEntity));
                    responseString = "Video file was uploaded to the server correctly!";
                } else {
                    responseString = "Error occurred! Please try again! Error Code :" + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            showAlert(result);
            super.onPostExecute(result);
        }

    }

    //Show response in the AlertDialog
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Message : ")
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Close the AlertDialog
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
