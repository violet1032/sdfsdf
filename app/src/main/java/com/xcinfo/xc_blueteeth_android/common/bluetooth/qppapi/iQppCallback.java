package com.xcinfo.xc_blueteeth_android.common.bluetooth.qppapi;

import android.bluetooth.BluetoothGatt;

/**
 * @Description QPP interface
 * @author fqZhang
 * @version 1.0
 * @date 2014-7-10
 * @Copyright (c) 2014 Quintic Co., Ltd. Inc. All rights reserved.
 *
 */

public interface iQppCallback {
	void onQppReceiveData(BluetoothGatt mBluetoothGatt, String qppUUIDForNotifyChar, byte[] qppData);
}
