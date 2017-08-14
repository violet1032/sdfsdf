package com.xcinfo.xc_blueteeth_android.common.toolbarbuilder;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by com.亚东 on 2016/12/30.
 */

public interface NavigationBuilder {
    void onCreatAndBind(ViewGroup parent);
    void setImageViewStyle(int viewId, int imageRes, View.OnClickListener onClickListener);
}
