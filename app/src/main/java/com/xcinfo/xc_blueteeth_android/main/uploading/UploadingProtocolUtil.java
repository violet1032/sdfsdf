package com.xcinfo.xc_blueteeth_android.main.uploading;

import android.util.Log;

import com.xcinfo.xc_blueteeth_android.common.bluetooth.bluetoothprotocol.ByteUtil;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.crc.CRC16Util;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.crc.CRCUtils;
import com.xcinfo.xc_blueteeth_android.common.bluetooth.crc.ScaleUtil;
import com.xcinfo.xc_blueteeth_android.common.utils.TimeUtil;
import com.xcinfo.xc_blueteeth_android.main.bean.ChannelData;

import java.util.List;

/**
 * 根据协议将数据转换换成上传的格式
 * Created by com.亚东 on 2017/5/11.
 */

public class UploadingProtocolUtil {

    //参数部分
    public static byte getDeviceParameter(int channelNum){
        byte parameter=(byte) 0x00;
        switch (channelNum){
            case 1:
                parameter=(byte) 0x31;
                break;
            case 2:
                parameter=(byte)0x32;
                //parameter=(byte) 0x12;
                break;
        }
        //Log.d("@@uploading","设备参数："+ ByteUtil.ByteToString(parameter)+" "+ByteUtil.ByteToBinaryString(parameter));
        return parameter;
    }

    //间隔
    public static byte getInterval(int interval){
        byte byteInterval;
        byteInterval= Byte.parseByte(Integer.toHexString(interval));
        //Log.d("@@uploading间隔",ByteUtil.ByteToString(byteInterval));
        return byteInterval;
    }

    public static byte[] getChannelData(ChannelData channelData){
        //统一利用CHL1传值
        float value=channelData.getCHL1_value();
        float maxLimit=channelData.getMaxLimit();
        float minLimit=channelData.getMinLimit();
        String unit=channelData.getCHL1_unit();//01H(温度),02H(湿度)
        Log.d("@@getChannelData",""+value+" "+unit);

        byte type=(byte) 0x00;//通道类型
        if (unit.equals("C")){
            type=(byte)0x41;
        }
        if (unit.equals("%")){
            type=(byte)0x43;
        }

        int intValue =Integer.parseInt((""+value).replace(".",""));
        byte hValue= (byte) (intValue>>>8);
        byte lValue= (byte) intValue;
        byte[] byteValue=new byte[]{lValue,hValue};

        byte[] byteMaxLimit=ByteUtil.getSetByte(maxLimit);
        byte[] byteMinLimit=ByteUtil.getSetByte(minLimit);

        byte mark=(byte) 0x00;//报警标志
        if (value>maxLimit){
            mark=(byte) 0x01;
        } if(value<minLimit){
            mark=(byte) 0x02;
        }

        byte[] data1=new byte[]{mark,type};
        byte[] data2=ByteUtil.byteMerger(data1,byteValue);
        byte[] data3=ByteUtil.byteMerger(data2,byteMaxLimit);
        byte[] data4=ByteUtil.byteMerger(data3,byteMinLimit);

//        for(byte b:data4){
//            Log.d("@@uploading通道信息",ByteUtil.ByteToString(b));
//        }
        return data4;
    }

    public static byte[] getBattery(){
        byte[] battery=new byte[]{(byte)0xff, (byte)0xff, (byte)0x29};
//        for(byte b:battery){
//            Log.d("@@uploading电量",ByteUtil.ByteToString(b));
//        }
        return battery;
    }

