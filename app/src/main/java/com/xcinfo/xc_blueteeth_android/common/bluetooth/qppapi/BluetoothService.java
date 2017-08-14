package com.xcinfo.xc_blueteeth_android.common.bluetooth.qppapi;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.xcinfo.xc_blueteeth_android.common.utils.TimeUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Created by com.亚东 on 2017/1/3.
 * 一般常用函数：
 * isBTopen() 判断蓝牙状态
 * Set<BluetoothDevice> getPairedDev() 获取已配对蓝牙列表
 * 使用device.getName()，device.getAddress()可获取蓝牙设备信息，使用getDevByMac(String mac)可通过地址获取device对象
 * 使用connect(BluetoothDevice device)连接设备，成功后handler会发送消息
 * 使用sendMessage(String message, String charset) 发送要打印的字符串（charset一般为"GBK"）
 * 使用stop()关闭服务
 */
public class BluetoothService {

    private static final String TAG = "BluetoothService";
    private static final boolean D = true;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_CONNECTION_LOST = 5;//连接丢失
    public static final int MESSAGE_UNABLE_CONNECT = 6;//无法连接设备
    //private static final String NAME = "BTPrinter";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Handler mHandler;
    private BluetoothService.AcceptThread mAcceptThread;
    private BluetoothService.ConnectThread mConnectThread;
    private BluetoothService.ConnectedThread mConnectedThread;
    private int mState = 0;
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;//已连接

    public static final String TITLE = "记录报告";
    public static final String NAME = "设备名称:";
    public static final String MODEL = "设备型号:";
    public static final String SERIL_NUMBER = "序 列 号:";
    public static final String USER_NAME = "用 户 名:";
    public static final String REMARK = "备    注:";
    public static final String RECORD_NUMBER = "记录条数:";
    public static final String N = "\n";

    /**
     * 复位打印机
     */
    public static final byte[] RESET = {0x1b, 0x40};

    /**
     * 左对齐
     */
    public static final byte[] ALIGN_LEFT = {0x1b, 0x61, 0x00};

    /**
     * 中间对齐
     */
    public static final byte[] ALIGN_CENTER = {0x1b, 0x61, 0x01};

    /**
     * 右对齐
     */
    public static final byte[] ALIGN_RIGHT = {0x1b, 0x61, 0x02};

    /**
     * 选择加粗模式
     */
    public static final byte[] BOLD = {0x1b, 0x45, 0x01};

    /**
     * 取消加粗模式
     */
    public static final byte[] BOLD_CANCEL = {0x1b, 0x45, 0x00};

    /**
     * 宽高加倍
     */
    public static final byte[] DOUBLE_HEIGHT_WIDTH = {0x1d, 0x21, 0x11};

    /**
     * 宽加倍
     */
    public static final byte[] DOUBLE_WIDTH = {0x1d, 0x21, 0x10};

    /**
     * 高加倍
     */
    public static final byte[] DOUBLE_HEIGHT = {0x1d, 0x21, 0x01};

    /**
     * 字体不放大
     */
    public static final byte[] NORMAL = {0x1d, 0x21, 0x00};

    /**
     * 设置默认行间距
     */
    public static final byte[] LINE_SPACING_DEFAULT = {0x1b, 0x32};

    public BluetoothService(Context context, Handler handler) {

        this.mHandler = handler;
    }

    //是否成功初始化BluetoothAdapter
    public synchronized boolean isAvailable() {

        return this.mAdapter != null;
    }


    //蓝牙是否开启
    public synchronized boolean isBTopen() {

        return this.mAdapter.isEnabled();
    }

    //获取mac地址对应的BluetoothDevice对象
    public synchronized BluetoothDevice getDevByMac(String mac) {
        return this.mAdapter.getRemoteDevice(mac);
    }

    //获取名字对应的BluetoothDevice对象
    public synchronized BluetoothDevice getDevByName(String name) {
        BluetoothDevice tem_dev = null;
        Set pairedDevices = this.getPairedDev();
        if (pairedDevices.size() > 0) {
            Iterator var5 = pairedDevices.iterator();

            while (var5.hasNext()) {
                BluetoothDevice device = (BluetoothDevice) var5.next();
                if (device.getName().indexOf(name) != -1) {
                    tem_dev = device;
                    break;
                }
            }
        }

        return tem_dev;
    }

