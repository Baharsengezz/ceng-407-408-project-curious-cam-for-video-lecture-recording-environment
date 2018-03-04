package com.example.cc.curiouscam;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Doğuş on 28.02.2018.
 */
public class Client extends AsyncTask<Void,Void,Void> {

    private String addr = "192.168.4.1";
    private int port = 5000;
    private String command;

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Socket s;
        DataOutputStream mdataOutputStream;
        try
        {
            s = new Socket(addr,port);

            mdataOutputStream = new DataOutputStream(s.getOutputStream());
            mdataOutputStream.writeBytes(command);
            s.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
