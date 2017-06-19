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
    private DataOutputStream dostos;
    private DataInputStream disfrs;
    private ObjectInputStream oisfrs;
    private ObjectOutputStream oostos;


    public SocketOperation(String ipAddress,int port){
        this.ipAddress =ipAddress;
        this.port = port;
    }

    public void setLink(){
        try {
            socket = new Socket(ipAddress,port);
            dostos = new DataOutputStream(socket.getOutputStream());
            disfrs = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLink(final Handler objectHandler) {
        try {
            socket = new Socket(ipAddress,port);
            oostos = new ObjectOutputStream(socket.getOutputStream());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        oisfrs = new ObjectInputStream(socket.getInputStream());
                        Object someObj;
                        while ((someObj = oisfrs.readObject())!=null) {
                            //readObject阻塞式
                            Message message = new Message();
                            message.obj = someObj;
                            objectHandler.sendMessage(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (ClassNotFoundException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayerToQueue(String msg,Handler handler){

        try {
            String line = msg;
            if (dostos!=null){
                dostos.writeUTF(line);
                dostos.flush();
            }
            if (disfrs!=null) {
                String lineIn;
                while ((lineIn = disfrs.readUTF()) != null) {
                    Message message = new Message();
                    message.obj = lineIn;
                    handler.sendMessage(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gameMsgToServer(final Object msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (oostos!=null){
                        oostos.writeObject(msg);
                        oostos.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void closeAll(){
        try {
            if (dostos!=null){
                dostos.close();
            }
            if (disfrs!=null){
                disfrs.close();
            }
            if(oisfrs!=null){
                oisfrs.close();
            }
            if (socket!=null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
