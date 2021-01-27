package com.proton.patch.ble;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.proton.ecgpatch.connector.EcgPatchManager;
import com.proton.ecgpatch.connector.callback.DataListener;
import com.wms.ble.bean.ScanResult;
import com.wms.ble.callback.OnConnectListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //需要获取定位权限，否则搜索不到设备
        PermissionUtils.getLocationPermission(this);

        findViewById(android.R.id.content).setOnClickListener(v -> {
            testPatchConnect();
        });
    }

    private void testPatchConnect() {
        EcgPatchManager.init(this);
        EcgPatchManager ecgPatchManager = EcgPatchManager.getInstance("C4:A1:1B:19:BB:C7");
        ecgPatchManager.setDataListener(new DataListener() {
            @Override
            public void receiveEcgRawData(byte[] bytes) {
                Log.e(TAG, "蓝牙数据:" + bytes.length);
            }

            @Override
            public void receivePackageNum(int packageNum) {
                Log.e(TAG, "包序: " + packageNum);
            }

            @Override
            public void receiveFallDown(boolean isFallDown) {
                Log.e(TAG, "是否跌倒: " + isFallDown);
            }
        });
        ecgPatchManager.connectEcgPatch(new OnConnectListener() {
            @Override
            public void onConnectSuccess() {
                Log.e(TAG, "连接成功");
            }

            @Override
            public void onConnectFaild() {
                Log.e(TAG, "连接失败");
            }

            @Override
            public void onDisconnect(boolean b) {
                Log.e(TAG, "断开连接，是否手动断开:" + b);
            }
        });
    }
}