package com.xcinfo.xc_blueteeth_android.common.sqliteutils;

import com.xcinfo.xc_blueteeth_android.main.bean.Device;
import com.xcinfo.xc_blueteeth_android.main.bean.Group;

import java.util.List;

/**
 * created by ：ycy on 2017/3/24.
 * email 1490258886@qq.com
 */

public interface GroupCallback {
    void onSuccess(Group group);
    void onFailure();
    void showProgress();
    void stopProgress();
}