    public static byte[] getTime(){
        String time= TimeUtil.getNowTime();
        Log.d("@@time",time);
        int year= Integer.parseInt(time.substring(0,4))-2000;
        int month=Integer.parseInt(time.substring(5,7));
        int day=Integer.parseInt(time.substring(8,10));
        int hour=Integer.parseInt(time.substring(11,13));
        int minute=Integer.parseInt(time.substring(14,16));
        int second=Integer.parseInt(time.substring(17,19));

        int data;
        data=year<<4;
        data=data+month;
        data=data<<5;
        data=data+day;
        data=data<<5;
        data=data+hour;
        data=data<<6;
        data=data+minute;
        data=data<<6;
        data=data+second;

        byte[] getTime=intToByteArray1(data);

//        for(byte b:getTime){
//            Log.d("@@uploading时间",ByteUtil.ByteToString(b));
//        }

        return getTime;
    }

    public static byte[] getGps(){
        byte[] bytes=new byte[]{(byte) 0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
//        for(byte b:bytes){
//            Log.d("@@uploadingGps",ByteUtil.ByteToString(b));
//        }
        return bytes;
    }

    public static byte[] getSerail(String serail){
        char[] serails=serail.toCharArray();
        byte[] byteSerail=new byte[]{(byte) 00};
        for (char c:serails){
            int asciil=Integer.valueOf(c);
            byte[] cs=new byte[]{ByteUtil.StringtoHexByte(""+asciil)};
            //Log.d("@@uploadingserail",c+"valueof  "+Integer.valueOf(c)+"   "+ByteUtil.ByteToString(cs[0]));
            byteSerail=ByteUtil.byteMerger(byteSerail,cs);
        }

        return byteSerail;
    }


    public static byte[] getupLoadingBytes(List<ChannelData> channelDatas, String serail, int channelNum, int interval){
        byte[] upLoadingBytes = new byte[2];
        if (channelNum==1){
            upLoadingBytes=new byte[]{(byte) 0x28,(byte) 0x1C,(byte) 0x18};//包总长，数据长度，设备类型
        }else if (channelNum==2){
            upLoadingBytes=new byte[]{(byte) 0x30,(byte) 0x24,(byte) 0x18};//包总长，数据长度，设备类型
        }
        
        upLoadingBytes=ByteUtil.byteMerger(upLoadingBytes,new byte[]{getDeviceParameter(channelNum)});//设备参数
        upLoadingBytes=ByteUtil.byteMerger(upLoadingBytes,new byte[]{(ByteUtil.StringtoHexByte(""+interval))});//间隔
        switch (channelNum){
            //通道数据
            case 1:
                upLoadingBytes=ByteUtil.byteMerger(upLoadingBytes,getChannelData(channelDatas.get(0)));
                break;
            case 2:
                ChannelData channelData=channelDatas.get(0);
                ChannelData channelData1=channelDatas.get(1);

                upLoadingBytes=ByteUtil.byteMerger(upLoadingBytes,getChannelData(channelData));
                upLoadingBytes=ByteUtil.byteMerger(upLoadingBytes,getChannelData(channelData1));
                break;
        }
        upLoadingBytes=ByteUtil.byteMerger(upLoadingBytes,getBattery());//电量
        upLoadingBytes=ByteUtil.byteMerger(upLoadingBytes,getTime());//时间
        upLoadingBytes=ByteUtil.byteMerger(upLoadingBytes,getGps());//地址
        upLoadingBytes=ByteUtil.byteMerger(upLoadingBytes,getSerail(serail));//编号

//        byte[] length=new byte[]{(byte) (upLoadingBytes.length+3)};//总长
//        upLoadingBytes=ByteUtil.byteMerger(length,upLoadingBytes);
        byte[] b= ScaleUtil.intToByte(CRCUtils.calcCrc16(upLoadingBytes));
//        for(byte b1:b){
//            Log.d("@@二号算法",ByteUtil.ByteToString(b1));
//        }
        CRC16Util.getCRC(upLoadingBytes);
        upLoadingBytes=ByteUtil.byteMerger(upLoadingBytes, new byte[]{b[1],b[0]});//crc
        return upLoadingBytes;
    }


    //int转byte数组
    public static byte[] intToByteArray1(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

}
