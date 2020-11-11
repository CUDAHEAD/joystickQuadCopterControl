package com.ravensoft.daniel.joysticktest;

import android.annotation.TargetApi;
import android.app.Application;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ravensoft.daniel.Throtletest.ThrotleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

import static android.bluetooth.BluetoothDevice.PHY_LE_1M_MASK;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_SIGNED;

@TargetApi(Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener, ThrotleView.JoystickListener  {

//    private BluetoothAdapter bla = (Context.BLUETOOTH_SERVICE)
    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();

    private android.bluetooth.BluetoothDevice device ;
    private BluetoothGatt bluetoothGatt;


    final String DEVICE_START= "DD:86:C9" ;
    private  boolean mConnected = false;
    private  BluetoothGattCharacteristic ledChar ;
    private BluetoothGattCharacteristic buttonChar ;
    private  boolean sw = true;



    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public final void onConnectionStateChange(@NonNull final BluetoothGatt gatt,
                                                  final int status, final int newState) {

            Log.d("BLE", "onConnectionStateChange");

            if (status == 8) {
                mConnected = false;
            }

            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED|| status == 8) {
                if (mConnected == false) {
                    mConnected = true;
                    Log.d("BLE", "onConnectionStateChange1");
                    bluetoothGatt = gatt;
                    gatt.discoverServices();
                }
            } else if ( newState == BluetoothProfile.STATE_DISCONNECTED)  {
                mConnected = true;

            }
        }
        @Override
        public final void onServicesDiscovered(@NonNull final BluetoothGatt gatt, final int status)
        {
            Log.d("BLE", "onServicesDiscovered");

            //TODO check if it works
            BluetoothGattService service= gatt.getService(UUID.fromString("00001801-0000-1000-8000-00805f9b34fb"));

            if (service != null) {
                Log.d("BLE", "onServicesDiscovered: found !! no search requered" );
                Button button = (Button) findViewById(R.id.button);
                button.setBackgroundColor(Color.GREEN);

            }

            java.util.List<android.bluetooth.BluetoothGattService> servList = gatt.getServices();
            for(final  BluetoothGattService serv:  servList) {
                Log.d("BLE", "onServicesDiscovered:" + serv.getUuid().toString());

                for (final BluetoothGattCharacteristic characteristic: serv.getCharacteristics()) {
                    Log.d("BLE", "onServicesDiscovered chars:  " + characteristic.getUuid());

                    if (characteristic.getUuid().toString().equals( "00001524-1212-efde-1523-785feabcd123")) {
                        Log.d("BLE", "onServicesDiscovered button char set:  " + characteristic.getUuid());

                        buttonChar = characteristic;
                        gatt.setCharacteristicNotification(buttonChar, true);
                        for (final BluetoothGattDescriptor descriptor: buttonChar.getDescriptors()) {
                            Log.d("BLE", "onServicesDiscovered button char desc:  " + descriptor.getUuid());
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }



                    if (characteristic.getUuid().toString().equals(  "00001525-1212-efde-1523-785feabcd123")) {
                        Log.d("BLE", "onServicesDiscovered led char set:  " + characteristic.getUuid());

                        ledChar = characteristic;
//                        gatt.requestMtu(185);
//                        ledChar.setValue(1,0x11,0);
//                        ledChar.setWriteType(WRITE_TYPE_DEFAULT);
//                        gatt.writeCharacteristic(ledChar);
                    }

                }


                }

        }

        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt,
                                         final BluetoothGattCharacteristic characteristic,
                                         final int status)
        {
            Log.d("BLE", "onCharacteristicRead data:" + characteristic.getIntValue(0x11,0));

        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt,
                                          final BluetoothGattCharacteristic characteristic,
                                          final int status)
        {
            Log.d("BLE", "onCharacteristicWrite status:" + status + " charUUID:" + characteristic.getUuid().toString() );
            final byte[] data = characteristic.getValue();

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "Data written to " + characteristic.getUuid() +
                        ", value len: " + data.length + "data :" + (data[0] == 1 ? "1" : "0"));
            }

        }

        @Override
        public final void onReliableWriteCompleted(@NonNull final BluetoothGatt gatt,
                                                   final int status) {

            Log.d("BLE", "onReliableWriteCompleted");

        }

        @Override
        public void onDescriptorRead(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
            final byte[] data = descriptor.getValue();

            Log.d("BLE", "onDescriptorRead");

        }

        @Override
        public void onDescriptorWrite(final BluetoothGatt gatt,
                                      final BluetoothGattDescriptor descriptor,
                                      final int status)
        {
            Log.d("BLE", "onDescriptorWrite");

        }

        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt,
                                            final BluetoothGattCharacteristic characteristic)
        {
            Log.d("BLE", "onCharacteristicChanged value:" + characteristic.getIntValue(0x11,0));

        }

        @Override
        public final void onMtuChanged(@NonNull final BluetoothGatt gatt,
                                       @IntRange(from = 23, to = 517) final int mtu,
                                       final int status) {
            Log.d("BLE", "onMtuChanged");


        }





    };


    @TargetApi(Build.VERSION_CODES.Q)
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, @NonNull final ScanResult result) {
            // This callback will be called only if the scan report delay is not set or is set to 0.

            Log.d("BLE", "scan found: " + result.getDevice().getName() + " Address:" + result.getDevice().getAddress());

            if (result.getDevice().getAddress().startsWith(DEVICE_START)){

                Log.d("BLE", "connecting to a device...");
                //final int preferredPhy = connectRequest.getPreferredPhy();

                device = result.getDevice();
                bluetoothGatt = device.connectGatt(getApplicationContext(), false, gattCallback,
                        BluetoothDevice.TRANSPORT_LE, PHY_LE_1M_MASK/*, handler*/);
            }

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

        Thread.sleep(1000);
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

        Button ledButton = (Button) findViewById(R.id.ledButton);
        ledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Led button" , "switched");
                int val ;
                if (sw == true) {
                    sw = false;
                    val = 0;
                } else {
                    sw = true;
                    val = 1 ;
                }
                ledChar.setValue(val,0x11, 0 );
                ledChar.setWriteType(WRITE_TYPE_DEFAULT);


                bluetoothGatt.writeCharacteristic(ledChar);

            }
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
