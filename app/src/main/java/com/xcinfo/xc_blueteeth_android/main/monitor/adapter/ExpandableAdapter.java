package com.xcinfo.xc_blueteeth_android.main.monitor.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.main.bean.Group;
import com.xcinfo.xc_blueteeth_android.main.monitor.activity.EditGroupActivity;
import com.xcinfo.xc_blueteeth_android.main.monitor.util.RefreshListener;

import java.util.List;

/**
 * created by ：ycy on 2017/3/18.
 * email 1490258886@qq.com
 */

public class ExpandableAdapter extends BaseExpandableListAdapter implements RefreshListener {
    private Context mContext;
    private List<Group>groups;
    private boolean isVisible;
    public ExpandableAdapter(Context context, List<Group>groupList,boolean isVisible) {
        super();
        this.mContext=context;
        this.groups=groupList;
        this.isVisible=isVisible;
    }

    @Override
    public void refresh(int fromWhere) {

    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getDeviceList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getDeviceList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder=new GroupHolder();
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.group_list,null);
            holder.tv_groupName=(TextView)convertView.findViewById(R.id.tv_groupLabel) ;
            holder.arrow=(ImageView)convertView.findViewById(R.id.image_expand);
            holder.image_delete=(ImageView)convertView.findViewById(R.id.image_delete);
            convertView.setTag(holder);
        }else{
            holder= (GroupHolder) convertView.getTag();
        }
        if(isVisible){
            holder.image_delete.setVisibility(View.VISIBLE);
        }else{
            holder.image_delete.setVisibility(View.GONE);
        }
        holder.tv_groupName.setText(groups.get(groupPosition).getGroupName()+"("+groups.get(groupPosition).getDeviceCount()+"台)");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder=new ChildHolder();
        if(convertView==null){
            convertView=LayoutInflater.from(mContext).inflate(R.layout.group_child_item,null);
            holder.tv_deviceName= (TextView) convertView.findViewById(R.id.tv_deviceName);
            convertView.setTag(holder);
        }else{
            holder= (ChildHolder) convertView.getTag();
        }
        holder.tv_deviceName.setText(groups.get(groupPosition).getDeviceList().get(childPosition).getDeviceName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    class GroupHolder{
        TextView tv_groupName;
        ImageView arrow;
        ImageView image_delete;
    }
    class ChildHolder{
        TextView tv_deviceName;
    }

//    private class QuickWayListener implements AdapterView.OnItemLongClickListener {
//        @Override
//        public boolean onItemLongClick(AdapterView<?> arg0, View view,
//                                       int pos, long id) {
//            int groupPos = (Integer)view.getTag(R.id.xxx01); //参数值是在setTag时使用的对应资源id号
//            int childPos = (Integer)view.getTag(R.id.xxx02);
//            if(childPos == -1){//长按的是父项
//                //根据groupPos判断你长按的是哪个父项，做相应处理（弹框等）
//            } else {
//                //根据groupPos及childPos判断你长按的是哪个父项下的哪个子项，然后做相应处理。
//            }
//            return false;
//        }
//    }

    class MyReciver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String response=intent.getStringExtra("about_arrow");
        }
    }
}
