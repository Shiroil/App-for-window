package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.os.Handler;
import android.os.Message;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;




public class bluetooth<registerReceiver> {
    private BluetoothAdapter mBtAdapter;
    private Intent enableIntent;
    private String TAG = "bluetooth:";
    private BluetoothDevice mCurDevice;
    // 输出流_客户端需要往服务端输出
    private BluetoothSocket mSocket;
    private BluetoothDevice mOldDevice;
    private OutputStream os;
    private final UUID MY_UUID = UUID
            .fromString("db764ac8-4b08-7f25-aafe-59d03c27bae3");
    // 连接对象的名称
    private final String NAME = "LGL";

    IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//    registerReceiver(bluetoothReceiver, intentFilter);




    public void aa( ){
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter == null){
            //表明此手机不支持蓝牙
            return;
        }
    }

    /**
     * 判断蓝牙开启
     * @return
     */
    public boolean jud_blt(){
        if(!mBtAdapter.isEnabled()) { //蓝牙未开启，则开启蓝牙
            return false;
        }
        else return true;
    }

    /**
     * 开启蓝牙
     * @return
     */
    public Intent bb(){
        if(!mBtAdapter.isEnabled()){ //蓝牙未开启，则开启蓝牙
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            return enableIntent;
    }
        return enableIntent;
    }
    /**
     * 搜索设备
     */

    public void searchDevices() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        // 判断是否在搜索,如果在搜索，就取消搜索
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        // 开始搜索
        mBtAdapter.startDiscovery();
        Log.e(TAG,"正在搜索...");
    }





}
