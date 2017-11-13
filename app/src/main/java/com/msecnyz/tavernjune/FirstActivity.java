package com.msecnyz.tavernjune;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.msecnyz.tavernjune.legionsupport.FirstServiceListener;
import com.msecnyz.tavernjune.net.HttpOperation;

import java.io.IOException;

public class FirstActivity extends BaseActivity {

    public static FirstActivity killMyself;

    private TextInputLayout userIdLayout,passwordLayout;
    private EditText userId,password;
    private Button register,logIn,offLine;
    private TextView myResponse;
    private CheckBox autoLogIn,rememberUser;
    private SharedPreferences sharedPreferences = null;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    private boolean hasNet = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        killMyself = this;

        sharedPreferences = getSharedPreferences("userIdInformation", Context.MODE_PRIVATE);

        Intent intentall = getIntent();
        if (intentall.getStringExtra("extraData").equals("userExit")) {
            MainActivity.killmyself.finish();
        }

        userIdLayout = (TextInputLayout)findViewById(R.id.userId);
        passwordLayout = (TextInputLayout)findViewById(R.id.userPassword);
        userId = userIdLayout.getEditText();
        password = passwordLayout.getEditText();
        register = (Button)findViewById(R.id.register);
        logIn = (Button)findViewById(R.id.login);
        offLine = (Button)findViewById(R.id.offline);
        myResponse = (TextView)findViewById(R.id.response);
        autoLogIn = (CheckBox)findViewById(R.id.autologin);
        rememberUser = (CheckBox)findViewById(R.id.rememberUser);

        rememberUser.setChecked(true);

        userId.setText(sharedPreferences.getString("userId",""));
        password.setText(sharedPreferences.getString("password",""));

        /*
        *网络变化广播
        */
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);

        userId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String id = userId.getText().toString();
                if(s.length()<3){
                    userIdLayout.setErrorEnabled(true);
                    userIdLayout.setError("用户名不能小于3位");
                }else{
                    if (id.contains(" ")){
                        userIdLayout.setErrorEnabled(true);
                        userIdLayout.setError("用户名不能含有空格");
                    }else {
                        userIdLayout.setErrorEnabled(false);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passWord = password.getText().toString();
                if(s.length()<1){
                    passwordLayout.setErrorEnabled(true);
                    passwordLayout.setError("密码不能为空");
                }else{
                    if (passWord.contains(" ")){
                        passwordLayout.setErrorEnabled(true);
                        passwordLayout.setError("密码不能含有空格");
                    }else {
                        passwordLayout.setErrorEnabled(false);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasNet) {
                    if (userIdLayout.isErrorEnabled() || passwordLayout.isErrorEnabled()) {
                        Toast.makeText(FirstActivity.this, "用户名/密码格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        sendSomethingToServer("1");
                    }
                }else {
                    Toast.makeText(FirstActivity.this,"请离线进入或连接网络",Toast.LENGTH_SHORT).show();
                }
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasNet) {
                    if (userIdLayout.isErrorEnabled() || passwordLayout.isErrorEnabled()) {
                        Toast.makeText(FirstActivity.this, "用户名/密码格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        sendSomethingToServer("2");
                    }
                }else {
                    Toast.makeText(FirstActivity.this,"请离线进入或连接网络",Toast.LENGTH_SHORT).show();
                }
            }
        });

        offLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("extraData","default");
                intent.setClass(FirstActivity.this,MainActivity.class);
                FirstActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    protected void activityReady() {
        super.activityReady();
        if (SplashActivity.killMyself!=null)SplashActivity.killMyself.finish();
    }

    private void showResponse(final String response){

        //密码加密？
        SharedPreferences.Editor editor = sharedPreferences.edit();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myResponse.setText(response);
            }
        });
        //如果服务器返回值为登录成功，进行跳转
        if (response.equals("登录成功")){
            editor.putString("userId",userId.getText().toString());
            editor.commit();
            if (rememberUser.isChecked()){
                editor.putString("password",password.getText().toString());
                editor.commit();
            }else {
                editor.putString("password","");
                editor.commit();
            }
            if (autoLogIn.isChecked()){
                editor.putString("autoLogIn","true");
                editor.commit();
            }else {
                editor.putString("autoLogIn","false");
                editor.commit();
            }
            //启动长连接TCP服务
            Intent startIntent = new Intent(this, FirstService.class);
            startService(startIntent);
            //跳转至下个Activity
            Intent intent = new Intent();
            intent.setClass(FirstActivity.this,MainActivity.class);
            intent.putExtra("extraData","default");
            FirstActivity.this.startActivity(intent);
        }
    }

    private void sendSomethingToServer(final String msgType){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpOperation httpOperation = new HttpOperation();
                if (msgType.equals("1")){
                    httpOperation.setUrl(FirstActivity.this.getString(R.string.registerHttpURL));
                }else{
                    httpOperation.setUrl(FirstActivity.this.getString(R.string.loginHttpURL));
                }
                String response;
                try {
                    response = httpOperation.sendUserInformation(userId.getText().toString(),password.getText().toString());
                    showResponse(response);
                } catch (IOException e) {
                    e.printStackTrace();
                    showResponse("链接失败");
                }finally {
                    try {
                        httpOperation.closeAll();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Thread.interrupted(); 执行完毕线程自动销毁,不用interrupted
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //动态注册的广播接收器需要取消注册
        unregisterReceiver(networkChangeReceiver);
    }

    class NetworkChangeReceiver extends BroadcastReceiver{
        //防止ANR不要耗时过长操作
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo!=null&&networkInfo.isAvailable()){
                //Toast.makeText(context,"network isAvailable",Toast.LENGTH_SHORT).show();
                hasNet = true;
            }else {
                //Toast.makeText(context,"network unAvailable",Toast.LENGTH_SHORT).show();
                hasNet = false;
            }

        }
    }
}
