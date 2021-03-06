package com.xanthuim.bluetoothsample;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.utils.LogUtils;
import com.xanthuim.bluetoothsample.bluetooth.BluetoothManager;
import com.xanthuim.bluetoothsample.bluetooth.GeneralBluetoothManager;
import com.xanthuim.bluetoothsample.bluetooth.IBluetoothCallback;
import com.xanthuim.bluetoothsample.bluetooth.ServiceBluetoothManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Xanthium on 2017/3/2.
 * <p>
 * 面向Unity蓝牙功能的类
 */

public class BluetoothService implements IBluetoothCallback
{
    public static final String TAG = "BluetoothService";

    //不支持蓝牙
    public static final int UNSUPPORT_BLUETOOTH = -1;
    //不支持低功耗
    public static final int UNSUPPORT_BLE = -2;
    //没有启用蓝牙
    public static final int BLUETOOTH_ENABLED = -3;
    //扫描到设备
    public static final int BLUETOOTH_SCAN_DEVICE = 0;
    //连接成功
    public static final int BLUETOOTH_CONNECTED = 1;
    //断开连接
    public static final int BLUETOOTH_DISCONNECTED = 2;
    //通信值有改变
    public static final int BLUETOOTH_VALUE_CHANGED = 3;
    //重连
    public static final int BLUETOOTH_RECONNECT = 4;
    //配对的设备
    public static final int BLUETOOTH_BONDE_DEVICE = 5;

    //蓝牙设备名前缀，固定前缀
    private static String FILTER_NAME = "SHOWBABY";

    private static final String[] PERMISSONS = {Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    //过滤重复蓝牙设备信息
    private Set<BluetoothDevice> devices = new HashSet<>();
    //蓝牙管理
    private BluetoothManager bluetoothManager;

    private String mAddress;
    private Map<String, String> valueMap;
    //是否是唤醒状态，返回应用
    private boolean mStop;
    private Context context;

    IResultView<BluetoothDevice> r;

    /**
     * 回掉接口对象和Context
     *
     * @param r
     * @param context
     */
    public BluetoothService(IResultView<BluetoothDevice> r, Context context)
    {
        this.r = r;
        this.context = context;
        createManager();
    }

    /**
     * 创建蓝牙管理器，并给Unity发送消息
     */
    private void createManager()
    {
        if (bluetoothManager == null)
        {
            bluetoothManager = GeneralBluetoothManager.getManager(context, this);

        }
    }

    /**
     * 扫描蓝牙
     */

    public void startScan()
    {
        devices.clear();
        //支持并启用了开始扫描
        if (!bluetoothManager.isSupportBluetooth())
        {
            LogUtils.d(TAG, context.getString(R.string.bluetooth_unsupported));
        } else if (!bluetoothManager.enableBluetooth())
        {
            LogUtils.d(TAG, context.getString(R.string.bluetooth_enable));
        } else if (!bluetoothManager.isSupportLE())
        {
            LogUtils.d(TAG, context.getString(R.string.bluetooth_not_ble));
        } else
        {
            bluetoothManager.startScan();
        }
    }

    /**
     * 停止扫描
     */
    public void stopScan()
    {
        bluetoothManager.stopScan();
    }

    @Override
    public void scanSuccess(BluetoothDevice device, int rssi)
    {
        //存在重复的设备
        if (devices.add(device))
        {
            r.successView(device, rssi);
        }
    }

    @Override
    public void connectionStateChange(BluetoothDevice device, int newState)
    {
        if (newState == ServiceBluetoothManager.STATE_CONNECTED)
        {
            r.connectedView();
        } else if (newState == ServiceBluetoothManager.STATE_DISCONNECTED)
        {
            stopBluetooth();
            r.disconnectedView(context.getString(R.string.bluetooth_disconnect));
        }
    }

    @Override
    public void serviceDiscoveryed(BluetoothDevice bluetoothDevice, int status)
    {
        r.completeView();
    }

    @Override
    public void valueChanged(String value)
    {
        if (valueMap == null)
        {
            valueMap = new HashMap<>();
        }
        valueMap.put("key", value);
        r.valueView(value);
    }

    /**
     * 销毁
     */
    public void destroyBluetooth()
    {
        bluetoothManager.destroyBluetooth();
    }

    /**
     * 唤醒
     */
    public void restartBluetooth()
    {
        if (mStop && !TextUtils.isEmpty(mAddress))
        {
            r.startView("开始连接");
            //是否连接成功
            if (!bluetoothManager.connectDevice(mAddress))
            {
                bluetoothManager.startScan();
                r.disconnectedView("连接失败");
            }
            mStop = false;
        }
    }

    /**
     * 暂停
     */
    public void pauseBluetooth()
    {
        mStop = true;
        bluetoothManager.pauseBluetooth();
    }

    /**
     * 连接断开
     */
    public void disconnectDevice()
    {
        bluetoothManager.disconnectDevice();
        bluetoothManager.closeDevice();
    }

    /**
     * 发送蓝牙MAC地址进行通信
     *
     * @param address
     */
    public void connectDevice(String address)
    {
        mAddress = address;
        boolean connectDevice = bluetoothManager.connectDevice(address);
        LogUtils.d(getClass().getSimpleName(), "connectDevice:" + connectDevice);
    }

    /**
     * 启用蓝牙
     *
     * @return
     */
    public boolean enableBluetooth()
    {
        return bluetoothManager.enableBluetooth();
    }

    /**
     * 停止一切蓝牙活动
     */
    private void stopBluetooth()
    {
        pauseBluetooth();
        disconnectDevice();
    }
}
