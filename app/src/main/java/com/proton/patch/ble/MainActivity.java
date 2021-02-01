package com.proton.patch.ble;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.proton.ecg.algorithm.callback.EcgPatchAlgorithmListener;
import com.proton.ecg.algorithm.interfaces.IEcgAlgorithm;
import com.proton.ecg.algorithm.interfaces.impl.EcgPatchAlgorithm;
import com.proton.ecgpatch.connector.EcgPatchManager;
import com.proton.ecgpatch.connector.callback.DataListener;
import com.proton.view.EcgRealTimeView;
import com.wms.ble.bean.ScanResult;
import com.wms.ble.callback.OnConnectListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EcgRealTimeView ecgRealTimeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ecgRealTimeView = findViewById(R.id.id_ecg_view);
        //需要获取定位权限，否则搜索不到设备
        PermissionUtils.getLocationPermission(this);

        ecgRealTimeView.setOnClickListener(v -> {
            //以心电贴为例，心电卡流程类似
            testPatchConnect();
        });
    }

    private void testPatchConnect() {
        IEcgAlgorithm ecgAlgorithm = new EcgPatchAlgorithm(new EcgPatchAlgorithmListener() {
            @Override
            public void receiveEcgFilterData(byte[] ecgData) {
                ecgRealTimeView.addEcgData(ecgData);
                if (!ecgRealTimeView.isRunning()) {
                    ecgRealTimeView.startDrawWave();
                }
            }

            @Override
            public void receiverHeartRate(int rate) {
                Log.e(TAG, "心率: " + rate);
            }

            @Override
            public void signalInterference(int signalQualityIndex) {
                Log.e(TAG, "signalInterference: " + signalQualityIndex);
            }
        });

        EcgPatchManager.init(this);
        EcgPatchManager ecgPatchManager = EcgPatchManager.getInstance("C4:A1:1B:19:BB:C7");
        ecgPatchManager.setDataListener(new DataListener() {
            @Override
            public void receiveEcgRawData(byte[] bytes) {
                Log.e(TAG, "蓝牙数据:" + bytes.length);
                ecgAlgorithm.processEcgData(bytes);
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