package com.msecnyz.tavernjune.social;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.legionsupport.GameServiceListener;
import com.msecnyz.tavernjune.net.SocketOperation;
import com.msecnyz.tavernjune.onuwerewolf.GameService;

/**
 * Created by neo on 2018/2/27.
 */

public class SocialService extends Service{
    private SocialService.SocialBinder socialBinder = new SocialBinder();
    private GameServiceListener gameServiceListener;
    SocketOperation socialSocket;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("SocialService","开启SocialService");
    }

    private void sendMsg(String msg){
        gameServiceListener.sendGameMsg(msg);
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

    public void setServiceListener(GameServiceListener serviceListener){
        gameServiceListener = serviceListener;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class SocialBinder extends Binder{
        public SocialService getService(){return SocialService.this;}
        public void setHandler(Handler handler){socialSocket.setHandler(handler);}
        public void sendMsgToServer(Object jsonObject){
            socialSocket.msgToServer(jsonObject);
        }
        public void startSocket(Handler handler){
            socialSocket  = new SocketOperation(SocialService.this.getString(R.string.serverIP),Integer.parseInt(SocialService.this.getString(R.string.gamePort)));
            socialSocket.setHandler(handler);
            socialSocket.setServiceLink();
        }
        public void closeSocket(){
            socialSocket.closeAll();
        }
    }
}
