package com.msecnyz.tavernjune;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.msecnyz.tavernjune.net.HttpOperation;

import java.io.IOException;

public class FirstActivity extends AppCompatActivity {

    public static FirstActivity killMyself;

    private TextInputLayout userIdLayout,passwordLayout;
    private EditText userId,password;
    private Button register,logIn,offLine;
    private TextView myResponse;
    private CheckBox autoLogIn,rememberUser;
    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        SplashActivity.killMyself.finish();
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

        userId.setText(sharedPreferences.getString("userId",""));
        password.setText(sharedPreferences.getString("password",""));

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
                if (userIdLayout.isErrorEnabled()||passwordLayout.isErrorEnabled()){
                    Toast.makeText(FirstActivity.this,"用户名/密码格式错误",Toast.LENGTH_SHORT).show();
                }else {
                    sendSomethingToServer("1");
                }
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userIdLayout.isErrorEnabled()||passwordLayout.isErrorEnabled()){
                    Toast.makeText(FirstActivity.this,"用户名/密码格式错误",Toast.LENGTH_SHORT).show();
                }else {
                    sendSomethingToServer("2");
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

    private void showResponse(final String response){

        //密码加密？
        SharedPreferences.Editor editor = sharedPreferences.edit();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myResponse.setText(response);
            }
        });
        //如果服务器返回值为登陆成功，进行跳转
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
                String response;
                try {
                    response = httpOperation.sendUserInformation(userId.getText().toString(),password.getText().toString(),"Nothing",msgType);
                    showResponse(response);
                } catch (IOException e) {
                    e.printStackTrace();
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

}
