package com.example.victor.timecounter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket btSocket = null;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;
    BluetoothDevice dispositivo=null;
    private ArrayList<String> BTdispositivosEncontrados;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    int i=0;

    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BTdispositivosEncontrados = new ArrayList<String>();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        ImageView imgBluetooth = findViewById(R.id.imgBluetooth);
        ImageView imgVolver = findViewById(R.id.imgVolver);
        ImageView imgMonitor = findViewById(R.id.imgMonitor);
        Glide.with(this).load("file:///android_asset/bluetooth.png").into(imgBluetooth);
        Glide.with(this).load("file:///android_asset/volver.png").into(imgVolver);
        Glide.with(this).load("file:///android_asset/screen.png").into(imgMonitor);





        TextView txtView8 = findViewById(R.id.textView8);
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        String addressA ="";
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                addressA = deviceHardwareAddress;
                txtView8.setText(txtView8.getText().toString() + "\n" + deviceName + " -- " + deviceHardwareAddress);
            }
            dispositivo = mBluetoothAdapter.getRemoteDevice(addressA);

        }

    }



    public void volver(View view) {

        finish();
    }

    public void activarBluetooth(){

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),
                    R.string.noBluetooth, Toast.LENGTH_SHORT).show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mBluetoothAdapter.disable();
        }
    }




    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(),
                        R.string.bluetoothActivado, Toast.LENGTH_SHORT).show();
            }  else if (resultCode == RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),
                        R.string.bluetoothNoActivado, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void activarBT(View view) {
        activarBluetooth();
    }

    public void buscarDispositivos(View view) {


    }

    public void cancelarBuscarDispositivos(View view) {
        try {
            tmpOut.write(i);
            i++;
        } catch (IOException e) { }
    }



    protected void onDestroy(){
        super.onDestroy();
       // unregisterReceiver(mReceiver);
        mBluetoothAdapter.cancelDiscovery();

        if(btSocket!=null){
            try {
                btSocket.close();
            } catch (Exception e) {}

            btSocket = null;

        }
    }

    public void BTconnect(View view) {
        try {
            btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
            btSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            tmpIn = btSocket.getInputStream();
            tmpOut = btSocket.getOutputStream();
        } catch (IOException e) { }

    }

    public void monitor(View view) {
        Intent intent = new Intent(this, MonitorActivity.class);
        startActivity(intent);
    }
}
