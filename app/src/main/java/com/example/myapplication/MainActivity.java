//客户端

package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static final String open_window = "1";
    public static final String shut_window = "0";
    public static final String zero_window = "0";
    public static final String tf_window = "2";
    public static final String ft_window = "3";
    public static final String sf_window = "4";
    public static final String max_window = "1";
    public static final String stop_bt = "9";
    public static final String light_bt = "5";
    private BluetoothAdapter mBtAdapter;
    private Set<BluetoothDevice> bondedDevices;
    private BluetoothDevice mOldDevice;
    private BluetoothDevice mCurDevice;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private OutputStream os;
    private String bluetooth_name;//藍牙名稱
    private String check_rept_mac;
    private Button bt_connect;
    private Button bt_start;
    private Button bt_breakoff;
    private Button bt_search;
    private Button bt_stop;
    private Button bt_light;
    private SeekBar skb;
    private TextView text_window;
    private BluetoothSocket mSocket;

    private ListView lt;
    private final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    // 连接对象的名称
    private final String NAME = "LGL";
    private int jud_seekbar;
    private ArrayAdapter<String> Adapter;
    private ArrayList<String> arr = new ArrayList<String>();
    public Toast toastCenter;
    public ArrayAdapter adapter;
    bluetooth bluet = new bluetooth();
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))//蓝牙搜索状态发生改变
            {
            }
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))//开始搜索蓝牙
            {
                Toast.makeText(getApplicationContext(),"开始搜索",Toast.LENGTH_LONG).show();
                arr.clear();
                mDeviceList.clear();//清除mac地址,蓝牙设备列表等
                check_rept_mac = "";
                Adapter.notifyDataSetChanged();//更新列表
                Log.e("aa","started");
            }
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))//搜索结束
            {
                Log.e("aa","finished");
                Toast.makeText(getApplicationContext(),"搜索結束",Toast.LENGTH_LONG).show();
            }
            if(BluetoothDevice.ACTION_FOUND.equals(action)){//找到一个设备
//                Toast.makeText(getApplicationContext(),"1111",Toast.LENGTH_LONG).show();
                Log.e("aa","get");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() == BluetoothDevice.BOND_BONDED)//如果已经绑定了
                {
                    Log.e("aa","a");
                }
                if(!check_rept_mac.contains(device.getAddress())){
                    //找到一个，添加一个
                    mDeviceList.add(device);//添加到蓝牙设备列表中
                    arr.add(device.getName() + "\n" + device.getAddress());//用户显示的列表分两行显示,第一行蓝牙设备名字,第二行MAC地址
                }
                check_rept_mac += device.getAddress();
                Adapter.notifyDataSetChanged();//更新显示列表

            }
        }
    };

    /**
     * 解决：无法发现蓝牙设备的问题
     *
     * 对于发现新设备这个功能, 还需另外两个权限(Android M 以上版本需要显式获取授权,附授权代码):
     */
    private final int ACCESS_LOCATION=1;
    @SuppressLint("WrongConstant")
    private void getPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int permissionCheck = 0;
            permissionCheck = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                //未获得权限
                this.requestPermissions( // 请求授权
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        ACCESS_LOCATION);// 自定义常量,任意整型
            }
        }
    }


    public void write(String message) {
        try {
            if (os != null) {
                os.write(message.getBytes("GBK"));
                Log.e("write", message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void connectDevice(BluetoothDevice device) {
        // 判断是否在搜索,如果在搜索，就取消搜索
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        try {
            // 获得远程设备
//            if (mCurDevice == null || mCurDevice != mOldDevice) {
//            mCurDevice = mBtAdapter.getRemoteDevice("7C:38:AD:51:EA:16");//C4:E1:A1:B6:4F:6E
                mCurDevice = mBtAdapter.getRemoteDevice(bluetooth_name);
            mOldDevice = mCurDevice;
            Log.e("asd", "device:" + mCurDevice);
            mSocket = mCurDevice.createRfcommSocketToServiceRecord(MY_UUID);
            // 连接
            mSocket.connect();
            // 获得输出流
            os = mSocket.getOutputStream();
            Log.e("getos", "device:" + mCurDevice);
//            }
            write("01");
            // 如果成功获得输出流

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("con", "device:" + mCurDevice);
        }
    }

        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //permission granted!
                }
                return;
            }
        }
    }

    private boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arr);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        if (Build.VERSION.SDK_INT >= 6.0) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    10);
        }
        getPermission();
        setContentView(R.layout.fragment_button);

        toastCenter = Toast.makeText(getApplicationContext(),"居中的Toast",Toast.LENGTH_LONG);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        registerBluetoothReceiver();
        Log.e("already","register");
        toastCenter.setGravity(Gravity.CENTER,0,0);
        bluet.aa();
        if(bluet.jud_blt() == false)
            startActivityForResult(bluet.bb(), 0);

        bt_connect = (Button)findViewById(R.id.button_connect);
        bt_start = (Button)findViewById(R.id.button_start);
        bt_breakoff = (Button)findViewById(R.id.button_breakoff);
        bt_search = (Button)findViewById(R.id.button_search);
        bt_stop = (Button)findViewById(R.id.button_stop);
        bt_light = (Button)findViewById(R.id.button_light);
        lt = (ListView)findViewById(R.id.list2);
        skb = (SeekBar)findViewById(R.id.seekBar_window);
        text_window = (TextView)findViewById(R.id.textView_window_open);
        lt.setAdapter(Adapter);
        bt_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                write(light_bt);
            }
        });
        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectDevice(mOldDevice);
                Log.e("lianjie",bluetooth_name);
            }
        });
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//按钮监听
//                setContentView(R.layout.fragment_button);
                bondedDevices = mBtAdapter.getBondedDevices();
                Log.e("aa",bondedDevices.toString());
                bluet.searchDevices();
