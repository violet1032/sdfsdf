package com.xcinfo.xc_blueteeth_android.common.sqliteutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xcinfo.xc_blueteeth_android.common.config.AppContext;
import com.xcinfo.xc_blueteeth_android.common.utils.SPUtils;
import com.xcinfo.xc_blueteeth_android.common.utils.StringUtils;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.main.bean.Alarme;
import com.xcinfo.xc_blueteeth_android.main.bean.ChannelData;
import com.xcinfo.xc_blueteeth_android.main.bean.ChannelWarnRecord;
import com.xcinfo.xc_blueteeth_android.main.bean.Exception;
import com.xcinfo.xc_blueteeth_android.main.bean.GroupChannel;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.bean.DeviceManager;
import com.xcinfo.xc_blueteeth_android.main.bean.Group;
import com.xcinfo.xc_blueteeth_android.main.monitor.bean.HistoryData;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.DeviceManagerUtil;
import com.xcinfo.xc_blueteeth_android.main.uploading.TCPSocketUtil;
import com.xcinfo.xc_blueteeth_android.main.uploading.UpLoadingUtil;
import com.xcinfo.xc_blueteeth_android.main.uploading.UploadingProtocolUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by com.亚东 on 2017/3/14.
 */

public class SqliteUtil  {
    private String DEVICE_TABLE="device";
    private String DEVICE_MANAGER_TABLE="device_manager";
    private String ALARME_TABLE="alarme";
    private String EXCEPTION_TABLE="exception";
    private String GROUP_TABLE="device_group";//设备的分组
    private String WARN_RECORD_TABLE="warn_record";//报警的记录表
    private  SQLiteDatabase db=null;
    private static Context mContext;
    private static SqliteUtil sqliteUtil;
    private String TAG="@@Sqlite";
    private boolean isFahrenheit;
    private SqliteUtil(Context context) {

        String path=mContext.getFilesDir().toString()+"/"+"xc_blueteeth_db.db";
        //String path= AppContext.getBinFileDir()+"/"+"xc_blueteeth_db.db";
        Log.d("@@dbPath",path);
        if(db==null){
            db=SQLiteDatabase.openOrCreateDatabase(path,null);
        }
        isFahrenheit= DeviceManagerUtil.getIsFahrenheit(context);
        Log.d("@@isFahrenheit",""+isFahrenheit);
    }

    public void setIsFahrenheit(boolean isFahrenheit){
        this.isFahrenheit=isFahrenheit;
    }

    public synchronized static SqliteUtil getInstance(Context context){
            mContext=context;
            if(sqliteUtil==null){
                sqliteUtil=new SqliteUtil(context);
            }
        return sqliteUtil;
    }

    public  Boolean isTableExist(String tableName){
        boolean result = false;
        if(tableName == null){
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from "+"sqlite_master"+" where type ='table' and name ='"+tableName+"' ";
            if(db!=null){
                cursor = db.rawQuery(sql, null);
                if(cursor.moveToNext()){
                    int count = cursor.getInt(0);
                    if(count>0){
                        result = true;
                    }
                }
            }else{

            }

        } catch (java.lang.Exception e) {
            // TODO: handle exception
        }
        return result;
    }
    /************************************************************************************************************/
    /*
    *静态建表
    * 创建蓝牙硬件设备表
    *
    */
    public  void createDeviceManagerTable(){
        String create_deviceManager="create table device_manager("
                +"name text primary key not null)";
        if(!isTableExist("device_manager")){
            db.execSQL(create_deviceManager);
        }
    }

    public void creatExceptionTable(){
        String creat_exception="create table exception(" +
                "device_serial varchar(10) primary key not null," +//0
                "max_range float," +//1
                "min_range float)";//2
        if (!isTableExist(EXCEPTION_TABLE)){
            db.execSQL(creat_exception);
        }
    }

    //保存或修改上下限
    public void saveException(Exception e){
        if (!isTableExist(EXCEPTION_TABLE)){
            creatExceptionTable();
        }
        ContentValues values=new ContentValues();
        values.put("device_serial",e.getDeviceSerail());
        values.put("max_range",e.getMaxRange());
        values.put("min_range",e.getMinRange());
        Cursor cursor=db.query(true,EXCEPTION_TABLE,null,"device_serial=?",new String[]{e.getDeviceSerail()},null,null,null,null);
        if (cursor.getCount()>0){
            db.update(EXCEPTION_TABLE,values,"device_serial=?",new String[]{e.getDeviceSerail()});
        }else {
            db.insert(EXCEPTION_TABLE,null,values);
        }
    }

    public Exception getException(String serial){
        if (!isTableExist(EXCEPTION_TABLE)){
            creatExceptionTable();
        }
        Exception exception=new Exception();
        exception.setDeviceSerail(serial);
        Cursor cursor=db.query(false,EXCEPTION_TABLE,null,"device_serial=?",new String[]{serial},null,null,null,null);
        if (cursor.moveToFirst()){
            exception.setMaxRange(cursor.getFloat(1));
            exception.setMinRange(cursor.getFloat(2));
        }

        return exception;
    }

    /*
    *静态建表
   * 创建蓝牙硬件设备下面对应的设备表
   * 单个通道的传感器应该将通道的ID存为1，多个从1开始增长
   * @param SQLiteDatabase db
   */
    public  void createDeviceTable(){
        String create_device="create table device("
//                +"device_id integer primary key autoincrement,"//自增长id
                +"device_serial varchar(10) primary key,"//0设备的序列号（用作服务器上传的身份识别，此号码不能为空不能重复）
                +"device_name varchar(20),"//1设备（传感器）本身的名字
                +"device_id integer,"//2设备的ID
                + "device_manager_name varchar(20),"//3蓝牙硬件的名字 作为外键
                +"total_channel_count int,"//4该设备的通道数
                +"device_state int,"//5设备状态
                +"belong_group varchar(30),"//6所属分组


                +"CHL1_id int not null,"//7通道1的ID
                +"CHL1_type varchar(20) not null,"//8通道1的类型
                +"CHL1_unit varchar(10) not null,"//9通道1的单位

                +"CHL2_id int,"//10通道2的ID
                +"CHL2_type varchar(20),"//11通道2的类型
                +"CHL2_unit varchar(10),"//12通道2的单位

//                +"CHL3_id int,"//13通道3的ID
//                +"CHL3_type varchar(20),"//14通道3的类型
//                +"CHL3_unit varchar(10),"//15通道3的单位
//
//                +"CHL4_id int,"//16通道2的ID
//                +"CHL4_type varchar(20),"//17通道4的类型
//                +"CHL4_unit varchar(10),"//18通道4的单位

                +"last_upload_time varchar(20),"//13上一次上传的时间
                +"location varchar(30),"//14
                +"channel_type int"//15 通道类型 01（温度）02（温湿度）
                +")";
        if(!isTableExist("device")){
            db.execSQL(create_device);
        }
    }

    /*
    *静态建表
    *报警表
    * @param SQLiteDatabase db
    */
    public  void createAlarmeTable(){
        String create_alarme="create table alarme("
//                +"id integer primary key autoincrement,"
                +"device_serial varchar(20) primary key not null,"//设备（传感器）本身的序列号 作为唯一身份标识0
                +"device_manager_name varchar(20),"//1
                +"CHL1_max_limit float,"//通道1的上限值2
                +"CHL1_min_limit float,"//通道1的下限值3
                +"CHL1_single_limit float,"//1单个限制4

                +"CHL2_max_limit float,"//通道1的上限值5
                +"CHL2_min_limit float,"//通道1的下限值6
                +"CHL2_single_limit float"//1单个限制7

//                +"CHL3_max_limit float,"//通道1的上限值8
//                +"CHL3_min_limit float,"//通道1的下限值9
//                +"CHL3_single_limit float,"//1单个限制10
//
//                +"CHL4_max_limit float,"//通道1的上限值11
//                +"CHL4_min_limit float,"//通道1的下限值12
//                +"CHL4_single_limit float,"//1单个限制13
                +"warn_way int,"//报警方式8


                +"alarmType int,"//9
                +"saveType  int"+")";//10

        if(!isTableExist("alarme")){
            db.execSQL(create_alarme);
        }
    }


    /*
    * 创建报警记录表格
    * */
    private void createWarnRecordTable(){
        String createWarnRecordTable="create table warn_record("
                +"device_serial varchar(20)  not null,"//序列号0
                +"channelId int not null,"//通道ID1
               // +"deviceName varchar(20),"//设备名2

                +"deviceManagerName varchar(20),"//蓝牙模块名2

                +"channelType varchar(10),"//通道类型（温度，湿度）3
                +"maxLimit float,"//报警时的限制4
                +"minLimit float,"//报警时的限制5
                +"realValue float,"//报警时的值6
                +"state int,"//是否处理7
                +"warnInfo varchar(40),"//报警提示语8
                +"recordTime datetime)";//时间9
        if(!isTableExist(WARN_RECORD_TABLE)){
            db.execSQL(createWarnRecordTable);
        }

    }
    /*
    *动态建立的数据表
    *因为表名已经标识了该通道属于哪一个设备（传感器）因此不需要上一层的字段
    *
    *
    * @param SQLiteDatabase db
    */
    public  void createDataTable(String tableName){
        String create_data="create table"+" "+ tableName+"("
                +"id integer primary key autoincrement,"//0
                +"CHL1_value float,"//1
                +"CHL1_unit char,"//2
                +"CHL2_value float,"//3
                +"CHL2_unit char,"//4


//                +"CHL3_value float,"
//                +"CHL4_value float,"
                +"time datetime not null"// 05 值对应的时间
                +")";
        //如果该表名不存在则新建表
        if(!isTableExist(tableName)){
            db.execSQL(create_data);
        }
    }


