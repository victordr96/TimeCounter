package com.example.victor.timecounter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MonitorActivity extends AppCompatActivity {

 //   private BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket btSocket = null;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;
    BluetoothDevice dispositivo=null;
    TextView txtEnviar = findViewById(R.id.txtEnviar);
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);




        ImageView imgBluetooth = findViewById(R.id.imgBluetooth);
        ImageView imgVolver = findViewById(R.id.imgVolver);
        Glide.with(this).load("file:///android_asset/bluetooth.png").into(imgBluetooth);
        Glide.with(this).load("file:///android_asset/volver.png").into(imgVolver);





    }

    public void volver(View view) {
        finish();
    }

    public void envarBT(View view) {
      //  try {
         //   tmpOut.write(txtEnviar.getText().toString().getBytes());
      //  } catch (IOException e) { }
    }
}