//                AcceptThread conect = new AcceptThread();
//                conect.start();
            }
        });
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                write(open_window);
            }
        });
        bt_breakoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                write(shut_window);
                skb.setProgress(0);
            }
        });
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                write(stop_bt);
            }
        });
        lt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetooth_name = "";
                bluetooth_name = ((TextView) view).getText().toString();
                bluetooth_name = bluetooth_name.substring(bluetooth_name.length()-17,bluetooth_name.length());
                Log.e("list",bluetooth_name);
            }
        });
        skb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                jud_seekbar = 2;
                text_window.setText("开窗百分比" + i + "%");
                if(i == 0)
                    jud_seekbar = 0;
                else if(i == 100)
                    jud_seekbar = 1;
                else
                    jud_seekbar = 2;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int seekProgress = skb.getProgress();
                if(seekProgress<13){
                    skb.setProgress(0);
                    write(zero_window);
                }else if(seekProgress>=13 && seekProgress<38){
                    skb.setProgress(25);
                    write(tf_window);
                }else if(seekProgress>=38 && seekProgress<63){
                    skb.setProgress(50);
                    write(ft_window);
                }else if(seekProgress>=63 && seekProgress<88){
                    skb.setProgress(75);
                    write(sf_window);
                }else if(seekProgress>=88){
                    skb.setProgress(100);
                    write(max_window);
                }
//                if(jud_seekbar == 0)//刻度0
//                if(jud_seekbar == 1)//刻度100
//                    write(text_window.getText().toString().substring(text_window.getText().toString().length()-4, text_window.getText().toString().length()-1));
//                if(jud_seekbar == 2)//刻度199
//                    write(text_window.getText().toString().substring(text_window.getText().toString().length()-3, text_window.getText().toString().length()-1));
//                Log.e("11", text_window.getText().toString().substring(text_window.getText().toString().length()-3, text_window.getText().toString().length()-1));

            }

        });

    }


    private void registerBluetoothReceiver() {//动态注册蓝牙广播
        IntentFilter filter = new IntentFilter();
        //开始查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //查找设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //绑定状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        Log.e("a","注册");
        registerReceiver(mReceiver, filter);
    }


    // 线程服务类
    public class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;
        private BluetoothSocket socket;
        // 连接对象的名称
        private final String NAME = "LGL";
        // 输入 输出流
        private OutputStream os;
        private InputStream is;

        private Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                Log.e("22", "服务端:" + msg.obj);
                super.handleMessage(msg);
            }
        };

        public AcceptThread() {
            try {
                serverSocket = mBtAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            // 截获客户端的蓝牙消息
            try {
                Log.e("stop", "waitformes");
                socket = serverSocket.accept(); // 如果阻塞了，就会一直停留在这里
                is = socket.getInputStream();
                os = socket.getOutputStream();
                Log.e("start", "asdsadasdasd");
                while (true) {
                    synchronized (this) {
                        byte[] tt = new byte[is.available()];
                        if (tt.length > 0) {
                            is.read(tt, 0, tt.length);
                            Message msg = new Message();
                            msg.obj = new String(tt, "GBK");
                            Log.e("aa", "客户端:" + msg.obj);
                            handler.sendMessage(msg);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

        @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
