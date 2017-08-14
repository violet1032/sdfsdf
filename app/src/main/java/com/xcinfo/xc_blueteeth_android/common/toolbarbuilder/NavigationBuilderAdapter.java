package com.xcinfo.xc_blueteeth_android.common.toolbarbuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcinfo.xc_blueteeth_android.R;

/**
 * Created by com.亚东 on 2016/12/30.
 */

public abstract class NavigationBuilderAdapter implements NavigationBuilder {

    private View contentView;
    private Context context;
    private ViewGroup parent;

    public Context getContext() {
        return context;
    }


    public NavigationBuilderAdapter(Context context, ViewGroup parent) {
        this.context = context;
        this.parent = parent;
    }

    //创建绑定ToolBar
    @Override
    public void onCreatAndBind(ViewGroup parent) {
        contentView = getContentView();
        ViewGroup viewGroup = (ViewGroup) contentView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(contentView);
        }
        parent.addView(contentView, 0);
    }


    public View getContentView() {
        if (contentView == null) {
            contentView = LayoutInflater.from(getContext()).inflate(getLayoutId(), parent, false);
        }

        return contentView;
    }

    //设置控件属性

    @Override
    public void setImageViewStyle(int viewId, int imageRes, View.OnClickListener onClickListener) {
        ImageView view = (ImageView) getContentView().findViewById(viewId);
        if (imageRes == 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setImageResource(imageRes);
            view.setOnClickListener(onClickListener);
        }

    }

    public void setTexttitleString(int viewId, String titleString) {
        TextView textView = (TextView) getContentView().findViewById(viewId);
        textView.setText(titleString);
    }

    public void setColor(int resId) {
        getContentView().setBackgroundColor(getContext().getResources().getColor(resId));
    }

    public abstract int getLayoutId();
}
