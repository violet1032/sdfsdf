package com.xcinfo.xc_blueteeth_android.common.sqliteutils;



import com.xcinfo.xc_blueteeth_android.main.monitor.bean.HistoryData;

import java.util.List;

/**
 * created by ：ycy on 2017/3/21.
 * email 1490258886@qq.com
 */

public interface DbCallback {
    void showProcess();
    void onSuccess(List<HistoryData> dataList);
    void stopProcess();
    void onFailure();
    void onHistorySuccess(HistoryData history);
    void noData();
}
