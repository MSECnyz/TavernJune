package com.msecnyz.tavernjune.legionsupport;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.msecnyz.tavernjune.FirstService;
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.onuwerewolf.GameService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by neo on 2018/1/4.
 */

public class AudioChat {

    private Activity activity;
    private FirstService.MsgBinder myBinder;

    private String audioMsg1 = "#请#求#语#音";
    private String audioMsg2 = "#进#行#语#音";
    private String audioMsg3 = "#分#配#房#间";

    private AudioRecord myRecord;
    private AudioTrack track;
    int buferSize;
    short[] buferOut;
    short[] buferIn;
    DataOutputStream mediaOutput;
    DataInputStream mediaInput;
    Socket toSocket = null;
    Socket comeSocket = null;
    final static int mySampleRateInHz = 16000;
    final static int myChannelConfig = AudioFormat.CHANNEL_IN_STEREO;
    final static int myAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    boolean isRecord = false;

    public AudioChat(Activity activity) {

        this.activity = activity;

        buferSize = AudioRecord.getMinBufferSize(mySampleRateInHz,myChannelConfig,myAudioFormat);
        System.out.println("************************zhubajie"+buferSize+"shaheshang************************");
        buferOut = new short[buferSize];
        buferIn = new short[buferSize];

        myRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,mySampleRateInHz,myChannelConfig,myAudioFormat,buferSize);
        track = new AudioTrack(AudioManager.STREAM_MUSIC,mySampleRateInHz,myChannelConfig,myAudioFormat,buferSize, AudioTrack.MODE_STREAM);

        Intent startIntent = new Intent(activity, FirstService.class);
        this.activity.bindService(startIntent, serviceConnection, BIND_AUTO_CREATE);

    }

    public void sendChatRequest(){
        //发起人通过主链接向服务器发送请求
        sendMsgByMainService(audioMsg1);
    }

    public void ready() throws IOException{
        //收到服务器广播后与服务器建立新TCP连接
        toSocket = new Socket(activity.getString(R.string.serverIP),Integer.parseInt(activity.getString(R.string.gamePort)));
        mediaOutput = new DataOutputStream(new BufferedOutputStream(toSocket.getOutputStream()));
        comeSocket = new Socket(activity.getString(R.string.serverIP),Integer.parseInt(activity.getString(R.string.gamePort)));
        mediaInput = new DataInputStream(new BufferedInputStream(comeSocket.getInputStream()));
    }

    public void start(){
        //开始语音？
        playGo();
        recordGo();
    }

    private void stopAll(){
        //断开链接和接触绑定service
        try {
            myRecord.stop();
            mediaOutput.close();
            toSocket.close();
            track.stop();
            mediaInput.close();
            comeSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        activity.unbindService(serviceConnection);
    }

    private void recordGo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                myRecord.startRecording();
                int bufferReadResult;
                while (true){
                    if (isRecord) {
                        bufferReadResult = myRecord.read(buferOut, 0, buferSize);
                        //read阻塞式方法
                        try {
                            for (int i = 0; i < bufferReadResult; i++) {
                                mediaOutput.writeShort(buferOut[i]);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            mediaOutput.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private void playGo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                track.play();
                while (true) {
                    int j = 0;
                    while (j < buferIn.length) {
                        try {
                            buferIn[j] = mediaInput.readShort();
                            j++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!isRecord) {
                        track.write(buferIn, 0, buferIn.length);
                    }
                }

            }
        }).start();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //绑定成功会执行这里
            myBinder = (FirstService.MsgBinder) service; //向下转型得到实例
            //Activity需要主动通信的时候，只需用binder调用方法来调用service内部的方法就好了。目前是这样的
            myBinder.setHandler(null);
            //传入null时，收到服务器的同一个stream的返回值将不作任何处理
            //sendMsgToServer("");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void sendMsgByMainService(final String msg){
        //用主service
        JSONObject msgJson = new JSONObject();
        try {
            msgJson.put("msgType",msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myBinder.sendMsgToServer(msgJson);
    }
}