    /*
  *静态建表
  * 创建分组表
  *
  */
    public  void createGroupTable(){
        String create_group="create table device_group("
                +"group_name text primary key not null,"
                +"device_manager_name text not null,"
                +"device_count int"+
                ")";
        if(!isTableExist(GROUP_TABLE)){
            db.execSQL(create_group);
        }
    }
    /*****************************************************静态数据存储*******************************************************/
    /*
    * 保存数据到device_manager这个表中
    * @param DeviceManagerUtil devicemanager
    * */
    public  void saveDeviceManager(DeviceManager deviceManager){
        if(!isTableExist("device_manager"))
            createDeviceManagerTable();
        createDeviceTable();
        if(deviceManager!=null) {
            ContentValues values = new ContentValues();
            values.put("name",deviceManager.getName());//按理说应该先遍历数据库观察是否已经存在相同的名字
            Cursor cursor=db.query(true,"device_manager",null,"name=?",new String[]{deviceManager.getName()},null,null,null,null);
            if(cursor.getCount()<=0){
                db.insert("device_manager",null,values);
            }else{
                //UIHelper.ToastMessage("该名字已经被使用");
            }
        }
    }

    /*
    * 保存数据到device_这个表中
    * @param Device device
    * @param chlCount// 通道数目  这个设备有几个通道
    * */
    public  void saveDevice(Device device,int chlCount){
        if(!isTableExist("device"))
            createDeviceTable();
        Cursor cursor=db.query(true,"device",new String[]{"device_serial"},"device_serial=?",new String[]{device.getDeviceSerial()},null,null,null,null);
        if(cursor.getCount()<=0) {
            if (device != null) {
                ContentValues values = new ContentValues();
                values.put("device_serial", device.getDeviceSerial());//设备的序列号，唯一身份标识
                values.put("device_name", device.getDeviceName());//设备名称
                values.put("device_id", device.getDeviceId());//设备的ID
                values.put("device_manager_name", device.getDevice_manager_name());
                values.put("total_channel_count", chlCount);//通道的总数
                values.put("location", device.getLocation());
                values.put("last_upload_time", device.getLastUpLoadTime());
                values.put("device_state",device.getDeviceState());//设备状态
                values.put("channel_type",device.getChannelType());//设备通道类型
//                if(device.getBelongGroup().isEmpty()||device.getBelongGroup()==null){
//                    values.put("belong_group","");
//                }else{
//                    values.put("belong_group",device.getBelongGroup());
//                }
                switch (chlCount) {
                    case 0:
                            break;
                    case 1:
                        values.put("CHL1_id", 1);
                        values.put("CHL1_type", device.getCHL1_type());
                        values.put("CHL1_unit", device.getCHL1_unit());
                        break;
                    case 2:
                        values.put("CHL1_id", 1);
                        values.put("CHL1_type", device.getCHL1_type());
                        values.put("CHL1_unit", device.getCHL1_unit());
                        values.put("CHL2_id", 2);
                        values.put("CHL2_type", device.getCHL2_type());
                        values.put("CHL2_unit", device.getCHL2_unit());
                        break;
//                    case 3:
//                        values.put("CHL1_id",1);
//                        values.put("CHL1_type", device.getCHL1_type());
//                        values.put("CHL1_unit", device.getCHL1_unit());
//                        values.put("CHL2_id", 2);
//                        values.put("CHL2_type", device.getCHL2_type());
//                        values.put("CHL2_unit", device.getCHL2_unit());
//                        values.put("CHL3_id", 3);
//                        values.put("CHL3_type", device.getCHL3_type());
//                        values.put("CHL3_unit", device.getCHL3_unit());
//                        break;
//                    case 4:
//                        values.put("CHL1_id", 1);
//                        values.put("CHL1_type", device.getCHL1_type());
//                        values.put("CHL1_unit", device.getCHL1_unit());
//                        values.put("CHL2_id", 2);
//                        values.put("CHL2_type", device.getCHL2_type());
//                        values.put("CHL2_unit", device.getCHL2_unit());
//                        values.put("CHL3_id", 3);
//                        values.put("CHL3_type", device.getCHL3_type());
//                        values.put("CHL3_unit", device.getCHL3_unit());
//                        values.put("CHL4_id", 4);
//                        values.put("CHL4_type", device.getCHL4_type());
//                        values.put("CHL4_unit", device.getCHL4_unit());
//                        break;
                }
                db.insert("device", null, values);
            }
        }
    }


    /*
   * 保存数据到alarme这个表中
   * @param Alarme alarme
   * */
    public void saveAlarme(Alarme alarme){
        if(!isTableExist("alarme"))
            createAlarmeTable();
        //先查看该条数据是否已经存在，不存在才可以向里面插入
        Cursor cursor=db.query(true,"alarme",new String[]{"device_serial"},"device_serial=?",new String[]{alarme.getDeviceSerial()},null,null,null,null);
        if(cursor.getCount()<=0){
            ContentValues values=new ContentValues();
            //values.put("device_manager_name",alarme.getDevice_manager_name());
            values.put("device_serial",alarme.getDeviceSerial());
            //values.put("warn_way",alarme.getWayOfWaring());//报警方式
            values.put("alarmType",alarme.getAlarmType());
            values.put("saveType",alarme.getSavetype());

            values.put("CHL1_max_limit",alarme.getCHL1_maxLimit());
            values.put("CHL1_min_limit",alarme.getCHL1_minLimit());
            values.put("CHL2_max_limit",alarme.getCHL2_maxLimit());
            values.put("CHL2_min_limit",alarme.getCHL2_minLimit());
//            switch (channelId){
//                case 0:
//                    values.put("CHL1_max_limit",alarme.getCHL1_maxLimit());
//                    values.put("CHL1_min_limit",alarme.getCHL1_minLimit());
//                    break;
//                case 1:
//                    values.put("CHL2_max_limit",alarme.getCHL2_maxLimit());
//                    values.put("CHL2_min_limit",alarme.getCHL2_minLimit());
//                    break;
//                case 2:
//                    values.put("CHL3_max_limit",alarme.getCHL3_maxLimit());
//                    values.put("CHL3_min_limit",alarme.getCHL3_minLimit());
//                    break;
//                case 3:
//                    values.put("CHL4_max_limit",alarme.getCHL4_maxLimit());
//                    values.put("CHL4_min_limit",alarme.getCHL4_minLimit());
//                    break;
//            }
            db.insert("alarme",null,values);
        }
    }
    /*
    *
    * 保存数据进报警记录表，其中通道的ID和设备的序列号必须要存在
    * */
    public void saveWarnRecord(String deviceManagerName, String deviceSerial, ChannelWarnRecord channelWarnRecord, int channelId){
        if(!isTableExist(WARN_RECORD_TABLE)){
            createWarnRecordTable();
        }
        ContentValues values=new ContentValues();
        values.put("device_serial",channelWarnRecord.getDeviceSerial());
        values.put("channelId",channelId);

        values.put("channelType",channelWarnRecord.getChannelType());
//        values.put("occurCount",channelWarnRecord.getOccurCount());
        values.put("recordTime",channelWarnRecord.getRecordTime());
        values.put("state",channelWarnRecord.getState());
        //values.put("deviceManagerName",deviceManagerName);
        values.put("warnInfo",channelWarnRecord.getWarningInfo());
        values.put("maxLimit",channelWarnRecord.getMaxLimit());
        values.put("minLimit",channelWarnRecord.getMinLimit());
        values.put("realValue",channelWarnRecord.getRealValue());
        Log.d("@@saveWarn",channelWarnRecord.getRecordTime()+"  "+channelWarnRecord.getRealValue());
        //Cursor cursor=db.query(true,WARN_RECORD_TABLE,null,"device_serial=? and channelId=? and recordTime=? and realValue=?",new String[]{deviceSerial,""+channelId,channelWarnRecord.getRecordTime(),""+channelWarnRecord.getRealValue()},null,null,null,null);
        //String sql="select* from warn_record where device_serial="+deviceSerial+" and recordTime='"+channelWarnRecord.getRecordTime()+"'"+" and channelId="+channelId;
        Cursor cursor=db.rawQuery("select* from warn_record where device_serial="+deviceSerial+" and recordTime='"+channelWarnRecord.getRecordTime()+"'"+" and channelId="+channelId ,null);
        if(cursor.getCount()>0){
            //db.update(WARN_RECORD_TABLE,values,"device_serial=? and channelId=?",new String[]{deviceSerial,""+channelId});
            Log.d("@@saveWarn","update");
        }else {
            db.insert(WARN_RECORD_TABLE,null,values);
        }
    }

    /*
    * 保存分组的名字
    * */
    public boolean saveGroup(Group group,String device_manager_name){
       if(!hasDeviceManagerName(device_manager_name)){
           UIHelper.ToastMessage("没有该蓝牙设备");
           return false;
       }
        if(!isTableExist(GROUP_TABLE)){
            createGroupTable();
        }
        if(!hasGroupName(group.getGroupName())){
            //不存在名字则插入
            ContentValues values=new ContentValues();
            values.put("group_name",group.getGroupName());
            values.put("device_count",group.getDeviceCount());
            values.put("device_manager_name",device_manager_name);
            db.insert(GROUP_TABLE,null,values);
            return true;
        }
        return false;
    }




    /*****************************************************动态数据存储*******************************************************/

      /*
   * 保存数据到数据表中
   * @param Channel data
   * */
    public void saveChannelData(String tableName,ChannelData data,int totalChannelCount){
        if(!isTableExist(tableName))
            createDataTable(tableName);
        ContentValues values=new ContentValues();
        values.put("time",data.getTime());
        switch (totalChannelCount){
            case 0:
                break;
            case 1:
                values.put("CHL1_value",data.getCHL1_value());
                values.put("CHL1_unit",data.getCHL1_unit());
                break;
            case 2:
                values.put("CHL1_value",data.getCHL1_value());
                values.put("CHL1_unit",data.getCHL1_unit());
                values.put("CHL2_value",data.getCHL2_value());
                values.put("CHL2_unit",data.getCHL2_unit());
                break;
//            case 3:
//                values.put("CHL1_value",data.getCHL1_value());
//                values.put("CHL2_value",data.getCHL2_value());
//                values.put("CHL3_value",data.getCHL3_value());
//                break;
//            case 4:
//                values.put("CHL1_value",data.getCHL1_value());
//                values.put("CHL2_value",data.getCHL2_value());
//                values.put("CHL3_value",data.getCHL3_value());
//                values.put("CHL4_value",data.getCHL4_value());
//                break;
        }
        db.insert(tableName,null,values);
    }

