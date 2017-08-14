package com.xcinfo.xc_blueteeth_android.main.monitor.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.main.monitor.bean.HistoryData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yhq on 2016/6/3.
 */
public class ChannelListAdapter extends BaseAdapter {
    private Context context;
    private List<HistoryData> historyDatas;
    private String channelType;
    private String channelUnit;

    public ChannelListAdapter(Context context, List<HistoryData>historyDatas,String channelType,String channelUnit) {
        this.context = context;
        this.historyDatas = historyDatas;
        this.channelType=channelType;
        this.channelUnit=channelUnit;
    }



    @Override
    public int getCount() {
        return historyDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChannelViewHolder holder = null;
        if (convertView == null) {
            holder = new ChannelViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_channel_history_list, null);;
            holder.channel = (TextView) convertView.findViewById(R.id.item_channel_unit_tv);
            holder.channelTime = (TextView) convertView.findViewById(R.id.item_channel_time_tv);
            convertView.setTag(holder);
        } else {
            holder = (ChannelViewHolder) convertView.getTag();
        }
        //字符串拼接
        StringBuilder sb=new StringBuilder();
        sb.append(channelType);
        sb.append(":");
        sb.append(historyDatas.get(position).getValue());
        sb.append(channelUnit);
        sb.append("  ");
        holder.channel.setText(sb);
        holder.channelTime.setText(historyDatas.get(position).getTime());
        return convertView;
    }

    public class ChannelViewHolder {
        TextView channel;
        TextView channelTime;
    }

    //设置item内部组件颜色
//    public void setLimitColor(ChannelViewHolder holder){
//        holder.channelName.setTextColor(context.getResources().getColor(R.color.channel_data_limit));
//        holder.channelUnit.setTextColor(context.getResources().getColor(R.color.channel_data_limit));
//        holder.channelValue.setTextColor(context.getResources().getColor(R.color.channel_data_limit));
//        holder.channelTime.setTextColor(context.getResources().getColor(R.color.channel_data_limit));
//    }
    //正常时
//    public void setNormalColor(ChannelViewHolder holder){
//        holder.channelName.setTextColor(context.getResources().getColor(R.color.channel_data_normal));
//        holder.channelUnit.setTextColor(context.getResources().getColor(R.color.channel_data_normal));
//        holder.channelValue.setTextColor(context.getResources().getColor(R.color.channel_data_normal));
//        holder.channelTime.setTextColor(context.getResources().getColor(R.color.channel_data_normal));
//    }
}
