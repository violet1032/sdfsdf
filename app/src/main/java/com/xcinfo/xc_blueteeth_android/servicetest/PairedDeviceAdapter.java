package com.xcinfo.xc_blueteeth_android.servicetest;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;

import java.util.List;

/**
 * Created by com.亚东 on 2017/4/10.
 */

public class PairedDeviceAdapter extends RecyclerView.Adapter {
    private List<BluetoothDevice> devices;
    private Context context;
    private OnclickListener listener;

    public PairedDeviceAdapter(List<BluetoothDevice> devices,Context context){
        this.devices=devices;
        this.context=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder=null;
        holder= new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_service_test_recycler,parent,false));

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        BaseViewHolder holder1= (BaseViewHolder) holder;
        holder1.tv.setText(devices.get(position).getName()+"\n"+devices.get(position).getAddress());
        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class BaseViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        public BaseViewHolder(View itemView) {
            super(itemView);
            tv= (TextView) itemView.findViewById(R.id.paired_device);
        }
    }

    public interface OnclickListener{
        void onClick(int position);
    }

    public void setItemOnclickListener(OnclickListener listener){
        this.listener=listener;
    }

}