    //上传数据
    public void upLoadingData(Device device,Context context){
        String serail=device.getDeviceSerial();
        int channelCount=device.getChannelType();
        int interval= Integer.parseInt((String) SPUtils.get(context,"interval"+device.getDeviceSerial(),""+60));
        String lastUploadTime=getLastupLoadTime(serail);
        Log.d("@@lastUploadTime",lastUploadTime);
        Cursor cursor1=db.rawQuery("select* from"+" serial"+serail+" where time>'"+lastUploadTime+"'",null);
        Cursor cursor2=db.query(true,ALARME_TABLE,null,"device_serial=?",new String[]{serail},null,null,null,null);
        List<ChannelData>channelDatas=new ArrayList<>();
        if (cursor1.moveToFirst()&&cursor2.moveToFirst())
        do {
            switch (channelCount){
                case 1:
                    //单温度通道
                    ChannelData channelData=new ChannelData();
                    channelData.setCHL1_value(cursor1.getFloat(1));
                    channelData.setCHL1_unit(cursor1.getString(2));
                    channelData.setMaxLimit(cursor2.getFloat(2));
                    channelData.setMinLimit(cursor2.getFloat(3));
                    channelDatas.add(channelData);
                    break;
                case 2:
                    //温湿度通道
                    //单温度通道
                    ChannelData channelData1=new ChannelData();
                    channelData1.setCHL1_value(cursor1.getFloat(1));

                    channelData1.setCHL1_unit(cursor1.getString(2));
                    Log.d("@@upload","温度："+channelData1.getCHL1_value()+channelData1.getCHL1_unit());
                    channelData1.setMaxLimit(cursor2.getFloat(2));
                    channelData1.setMinLimit(cursor2.getFloat(3));

                    //单温度通道
                    ChannelData channelData2=new ChannelData();
                    channelData2.setCHL1_value(cursor1.getFloat(3));
                    channelData2.setCHL1_unit(cursor1.getString(4));
                    Log.d("@@upload","湿度："+channelData2.getCHL1_value()+channelData2.getCHL1_unit());
                    channelData2.setMaxLimit(cursor2.getFloat(5));
                    channelData2.setMinLimit(cursor2.getFloat(6));

                    channelDatas.add(channelData1);
                    channelDatas.add(channelData2);
                    break;
            }

            if (TCPSocketUtil.getInstance()
                    .send(UploadingProtocolUtil
                            .getupLoadingBytes(channelDatas,serail,channelCount,interval))){
                setLastUploadTime(serail,cursor1.getString(5));
            }


        }while (cursor1.moveToNext());
        //本次上传后关闭
        try {
            TCPSocketUtil.getInstance().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDeviceSerialByManngerName(String manngerName){
        if (!isTableExist(DEVICE_TABLE)){
            createDeviceTable();
        }
        Cursor cursor=db.query(true,DEVICE_TABLE,null,"device_manager_name=?",new String[]{manngerName},null,null,null,null);
        if (cursor.moveToFirst()){
            return cursor.getString(0);
        }else return "";
    }
    public String getDeviceNameByserial(String deviceSerial){
        String name="--";
        Cursor cursor=db.query(true,DEVICE_TABLE,null,"device_serial=?",new String[]{""+deviceSerial},null,null,null,null);
        if (cursor.moveToFirst()){
            name=cursor.getString(1);
        }
        return name;
    }
    /*****************************************************数据查询*******************************************************/
   /*
   * 遍历device_manager表中的所有数据
   * */
    public List<DeviceManager> getDeviceManagerList(){
        List<DeviceManager> deviceManagers=new ArrayList<>();
        Cursor cursor;
        if(isTableExist("device_manager")){
            cursor=db.query(true,"device_manager",null,null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    DeviceManager deviceManager=new DeviceManager();
                    deviceManager.setName(cursor.getString(cursor.getColumnIndex("name")));
                    deviceManagers.add(deviceManager);

                }while (cursor.moveToNext());
                if(cursor!=null){
                    cursor.close();
                }
            }
        }
        return deviceManagers;
    }

    /*
    *注意：：：：此方法为了提高效率，减少遍历的次数，对通道数目的参数加以了封装，如果是单通道设备用2的方式访问需要加null判断
     * 遍历device表中的所有数据
    */
    public void getDeviceListByGroupName(String groupName,GroupCallback callback){
        if(isTableExist(DEVICE_TABLE)){
            Group group=new Group();
            Cursor cursor;
            List<Device>devices=new ArrayList<>();
            cursor=db.query(true,DEVICE_TABLE,null,"belong_group=?",new String[]{groupName},null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    Device device=new Device();
                    device.setDeviceSerial(cursor.getString(0));
                    device.setDeviceName(cursor.getString(1));
                    devices.add(device);
                }while (cursor.moveToNext());
                group.setGroupName(groupName);
                group.setDeviceList(devices);
                group.setDeviceCount(devices.size());
                if(cursor!=null){
                    cursor.close();
                }
                callback.onSuccess(group);
            }
        }else{
            callback.onFailure();
        }
    }

