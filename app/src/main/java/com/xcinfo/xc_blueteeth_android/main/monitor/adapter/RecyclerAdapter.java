package com.xcinfo.xc_blueteeth_android.main.monitor.adapter;

import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.xcinfo.xc_blueteeth_android.R;
import com.xcinfo.xc_blueteeth_android.main.bean.GroupChannel;
import com.xcinfo.xc_blueteeth_android.main.monitor.fragment.CurrentFragment;

import java.util.List;

/**
 * Created by ycy on 2017/03/21.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    protected Context mContext;
    private List<GroupChannel> mDatas;
    private MyonItemClickListener onItemClickListener;

    public void setOnItemClickListener(MyonItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private int select = 0;//被选中的单位位置

    public void setSelect(int select) {
        this.select = select;
    }

    public RecyclerAdapter(Context context, List<GroupChannel> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.recyclerview_item_type, parent,
                false),onItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.mTxt.setText(mDatas.get(position).getType());
        if(select==position){
            holder.mTxt.setTextColor(mContext.getResources().getColor(R.color.blue_main_ui));
        }else{
            holder.mTxt.setTextColor(mContext.getResources().getColor(R.color.main_balck));
        }
   }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mTxt;
        MyonItemClickListener myonItemClickListener;
        public MyViewHolder(View view, MyonItemClickListener myonItemClickListener) {
            super(view);
            this.myonItemClickListener=myonItemClickListener;
            mTxt = (TextView) view.findViewById(R.id.channel_data_type_tv);
            view.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            if(myonItemClickListener!=null){
                myonItemClickListener.onItemClick(v,getAdapterPosition());
            }
        }
    }
    public interface MyonItemClickListener{
        void onItemClick(View view,int position);
    }
}
