package com.msecnyz.tavernjune.net;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class HttpOperation {
    protected String myURL;

    private  HttpURLConnection connection;
    private DataOutputStream sendInformation;


    public void setUrl(String url){
        myURL = url;
    }

    public String sendUserInformation(String username,String password)throws IOException{
        URL url = null;
        url = new URL(myURL);
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5000); //连接超时ms
        connection.setReadTimeout(5000); //读取超时
        try {
            sendInformation = new DataOutputStream(connection.getOutputStream());
          }catch (SocketTimeoutException se){
            String sse = "连接超时";
            return sse;
         }
        sendInformation.writeUTF("&username="+username+"&password="+password);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); //服务器用的printwriter字符流
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null){
            response.append(line);
        }
        return response.toString();
    }

    public String sendSTHtoServer(String username)throws IOException{
        URL url = null;
        url = new URL(myURL);
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        try {
            sendInformation = new DataOutputStream(connection.getOutputStream());
        }catch (SocketTimeoutException se){
            String sse = "连接超时";
            return sse;
        }
        sendInformation.writeUTF("&username="+username+"&msgType="+3);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null){
            response.append(line);
        }
        return response.toString();
    }

    public void closeAll()throws IOException{
        if (sendInformation != null) {
            sendInformation.close();
        }
        if (connection != null) {
            connection.disconnect();
        }
    }
}