    public List<Device> getDeviceListByGroupName(String groupName,String deviceManagerName){
        List<Device>devices=new ArrayList<>();
        if(isTableExist(DEVICE_TABLE)){
            Cursor cursor;
            cursor=db.query(true,DEVICE_TABLE,null,"belong_group=? and device_manager_name=?",new String[]{groupName,deviceManagerName},null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    Device device=new Device();
                    device.setDeviceSerial(cursor.getString(0));
                    device.setDeviceName(cursor.getString(1));
                    device.setDeviceId(cursor.getInt(2));
                    device.setDevice_manager_name(cursor.getString(3));
                    device.setToatlChannelCount(cursor.getInt(4));//获得通道总数
                    device.setDeviceState(cursor.getInt(5));
                    device.setBelongGroup(cursor.getString(6));
                    device.setLastUpLoadTime(cursor.getString(19));//上一次上传的时间
                    device.setLocation(cursor.getString(20));
                    ChannelData data=null;
                    List<GroupChannel> groupChannels =new ArrayList<>();
                    /***********************通道1********************************/
                    device.setCHL1_ID(1);
                    device.setCHL1_type(cursor.getString(8));
                    device.setCHL1_unit(cursor.getString(9));
                    /***********************通道2*********************************/
                    device.setCHL2_ID(2);
                    device.setCHL2_type(cursor.getString(11));
                    device.setCHL2_unit(cursor.getString(12));
                    /***********************通道3*********************************/
                    device.setCHL3_ID(3);
                    device.setCHL3_type(cursor.getString(14));
                    device.setCHL3_unit(cursor.getString(15));
                    /***********************通道4*********************************/
                    device.setCHL4_ID(4);
                    device.setCHL4_type(cursor.getString(17));
                    device.setCHL4_unit(cursor.getString(18));
                    //设置实时值
                    data=getCurrentChlValue(cursor.getString(3)+cursor.getString(0),4);
                    device.setCHL1_current(data.getCHL1_value());
                    device.setCHL2_current(data.getCHL2_value());
                    device.setCHL3_current(data.getCHL3_value());
                    device.setCHL4_current(data.getCHL4_value());

                    //分组显示所需信息  其他可以不考虑
                    GroupChannel groupChannel41=new GroupChannel();
                    groupChannel41.setType(device.getCHL1_type());
                    groupChannel41.setUnit(device.getCHL1_unit());
                    groupChannel41.setValue(device.getCHL1_current());
                    GroupChannel groupChannel42=new GroupChannel();
                    groupChannel42.setType(device.getCHL2_type());
                    groupChannel42.setUnit(device.getCHL2_unit());
                    groupChannel42.setValue(device.getCHL2_current());
                    GroupChannel groupChannel43=new GroupChannel();
                    groupChannel43.setType(device.getCHL3_type());
                    groupChannel43.setUnit(device.getCHL3_unit());
                    groupChannel43.setValue(device.getCHL3_current());
                    GroupChannel groupChannel44=new GroupChannel();
                    groupChannel44.setType(device.getCHL4_type());
                    groupChannel44.setUnit(device.getCHL4_unit());
                    groupChannel44.setValue(device.getCHL4_current());
                    groupChannels.add(groupChannel41);
                    groupChannels.add(groupChannel42);
                    groupChannels.add(groupChannel43);
                    groupChannels.add(groupChannel44);
                    device.setGroupChannels(groupChannels);

                    devices.add(device);
                }while (cursor.moveToNext());
                if(cursor!=null){
                    cursor.close();
                }
            }
        }else{

        }
        return devices;
    }


    /*
    *注意：：：：此方法为了提高效率，减少遍历的次数，对通道数目的参数加以了封装，如果是单通道设备用2的方式访问需要加null判断
     * 遍历device表中的所有数据
    */
    public List<Device> getDeviceList(int channelCount){
        List<Device> devices=new ArrayList<>();
        Cursor cursor;
        if(isTableExist("device")){
            cursor=db.query(true,"device",null,null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    Device device=new Device();
                    device.setDeviceSerial(cursor.getString(0));
                    device.setDeviceName(cursor.getString(1));
                    device.setDeviceId(cursor.getInt(2));
                    device.setDevice_manager_name(cursor.getString(3));
                    device.setToatlChannelCount(cursor.getInt(4));//获得通道总数
                    device.setDeviceState(cursor.getInt(5));
                    device.setBelongGroup(cursor.getString(6));
                    device.setLastUpLoadTime(cursor.getString(19));//上一次上传的时间
                    device.setLocation(cursor.getString(20));
                    ChannelData data=null;
                    List<GroupChannel> groupChannels =new ArrayList<>();

                    switch (channelCount){
                        case 0:
                            break;
                        case 1:
                            /***********************通道1*********************************/
                            device.setCHL1_ID(1);
                            device.setCHL1_type(cursor.getString(8));
                            device.setCHL1_unit(cursor.getString(9));
                            //设置实时值
                            data=getCurrentChlValue(cursor.getString(3)+cursor.getString(0),channelCount);
                            device.setCHL1_current(data.getCHL1_value());
                            //分组显示所需信息  其他可以不考虑
                            GroupChannel groupChannel=new GroupChannel();
                            groupChannel.setType(device.getCHL1_type());
                            groupChannel.setUnit(device.getCHL1_unit());
                            groupChannel.setValue(device.getCHL1_current());
                            groupChannels.add(groupChannel);
                            device.setGroupChannels(groupChannels);

                            break;
                        case 2:
                            /***********************通道1*********************************/
                            device.setCHL1_ID(1);
                            device.setCHL1_type(cursor.getString(8));
                            device.setCHL1_unit(cursor.getString(9));


                            /***********************通道2*********************************/
                            device.setCHL2_ID(2);
                            device.setCHL2_type(cursor.getString(11));
                            device.setCHL2_unit(cursor.getString(12));
                            //设置实时值
                            data=getCurrentChlValue(cursor.getString(3)+cursor.getString(0),channelCount);
                            device.setCHL1_current(data.getCHL1_value());
                            device.setCHL2_current(data.getCHL2_value());
                            //分组显示所需信息  其他可以不考虑
                            GroupChannel groupChannel21=new GroupChannel();
                            groupChannel21.setType(device.getCHL1_type());
                            groupChannel21.setUnit(device.getCHL1_unit());
                            groupChannel21.setValue(device.getCHL1_current());
                            GroupChannel groupChannel22=new GroupChannel();
                            groupChannel22.setType(device.getCHL2_type());
                            groupChannel22.setUnit(device.getCHL2_unit());
                            groupChannel22.setValue(device.getCHL2_current());
                            groupChannels.add(groupChannel21);
                            groupChannels.add(groupChannel22);
                            device.setGroupChannels(groupChannels);
                            break;
                        case 3:
                            /***********************通道1*********************************/
                            device.setCHL1_ID(1);
                            device.setCHL1_type(cursor.getString(8));
                            device.setCHL1_unit(cursor.getString(9));
                            /***********************通道2*********************************/
                            device.setCHL2_ID(2);
                            device.setCHL2_type(cursor.getString(11));
                            device.setCHL2_unit(cursor.getString(12));
                            /***********************通道3*********************************/
                            device.setCHL3_ID(3);
                            device.setCHL3_type(cursor.getString(14));
                            device.setCHL3_unit(cursor.getString(15));
                            //设置实时值
                            data=getCurrentChlValue(cursor.getString(3)+cursor.getString(0),channelCount);
                            device.setCHL1_current(data.getCHL1_value());
                            device.setCHL2_current(data.getCHL2_value());
                            device.setCHL3_current(data.getCHL3_value());
                            //分组显示所需信息  其他可以不考虑
                            GroupChannel groupChannel31=new GroupChannel();
                            groupChannel31.setType(device.getCHL1_type());
                            groupChannel31.setUnit(device.getCHL1_unit());
                            groupChannel31.setValue(device.getCHL1_current());
                            GroupChannel groupChannel32=new GroupChannel();
                            groupChannel32.setType(device.getCHL2_type());
                            groupChannel32.setUnit(device.getCHL2_unit());
                            groupChannel32.setValue(device.getCHL2_current());
                            GroupChannel groupChannel33=new GroupChannel();
                            groupChannel33.setType(device.getCHL3_type());
                            groupChannel33.setUnit(device.getCHL3_unit());
                            groupChannel33.setValue(device.getCHL3_current());
                            groupChannels.add(groupChannel31);
                            groupChannels.add(groupChannel32);
                            groupChannels.add(groupChannel33);
                            device.setGroupChannels(groupChannels);
                            break;
                        case 4:
                            /***********************通道1********************************/
                            device.setCHL1_ID(1);
                            device.setCHL1_type(cursor.getString(8));
                            device.setCHL1_unit(cursor.getString(9));
                            /***********************通道2*********************************/
                            device.setCHL2_ID(2);
                            device.setCHL2_type(cursor.getString(11));
                            device.setCHL2_unit(cursor.getString(12));
                            /***********************通道3*********************************/
                            device.setCHL3_ID(3);
                            device.setCHL3_type(cursor.getString(14));
                            device.setCHL3_unit(cursor.getString(15));
                            /***********************通道4*********************************/
                            device.setCHL4_ID(4);
                            device.setCHL4_type(cursor.getString(17));
                            device.setCHL4_unit(cursor.getString(18));

                            //设置实时值
                            data=getCurrentChlValue(cursor.getString(3)+cursor.getString(0),channelCount);
                            device.setCHL1_current(data.getCHL1_value());
                            device.setCHL2_current(data.getCHL2_value());
                            device.setCHL3_current(data.getCHL3_value());
                            device.setCHL4_current(data.getCHL4_value());

                            //分组显示所需信息  其他可以不考虑
                            GroupChannel groupChannel41=new GroupChannel();
                            groupChannel41.setType(device.getCHL1_type());
                            groupChannel41.setUnit(device.getCHL1_unit());
                            groupChannel41.setValue(device.getCHL1_current());
                            GroupChannel groupChannel42=new GroupChannel();
                            groupChannel42.setType(device.getCHL2_type());
                            groupChannel42.setUnit(device.getCHL2_unit());
                            groupChannel42.setValue(device.getCHL2_current());
                            GroupChannel groupChannel43=new GroupChannel();
                            groupChannel43.setType(device.getCHL3_type());
                            groupChannel43.setUnit(device.getCHL3_unit());
                            groupChannel43.setValue(device.getCHL3_current());
                            GroupChannel groupChannel44=new GroupChannel();
                            groupChannel44.setType(device.getCHL4_type());
                            groupChannel44.setUnit(device.getCHL4_unit());
                            groupChannel44.setValue(device.getCHL4_current());
                            groupChannels.add(groupChannel41);
                            groupChannels.add(groupChannel42);
                            groupChannels.add(groupChannel43);
                            groupChannels.add(groupChannel44);
                            device.setGroupChannels(groupChannels);
                            break;
                    }
                    devices.add(device);
                }while (cursor.moveToNext());
                if(cursor!=null){
                    cursor.close();
                }
            }
        }
        return devices;
    }
     /*
    *
    *拿到某个蓝牙主机下的状态的全部设备
    * */

    public List<Device> getAllDeviceListInManager(String deviceManagerName){
        List<Device> devices=new ArrayList<>();
        Cursor cursor;
        if(isTableExist(DEVICE_TABLE)){
            cursor=db.query(true,DEVICE_TABLE,null,"device_manager_name=?",new String[]{deviceManagerName},null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    Device device=new Device();
                    device.setDeviceSerial(cursor.getString(0));
                    device.setDeviceName(cursor.getString(1));
                    device.setDeviceId(cursor.getInt(2));
                    device.setDevice_manager_name(cursor.getString(3));
                    device.setToatlChannelCount(cursor.getInt(4));//获得通道总数
                    device.setDeviceState(cursor.getInt(5));
                    device.setBelongGroup(cursor.getString(6));
                    device.setLastUpLoadTime(cursor.getString(13));//上一次上传的时间
                    device.setLocation(cursor.getString(14));
                    device.setChannelType(cursor.getInt(15));
                    ChannelData data=null;
                    List<GroupChannel> groupChannels =new ArrayList<>();
                            /***********************通道1********************************/
                            device.setCHL1_ID(1);
                            device.setCHL1_type(cursor.getString(8));
                            device.setCHL1_unit(cursor.getString(9));
                            /***********************通道2*********************************/
                            device.setCHL2_ID(2);
                            device.setCHL2_type(cursor.getString(11));
                            device.setCHL2_unit(cursor.getString(12));
//                            /***********************通道3*********************************/
//                            device.setCHL3_ID(3);
//                            device.setCHL3_type(cursor.getString(14));
//                            device.setCHL3_unit(cursor.getString(15));
//                            /***********************通道4*********************************/
//                            device.setCHL4_ID(4);
//                            device.setCHL4_type(cursor.getString(17));
//                            device.setCHL4_unit(cursor.getString(18));
                            //设置实时值
                            data=getCurrentChlValue("serial"+cursor.getString(0),cursor.getInt(4));
                            device.setTime(data.getTime());

                        device.setCHL1_current(data.getCHL1_value());


                            device.setCHL2_current(data.getCHL2_value());
//                            device.setCHL3_current(data.getCHL3_value());
//                            device.setCHL4_current(data.getCHL4_value());

                            //分组显示所需信息  其他可以不考虑
                            GroupChannel groupChannel41=new GroupChannel();
                            groupChannel41.setType(device.getCHL1_type());
                            groupChannel41.setUnit(device.getCHL1_unit());
                            groupChannel41.setValue(device.getCHL1_current());
                            GroupChannel groupChannel42=new GroupChannel();
                            groupChannel42.setType(device.getCHL2_type());
                            groupChannel42.setUnit(device.getCHL2_unit());
                            groupChannel42.setValue(device.getCHL2_current());
                            GroupChannel groupChannel43=new GroupChannel();
                            groupChannel43.setType(device.getCHL3_type());
                            groupChannel43.setUnit(device.getCHL3_unit());
                            groupChannel43.setValue(device.getCHL3_current());
                            GroupChannel groupChannel44=new GroupChannel();
                            groupChannel44.setType(device.getCHL4_type());
                            groupChannel44.setUnit(device.getCHL4_unit());
                            groupChannel44.setValue(device.getCHL4_current());
                            groupChannels.add(groupChannel41);
                            groupChannels.add(groupChannel42);
                            groupChannels.add(groupChannel43);
                            groupChannels.add(groupChannel44);
                            device.setGroupChannels(groupChannels);

                    devices.add(device);
                }while (cursor.moveToNext());
                if(cursor!=null){
                    cursor.close();
                }
            }
        }
        return devices;
    }

    public String getDeviceMannagerNameByserail(String deviceSerial){
        if(isTableExist(DEVICE_TABLE)){
            Cursor cursor=db.query(true,DEVICE_TABLE,null,"device_serial=?",new String[]{deviceSerial},null,null,null,null);
            if(cursor.moveToFirst()){
                return cursor.getString(3);
            }
        }
        return null;
    }

    public String getDeviceName(String deviceSerial){
        if(isTableExist(DEVICE_TABLE)){
            Cursor cursor=db.query(true,DEVICE_TABLE,null,"device_serial=?",new String[]{deviceSerial},null,null,null,null);
            if(cursor.moveToFirst()){
                return cursor.getString(1);
            }
        }
        return null;
    }

    public Device getDeviceInfo(String deviceManager,String deviceSerial,int channnelId){
        Device device=new Device();
        if(isTableExist(DEVICE_TABLE)){
            Cursor cursor=db.query(true,DEVICE_TABLE,null,"device_serial=?",new String[]{deviceSerial},null,null,null,null);
            if(cursor.getCount()>0){
                ChannelData channelData=getSingleCurrentChlValue(deviceManager+deviceSerial,channnelId);
                switch (channnelId){
                    case 0:
                        device.setCHL1_type(cursor.getString(8));
                        device.setCHL1_current(channelData.getCHL1_value());
                        break;
                    case 1:
                        device.setCHL2_type(cursor.getString(11));
                        device.setCHL2_current(channelData.getCHL2_value());
                        break;
                    case 2:
                        device.setCHL3_type(cursor.getString(14));
                        device.setCHL3_current(channelData.getCHL3_value());
                        break;
                    case 3:
                        device.setCHL4_type(cursor.getString(17));
                        device.setCHL4_current(channelData.getCHL3_value());
                        break;
                }
                device.setDeviceSerial(deviceSerial);
            }


        }
        return device;
    }

    /*
    *
    * 通过参数拿到某个状态的全部设备
    * */
    public List<Device> getAllDeviceListByState(String deviceManagerName,int state){
        List<Device> devices=new ArrayList<>();
        Cursor cursor;
        if(isTableExist(DEVICE_TABLE)){
            cursor=db.query(true,DEVICE_TABLE,null,"device_manager_name=? and device_state=?",new String[]{deviceManagerName,""+state},null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    Device device=new Device();
                    device.setDeviceSerial(cursor.getString(0));
                    device.setDeviceName(cursor.getString(1));
                    device.setDeviceId(cursor.getInt(2));
                    device.setDevice_manager_name(cursor.getString(3));
                    device.setToatlChannelCount(cursor.getInt(4));//获得通道总数
                    device.setDeviceState(cursor.getInt(5));
                    device.setBelongGroup(cursor.getString(6));
                    device.setLastUpLoadTime(cursor.getString(13));//上一次上传的时间
                    device.setLocation(cursor.getString(14));
                    ChannelData data=null;
                    List<GroupChannel> groupChannels =new ArrayList<>();
                    /***********************通道1********************************/
                    device.setCHL1_ID(1);
                    device.setCHL1_type(cursor.getString(8));
                    device.setCHL1_unit(cursor.getString(9));
                    /***********************通道2*********************************/
                    device.setCHL2_ID(2);
                    device.setCHL2_type(cursor.getString(11));
                    device.setCHL2_unit(cursor.getString(12));
//                    /***********************通道3*********************************/
//                    device.setCHL3_ID(3);
//                    device.setCHL3_type(cursor.getString(14));
//                    device.setCHL3_unit(cursor.getString(15));
//                    /***********************通道4*********************************/
//                    device.setCHL4_ID(4);
//                    device.setCHL4_type(cursor.getString(17));
//                    device.setCHL4_unit(cursor.getString(18));
                    //设置实时值
                    data=getCurrentChlValue("serial"+cursor.getString(0),cursor.getInt(4));
                    device.setTime(data.getTime());
//                    if (isFahrenheit){
//                        device.setCHL1_current(getFahrenHeit(data.getCHL1_value()));
//                    }else {
                        device.setCHL1_current(data.getCHL1_value());
//                    }

                    device.setCHL2_current(data.getCHL2_value());
                    device.setCHL3_current(data.getCHL3_value());
                    device.setCHL4_current(data.getCHL4_value());

                    //分组显示所需信息  其他可以不考虑
                    GroupChannel groupChannel41=new GroupChannel();
                    groupChannel41.setType(device.getCHL1_type());
                    groupChannel41.setUnit(device.getCHL1_unit());
                    groupChannel41.setValue(device.getCHL1_current());
                    GroupChannel groupChannel42=new GroupChannel();
                    groupChannel42.setType(device.getCHL2_type());
                    groupChannel42.setUnit(device.getCHL2_unit());
                    groupChannel42.setValue(device.getCHL2_current());
                    GroupChannel groupChannel43=new GroupChannel();
                    groupChannel43.setType(device.getCHL3_type());
                    groupChannel43.setUnit(device.getCHL3_unit());
                    groupChannel43.setValue(device.getCHL3_current());
                    GroupChannel groupChannel44=new GroupChannel();
                    groupChannel44.setType(device.getCHL4_type());
                    groupChannel44.setUnit(device.getCHL4_unit());
                    groupChannel44.setValue(device.getCHL4_current());
                    groupChannels.add(groupChannel41);
                    groupChannels.add(groupChannel42);
                    groupChannels.add(groupChannel43);
                    groupChannels.add(groupChannel44);
                    device.setGroupChannels(groupChannels);
                    devices.add(device);
                }while (cursor.moveToNext());
                if(cursor!=null){
                    cursor.close();
                }
            }
        }
        return devices;
    }
