package com.xcinfo.xc_blueteeth_android.main.device_class;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.monitor.adapter.GridAdapter;

import java.util.List;

/**
 * Created by com.亚东 on 2017/3/25.
 */

public class DeviceClassAdapter extends RecyclerView.Adapter {
    DeviceAdapterListener listener;
    List<Device> devices;
    Context context;

    public DeviceClassAdapter(List<Device> devices, Context context) {
        this.devices = devices;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        holder = new NormalHolder(LayoutInflater.from(context).inflate(R.layout.item_deviceclass_recycler, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Device device = devices.get(position);
        NormalHolder holder1 = (NormalHolder) holder;
        if (device.getBelongGroup() == null) {
            holder1.gropName.setText("暂无分组");
        } else {
            holder1.gropName.setText(device.getBelongGroup());
        }
        holder1.deviceName.setText("设备名："+device.getDeviceName()+"  序列号："+device.getDeviceSerial());
        holder1.gridView.setAdapter(new GridAdapter(context, device.getGroupChannels()));
        holder1.gridView.setEnabled(false);
        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {

                    listener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class NormalHolder extends RecyclerView.ViewHolder {
        TextView gropName;
        TextView deviceName;
        GridView gridView;

        public NormalHolder(View itemView) {
            super(itemView);
            gropName = (TextView) itemView.findViewById(R.id.grop_state);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            gridView = (GridView) itemView.findViewById(R.id.gridview_channels);
        }
    }

    public interface DeviceAdapterListener {
        void onClick(int position);
    }

    public void setOnclickListener(DeviceAdapterListener listener) {
        this.listener = listener;
    }
}
