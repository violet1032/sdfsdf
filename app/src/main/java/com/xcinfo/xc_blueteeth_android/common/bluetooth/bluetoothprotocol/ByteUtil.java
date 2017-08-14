package com.xcinfo.xc_blueteeth_android.common.bluetooth.bluetoothprotocol;

import android.util.Log;

import com.xcinfo.xc_blueteeth_android.common.bluetooth.crc.CRC16Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by com.亚东 on 2017/3/9.
 */

public class ByteUtil {
    //将10进制String转化为16进制数后转化为Byte
    public static byte StringtoHexByte(String s){
        return Byte.parseByte(Integer.toHexString(Integer.parseInt(s)),16);
    }

    //查看byte的方法
    public  static String ByteToString(Byte b){

        return String.format("%02x", b);

    }

    //查看byte的方法
    public  static String ByteToBinaryString(Byte b){

        return Integer.toBinaryString(b);

    }



    //解析byte中的数值
    public static int ByteToInt(Byte b  ){
        return Integer.parseInt(String.format("%02x",b),16);
    }

    public static Boolean crcChecker(byte[] qppData){
        //校验crc
        byte[] CRC=new byte[]{qppData[qppData.length-2],qppData[qppData.length-1]};
        byte[] data=new byte[qppData.length-2];
        for (int i = 0; i < data.length; i++) {
            data[i]=qppData[i];
        }
        byte[] crc= CRC16Util.crcByte(data);

        if (CRC[0]==crc[0]&&CRC[1]==crc[1]){
            Log.d("@@@", "ServiceRecviced:" + "crc校验成功");
            Log.d("@@crc校验结果：",ByteUtil.ByteToString(crc[0])+"\n"+ByteUtil.ByteToString(crc[1]));
            return true;
        }else {
            Log.d("@@@", "ServiceRecviced:" + "crc校验失败");
            Log.d("@@crc校验结果：",ByteUtil.ByteToString(crc[0])+"\n"+ByteUtil.ByteToString(crc[1]));
            return false;
        }

    }


    //去掉数据前面的0，例如：0032-32
    public static String getRealData(String data){
        int index=0;
        char[] datas=data.toCharArray();
        for (int i = 0; i < datas.length; i++) {
            if(datas[i]!='0'){
                index=i;
                break;
            }


        }
        System.out.println("index:"+index);
        String realData=data.substring(index,datas.length);
        return realData;
    }


    /**
     * 将InputStream转换成byte数组
     * @param in InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] InputStreamTOByte(InputStream in) throws IOException{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count = -1;
        while((count = in.read(data,0,1024)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return outStream.toByteArray();
    }


    //温度上下限设置
    public static byte[] getSetByte(double num){
        byte[] bytes=new byte[2];

        double num1=num*10;
        short shortNum= (short) num1;
        byte high= (byte) (shortNum>>8);
        bytes[0]=high;
        byte low= (byte) shortNum;
        bytes[1]=low;
        return bytes;
    }

    //java 合并两个byte数组
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    // 两个byte转int 再除以10
    public static float channelValue(byte hdata, byte ldata) {
        short data = (short) ((hdata << 8) | (ldata & 0xff));
        float f = data / 10f;
        return f;
    }


}
