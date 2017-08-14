package com.xcinfo.xc_blueteeth_android.main.monitor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;

import java.util.List;

/**
 * created by ：ycy on 2017/3/19.
 * email 1490258886@qq.com
 */

public class MonitorDeviceRecyclerSubAdapter extends RecyclerView.Adapter<MonitorDeviceRecyclerSubAdapter.MonitorDeviceSubHolder>{
    private Context mContext;
    private Device device;
    public MonitorDeviceRecyclerSubAdapter(Context context, Device device) {
        super();
        mContext=context;
        this.device=device;
    }

    @Override
    public int getItemCount() {
        return device.getToatlChannelCount();
    }

    @Override
    public void onBindViewHolder(MonitorDeviceSubHolder holder, int position) {
       switch (device.getToatlChannelCount()){
           case 0:
               break;
           case 1:
               if(device.getCHL1_type().equals("温度")){
                   holder.imageView.setImageResource(R.drawable.temperature);
               }
               if(device.getCHL1_type().equals("湿度"))
                   holder.imageView.setImageResource(R.drawable.temperature);
               break;
       }
    }

    @Override
    public MonitorDeviceSubHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MonitorDeviceSubHolder subHolder=new MonitorDeviceSubHolder(LayoutInflater.from(mContext).inflate(R.layout.item_grid_device,null));
        return subHolder;
    }

    class  MonitorDeviceSubHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView tv_deviceName_value;
        public MonitorDeviceSubHolder(View itemView) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.grid_icon);
            tv_deviceName_value= (TextView) itemView.findViewById(R.id.tv_detail_grid);
        }
    }
}