//    /*
//     * 遍历alarme表中的所有数据
//    */
//    public List<Alarme> getAlarmeList(){
//        List<Alarme> alarmes=new ArrayList<>();
//        Cursor cursor;
//        if(isTableExist("alarme")){
//            cursor=db.query(true,"alarme",null,null,null,null,null,null,null);
//            if(cursor.moveToFirst()){
//                do{
//                    Alarme alarme=new Alarme();
//                    alarme.setDeviceSerial(cursor.getString(0));
//                    alarme.setDevice_manager_name(cursor.getString(1));
//
//                    alarme.setCHL1_maxLimit(cursor.getFloat(2));
//                    alarme.setCHL1_minLimit(cursor.getFloat(3));
//                    alarme.setCHL1_singleLimit(cursor.getFloat(4));
//
//                    alarme.setCHL2_maxLimit(cursor.getFloat(5));
//                    alarme.setCHL2_minLimit(cursor.getFloat(6));
//                    alarme.setCHL2_singleLimit(cursor.getFloat(7));
//
//                    alarme.setCHL3_maxLimit(cursor.getFloat(8));
//                    alarme.setCHL3_minLimit(cursor.getFloat(9));
//                    alarme.setCHL3_singleLimit(cursor.getFloat(10));
//
//                    alarme.setCHL4_maxLimit(cursor.getFloat(11));
//                    alarme.setCHL4_minLimit(cursor.getFloat(12));
//                    alarme.setCHL4_singleLimit(cursor.getFloat(13));
//                    alarme.setWayOfWaring(cursor.getInt(14));
//                    alarmes.add(alarme);
//                }while (cursor.moveToNext());
//                if(cursor!=null){
//                    cursor.close();
//                }
//            }
//        }else{
//           UIHelper.ToastMessage("alarme表不存在");
//        }
//        return alarmes;
//    }

    //获取报警上下限
    public ChannelData getAlarmeInfoBySerial(String deviceSerial,int channelId){
        ChannelData channelData=new ChannelData();
        Cursor cursor;
        if(isTableExist(ALARME_TABLE)){
            cursor=db.query(true,ALARME_TABLE,null,"device_serial=?",new String[]{deviceSerial},null,null,null,null);
            if(cursor.moveToFirst()){
                channelData.setAlarmType(cursor.getInt(8));
                channelData.setSaveType(cursor.getInt(9));
                do{
                    switch (channelId){
                        case 0:
                            channelData.setMaxLimit(cursor.getFloat(2));
                            channelData.setMinLimit(cursor.getFloat(3));

                            break;
                        case 1:
                            channelData.setMaxLimit(cursor.getFloat(5));
                            channelData.setMinLimit(cursor.getFloat(6));
                            break;
//                        case 2:
//                            channelData.setMaxLimit(cursor.getFloat(8));
//                            channelData.setMinLimit(cursor.getFloat(9));
//                            break;
//                        case 3:
//                            channelData.setMaxLimit(cursor.getFloat(11));
//                            channelData.setMinLimit(cursor.getFloat(12));
//                            break;
                    }


                }while (cursor.moveToNext());
                if(cursor!=null){
                    cursor.close();
                }
            }
        }else{
//            UIHelper.ToastMessage("alarme表不存在");
        }
        return channelData;
    }

    public  boolean hasAlarmByDeviceSerial(String deviceSerial){
        if(isTableExist(ALARME_TABLE)){
            Cursor cursor=db.query(true,ALARME_TABLE,null,"device_serial=?",new String[]{deviceSerial},null,null,null,null);
            if(cursor.getCount()>0){
                return true;
            }
        }
        return false;
    }


    public List<ChannelWarnRecord> getWarnRecordBySerail(String deviceSerail,int channelId){
        if (!isTableExist(WARN_RECORD_TABLE)){
            createWarnRecordTable();
        }
        List<ChannelWarnRecord> warnRecords=new ArrayList<>();
        Cursor cursor=db.query(true,WARN_RECORD_TABLE,null,"device_serial=? and channelId=? and state=?",new String[]{""+deviceSerail,""+channelId,""+0},null,null,null,null);
       // Cursor cursor=db.rawQuery("select* from warn_record where device_serial="+deviceSerail+" and "+channelId+"="+channelId,null);
        Log.d("@@getWarnRecordBySerail",""+deviceSerail+ "  "+channelId);
        if (cursor.moveToFirst()){
            do {
                ChannelWarnRecord warnRecord=new ChannelWarnRecord();
                warnRecord.setDeviceSerial(cursor.getString(0));
                warnRecord.setChannelId(cursor.getInt(1));
                warnRecord.setDeviceName(getDeviceName(cursor.getString(0)));
                warnRecord.setDeviceManngerName(cursor.getString(2));
                warnRecord.setChannelType(cursor.getString(3));
                warnRecord.setMaxLimit(cursor.getFloat(4));
                warnRecord.setMinLimit(cursor.getFloat(5));
                if (isFahrenheit){
                    warnRecord.setRealValue(getFahrenHeit(cursor.getFloat(6)));
                }else {
                    warnRecord.setRealValue(cursor.getFloat(6));
                }

                warnRecord.setState(cursor.getInt(7));
                warnRecord.setRecordTime(cursor.getString(9));
                warnRecords.add(warnRecord);
                //Log.d("@@wanrnRecord",""+warnRecord.getRealValue()+"  "+warnRecord.getRecordTime());
            }while (cursor.moveToNext());

        }else {
            return null;
        }
        return warnRecords;
    }

    //从上次的时间继续扫描是否有记录报警
    public List<ChannelWarnRecord> getChannelWarnRecord(String deviceSerial,int channelId,Device device){

        String lastTemperatureTime=getLastTemperatureTime(deviceSerial+channelId);
        String lastHumidityTime=getLastHumidityTime(deviceSerial+channelId);

        List<ChannelWarnRecord> records=new ArrayList<>();
        if (!isTableExist("serial"+deviceSerial))
            return records;
        float value;
        //获取报警上下限
        ChannelData channelData=getAlarmeInfoBySerial(deviceSerial,channelId);
        Log.d("@@上下限",""+channelData.getMaxLimit()+""+channelData.getMinLimit());
        ChannelWarnRecord record;
        Cursor cursor1=db.rawQuery("select* from"+" serial"+deviceSerial+" where time>'"+lastTemperatureTime+"'",null);
        Cursor cursor2=db.rawQuery("select* from"+" serial"+deviceSerial+" where time>'"+lastHumidityTime+"'",null);

                switch (channelId){
                    //温度通道
                    case 0:
                        if (cursor1.moveToFirst()){
                            do {
                                value=cursor1.getFloat(1);
                                if ((value>=channelData.getMaxLimit()||value<=channelData.getMinLimit())&&channelData.getMaxLimit()!=0){
                                    Log.d("@@getChannelWarnRecord","value:"+value+"  time:"+cursor1.getString(5));
                                    record=new ChannelWarnRecord();
                                    record.setChannelId(channelId);
                                    record.setDeviceSerial(deviceSerial);
                                    record.setDeviceName(getDeviceName(deviceSerial));
                                    record.setMaxLimit(channelData.getMaxLimit());
                                    record.setMinLimit(channelData.getMinLimit());
                                    record.setRealValue(value);
                                    record.setRecordTime(cursor1.getString(5));
                                    record.setChannelType(device.getCHL1_type());
                                    record.setDeviceManngerName(device.getDevice_manager_name());
                                    records.add(record);

                                }
                            }while (cursor1.moveToNext());
                            if (records.size()>0)
                            setLastTemperatureTime(deviceSerial+channelId,records.get(records.size()-1).getRecordTime());
                        }
                        break;
                    //湿度通道
                    case 1:
                        if (cursor2.moveToFirst()){
                            do {
                                value=cursor2.getFloat(3);
                                if ((value>=channelData.getMaxLimit()||value<=channelData.getMinLimit())&&channelData.getMaxLimit()!=0){
                                    record=new ChannelWarnRecord();
                                    record.setChannelId(channelId);
                                    record.setDeviceSerial(deviceSerial);
                                    record.setDeviceName(getDeviceName(deviceSerial));
                                    record.setMaxLimit(channelData.getMaxLimit());
                                    record.setMinLimit(channelData.getMinLimit());
                                    record.setRealValue(value);
                                    record.setRecordTime(cursor2.getString(5));
                                    record.setChannelType(device.getCHL2_type());
                                    record.setDeviceManngerName(device.getDevice_manager_name());
                                    Log.d("@@records.add湿度",record.getRecordTime()+record.getRealValue());
                                    records.add(record);
                                }
                            }while (cursor2.moveToNext());
                            if (records.size()>0)
                            setLastHumidityTime(deviceSerial+channelId,records.get(records.size()-1).getRecordTime());
                        }
                        break;
                }



        return records;
    }


    //获取某个设备的全部报警记录时间，用来判断设备状态
    public List<ChannelWarnRecord> getWarnRecord(Device device){
        List<ChannelWarnRecord> records=new ArrayList<>();
        ChannelWarnRecord channelWarnRecord;
        if (!isTableExist(WARN_RECORD_TABLE))
            return records;
        Cursor cursor=db.query(true,WARN_RECORD_TABLE,null,"device_serial=? and state=?",new String[]{device.getDeviceSerial(), ""+0},null,null,null,null);
        if (cursor.moveToLast()){
            do {
                channelWarnRecord=new ChannelWarnRecord();
                channelWarnRecord.setRecordTime(cursor.getString(9));
                records.add(channelWarnRecord);
            }while (cursor.moveToNext());
        }
        return records;
    }


    /*
     * 获得某个设备的报警的方式
    */
    public int getAlarmeWayOfWarn(String deviceSerial){
        int way=0;
        Cursor cursor;
        if(isTableExist(ALARME_TABLE)){
            cursor=db.query(true,"alarme",null,"device_serial=?",new String[]{deviceSerial},null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    way=cursor.getInt(8);
                }while (cursor.moveToNext());
                if(cursor!=null){
                    cursor.close();
                }
            }
        }else{
//            UIHelper.ToastMessage("alarme表不存在");
        }
        return way;
    }



    /*
    * 返回某个数据表的数据信息
   */
    public List<ChannelData> getChannelDataList(String tableName,int totalChannelCount){
        List<ChannelData> dataList=new ArrayList<>();
        Cursor cursor;
        if(isTableExist(tableName)){
            cursor=db.query(true,tableName,null,null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    ChannelData channelData=new ChannelData();
                    switch (totalChannelCount){
                        case 0:
                            break;
                        case 1:
                            /***********************通道1*********************************/
                            channelData.setCHL1_value(cursor.getFloat(1));
                            break;
                        case 2:
                            channelData.setCHL1_value(cursor.getFloat(1));
                            channelData.setCHL2_value(cursor.getFloat(2));
                            break;
                        case 3:
                            channelData.setCHL1_value(cursor.getFloat(1));
                            channelData.setCHL2_value(cursor.getFloat(2));
                            channelData.setCHL3_value(cursor.getFloat(3));
                            break;
                        case 4:
                            channelData.setCHL1_value(cursor.getFloat(1));
                            channelData.setCHL2_value(cursor.getFloat(2));
                            channelData.setCHL3_value(cursor.getFloat(3));
                            channelData.setCHL4_value(cursor.getFloat(4));
                            break;
                    }
                    channelData.setTime(cursor.getString(5));
                    dataList.add(channelData);
                }while (cursor.moveToNext());
                if(cursor!=null){
                    cursor.close();
                }
            }
        }else{
        }
        return dataList;
    }

    /*
* 返回某个数据表的某个通道的数据信息(根据时间小时)
*/
    public void getChannelDataListByTime(final String tableName, final int channelId, final String startTime, final String endTime, final DbCallback dbCallback){
        final List<HistoryData> dataList=new ArrayList<>();
        Log.e(TAG,"getChannelDataListByTime "+tableName+" Id "+channelId);
        dataList.clear();
        dbCallback.showProcess();

        if(isTableExist(tableName)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG,"note0");
                     Cursor cursor;
                    //cursor=db.query(true,tableName,null,"time<=? and time>=?",new String[]{endTime,startTime},null,null,null,null);
                    cursor=db.rawQuery("select* from"+" "+tableName+" where time between '"+startTime+"' and "+"'"+endTime+"'",null);
                    Log.d("@@sqlite","   starttime"+startTime+"   endtime"+endTime);
                    if(cursor.getCount()<=0){
                        Log.d(TAG,"nodata");
                        dbCallback.noData();
                        dbCallback.stopProcess();
                    }
                    if(cursor.moveToFirst()){
                        Log.d("@@sqlitedatas",""+cursor.getCount());
                        do{
                            HistoryData historyData=new HistoryData();
//                            Log.d("@@sqlitedatas",""+cursor.getString(7));
                            switch (channelId){
                                case 0:
                                    if (isFahrenheit){
                                        historyData.setValue(getFahrenHeit(cursor.getFloat(1)));
                                    }else {
                                        historyData.setValue(cursor.getFloat(1));
                                    }

                                    break;
                                case 1:
                                    historyData.setValue(cursor.getFloat(3));
                                    break;
                                case 2:
                                    historyData.setValue(cursor.getFloat(3));
                                    break;
                                case 3:
                                    historyData.setValue(cursor.getFloat(4));
                                    break;
                            }

                            historyData.setTime(cursor.getString(5));
                            dataList.add(historyData);
                        }while (cursor.moveToNext());

                        dbCallback.onSuccess(dataList);
                        dbCallback.stopProcess();
                        if(cursor!=null){
                            cursor.close();
                        }
                    }
                }}).start();
        }
        else{
            Log.e(TAG,"note4");
            dbCallback.noData();
            dbCallback.onFailure();
        }
        dbCallback.stopProcess();
    }


    /*
    * 查询单个通道的历史最大和最小的历史数据
    * */
    public void getChannelDataMaxAndMin(final String tableName, final int totalChannelCount, final DbCallback dbCallback){
        final HistoryData historyData=new HistoryData();
        final List<ChannelData> dataList=new ArrayList<>();
        Log.e(TAG,tableName);
        dataList.clear();
        dbCallback.showProcess();
                if(isTableExist(tableName)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                    ChannelData channelData=new ChannelData();
                    Cursor cursor;
                    switch (totalChannelCount){
                        case 0:
                            cursor=db.query(true,tableName,null,null,null,null,null,"CHL1_value",null);
                            if(cursor.moveToFirst()){
                                float[] values=new float[cursor.getCount()];
                                for (int i = 0; i < cursor.getCount(); i++) {

                                    values[i]=cursor.getFloat(1);
                                    cursor.moveToNext();
                                }

                                Arrays.sort(values);
                                if(isFahrenheit){
                                    historyData.setMaxValue(getFahrenHeit(values[cursor.getCount()-1]));
                                    historyData.setMinValue(getFahrenHeit(values[0]));
                                }else {
                                    historyData.setMaxValue(values[cursor.getCount()-1]);
                                    historyData.setMinValue(values[0]);
                                }

                                Log.d("@@max and min","-"+values[cursor.getCount()-1]+"  -"+values[0]);
                                dbCallback.onHistorySuccess(historyData);
                                if(cursor!=null){
                                    cursor.close();
                                }
                            }
                            break;
                        case 1:
                            cursor=db.query(true,tableName,null,null,null,null,null,"CHL2_value",null);
                            if(cursor.moveToFirst()){
                                float[] values=new float[cursor.getCount()];
                                for (int i = 0; i < cursor.getCount(); i++) {
//                                    Log.d("@@getAlldata",""+cursor.getFloat(3));
                                    values[i]=cursor.getFloat(3);
                                    cursor.moveToNext();
                                }

                                Arrays.sort(values);
                                historyData.setMaxValue(values[cursor.getCount()-1]);
                                historyData.setMinValue(values[0]);
                                dbCallback.onHistorySuccess(historyData);
                                if(cursor!=null){
                                    cursor.close();
                                }
                            }
                            break;
                        case 2:
                            cursor=db.query(true,tableName,null,null,null,null,null,"CHL3_value",null);
                            if(cursor.moveToFirst()){
                                do{
                                    channelData.setCHL3_value(cursor.getFloat(5));
                                    channelData.setTime(cursor.getString(5));
                                    dataList.add(channelData);
                                }while (cursor.moveToNext());
                                historyData.setMaxValue(dataList.get(dataList.size()-1).getCHL3_value());
                                historyData.setMinValue(dataList.get(0).getCHL3_value());
                                dbCallback.onHistorySuccess(historyData);
                                if(cursor!=null){
                                    cursor.close();
                                }
                            }
                            break;
                        case 3:
                            cursor=db.query(true,tableName,null,null,null,null,null,"CHL4_value",null);
                            if(cursor.moveToFirst()){
                                do{
                                    channelData.setCHL4_value(cursor.getFloat(4));
                                    channelData.setTime(cursor.getString(5));
                                    dataList.add(channelData);
                                }while (cursor.moveToNext());
                                historyData.setMaxValue(dataList.get(dataList.size()-1).getCHL4_value());
                                historyData.setMinValue(dataList.get(0).getCHL4_value());
                                dbCallback.onHistorySuccess(historyData);
                                if(cursor!=null){
                                    cursor.close();
                                }
                            }
                            break;
                    }
                }}).start();
                    dbCallback.stopProcess();
            }else{
        //没有找到表
                    //
        dbCallback.stopProcess();
        dbCallback.noData();
        dbCallback.onFailure();}



    }

    /*
    * 返回某个数据表的数据信息
   */
