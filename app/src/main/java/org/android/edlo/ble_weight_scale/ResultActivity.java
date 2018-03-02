package org.android.edlo.ble_weight_scale;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.BluetoothService;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_FAILED;
import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

public class ResultActivity extends AppCompatActivity {
    private String deviceMAC;
    private BluetoothClient mClient;
    private List<BleGattService> mService;
    private final UUID serviceUUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e4");
    private final UUID notifyCharacterUUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e1");
    private final UUID writeCharacterUUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e0");
    private final UUID readCharacterUUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e1");
    private boolean isConnected;
    private List<SearchResult> device_List;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
       //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
        setContentView(R.layout.activity_result);
        init();
    }

    private void init(){
        isConnected = false;
        device_List = new ArrayList();
        mClient = new BluetoothClient(getApplicationContext());

        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3).build();

        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                Log.d("ed43","onSearchStarted");
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                Beacon beacon = new Beacon(device.scanRecord);
                BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
                device_List.add(device);
//                Log.d("Blue_Tooth_Test",String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
                device.getAddress();
//                Log.d("Blue_Tooth_Test","device.getName() + " + device.getName());
//                Log.d("Blue_Tooth_Test","device.getAddress() + " + device.getAddress());
                deviceMAC = device.getAddress();
                connectDevice(mClient);
                testNoptify();

            }

            @Override
            public void onSearchStopped() {
                Log.d("ed43","onSearchStopped");
            }

            @Override
            public void onSearchCanceled() {
                Log.d("ed43","onSearchCanceled");
            }
        });

        mClient.registerBluetoothStateListener(mBluetoothStateListener);
//        mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
        mClient.registerBluetoothBondListener(mBluetoothBondListener);
//        mClient.unregisterBluetoothBondListener(mBluetoothBondListener);
        /*
        String[] device_Name = new String[device_List.size()];
        String[] device_Address = new String[device_List.size()];
        for (int i = 0; i < device_List.size(); i++){
            device_Name[i] = device_List.get(i).getName();
            device_Address[i] = device_List.get(i).getAddress();
        }

        AlertDialog.Builder dialog_list = new AlertDialog.Builder(this);
        dialog_list.setTitle("Select Device");
        dialog_list.setItems(device_Name, new DialogInterface.OnClickListener(){
            @Override

            //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //Toast.makeText(getParent(), "你選的是" + device_Name[which], Toast.LENGTH_SHORT).show();
            }
        });
        dialog_list.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog_list.show();*/
    }

    private boolean connectDevice(BluetoothClient mClient){
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)   // 连接如果失败重试3次
                .setConnectTimeout(5)   // 连接超时30s
                .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
                .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
                .build();

        mClient.connect(deviceMAC, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                Log.d("ed43","onResponse");
                if(code == REQUEST_SUCCESS){
                    mService = data.getServices();
                    isConnected = true;
                    testRead();
                    Log.d("ed43", new Gson().toJson(data));
                }else if (code == REQUEST_FAILED){
                    Log.d("ed43","REQUEST_FAILED");
                }
            }
        });
        return true;
    }

    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            Log.d("ed43","onBluetoothStateChanged");
        }

    };

    private final BluetoothBondListener mBluetoothBondListener = new BluetoothBondListener() {
        @Override
        public void onBondStateChanged(String mac, int bondState) {
            // bondState = Constants.BOND_NONE, BOND_BONDING, BOND_BONDED
            Log.d("ed43","onBondStateChanged");
        }
    };

    public void connect(View view){
        connectDevice(mClient);
    }

    public void write(View view){
        testWrite();
    }

    public void read(View view){
        testRead();
    }

    private void testRead(){
        Log.d("ed43", "testRead");
        mClient.read(deviceMAC, serviceUUID, readCharacterUUID, new BleReadResponse() {
            @Override
            public void onResponse(int code, byte[] data) {
                Log.d("ed43", "read response code : " + code);
                if (code == REQUEST_SUCCESS) {
                    String mData = toHexString(data);
                    Log.d("ed43", mData);
                }
            }
        });
    }

    private void testNoptify(){
        mClient.notify(deviceMAC, serviceUUID, notifyCharacterUUID, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                String mData = toHexString(value);
                Log.d("ed43", mData);
                Log.d("ed43", "---------------------------");
            }
            @Override
            public void onResponse(int code) {
                Log.d("ed43", "notify response code : " + code);
                if (code == REQUEST_SUCCESS) {
                    Log.d("ed43", "REQUEST_SUCCESS");
                }
            }
        });
    }

    private void testWrite(){
        //String data = "5a d5 05 12 01 01 9c 00 c0 00 00 00 8e aa";
        byte[] bytes = new byte[]{90, -43, 5, 18, 1, -100, 0, -64, 0, 0, 0, -114, -86};
        mClient.write(deviceMAC, serviceUUID, writeCharacterUUID, bytes, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                Log.d("ed43", "write response code : " + code);
                if (code == REQUEST_SUCCESS) {
                    Log.d("ed43", "OK");
                    if(isConnected){
                        testRead();
                    }
                }
            }
        });
    }

    public void save(View view){
        Intent it = new Intent(this, LastWeightActivity.class);
        startActivity(it);
    }

    public void discard(View view){
        Intent it = new Intent(this, LastWeightActivity.class);
        startActivity(it);
    }

    public String toHexString (byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
