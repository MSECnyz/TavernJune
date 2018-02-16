package com.msecnyz.tavernjune.legionsupport;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.msecnyz.tavernjune.FirstService;
import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.onuwerewolf.GameService;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by neo on 2018/1/4.
 */

public class AudioChat {

    private String TAG = "AudioChat";

    private Activity activity;
    private Handler handler;
    private FirstService.MsgBinder myBinder;

    private String audioMsg1 = "#请#求#语#音";
    private String audioMsg2 = "#进#行#语#音";
    private String audioMsg3 = "#分#配#房#间";

    private ArrayList<String> userNameList;

    private AudioRecord myRecord;
    private AudioTrack track;
    int buferSize;
    short[] buferOut;
    short[] buferIn;
    DataOutputStream mediaOutput;
    DataInputStream mediaInput;
    Socket toSocket = null;
    Socket comeSocket = null;
    //final static int mySampleRateInHz = 16000;
    final static int mySampleRateInHz = 8000;
    final static int myChannelConfig = AudioFormat.CHANNEL_IN_STEREO;
    final static int myAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //boolean isRecord = false;

    boolean tempFlag = false;

    private String myUserName;

    public AudioChat(Activity activity,Boolean flag) {

        this.tempFlag = flag;

        this.activity = activity;

        buferSize = AudioRecord.getMinBufferSize(mySampleRateInHz,myChannelConfig,myAudioFormat); //2560
        //buferSize = 8192;  //似乎newRecord的时候bufersize要尽量大，于数组的2倍
        System.out.println("************************zhubajie"+buferSize+"shaheshang************************");
        buferOut = new short[buferSize];
        buferIn = new short[buferSize];

        //申请录音权限
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.RECORD_AUDIO},1);
        }

        myRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, mySampleRateInHz, myChannelConfig, myAudioFormat, buferSize);
        track = new AudioTrack(AudioManager.STREAM_MUSIC, mySampleRateInHz, myChannelConfig, myAudioFormat, buferSize, AudioTrack.MODE_STREAM);

        Intent startIntent = new Intent(activity, FirstService.class);
        this.activity.bindService(startIntent, serviceConnection, BIND_AUTO_CREATE);

    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

    public void setMyUserName(String myUserName){
        this.myUserName = myUserName;
    }

    public void sendChatRequest(ArrayList<String> userNameList){
        //发起人通过主链接向服务器发送请求
        sendMsgByMainService(audioMsg1,userNameList);
    }

    public void ready(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //收到服务器广播后与服务器建立新TCP连接
                try {
                    toSocket = new Socket(activity.getString(R.string.serverIP),Integer.parseInt(activity.getString(R.string.audioPortOut)));
                    mediaOutput = new DataOutputStream(new BufferedOutputStream(toSocket.getOutputStream()));
                    mediaOutput.writeUTF(myUserName);
                    mediaOutput.flush();
                    //初次链接要传用户名让服务器识别

                    comeSocket = new Socket(activity.getString(R.string.serverIP),Integer.parseInt(activity.getString(R.string.audioPortIn)));
                    mediaInput = new DataInputStream(new BufferedInputStream(comeSocket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
                myRecord.release();
                myRecord = null;
                myRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, mySampleRateInHz, myChannelConfig, myAudioFormat, buferSize);
                //释放资源
                myRecord.startRecording();
                int bufferReadResult;
                while (true){
//                    if (isRecord) {
                        Log.i(TAG,"readToRecord");
                        bufferReadResult = myRecord.read(buferOut, 0, buferSize);
                        //read阻塞式方法
                        Log.i(TAG,"recordDone");
                        try {
                            for (int i = 0; i < bufferReadResult; i++) {
                                mediaOutput.writeShort(buferOut[i]);
                            }
                            Log.i(TAG,"sendAudioToServer");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            try {
                                mediaOutput.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
//                    }else {
//                        try {
//                            mediaOutput.flush();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
                }
            }
        }).start();
    }

    private void playGo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"trackReadToPlay");
                track.play();
                Log.i(TAG,"trackPlaying");
                while (true) {
                    int j = 0;
                    while (j < buferIn.length) {
                        try {
                            Log.i(TAG,"trackReadToRead");
                            buferIn[j] = mediaInput.readShort();
                            j++;
                            Log.i(TAG,"reciveAudioFromServer");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
//                    if (!isRecord) {
                        track.write(buferIn, 0, buferIn.length);
//                    }
                }

            }
        }).start();
    }

    public void sendRoomList(){
        sendMsgByMainService(audioMsg3,userNameList);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //绑定成功会执行这里
            myBinder = (FirstService.MsgBinder) service; //向下转型得到实例
            //Activity需要主动通信的时候，只需用binder调用方法来调用service内部的方法就好了。目前是这样的
            //传入null时，收到服务器的同一个stream的返回值将不作任何处理

            if (tempFlag){
                userNameList = new ArrayList<String>();
                String zjj = "zjj";
                String nyz = "nyz";
                userNameList.add(zjj);
                userNameList.add(nyz);
                sendMsgByMainService(audioMsg1,userNameList);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void sendMsgByMainService(final String msg,ArrayList<String> userNameList){
        //用主service
        JSONObject msgJson = new JSONObject();
        JSONArray userNameArrary = JSONArray.fromObject(userNameList);
        msgJson.put("msgType",msg);
        msgJson.put("msg1", userNameArrary);

        myBinder.sendMsgToServer(msgJson.toString());
    }
}
