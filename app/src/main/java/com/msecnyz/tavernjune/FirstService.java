package com.msecnyz.tavernjune;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.msecnyz.tavernjune.legionsupport.FirstServiceListener;
import com.msecnyz.tavernjune.net.SocketOperation;

import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Neo on 2017/10/17.
 */

public class FirstService extends Service {

    private MsgBinder msgBinder = new MsgBinder();
    private FirstServiceListener firstServiceListener;
    SocketOperation heartSocket;

    @Override
    public void onCreate() {
        super.onCreate();

        //bind会执行onCreate，避免在里面重复操作

    }

    private void sendMsg(String msg){
        firstServiceListener.msg(msg);
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        //@IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true) flags参数前
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //Activity调用这个函数，然后传入对应接口，定义这些方法在被service调用的时候具体要执行什么功能
    //从而起到了service调用Activity方法的作用,接口的特性很适合这样做.

    //定接口的负责何时执行，用回调的负责执行的内容。此处回调了被重写的msg方法。
    public void setServiceListener(FirstServiceListener serviceListener){
        firstServiceListener = serviceListener;
        //传过来一个重写好的Listener对象。我用你的方法执行。
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //return null;
        return msgBinder;
    }

    public class MsgBinder extends Binder {
        public FirstService getService(){
            return FirstService.this;
        }
        public void setHandler(Handler handler){heartSocket.setHandler(handler);}
        public void startSocket(){
            heartSocket  = new SocketOperation(FirstService.this.getString(R.string.serverIP),Integer.parseInt(FirstService.this.getString(R.string.heartPort)));
            heartSocket.setHandler(null);
            heartSocket.setServiceLink();
        }
        public void sendMsgToServer(Object jsonObject){
            heartSocket.msgToServer(jsonObject);
        }
    }
}
