package com.xcinfo.xc_blueteeth_android.common.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.kymjs.kjframe.ui.KJFragment;

/**
 * created by ：ycy on 2017/3/21.
 * email 1490258886@qq.com
 */

public abstract class BasicFragment extends Fragment implements View.OnClickListener{
    private View viewContent;//缓存视图
    private boolean isInit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (viewContent == null){
            viewContent = inflater.inflate(getContentView(),container,false);
            //创建视图时做的工作:获取控件
            initWidget(viewContent);
        }

        //判断Fragment对应的Activity是否存在这个视图
        ViewGroup parent = (ViewGroup) viewContent.getParent();
        if (parent != null){
            //如果存在,那么我就干掉,重写添加,这样的方式我们就可以缓存视图
            parent.removeView(viewContent);
        }
        return viewContent;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isInit){
            this.isInit = true;
            initData();
        }
    }



    public abstract int getContentView();

    //视图创建完后使用
    public void initData(){

    }

    public abstract void initWidget(View viewContent);

    @Override
    public void onClick(View v) {

    }
}
