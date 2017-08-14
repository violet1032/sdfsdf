package com.xcinfo.xc_blueteeth_android.bluetoothtest;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;

import java.util.List;

/**
 * Created by com.亚东 on 2017/2/13.
 */

public class ListAdapter extends BaseAdapter {
    List<BluetoothDevice> devices;
    Context mContext;

    public ListAdapter(Context mContext,List<BluetoothDevice>list) {
        super();
        this.mContext=mContext;
        this.devices=list;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View contentView;
        ViewHolder mViewHolder=new ViewHolder();
        if (convertView==null){
            contentView= LayoutInflater.from(mContext).inflate(R.layout.item_device_list,null);
            mViewHolder.name= (TextView) contentView.findViewById(R.id.device_name);
            mViewHolder.mac= (TextView) contentView.findViewById(R.id.device_mac);
            contentView.setTag(mViewHolder);
        }else {
            contentView=convertView;
            mViewHolder= (ViewHolder) contentView.getTag();
        }
        mViewHolder.name.setText(devices.get(position).getName());
        mViewHolder.mac.setText(devices.get(position).getAddress());

        return contentView;
    }

    class  ViewHolder {
        TextView name;
        TextView mac;
    }
}
