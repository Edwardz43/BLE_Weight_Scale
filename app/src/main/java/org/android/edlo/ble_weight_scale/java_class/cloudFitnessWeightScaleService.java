package org.android.edlo.ble_weight_scale.java_class;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.model.BleGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by EdLo on 2018/3/2.
 */

public class cloudFitnessWeightScaleService extends Service{
    private String deviceMAC;
    private BluetoothClient mClient;
    private List<BleGattService> mService;
    private BluetoothStateListener mBluetoothStateListener;
    private BleConnectStatusListener mBleConnectStatusListener;
    private final UUID serviceUUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e4");
    private final UUID notifyUUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e1");
    private final UUID writeUUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e0");
    private final UUID readUUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e1");
    private static boolean isConnected, isBlueToothOpen;

    @Override
    public void onCreate() {
        super.onCreate();
        isBlueToothOpen = isConnected = false;
        initBLE();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!isBlueToothOpen){
            initBLE();
        }

        if(intent != null){
            int cmd = intent.getIntExtra("CMD", -1);
            String id = intent.getStringExtra("id");
            int value = intent.getIntExtra("value", 0);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void initBLE(){
        Log.d("ed43", "initBLE()");
        isBlueToothOpen = isConnected = false;
        mClient = new BluetoothClient(this);
        mClient.openBluetooth();

        // 偵測 BLE 狀態Listener
        mBluetoothStateListener = new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean openOrClosed) {
                isBlueToothOpen = openOrClosed;
//                Intent localIntent = new Intent("BugooService1");
//                localIntent.putExtra("isBluetoothOpen", BugooTPMSService.isBluetoothOpen);
//                BugooTPMSService.this.sendBroadcast(localIntent);

                Log.i("ed43", "BLE:status:" + openOrClosed);

            }
        };
        mClient.registerBluetoothStateListener(this.mBluetoothStateListener);

        // 設定 BLE 狀態偵測
        mBleConnectStatusListener = new BleConnectStatusListener() {
            @Override
            public void onConnectStatusChanged(String mac, int status) {
                Log.i("ed43", "DEBUG");
                if (status == Constants.STATUS_CONNECTED) {
                    isBlueToothOpen = true;
                    cloudFitnessWeightScaleService.this.deviceMAC = mac;

                    // 發出廣播
                    Intent it = new Intent("cloudFitnessWeightScaleService");
                    it.putExtra("isBluetoothOpen", isBlueToothOpen);
                    sendBroadcast(it);
                    Log.i("ed43", "connecting");

                } else if (status == Constants.STATUS_DISCONNECTED) {
                    cloudFitnessWeightScaleService.isBlueToothOpen = false;
                    Log.i("ed43", "disconnect");


//                    // 發出廣播
//                    Intent it = new Intent("BugooService1");
//                    it.putExtra("isBluetoothOpen", isBlueToothOpen);
//                    sendBroadcast(it);

                }
            }
        };

        mClient.registerConnectStatusListener(deviceMAC, mBleConnectStatusListener);

        if (!mClient.isBluetoothOpened()) {
            Log.i("ed43", "DEBUG:X1");
            mClient.openBluetooth();
            return;
        }

        isBlueToothOpen = true;
        Intent localIntent = new Intent("cloudFitnessWeightScaleService");
        localIntent.putExtra("isBluetoothOpen", isBlueToothOpen);
        sendBroadcast(localIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
