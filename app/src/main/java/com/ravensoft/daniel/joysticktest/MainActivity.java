package com.ravensoft.daniel.joysticktest;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ravensoft.daniel.Throtletest.ThrotleView;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener, ThrotleView.JoystickListener  {

//    private BluetoothAdapter bla = (Context.BLUETOOTH_SERVICE)
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();

    @TargetApi(Build.VERSION_CODES.Q)
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, @NonNull final ScanResult result) {
            // This callback will be called only if the scan report delay is not set or is set to 0.

            Log.d("BLE", "scan found: " + result.getDevice().getName());
//            // If the packet has been obtained while Location was disabled, mark Location as not required
//            if (Utils.isLocationRequired(getApplication()) && !Utils.isLocationEnabled(getApplication()))
//                Utils.markLocationNotRequired(getApplication());
//
//            if (devicesLiveData.deviceDiscovered(result)) {
//                devicesLiveData.applyFilter();
//                scannerStateLiveData.recordFound();
//            }
        }

        @Override
        public void onBatchScanResults(@NonNull final List<ScanResult> results) {
            // This callback will be called only if the report delay set above is greater then 0.
            Log.d("BLE", "scan found: we are here" );

//            // If the packet has been obtained while Location was disabled, mark Location as not required
//            if (Utils.isLocationRequired(getApplication()) && !Utils.isLocationEnabled(getApplication()))
//                Utils.markLocationNotRequired(getApplication());
//
//            boolean atLeastOneMatchedFilter = false;
//            for (final ScanResult result : results)
//                atLeastOneMatchedFilter = devicesLiveData.deviceDiscovered(result) || atLeastOneMatchedFilter;
//            if (atLeastOneMatchedFilter) {
//                devicesLiveData.applyFilter();
//                scannerStateLiveData.recordFound();
//            }
        }

        @Override
        public void onScanFailed(final int errorCode) {
            // TODO This should be handled
            Log.d("BLE", "scan -- shit happens" );

//            scannerStateLiveData.scanningStopped();
        }
    };

    /**
     * Start scanning for Bluetooth devices.
     */
    @TargetApi(Build.VERSION_CODES.Q)
    public void startScan() throws InterruptedException {


        // Scanning settings
        final ScanSettings settings = new ScanSettings.Builder()
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .setReportDelay(0L)
                .build();
//                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)//.SCAN_MODE_LOW_LATENCY)
//                .setReportDelay(0L)
//           //     .setUseHardwareBatchingIfSupported(false)
//                .build();

        Log.d("BLE", "scan started");

        List<ScanFilter> filters = new ArrayList<>();
        scanner.startScan(filters, settings, scanCallback);

        Thread.sleep(500);
        scanner.stopScan( scanCallback);
        Log.d("BLE", "scan stopped");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoystickView joystick = new JoystickView(this);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startScan();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }            }
        });
    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        switch (id)
        {
            case R.id.joystickRight:
                Log.d("Right Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);
                break;
            case R.id.joystickLeft:
                Log.d("Left Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);
                break;
        }
    }


}
