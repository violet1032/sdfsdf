package com.xcinfo.xc_blueteeth_android.main.monitor.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.common.api.ApiCommon;
import com.xcinfo.xc_blueteeth_android.common.utils.UIHelper;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;

import java.util.List;

/**
 * created by ：ycy on 2017/3/15.
 * email 1490258886@qq.com
 */

public class MonitorDeviceRecyclerAdapter extends RecyclerView.Adapter<MonitorDeviceRecyclerAdapter.MonitorGridViewHolder> {
    private List<Device>deviceList;
    private Context mContext;
    private MyItemClickListener onItemClickListener;

    public void setOnItemClickListener(MyItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MonitorDeviceRecyclerAdapter(Context context, List<Device>devices) {
        super();
        this.mContext=context;
        this.deviceList=devices;
    }

    @Override
    public MonitorGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MonitorGridViewHolder holder=new MonitorGridViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_monitor_device,null),onItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(MonitorGridViewHolder holder, int position) {
        switch (deviceList.get(position).getDeviceState()){
            case 0://正常
                holder.tv_device_state.setBackgroundResource(R.drawable.device_normal_shape);
                holder.tv_device_state.setTextColor(mContext.getResources().getColor(R.color.green_normal));
                holder.tv_device_state.setText("正常");
                break;
            case 1://报警
                holder.tv_device_state.setBackgroundResource(R.drawable.device_waring_shape);
                holder.tv_device_state.setTextColor(mContext.getResources().getColor(R.color.channel_status_warning));
                holder.tv_device_state.setText("报警");
                break;
            case 2://异常
                holder.tv_device_state.setBackgroundResource(R.drawable.device_abnormal_shape);
                holder.tv_device_state.setTextColor(mContext.getResources().getColor(R.color.channel_status_abnormal));
                holder.tv_device_state.setText("异常");
                break;
            case 3://离线
                holder.tv_device_state.setBackgroundResource(R.drawable.device_offline_shape);
                holder.tv_device_state.setTextColor(mContext.getResources().getColor(R.color.channel_status_off));
                holder.tv_device_state.setText("离线");
                break;
        }
        holder.tv_deviceName.setText(deviceList.get(position).getDeviceName());
//        GridLayoutManager gridLayoutManager=new GridLayoutManager(mContext,2);
        holder.gridView.setAdapter(new GridAdapter(mContext,deviceList.get(position).getGroupChannels()));
        holder.gridView.setClickable(false);
        holder.gridView.setFocusable(false);
        holder.gridView.setPressed(false);
        holder.gridView.setEnabled(false);
//
//        holder.recyclerView.setLayoutManager(gridLayoutManager);
//        holder.recyclerView.setAdapter(new MonitorDeviceRecyclerSubAdapter(mContext,deviceList.get(position).getGroupChannels()));
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    class MonitorGridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_device_state;
        TextView tv_deviceName;
        LinearLayout root_layout;
//        RecyclerView recyclerView;
        GridView gridView;
        MyItemClickListener myItemClickListener;
        public MonitorGridViewHolder(View itemView,MyItemClickListener myItemClickListener) {
            super(itemView);
            this.myItemClickListener=myItemClickListener;
            tv_device_state= (TextView) itemView.findViewById(R.id.listitem_monitoring_equipment_status);
            tv_deviceName= (TextView) itemView.findViewById(R.id.listitem_monitoring_equipment_name);
            gridView=(GridView)itemView.findViewById(R.id.recycler_detector_grid) ;
            root_layout= (LinearLayout) itemView.findViewById(R.id.root_layout);
            root_layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(myItemClickListener!=null){
                myItemClickListener.onItemClick(v,getAdapterPosition());
            }
        }
    }
    public interface MyItemClickListener{
        void onItemClick(View view,int position);
    }
}