//    public List<Group> getGroupList(){
//        List<Group> groupList=new ArrayList<>();
//        if(isTableExist(GROUP_TABLE)){
//            Cursor cursor=db.query(true,GROUP_TABLE,null,null,null,null,null,null,null);
//            if(cursor.moveToFirst()){
//                do{
//                    Group group=new Group();
//                    group.setGroupName(cursor.getString(0));
//                    group.setDeviceCount(cursor.getInt(1));
//                    groupList.add(group);
//                }while (cursor.moveToNext());
//            }
//        }
//        return groupList;
//    }
    /*
    * 返回某个数据表的数据信息
   */
    public List<Group> getGroupListByDeviceManager(String device_manager_name){
        List<Group> groupList=new ArrayList<>();
        if(isTableExist(GROUP_TABLE)){
            Cursor cursor=db.query(true,GROUP_TABLE,null,"device_manager_name=?",new String[]{device_manager_name},null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    Group group=new Group();
                    group.setGroupName(cursor.getString(0));
                    group.setDeviceCount(cursor.getInt(1));
                    groupList.add(group);
                }while (cursor.moveToNext());
            }
        }
        return groupList;
    }


    /*
    * 查询设备下通道的类型
    * @param devceSerial 设备序列号
    * @param channelSeq 第几个通道
    * */
    public String getChannelType(String devceSerial,int channelSeq){
        Cursor cursor;
        String channelType=null;
        if(isTableExist(DEVICE_TABLE)){
            cursor=db.query(true,DEVICE_TABLE,null,"device_serial=?",new String[]{devceSerial},null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    switch (channelSeq){
                        case 0:
                            break;
                        case 1:
                            channelType=cursor.getString(8);
                            break;
                        case 2:
                          channelType=cursor.getString(11);
                            break;
                        case 3:
                            channelType=cursor.getString(14);
                            break;
                        case 4:
                            channelType=cursor.getString(17);
                            break;
                    }
                }while (cursor.moveToNext());
                if(cursor!=null){
                    cursor.close();
                }
            }
        }
        return channelType;
    }
    /*
    * 查询设备下通道的单位
    * @param devceSerial 设备序列号
    * @param channelSeq 第几个通道
    * */
    public String getChannelTUnit(String devceSerial,int channelSeq){
        Cursor cursor;
        String channelUnit=null;
        if(isTableExist(DEVICE_TABLE)){
            cursor=db.query(true,DEVICE_TABLE,null,"device_serial=?",new String[]{devceSerial},null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    switch (channelSeq){
                        case 0:
                            break;
                        case 1:
                            channelUnit=cursor.getString(9);
                            break;
                        case 2:
                            channelUnit=cursor.getString(12);
                            break;
                        case 3:
                            channelUnit=cursor.getString(15);
                            break;
                        case 4:
                            channelUnit=cursor.getString(18);
                            break;
                    }
                }while (cursor.moveToNext());
                if(cursor!=null){
                    cursor.close();
                }
            }
        }
        return channelUnit;
    }

    /*
    *
    * 获得通道的当前值（实时值）
    * */
    public ChannelData getSingleCurrentChlValue(String tableName,int channelId){
        ChannelData data=new ChannelData();
        Cursor cursor;
        if(isTableExist(tableName)){
            cursor=db.query(true,tableName,null,null,null,null,null,null,null);
            cursor.moveToLast();
            switch (channelId){
                case 0:
                    data.setCHL1_value(cursor.getFloat(1));
                    break;
                case 1:
                    data.setCHL2_value(cursor.getFloat(2));
                    break;
                case 2:
                    data.setCHL3_value(cursor.getFloat(3));
                    break;
                case 3:
                    data.setCHL4_value(cursor.getFloat(4));
                    break;
            }
        }else{
            data.setCHL1_value(-0);
            data.setCHL2_value(-0);
            data.setCHL3_value(-0);
            data.setCHL4_value(-0);
        }
        return data;
    }

    /*
   *
   * 获得通道的当前值（实时值）
   * */
    public ChannelData getCurrentChlValue(String tableName,int totalChannelCount){
        ChannelData data=new ChannelData();
        Cursor cursor;
        if(isTableExist(tableName)){
            cursor=db.query(true,tableName,null,null,null,null,null,null,null);
            if (cursor.moveToLast()){
                switch (totalChannelCount){
                    case 1:
                        if (isFahrenheit){
                            data.setCHL1_value(getFahrenHeit(cursor.getFloat(1)));
                            Log.d("@@getCurrent",""+getFahrenHeit(cursor.getFloat(1)));
                        }else {
                            data.setCHL1_value(cursor.getFloat(1));
                        }

                        break;
                    case 2:
                        if (isFahrenheit){
                            data.setCHL1_value(getFahrenHeit(cursor.getFloat(1)));
                            Log.d("@@getCurrent",""+getFahrenHeit(cursor.getFloat(1)));
                        }else {
                            data.setCHL1_value(cursor.getFloat(1));
                        }
//                    data.setCHL1_value(cursor.getFloat(1));
                        data.setCHL2_value(cursor.getFloat(3));
                        break;
                    case 3:
                        data.setCHL1_value(cursor.getFloat(1));
                        data.setCHL2_value(cursor.getFloat(2));
                        data.setCHL3_value(cursor.getFloat(3));
                        break;
                    case 4:
                        data.setCHL1_value(cursor.getFloat(1));
                        data.setCHL2_value(cursor.getFloat(2));
                        data.setCHL3_value(cursor.getFloat(3));
                        data.setCHL4_value(cursor.getFloat(4));
                        break;
                }
                data.setTime(cursor.getString(5));
                if(cursor!=null){cursor.close();}
            }

        }else{
            data.setCHL1_value(-0);
            data.setCHL2_value(-0);
            data.setCHL3_value(-0);
            data.setCHL4_value(-0);
        }

        return data;
    }



    /*
    *
    * 获得蓝牙主机下所有的报警信息
    * */
    public List<ChannelWarnRecord> getAllWarnRecordByDeviceManagerName(String deviceManagerName){
        List<ChannelWarnRecord>channelWarnRecords=new ArrayList<>();
        String deviceSerial=getDeviceSerialByManngerName(deviceManagerName);
        if(isTableExist(WARN_RECORD_TABLE)){
            Cursor cursor=db.query(true,WARN_RECORD_TABLE,null,"device_serial=? and state=?",new String[]{deviceSerial,""+0},null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    ChannelWarnRecord channelWarnRecord=new ChannelWarnRecord();
                    channelWarnRecord.setDeviceSerial(cursor.getString(0));
                    channelWarnRecord.setChannelId(cursor.getInt(1));
                    channelWarnRecord.setDeviceName(getDeviceNameByserial(cursor.getString(0)));
                    channelWarnRecord.setDeviceManngerName(cursor.getString(2));
                    channelWarnRecord.setChannelType(cursor.getString(3));
                    channelWarnRecord.setMaxLimit(cursor.getFloat(4));
                    channelWarnRecord.setMinLimit(cursor.getFloat(5));
                    channelWarnRecord.setRealValue(cursor.getFloat(6));
                    channelWarnRecord.setState(cursor.getInt(7));
                    channelWarnRecord.setWarningInfo(cursor.getString(8));
                    channelWarnRecord.setRecordTime(cursor.getString(9));
                    channelWarnRecords.add(channelWarnRecord);
                    Log.d("@@getAllWarnRecord",channelWarnRecord.getDeviceName()+channelWarnRecord.getRealValue());
                }while (cursor.moveToNext());
                if(cursor!=null){
                cursor.close();
                }
            }

        }

        return channelWarnRecords;
    }


    /*****************************************************数据修改*******************************************************/
    /*
    * 修改alarme表中的内容
    * @param Alarme alarme//改参数务必确保有前面两个值
    * @param int totalChannelCount通道的个数
    * */
    public boolean upDateAlarme(Alarme alarme,int channelId){
        ContentValues values=new ContentValues();
        values.put("device_manager_name",alarme.getDevice_manager_name());
        values.put("device_serial",alarme.getDeviceSerial());
        switch (channelId){
            case 0:
                Log.e("tag++","note1111");
                if (alarme.getCHL1_maxLimit()!=0)
                values.put("CHL1_max_limit",alarme.getCHL1_maxLimit());
                if (alarme.getCHL1_minLimit()!=0)
                values.put("CHL1_min_limit",alarme.getCHL1_minLimit());
                break;
            case 1:
                Log.e("tag++","note222");
                if (alarme.getCHL2_maxLimit()!=0)
                values.put("CHL2_max_limit",alarme.getCHL2_maxLimit());
                if (alarme.getCHL2_minLimit()!=0)
                values.put("CHL2_min_limit",alarme.getCHL2_minLimit());
                break;
            case 2:
                Log.e("tag++","note3333");
                values.put("CHL3_min_limit",alarme.getCHL3_minLimit());
                values.put("CHL3_single_limit",alarme.getCHL3_singleLimit());
                break;
            case 3:
                Log.e("tag++","note4444");
                /***********************通道4*********************************/
                values.put("CHL4_max_limit",alarme.getCHL4_maxLimit());
                values.put("CHL4_min_limit",alarme.getCHL4_minLimit());

                break;

        }
        if(db.update("alarme",values," device_serial=?",new String[]{alarme.getDeviceSerial()})>0){
            return true;
        }else return false;
    }

    public boolean upDateAlarme(Alarme alarme){
        ContentValues values=new ContentValues();
        values.put("device_manager_name",alarme.getDevice_manager_name());
        values.put("device_serial",alarme.getDeviceSerial());
        if (alarme.getAlarmType()!=0)
        values.put("alarmType",alarme.getAlarmType());
        if (alarme.getSavetype()!=0)
        values.put("saveType",alarme.getSavetype());

        if(db.update("alarme",values," device_serial=?",new String[]{alarme.getDeviceSerial()})>0){
            return true;
        }else return false;
    }

    /*
    * 修改设备表的分组信息
    *
    * @param preGroupName 原来的分组名
    * param nowGroupName 现在的分组名
    * */
    public boolean upDateDeviceGroup(String nowGroupName,String preGroupName){
        ContentValues values=new ContentValues();
        values.put("belong_group",nowGroupName);
        if(db.update(DEVICE_TABLE,values,"belong_group=?",new String[]{preGroupName})>0)
            return true;
        return false;

    }

    public boolean upDateDeviceGroupBySerial(String nowGroupName,String deviceSerial,String preGroupName){
        if(isTableExist(DEVICE_TABLE)){
            ContentValues values=new ContentValues();
            Log.e("tag++","pre5656"+preGroupName);
            Log.e("tag++","now4545"+nowGroupName);
            values.put("belong_group",nowGroupName);
            Cursor cursor=db.query(true,DEVICE_TABLE,null,"device_serial=? and belong_group=?",new String[]{deviceSerial,preGroupName},null,null,null,null);
            if(cursor.moveToFirst()){
                if(db.update(DEVICE_TABLE,values,"device_serial=? and belong_group=?",new String[]{deviceSerial,preGroupName})>0)
                    return true;
            }
            if(cursor!=null){
                cursor.close();
            }
        }
        return false;

    }

    public boolean upDateDeviceNameBySerial(String deviceSerial, String deviceName){
        if(isTableExist(DEVICE_TABLE)){
//            Cursor cursor=db.query(true,DEVICE_TABLE,null,"device_serial=?",new String[]{deviceSerial},null,null,null,null);
//            if(cursor.moveToFirst()){
                ContentValues values=new ContentValues();
                values.put("device_name",deviceName);
                ;
            if(db.update(DEVICE_TABLE,values,"device_serial=?",new String[]{deviceSerial})>0)
                return true;
//            }
        }
        return false;
    }

    public boolean upDateDeviceStateBySerial(String deviceSerial, int state){
        if(isTableExist(DEVICE_TABLE)){
//            Cursor cursor=db.query(true,DEVICE_TABLE,null,"device_serial=?",new String[]{deviceSerial},null,null,null,null);
//            if(cursor.moveToFirst()){
            ContentValues values=new ContentValues();
            values.put("device_state",state);
            ;
            if(db.update(DEVICE_TABLE,values,"device_serial=?",new String[]{deviceSerial})>0)
                return true;
//            }
        }
        return false;
    }

    /*
* 修改设备表的分组信息
*
* @param preGroupName 原来的分组名
* param nowGroupName 现在的分组名
* */
    public int upDateGroupName(String nowGroupName,String preGroupName,String deviceManagerName){
        ContentValues values=new ContentValues();
        values.put("group_name",nowGroupName);
        if(hasGroupName(nowGroupName,deviceManagerName))
            return -2;//分组的名字已经存在
        if(db.update(GROUP_TABLE,values,"group_name=? and device_manager_name=?",new String[]{preGroupName,deviceManagerName})>0)
            return 0;
        return -1;

    }
    public int upDateGroupCount(String nowGroupName,String preGroupName,String deviceManagerName){
        ContentValues values=new ContentValues();
        values.put("group_name",nowGroupName);
        if(hasGroupName(nowGroupName,deviceManagerName))
            return -2;//分组的名字已经存在
        if(db.update(GROUP_TABLE,values,"group_name=? and device_manager_name=?",new String[]{preGroupName,deviceManagerName})>0)
            return 0;
        return -1;
    }

    /*
 *
 * 保存数据进报警记录表，其中通道的ID和设备的序列号必须要存在
 * */
    private void upDateWarnRecord(String deviceSerial,int channelId,String recordTime,int count){
        if(!isTableExist(WARN_RECORD_TABLE)){
            createWarnRecordTable();
        }
        Cursor cursor=db.query(true,WARN_RECORD_TABLE,null,"device_serial=? and channelId=?",new String[]{deviceSerial,""+channelId},null,null,null,null);
        if(cursor.getCount()>=0){
            ContentValues values=new ContentValues();
            values.put("occurCount",count);
            values.put("recordTime",recordTime);
            db.update(WARN_RECORD_TABLE,values,"device_serial=? and channelId=?",new String[]{deviceSerial,""+count});
        }
    }

    //处理报警记录
    public void upDateWarnRecord(String deviceSerial,int channelId,String recoedTime){
        ContentValues values=new ContentValues();
        values.put("state",1);
        db.update(WARN_RECORD_TABLE,values,"device_serial=? and channelId=? and recordTime=?",new String[]{deviceSerial,""+channelId,recoedTime});

    }



    /*****************************************************数据删除*******************************************************/


    /*
    *
    * @param String serial//设备的序列号
    * */
    public boolean deleteDeviceDataBySerial(String serial){
        Cursor cursor=db.query(true,DEVICE_TABLE,null,"device_serial=?",new String[]{serial},null,null,null,null);
        if(cursor.getCount()>0){
            if(db.delete(DEVICE_TABLE,"device_serial=?",new String[]{serial})>0)
                return true;
            ;
        }else{
            //该序列号对应的数据不存在
            return false;
        }
        return false;
    }

