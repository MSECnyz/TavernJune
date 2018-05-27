package com.msecnyz.tavernjune.onuwerewolf;

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
import com.msecnyz.tavernjune.social.SocialServiceListener;

/**
 * Created by neo on 2017/11/2.
 */

public class GameService extends Service{
    private GameService.GameBinder gameBinder = new GameBinder();
    private SocialServiceListener socialServiceListener;
    SocketOperation gameSocket;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("GameService","开启gameService");
    }

    private void sendMsg(String msg){
        socialServiceListener.sendGameMsg(msg);
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

    public void setServiceListener(SocialServiceListener serviceListener){
        socialServiceListener = serviceListener;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //return null;
        return gameBinder;
    }

    public class GameBinder extends Binder {
        public GameService getService(){
            return GameService.this;
        }
        public void setHandler(Handler handler){gameSocket.setHandler(handler);}
        public void sendMsgToServer(Object jsonObject){
            gameSocket.msgToServer(jsonObject);
        }
        public void startSocket(Handler handler){
            gameSocket  = new SocketOperation(GameService.this.getString(R.string.serverIP),Integer.parseInt(GameService.this.getString(R.string.gamePort)));
            gameSocket.setHandler(handler);
            gameSocket.setServiceLink();
        }
        public void closeSocket(){
            gameSocket.closeAll();
        }
    }
}
