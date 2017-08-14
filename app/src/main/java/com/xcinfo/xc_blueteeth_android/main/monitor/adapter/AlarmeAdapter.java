package com.xcinfo.xc_blueteeth_android.main.monitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.main.bean.ChannelWarnRecord;

import java.util.List;

/**
 * Created by ycy on 2017/03/25.
 */
public class AlarmeAdapter extends BaseAdapter {
    private List<ChannelWarnRecord> warningList;
    private Context mContext;
    private ChannelWarnRecord warning;


    public AlarmeAdapter(Context context, List<ChannelWarnRecord> warningList) {
        mContext = context;

        this.warningList = warningList;

    }

    @Override
    public int getCount() {
        return warningList.size();
    }

    @Override
    public Object getItem(int position) {
        return warningList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        TextView tvEquipName;//报警设备名称
        TextView tvSerail;//设备序列号
        TextView tvWarnType;//设备类型
        TextView tvWarnValue;//报警解决状态
        TextView tvMaxLimit;//报警上限
        TextView tvMinlimit;//报警下限
        TextView tvWarnTime;//报警时间

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_alamefragment, null);
            viewHolder = new ViewHolder();

            viewHolder.tvEquipName = (TextView) convertView.findViewById(R.id.tv_device_name);
            viewHolder.tvSerail= (TextView) convertView.findViewById(R.id.tv_device_serial);
            viewHolder.tvWarnType= (TextView) convertView.findViewById(R.id.tv_warn_type);
            viewHolder.tvWarnValue= (TextView) convertView.findViewById(R.id.tv_warn_value);
            viewHolder.tvMaxLimit= (TextView) convertView.findViewById(R.id.tv_max_limit);
            viewHolder.tvMinlimit= (TextView) convertView.findViewById(R.id.tv_min_limit);
            viewHolder.tvWarnTime= (TextView) convertView.findViewById(R.id.tv_warn_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        warning = warningList.get(position);


        viewHolder.tvEquipName.setText(warning.getDeviceName());
        viewHolder.tvSerail.setText(warning.getDeviceSerial());
        viewHolder.tvWarnType.setText(warning.getChannelType());
        viewHolder.tvWarnValue.setText(""+warning.getRealValue());
        viewHolder.tvMaxLimit.setText(""+warning.getMaxLimit());
        viewHolder.tvMinlimit.setText(""+warning.getMinLimit());
        viewHolder.tvWarnTime.setText(""+warning.getRecordTime());
        return convertView;
    }
}
