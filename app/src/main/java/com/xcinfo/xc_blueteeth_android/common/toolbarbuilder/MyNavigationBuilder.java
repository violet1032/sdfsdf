package com.xcinfo.xc_blueteeth_android.common.toolbarbuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xcinfo.xc_blueteeth_android.R;


/**
 * Created by com.亚东 on 2016/12/30.
 */

public class MyNavigationBuilder extends NavigationBuilderAdapter {
    private int leftIconRes;
    private int rightIconRes;
    private String titleString;

    private View.OnClickListener leftIconOnClickListener;
    private View.OnClickListener rightIconOnClickListener;

    public MyNavigationBuilder setLeftIconRes(int iconRes) {

        leftIconRes = iconRes;
        return this;
    }

    public MyNavigationBuilder setrightIconRes(int iconRes) {
        rightIconRes = iconRes;
        return this;
    }

    public MyNavigationBuilder settitleString(String iconRes) {

        titleString = iconRes;
        return this;
    }

    public MyNavigationBuilder setleftIconOnClickListener(View.OnClickListener onClickListener) {
        leftIconOnClickListener = onClickListener;
        return this;
    }

    public MyNavigationBuilder setrightIconOnClickListener(View.OnClickListener onClickListener) {
        rightIconOnClickListener = onClickListener;
        return this;
    }

    public MyNavigationBuilder setBackgroundColor(int resId) {
        setColor(resId);
        return this;
    }



    public MyNavigationBuilder(Context context,ViewGroup parent) {
        super(context,parent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.toolbar_layout;
    }

    @Override
    public void onCreatAndBind(ViewGroup parent) {
        super.onCreatAndBind(parent);
        setImageViewStyle(R.id.iv_left,leftIconRes,leftIconOnClickListener);
        setImageViewStyle(R.id.iv_right,rightIconRes,rightIconOnClickListener);
        //setImageViewStyle(R.id.iv_title,titleIconRes,null);
        setTexttitleString(R.id.iv_title,titleString);
    }
}
