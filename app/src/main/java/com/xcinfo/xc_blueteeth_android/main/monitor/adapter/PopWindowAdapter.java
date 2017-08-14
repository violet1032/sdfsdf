package com.xcinfo.xc_blueteeth_android.main.monitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.main.bean.Group;

import java.util.List;

/**
 * created by ：ycy on 2017/3/17.
 * email 1490258886@qq.com
 */

public class PopWindowAdapter extends BaseAdapter {
    private Context mContext;
    private List<Group>groups;
    public static int selectItem=-1;
    public PopWindowAdapter(Context context, List<Group>groupList) {
        super();
        mContext=context;
        groups=groupList;
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Object getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=new ViewHolder();
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_group_listview,null);
            viewHolder.tv_groupName= (TextView) convertView.findViewById(R.id.tv_groupName);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        if(selectItem==position){
            viewHolder.tv_groupName.setTextColor(mContext.getResources().getColor(R.color.blue_main_ui));
        }else{
            viewHolder.tv_groupName.setTextColor(mContext.getResources().getColor(R.color.white));
        }
        StringBuilder sb=new StringBuilder();
        sb.append(groups.get(position).getGroupName())
                .append("(")
                .append(groups.get(position).getDeviceList().size())
                .append("台)");
        viewHolder.tv_groupName.setText(sb);

        return convertView;
    }
    class ViewHolder{
        TextView tv_groupName;
    }
}