//    /*
//    * 删除分组
//    * @param groupName分组名称
//    * */
//
    public int deleteGroup(String groupName,String deviceManagerName){
        if(hasGroupName(groupName)){
            if(db.delete(GROUP_TABLE,"group_name=? and device_manager_name=?",new String[]{groupName,deviceManagerName})>0){
                upDateDeviceGroup("",groupName);
                return 0;
            }
        }
        return -1;
    }

/****************************************************检查********************************************************************/

    /*
* 检查分组名字是否存在，若存在则返回true,不存在返回FALSE
* */
    public boolean hasGroupName(String groupName){
        Cursor cursor=db.query(true,GROUP_TABLE,null,"group_name=?",new String[]{groupName},null,null,null,null);
        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }
    public boolean hasGroupName(String groupName,String deviceManagerName){
        Cursor cursor=db.query(true,GROUP_TABLE,null,"group_name=? and device_manager_name=?",new String[]{groupName,deviceManagerName},null,null,null,null);
        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }

    /*
* 检查分组名字是否存在，若存在则返回true,不存在返回FALSE
* */
    public boolean hasDeviceManagerName(String device_manager_name){
        Cursor cursor=db.query(true,DEVICE_MANAGER_TABLE,null,"name=?",new String[]{device_manager_name},null,null,null,null);
        if(cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }

    //上次查询报警的最后时间
    private String getLastTemperatureTime(String deviceInfo){
        String time= (String) SPUtils.get(mContext,deviceInfo+"lastTemperatureTime","0000-00-00 00:00:00");
        Log.d("@@get","LastTemperatureTime"+time);
        return time;
    }

    private void setLastTemperatureTime(String deviceInfo,String time){
        SPUtils.put(mContext,deviceInfo+"lastTemperatureTime",time);
        Log.d("@@set","LastTemperatureTime"+time);
    }

    private String getLastHumidityTime(String deviceInfo){
        String time=(String) SPUtils.get(mContext,deviceInfo+"lastHumidityTime","0000-00-00 00:00:00");
        Log.d("@@get","LastHumidityTime"+time);
        return time;
    }

    private void setLastHumidityTime(String deviceInfo,String time){
        SPUtils.put(mContext,deviceInfo+"lastHumidityTime",time);
        Log.d("@@set","LastHumidityTime"+time);
    }

    private void setLastUploadTime(String deviceSerial,String  time){
        SPUtils.put(mContext,deviceSerial+"lastupLoadTIme",time);
    }

    private String getLastupLoadTime(String deviceSerial){
        return (String) SPUtils.get(mContext,deviceSerial+"lastupLoadTIme","0000-00-00 00:00:00");
    }

    private float getFahrenHeit(float degree){
        float fahrenheit= (float) (degree*1.8+32);
        return fahrenheit;
    }
}
