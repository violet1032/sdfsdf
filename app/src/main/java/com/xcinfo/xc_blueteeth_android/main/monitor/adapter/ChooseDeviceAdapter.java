package com.xcinfo.xc_blueteeth_android.main.monitor.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.main.bean.Device;

import java.util.HashMap;
import java.util.List;

/**
 * created by ：ycy on 2017/3/18.
 * email 1490258886@qq.com
 */

public class ChooseDeviceAdapter extends BaseAdapter {
    private Context mContext;
    private List<Device>datas;
    private HashMap<Integer,Boolean>isSelected;//标记是否选中

    public ChooseDeviceAdapter(Context context, List<Device>datas) {
        super();
        this.mContext=context;
        this.datas=datas;
        Log.e("size",""+datas.size());
        isSelected=new HashMap<>();
        initData();
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

    private void initData(){
        for(int i=0;i<datas.size();i++){
            //全部设为没有选中
            getIsSelected().put(i,false);
        }
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.choose_device_item,null);
            holder.tv_device_name= (TextView) convertView.findViewById(R.id.tv_deviceName);
            holder.checkBox =(CheckBox) convertView.findViewById(R.id.btn_check);
            holder.root_layout=(RelativeLayout)convertView.findViewById(R.id.root_layout);
            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
        }
        holder.tv_device_name.setText(datas.get(position).getDeviceName());
        //根据isSelected来设置item的选中情况
        holder.checkBox.setChecked(getIsSelected().get(position));
        notifyDataSetChanged();
        return convertView;
    }
    class Holder{
        RelativeLayout root_layout;
        TextView tv_device_name;
        CheckBox checkBox;
    }


    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        this.isSelected = isSelected;
    }
}
