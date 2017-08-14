package com.xcinfo.xc_blueteeth_android.common.bluetooth.crc;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScaleUtil {

	public static byte[] intToByte(int data) {
		byte[] bt = new byte[2];
		bt[1] = (byte) ((data & 0xff00) >> 8); // 高位
		bt[0] = (byte) (data & 0x00ff); // 低位
		return bt;
	}

	public static byte[] intToByteCloud(int data) {
		byte[] bt = new byte[2];

		if (data < 0) {
			bt[0] = (byte) ((data & 0xff00) >> 8); // 高位
			bt[1] = (byte) (data & 0x00ff); // 低位
		} else {
			bt[1] = (byte) ((data & 0xff00) >> 8); // 高位
			bt[0] = (byte) (data & 0x00ff); // 低位
		}
		return bt;
	}

	// AsciiתChar
	public static char asciiToChar(int num) {
		return (char) num;
	}

	// 10进制转16
	public static int hex10to16(int i) {
		return Integer.parseInt(Integer.toHexString(i));
	}

	public static int byteToShort(byte hData, byte lData) {
		int data = (((hData << 8) | (lData & 0xff)) & 0xffff);
		return data;
	}

	// 两个byte转int 再除以10
	public static float channelValue(byte hdata, byte ldata) {
		short data = (short) ((hdata << 8) | (ldata & 0xff));
		float f = data / 10f;
		return f;
	}

	public static int decimaTo2(byte hdata, byte ldata) {
		short data = (short) ((hdata << 8) | (ldata & 0xff));
		return data;
	}

	public static String bianryTo10(String data) {
		return Integer.valueOf(data, 2).toString();
	}

	public static List<Object> getTimeList() {
		List<Object> list = new ArrayList<Object>();
		Calendar nowTime = Calendar.getInstance();
		list.add(nowTime.get(Calendar.YEAR));
		list.add(nowTime.get(Calendar.MONTH) + 1);
		list.add(nowTime.get(Calendar.DAY_OF_MONTH));
		list.add(nowTime.get(Calendar.HOUR_OF_DAY));
		list.add(nowTime.get(Calendar.MINUTE));
		list.add(nowTime.get(Calendar.SECOND));
		return list;
	}

	// 判断两个时期是否是同一天
	public static boolean isSameDay(Date day1, Date day2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String ds1 = sdf.format(day1);
		String ds2 = sdf.format(day2);
		if (ds1.equals(ds2)) {
			return true;
		} else {
			return false;
		}
	}

	// String 转date
	public static Date stringToDate(String str) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.parse(str);
	}

	// 获取当前时间，年月日
	public static String getStringYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}

	// 获取当前时间，String
	public static String getStringNowDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		return sdf.format(new Date());
	}
	//获取当前时间
	public static String getNowTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	// 时间格式化，获取当前时间,把秒数去掉了
	public static Date getNowDate() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		return sdf.parse(sdf.format(new Date()));
	}

	// date 转String
	public static String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		return sdf.format(date);
	}

	// 截取天
	public static String getDay(String str) {
		return str.substring(0, 10);
	}

	// 截取时分
	public static String getMinute(String str) {
		return str.substring(11, 16);
	}

	// 加间隔，正常或者是报警
	public static String addTime(Date start, int space) {
		long time = start.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long timeAdd = time + 60 * 1000 * space;
		Date date = new Date(timeAdd);
		return sdf.format(date);
	}

	// 加一天
	public static String addDay(String str) throws ParseException {
		long time = stringToDate(str).getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		long timeAdd = time + 24 * 60 * 60 * 1000;
		Date date = new Date(timeAdd);
		return sdf.format(date);
	}

	// 加一分钟
	public static String addMinute(String str) throws ParseException {
		long time = stringToDate(str).getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long timeAdd = time + 60 * 1000;
		Date date = new Date(timeAdd);
		return sdf.format(date);
	}

	// 加一秒
	public static String addSecond(Date start) {
		long time = start.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long timeAdd = time + 1000;
		Date date = new Date(timeAdd);
		return sdf.format(date);
	}

	// 获取毫秒数
	public static long getMS() {
		Calendar c = Calendar.getInstance();
		return c.getTimeInMillis();
	}

	// 获取当前程序运行路径
	public static String getRunPath() {
		File f = new File("");
		String parentPath = null;
		try {
			parentPath = f.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return parentPath;
	}

	// 删除
	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 * 
	 * @param sPath
	 *            要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.exists() && file.isFile()) { // 为文件时调用删除文件方法
				file.delete();
				return true;
			}
			return flag;
		}
	}

	// 检查文件夹路径是否存在，不存在新建
	public static void checkFolder(String path) {
		File file = new File(path);
		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
	}

	// 检查文件是否存在
	public static boolean checkFile(String filePath, String fileName) {
		File file = new File(filePath, fileName);
		if (!file.exists()) {
			checkFolder(filePath);
			return false;
		} else {
			return true;
		}
	}
	
//	//获取该主机下的设备的最早上传设备时间
//	public static String getEarlyDate(List<Hostprodevicetable>deviceList){
//		List<Long> list=new ArrayList<Long>();
//		long time;
//		for(int i=0;i<deviceList.size();i++){
//			time = deviceList.get(i).getUpCloudTime().getTime();
//			list.add(time);
//		}
//		Collections.sort(list);
//		Date date = new Date(list.get(0));
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		return sdf.format(date);
//	}
	//检查两个时间先后
	public static boolean checkTime(String early){
		
		long earlyMS = 0;
		try {
			earlyMS = stringToDate(early).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long nowMS=getMS();
		if(earlyMS<nowMS){
			return false;
		}else{
			return true;
		}
	}
}
