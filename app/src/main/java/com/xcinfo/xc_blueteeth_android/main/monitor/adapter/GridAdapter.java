package com.xcinfo.xc_blueteeth_android.main.monitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.main.bean.Group;
import com.xcinfo.xc_blueteeth_android.main.bean.GroupChannel;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.DeviceManagerUtil;

import java.util.List;

/**
 * created by ：ycy on 2017/3/19.
 * email 1490258886@qq.com
 */

public class GridAdapter extends BaseAdapter {
    private Context mContext;
    private List<GroupChannel> groupChannels;

    public GridAdapter(Context context,List<GroupChannel> groupChannels) {
        super();
        this.mContext=context;
        this.groupChannels=groupChannels;
    }

    @Override
    public int getCount() {
        return groupChannels.size();
    }

    @Override
    public Object getItem(int position) {
        return groupChannels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SubHolder holder=new SubHolder();
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_grid_device,null);
            holder.imageView= (ImageView) convertView.findViewById(R.id.grid_icon);
            holder.tv_deviceName_value= (TextView) convertView.findViewById(R.id.tv_detail_grid);
            convertView.setTag(holder);
        }else{
            holder= (SubHolder) convertView.getTag();
        }
        if(groupChannels.get(position)!=null&&groupChannels.get(position).getType()!=null){
            switch (groupChannels.get(position).getType()){
                case "温度":
                    holder.imageView.setImageResource(R.drawable.temperature);
                    break;
                case "湿度":
                    holder.imageView.setImageResource(R.drawable.icon_humidity);
                    break;
                default:
                    holder.imageView.setImageResource(R.drawable.icon_common);
                    break;
            }
            String unit=groupChannels.get(position).getUnit();
            if (DeviceManagerUtil.getIsFahrenheit(mContext)){
                if (position==0){
                    unit="℉";
                }
            }
            holder.tv_deviceName_value.setText(groupChannels.get(position).getType()+"  "+groupChannels.get(position).getValue()+unit);
        }

        return convertView;
    }
    class SubHolder{
        ImageView imageView;
        TextView tv_deviceName_value;
    }
}
