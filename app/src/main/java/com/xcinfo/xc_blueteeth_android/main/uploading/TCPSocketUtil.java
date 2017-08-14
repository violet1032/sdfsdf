package com.xcinfo.xc_blueteeth_android.main.uploading;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by com.亚东 on 2017/5/14.
 */

public class TCPSocketUtil {
    String ip="121.42.50.102";
    int port=30003;
//    String ip="192.168.1.125";
//    int port=60001;
    private static TCPSocketUtil tcpSocketUtil;
    private  Socket mSocket;
    private SocketAddress socketAddress;
    private int connectTimeout = 1000 * 60;// 10 sec
    private int getInputStremTimeout = 1000 * 60;// 2sec

    private TCPSocketUtil(){
        socketAddress = new InetSocketAddress(ip.toString().trim(), port);
        mSocket = new Socket();
        try {
            mSocket.connect(socketAddress, connectTimeout);
            mSocket.setSoTimeout(getInputStremTimeout);
            Log.e("@@TCPSocketUtil","getInstance");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("@@socket",e.toString());

        }

    }



    public void reConnected() throws IOException {
        mSocket.close();
        mSocket.connect(socketAddress,connectTimeout);
    }

    public static TCPSocketUtil getInstance(){
        if (tcpSocketUtil==null){
            tcpSocketUtil=new TCPSocketUtil();
        }
        return tcpSocketUtil;
    }

    public boolean send(@NonNull byte[] data)  {

        if (data!=null&&tcpSocketUtil.getmSocket().isConnected()){
            OutputStream outputStream= null;
            try {
                outputStream = tcpSocketUtil.getmSocket().getOutputStream();
                outputStream.write(data);
                outputStream.flush();
                Log.e("@@Tcp","已发送");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("@@sendError1",e.toString());
                try {
                    reConnected();

                } catch (IOException e1) {
                    e1.printStackTrace();
                    Log.e("@@sendError2",e.toString());
                }
                return false;

            }

        }else {
            Log.e("@@socket","未连接");
            return false;

        }

    }


    public Socket getmSocket(){
        return this.mSocket;
    }

    public void close() throws IOException {
        if (tcpSocketUtil!=null){
            tcpSocketUtil.getmSocket().close();
            tcpSocketUtil=null;
        }
        Log.e("@@Tcp","已关闭");
    }

}
