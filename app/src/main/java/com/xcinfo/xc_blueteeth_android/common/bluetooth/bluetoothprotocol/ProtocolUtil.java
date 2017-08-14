package com.xcinfo.xc_blueteeth_android.common.bluetooth.bluetoothprotocol;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.xcinfo.xc_blueteeth_android.common.bluetooth.crc.CRC16Util;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.qppapi.QppApi;
import com.xcinfo.xc_blueteeth_android.common.utils.TimeUtil;

/**
 * Created by com.亚东 on 2017/2/9.
 */

public class ProtocolUtil {

    BluetoothGatt mBluetoothGatt;
    public ProtocolUtil(BluetoothGatt mBluetoothGatt){
        this.mBluetoothGatt=mBluetoothGatt;
        Log.d("@ProtocolUtil:","初始化成功");
    }

    private boolean sendMessage(byte[] data){

        byte[] crc=CRC16Util.crcByte(data);//获取crc校验
        for (byte b:addByte(data,crc)){
            Log.d("@@发送byte：",ByteUtil.ByteToString(b));
        }
        if (mBluetoothGatt==null){
            return false;
        }
        return QppApi.qppSendData(mBluetoothGatt,addByte(data,crc));
    }

    //设置时间
    public boolean setTime(){

        String time= TimeUtil.getNowTime();
        Log.d("@@time",time);
        String year=time.substring(2,4);
        byte yearByte=ByteUtil.StringtoHexByte(year);
        String month=time.substring(5,7);
        byte monthByte=ByteUtil.StringtoHexByte(month);
        String day=time.substring(8,10);
        byte dayByte=ByteUtil.StringtoHexByte(day);
        String hour=time.substring(11,13);
        byte hourByte=ByteUtil.StringtoHexByte(hour);
        String minute=time.substring(14,16);
        byte minuteByte=ByteUtil.StringtoHexByte(minute);
        String second=time.substring(17,19);
        byte secondByte=ByteUtil.StringtoHexByte(second);

        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x07,(byte)0x05,yearByte,monthByte,dayByte,hourByte,minuteByte,secondByte};
        return sendMessage(bytes);

    }

    //设置通道报警上下限
    public  Boolean setAlarmLimit(float temH, float temL, float humH, float humL){

        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x09,(byte)0x0C};
        bytes=ByteUtil.byteMerger(bytes,ByteUtil.getSetByte(temH));
        bytes=ByteUtil.byteMerger(bytes,ByteUtil.getSetByte(temL));
        bytes=ByteUtil.byteMerger(bytes,ByteUtil.getSetByte(humH));
        bytes=ByteUtil.byteMerger(bytes,ByteUtil.getSetByte(humL));
        return sendMessage(bytes);

    }

    //发送更新程序请求
    public boolean updateVision(){
        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x01};
        return sendMessage(bytes);
    }

    //发送更新程序请求
    public boolean getScope(){
        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x17};
        return sendMessage(bytes);
    }

    //设置报警方式
    public  Boolean setAlarmType(boolean isSingle){
       byte[] bytes1={(byte)0xAA,(byte)0x00,(byte)0x02,(byte)0x0E,(byte)0x01};
        byte[] bytes2={(byte)0xAA,(byte)0x00,(byte)0x02,(byte)0x0E,(byte)0x02};
        if (isSingle){
            return sendMessage(bytes2);
        }else {
            return sendMessage(bytes1);
        }
    }

    //获取MTK
    public  Boolean getMTK(){
        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x10};
        return sendMessage(bytes);
    }

    //请求设备版本号
    public  Boolean getVisionNumber(){
        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x00};
        return sendMessage(bytes);
    }

    //获取基本信息
    public  Boolean getBaseInformation(){
        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x14};
        return sendMessage(bytes);
    }

    //获取设备详情
    public  Boolean getChannelInformation(){
        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x11};
        return sendMessage(bytes);
    }

    //获取电量
    public  Boolean getBatteryPower(){
        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x0B};
        return sendMessage(bytes);
    }

    //设置存储满后处理的指令（是否覆盖）
    public  Boolean whenMemoryFull(boolean isCover){
        byte[] byte1={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x12,(byte)0x01};
        byte[] byte2={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x12,(byte)0x02};
        if (isCover){
            return sendMessage(byte1);
        }else {
            return sendMessage(byte2);
        }


    }

    //下载历史数据(温度，湿度等等)
    public boolean getHistoryInformation(){
        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x03};
        return sendMessage(bytes);
    }

    //摄氏度显示
    public boolean degreeSet(){
        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x09,(byte)0x00};
        return sendMessage(bytes);
    }

    //华氏度显示显示
    public boolean fahrenheitSet(){
        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x09,(byte)0x01};
        return sendMessage(bytes);
    }

    //修改记录间隔
    public boolean setInterval(String interval){
        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x07,ByteUtil.StringtoHexByte(interval)};
        return sendMessage(bytes);
    }

    //跟新程序
    public boolean upDate(){
        byte[] bytes={(byte)0xAA,(byte)0x00,(byte)0x01,(byte)0x01};
        return sendMessage(bytes);
    }



    private byte[] addByte(byte[] byte1,byte[] crc){
        //为指令添加crc校验
        byte[] bigByte;
        int length=byte1.length+crc.length;
        bigByte=new byte[length];
        for (int i = 0; i < byte1.length; i++) {
            bigByte[i]=byte1[i];
        }

        for (int i = 0; i < 2; i++) {
            bigByte[length-2+i]=crc[i];
        }
        for (byte b : bigByte) {
            Log.d("crc",String.format("%02x", b) + " ");
        }
        return bigByte;
    }

}
