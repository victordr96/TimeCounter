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
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<String> BTdispositivosEncontrados;


    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BTdispositivosEncontrados = new ArrayList<String>();
        BTdispositivosEncontrados.add("Prueba");
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    //    registerReceiver(mReceiver, intentFilter);
        ImageView imgBluetooth = findViewById(R.id.imgBluetooth);
        ImageView imgVolver = findViewById(R.id.imgVolver);
        Glide.with(this).load("file:///android_asset/bluetooth.png").into(imgBluetooth);
        Glide.with(this).load("file:///android_asset/volver.png").into(imgVolver);
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
            //IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            //registerReceiver(mReceiver, BTIntent);
        } else {
            mBluetoothAdapter.disable();
            //IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            //registerReceiver(mReceiver, BTIntent);
        }
    }


    /*
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(),
                    "mReceiver", Toast.LENGTH_SHORT).show();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTdispositivosEncontrados.add(device.getName() + " - " + device.getAddress());
            }

            TextView txtView8 = findViewById(R.id.textView8);

            for(int i=0; i<BTdispositivosEncontrados.size(); i++){
                txtView8.setText(txtView8.getText().toString()+BTdispositivosEncontrados.get(i));
            }
        }
    };
    */

    void actualizarTxt(){
        TextView txtView8 = findViewById(R.id.textView8);

        for(int i=0; i<BTdispositivosEncontrados.size(); i++){
            txtView8.setText("");
            txtView8.setText(txtView8.getText().toString()+BTdispositivosEncontrados.get(i));
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

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        BTdispositivosEncontrados.clear();
      //  mBluetoothAdapter.startDiscovery();
        Toast.makeText(getApplicationContext(),
                "StartDiscovery", Toast.LENGTH_SHORT).show();


        Set<BluetoothDevice> dispositivosVinculados = mBluetoothAdapter.getBondedDevices();
        if (dispositivosVinculados.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : dispositivosVinculados) {
                // Add the name and address to an array adapter to show in a ListView
                BTdispositivosEncontrados.add(device.getName() + "   -   " + device.getAddress());
            }
        }
        actualizarTxt();
    }

    public void cancelarBuscarDispositivos(View view) {
       /*
        mBluetoothAdapter.cancelDiscovery();
        Toast.makeText(getApplicationContext(),
               "StopDiscovery", Toast.LENGTH_SHORT).show();

       */
    }



    protected void onDestroy(){
        super.onDestroy();
       // unregisterReceiver(mReceiver);
        mBluetoothAdapter.cancelDiscovery();
    }
}
