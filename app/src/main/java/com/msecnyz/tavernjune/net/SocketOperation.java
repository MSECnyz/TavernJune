package com.msecnyz.tavernjune.net;

import android.os.Handler;
import android.os.Message;

import java.io.ObjectOutputStream;
import java.lang.Object;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class SocketOperation {

    private String ipAddress;
    private int port;

    private Socket socket;
    private ObjectOutputStream oosTos;
    private ObjectInputStream oisFrs;
    private Handler msgHandler;


    public SocketOperation(String ipAddress,int port){
        this.ipAddress =ipAddress;
        this.port = port;
    }

    public void setServiceLink(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ipAddress,port);
                    oosTos = new ObjectOutputStream(socket.getOutputStream());
                    oisFrs = new ObjectInputStream(socket.getInputStream());
                    Object someObj;
                    while ((someObj = oisFrs.readObject())!=null) {
                        //readObject阻塞式
                        Message message = new Message();
                        message.obj = someObj;
                        if (msgHandler!=null){
                            msgHandler.sendMessage(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setHandler(Handler handler){
        msgHandler = handler;
    }


    public void msgToServer(final Object msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (oosTos!=null){
                        oosTos.writeObject(msg);
                        oosTos.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void closeAll(){
        try {
            if (oosTos!=null){
                oosTos.close();
            }
            if(oisFrs!=null){
                oisFrs.close();
            }
            if (socket!=null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
