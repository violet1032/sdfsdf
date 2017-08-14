package com.xcinfo.xc_blueteeth_android.main.activity.manngerlistactivity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.servicetest.PairedDeviceAdapter;

import java.util.List;

/**
 * Created by com.亚东 on 2017/4/20.
 */

public class ManngerListAdapter extends RecyclerView.Adapter {
    List<String> manngerList;
    Context mContext;
    clickListener listener;

    public ManngerListAdapter(List<String> manngerList,Context mContext){
        this.manngerList=manngerList;
        this.mContext=mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_mannger,parent,false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        NormalViewHolder normalViewHolder= (NormalViewHolder) holder;
        normalViewHolder.manngerName.setText(manngerList.get(position));
        normalViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null)
                    listener.onClick(manngerList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return manngerList.size();
    }

    class NormalViewHolder extends RecyclerView.ViewHolder{
        TextView manngerName;
        public NormalViewHolder(View itemView) {
            super(itemView);
            manngerName= (TextView) itemView.findViewById(R.id.paired_device);
        }
    }

    public interface clickListener{
        void onClick(String mannger);
    }

    public void setClickListener(clickListener listener){
        this.listener=listener;
    }
}
