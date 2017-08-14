package com.xcinfo.xc_blueteeth_android.main.monitor.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.sqliteutils.SqliteUtil;
import com.xcinfo.xc_blueteeth_android.main.bean.ChannelData;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;

import java.util.List;

/**
 * created by ：ycy on 2017/3/27.
 * email 1490258886@qq.com
 */

public class ChannelSimpleAdapter extends BaseAdapter {
    private Device device;
    private List<String>datas;
    private Context context;
    private ChannelData channelData;
    public ChannelSimpleAdapter(Context context, Device device,List<String>datas) {
        super();
        this.context=context;
        this.device=device;
        this.datas=datas;

    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_channel_type,null);
            holder.tv_name= (TextView) convertView.findViewById(R.id.channelType);
            holder.tv_value= (TextView) convertView.findViewById(R.id.channel_value);
            convertView.setTag(holder);
        }else{
            holder= (Holder) convertView.getTag();
        }
        holder.tv_name.setText(datas.get(position));
        holder.tv_value.setText(getChannelValue(position));
        return convertView;
    }

    private String getChannelValue(int posion) {
        channelData=SqliteUtil.getInstance(context).getAlarmeInfoBySerial(device.getDeviceSerial(),posion);
        return "报警上限："+channelData.getMaxLimit()+"    报警下限"+channelData.getMinLimit();
    }

    class Holder{
        TextView tv_name;
        TextView tv_value;
    }
}
