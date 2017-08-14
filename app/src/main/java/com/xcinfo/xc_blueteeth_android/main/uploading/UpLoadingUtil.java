package com.xcinfo.xc_blueteeth_android.main.uploading;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by com.亚东 on 2017/5/14.
 */

public class UpLoadingUtil {
    public static void upLoading(){
        try {

            Socket socket=new Socket("121.42.50.102",30003);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("@@socket异常",e.toString());
        }
    }
}
