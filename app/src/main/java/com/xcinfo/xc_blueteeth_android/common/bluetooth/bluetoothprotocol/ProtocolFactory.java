package com.xcinfo.xc_blueteeth_android.common.bluetooth.bluetoothprotocol;

/**
 * Created by com.亚东 on 2017/2/9.
 */

public class ProtocolFactory {
    public interface VisionNumber{
        int getVisionNUmber();
    }

    public interface Battery{
        int getPower();
    }

    public interface isOk{
        void setIsOk(Boolean isOk);
    }


}