    //通过蓝牙发送String
    public synchronized void sendMessage(String message, String charset) {
        if (message.length() > 0) {
            byte[] send;
            try {
                send = message.getBytes(charset);
            } catch (UnsupportedEncodingException var5) {
                send = message.getBytes();
            }
            this.write(RESET);

            this.write(send);

        }

    }

    //打印标题
    public synchronized void printTitle() {
        byte[] send;
        try {
            send = (TITLE + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = "记录报告".getBytes();
        }
        this.write(RESET);
        this.write(DOUBLE_HEIGHT_WIDTH);
        this.write(ALIGN_CENTER);
        this.write(send);
    }

    //打印设备名称
    public synchronized void printName(String name) {
        byte[] send;
        try {
            send = (NAME + name + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = (NAME + name + N).getBytes();
        }
        this.write(RESET);

        this.write(send);
    }
    //打印编号
    public synchronized void printorderNumber(String orderNumber) {
        byte[] send;
        try {
            send = ("订单编号:" + orderNumber + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = ("订单编号:" + orderNumber + N).getBytes();
        }
        this.write(RESET);

        this.write(send);
    }

    //打印设备型号
    public synchronized void printModel(String model) {
        byte[] send;
        try {
            send = (MODEL + model + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = (MODEL + model + N).getBytes();
        }
        this.write(RESET);

        this.write(send);
    }

    //打印序列号
    public synchronized void printSerilNumber(String serilNumber) {
        byte[] send;
        try {
            send = (SERIL_NUMBER + serilNumber + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = (SERIL_NUMBER + serilNumber + N).getBytes();
        }
        this.write(RESET);
        this.write(send);
    }

    //打印用户名
    public synchronized void printuserName(String userName) {
        byte[] send;
        try {
            send = (USER_NAME + userName + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = (USER_NAME + userName + N).getBytes();
        }
        this.write(RESET);
        this.write(send);
    }

    //打印备注
    public synchronized void printRemark(String remark) {
        byte[] send;
        try {
            send = (REMARK + remark + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = (REMARK + remark + N).getBytes();
        }
        this.write(RESET);
        this.write(send);
    }


    //打印记录条数
    public synchronized void printRecoedNumber(String recoedNumber) {
        byte[] send;
        try {
            send = (RECORD_NUMBER + recoedNumber + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = (RECORD_NUMBER + recoedNumber + N + N + N).getBytes();
        }
        this.write(RESET);
        this.write(send);
    }

    //打印打印时间
    public synchronized void printTime() {
        Date now = new Date();
        String mDate = TimeUtil.getNowDatetime();
        byte[] send;
        try {
            send = ("打印时间:" + mDate.substring(0, mDate.length() - 3) + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = (mDate + N).getBytes();
        }
        this.write(RESET);
        this.write(send);
    }


    //打印通道开头
    public synchronized void printRecoedTitle(String recoedId, String low, String height) {
        byte[] send;
        try {
            send = ("通道" + recoedId + ":/℃("+low+"~"+height+")" + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = ("通道" + recoedId + ":/℃("+low+"~"+height+")" + N).getBytes();
        }
        this.write(RESET);
        this.write(send);
    }

    //打印通道信息(最大值，最小值)
    public synchronized void printchannelInformation(String recoedId, Boolean isMax, String weather,
                                                     String time) {
        String Max = new String();
        if (isMax) {
            Max = "(Max):";
        } else {
            Max = "(Min):";
        }
        byte[] send;
        try {
            send = ("通道" + recoedId + Max + weather + "℃" + N + "     " + time + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = ("通道" + recoedId + Max + weather + "℃" + N + "     " + time + N).getBytes();
        }
        this.write(RESET);
        this.write(send);
    }

    //打印通道详细信息开头
    public synchronized void printRecoedInformationTitle(List<String> title) {

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < title.size(); i++) {
            if (title.size()==1){
                s.append(title.get(0));
                s.append("         ");
                break;
            }
            if (i%2==0&&i==title.size()-1&&i>1){
                s.append("                   ");
                s.append(title.get(i));
                s.append("         ");
                break;
            }
            if (i%1==0&&i==title.size()-1){
                s.append(title.get(i));
                s.append("  ");
                break;
            }
            s.append(title.get(i));

            if (i % 2 == 0||i==0) {
                s.append("   ");
                if (i == title.size() - 1) {//如果是最后一个

                    s.append("      ");

                    s.append("\n");
                }

            }
            if (i % 2 == 1) {
                s.append("  ");
                if (i == title.size() - 1) {//如果是最后一个
                    s.append("  ");
                    s.append("\n");

                }

                //s.append("                     ");
            }


        }
//        if (title.size() == 1) {
//            s.append("      ");
//        }
        byte[] send;
        try {
            send = ("记录时间           " + s).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = ("记录时间           " + s).getBytes();
        }
        this.write(RESET);

        this.write(send);
    }

    //打印分割线
    public synchronized void printLine() {
        byte[] send;
        try {
            send = ("--------------------------------" + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = ("________________________________" + N).getBytes();
        }
        this.write(ALIGN_CENTER);
        this.write(send);
    }

    //打印通道详细信息
    public synchronized void detailInformation(String time, String weather) {
        String mTime = time.substring(10);

        String mDay = time.substring(0, 10);


        byte[] send;
        try {
            send = (mDay + " " + mTime + "  " + weather + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = (mDay + " " + mTime + "  " + weather + N).getBytes();
        }
        this.write(RESET);
        this.write(send);
    }

    //打印签名
    public synchronized void printSign() {
        byte[] send;
        try {
            send = ("签名：" + N + N).getBytes("GBK");
        } catch (UnsupportedEncodingException var5) {
            send = ("签名：" + N + N).getBytes();
        }
        this.write(RESET);
        this.write(DOUBLE_HEIGHT_WIDTH);

        this.write(send);
    }


    //获取以配对蓝牙列表
    public synchronized Set<BluetoothDevice> getPairedDev() {
        Set dev = null;
        dev = this.mAdapter.getBondedDevices();
        return dev;
    }

    //取消搜索设备
    public synchronized boolean cancelDiscovery() {

        return this.mAdapter.cancelDiscovery();
    }

    //是否正在搜索
    public synchronized boolean isDiscovering() {
        return this.mAdapter.isDiscovering();
    }

    //开始搜索
    public synchronized boolean startDiscovery() {

        return this.mAdapter.startDiscovery();
    }

    private synchronized void setState(int state) {
        this.mState = state;
        this.mHandler.obtainMessage(1, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return this.mState;
    }

    public synchronized void start() {
        Log.d("BluetoothService", "start");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        if (this.mAcceptThread == null) {
            this.mAcceptThread = new BluetoothService.AcceptThread();
            this.mAcceptThread.start();
        }

        this.setState(1);
    }


    //连接蓝牙设备
    public synchronized void connect(BluetoothDevice device) {
        Log.d("BluetoothService", "connect to: " + device);
        if (this.mState == 2 && this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        this.mConnectThread = new BluetoothService.ConnectThread(device);
        this.mConnectThread.start();
        this.setState(2);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.d("BluetoothService", "connected");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        if (this.mAcceptThread != null) {
            this.mAcceptThread.cancel();
            this.mAcceptThread = null;
        }

        this.mConnectedThread = new BluetoothService.ConnectedThread(socket);
        this.mConnectedThread.start();
        Message msg = this.mHandler.obtainMessage(4);
        this.mHandler.sendMessage(msg);
        this.setState(3);
    }

    //停止连接
    public synchronized void stop() {
        Log.d("BluetoothService", "stop");
        this.setState(0);
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        if (this.mAcceptThread != null) {
            this.mAcceptThread.cancel();
            this.mAcceptThread = null;
        }

    }

    private void write(byte[] out) {
        BluetoothService.ConnectedThread r;
        synchronized (this) {
            if (this.mState != 3) {
                return;
            }

            r = this.mConnectedThread;
        }

        r.write(out);
    }

    private void connectionFailed() {
        this.setState(1);
        Message msg = this.mHandler.obtainMessage(6);
        this.mHandler.sendMessage(msg);
    }

    private void connectionLost() {
        Message msg = this.mHandler.obtainMessage(5);
        this.mHandler.sendMessage(msg);
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = BluetoothService.this.mAdapter.listenUsingRfcommWithServiceRecord("BTPrinter", BluetoothService.MY_UUID);
            } catch (IOException var4) {
                Log.e("BluetoothService", "listen() failed", var4);
            }

            this.mmServerSocket = tmp;
        }

        public void run() {
            Log.d("BluetoothService", "BEGIN mAcceptThread" + this);
            this.setName("AcceptThread");
            BluetoothSocket socket = null;

            while (BluetoothService.this.mState != 3) {
                Log.d("AcceptThread线程运行", "正在运行......");

                try {
                    socket = this.mmServerSocket.accept();
                } catch (IOException var6) {
                    Log.e("BluetoothService", "accept() failed", var6);
                    break;
                }

                if (socket != null) {
                    BluetoothService e = BluetoothService.this;
                    synchronized (BluetoothService.this) {
                        switch (BluetoothService.this.mState) {
                            case 0:
                            case 3:
                                try {
                                    socket.close();
                                } catch (IOException var4) {
                                    Log.e("BluetoothService", "Could not close unwanted socket", var4);
                                }
                                break;
                            case 1:
                            case 2:
                                BluetoothService.this.connected(socket, socket.getRemoteDevice());
                        }
                    }
                }
            }

            Log.i("BluetoothService", "END mAcceptThread");
        }

        public void cancel() {
            Log.d("BluetoothService", "cancel " + this);

            try {
                this.mmServerSocket.close();
            } catch (IOException var2) {
                Log.e("BluetoothService", "close() of server failed", var2);
            }

        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            this.mmDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(BluetoothService.MY_UUID);
            } catch (IOException var5) {
                Log.e("BluetoothService", "create() failed", var5);
            }

            this.mmSocket = tmp;
        }

        public void run() {
            Log.i("BluetoothService", "BEGIN mConnectThread");
            this.setName("ConnectThread");
            BluetoothService.this.mAdapter.cancelDiscovery();

            try {
                this.mmSocket.connect();
            } catch (IOException var5) {
                BluetoothService.this.connectionFailed();

                try {
                    this.mmSocket.close();
                } catch (IOException var3) {
                    Log.e("BluetoothService", "unable to close() socket during connection failure", var3);
                }

                BluetoothService.this.start();
                return;
            }

            BluetoothService e = BluetoothService.this;
            synchronized (BluetoothService.this) {
                BluetoothService.this.mConnectThread = null;
            }

            BluetoothService.this.connected(this.mmSocket, this.mmDevice);
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException var2) {
                Log.e("BluetoothService", "close() of connect socket failed", var2);
            }

        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d("BluetoothService", "create ConnectedThread");
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException var6) {
                Log.e("BluetoothService", "temp sockets not created", var6);
            }

            this.mmInStream = tmpIn;
            this.mmOutStream = tmpOut;
        }

        public void run() {
            Log.d("ConnectedThread线程运行", "正在运行......");
            Log.i("BluetoothService", "BEGIN mConnectedThread");

            try {
                while (true) {
                    byte[] e = new byte[256];
                    int bytes = this.mmInStream.read(e);
                    if (bytes <= 0) {
                        Log.e("BluetoothService", "disconnected");
                        BluetoothService.this.connectionLost();
                        if (BluetoothService.this.mState != 0) {
                            Log.e("BluetoothService", "disconnected");
                            BluetoothService.this.start();
                        }
                        break;
                    }

                    BluetoothService.this.mHandler.obtainMessage(2, bytes, -1, e).sendToTarget();
                }
            } catch (IOException var3) {
                Log.e("BluetoothService", "disconnected", var3);
                BluetoothService.this.connectionLost();
                if (BluetoothService.this.mState != 0) {
                    BluetoothService.this.start();
                }
            }

        }

        public void write(byte[] buffer) {
            try {
                this.mmOutStream.write(buffer);
                BluetoothService.this.mHandler.obtainMessage(3, -1, -1, buffer).sendToTarget();
            } catch (IOException var3) {
                Log.e("BluetoothService", "Exception during write", var3);
            }

        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException var2) {
                Log.e("BluetoothService", "close() of connect socket failed", var2);
            }

        }
    }
}

