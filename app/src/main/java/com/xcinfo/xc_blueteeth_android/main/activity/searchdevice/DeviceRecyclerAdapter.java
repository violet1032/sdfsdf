package com.xcinfo.xc_blueteeth_android.main.activity.searchdevice;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by com.亚东 on 2017/3/16.
 */

public class DeviceRecyclerAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<BluetoothDevice> devices = new ArrayList<>();
    private RecyclerviewListener listener;
    private BluetoothDevice hasConnectedDevice = null;

    public void setHasConnectedDevice(BluetoothDevice hasConnectedDevice) {
        this.hasConnectedDevice = hasConnectedDevice;
    }

    public DeviceRecyclerAdapter(Context mContext, List<BluetoothDevice> devices) {
        this.mContext = mContext;

        for (BluetoothDevice device : devices) {
            this.devices.add(device);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        viewHolder = new NormalHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_device, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final NormalHolder normalHolder = (NormalHolder) holder;
        normalHolder.deviceName.setText(devices.get(position).getName());
        normalHolder.deviceAddress.setText(devices.get(position).getAddress());
        if (listener != null) {
            ((NormalHolder) holder).btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(devices.get(position), normalHolder.linearLayoutLoading, normalHolder.btnConnect);
                }
            });
        }

        if (hasConnectedDevice != null) {
            if (hasConnectedDevice.getName().equals(devices.get(position).getName())) {
                TextView tv = (TextView) normalHolder.linearLayoutLoading.findViewById(R.id.tv_connect);
                tv.setText("已连接");
            }
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class NormalHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        LinearLayout linearLayoutLoading;
        Button btnConnect;

        public NormalHolder(View itemView) {
            super(itemView);
            deviceName = (TextView) itemView.findViewById(R.id.bluetooth_device_name);
            deviceAddress = (TextView) itemView.findViewById(R.id.bluetooth_device_address);
            btnConnect = (Button) itemView.findViewById(R.id.btn_connect);
            btnConnect.setText("连接");
            linearLayoutLoading = (LinearLayout) itemView.findViewById(R.id.linearlayout_loadingconnect);
            linearLayoutLoading.setVisibility(View.GONE);
        }
    }

    public void setRecyclerviewListener(RecyclerviewListener listener) {
        this.listener = listener;
    }

    public interface RecyclerviewListener {
        void onClick(BluetoothDevice device, LinearLayout dialogLayout, Button button);
    }
}
