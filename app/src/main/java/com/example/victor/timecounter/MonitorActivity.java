package com.example.victor.timecounter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MonitorActivity extends AppCompatActivity {


    EditText txtEnviar;
    TextView txtMonitor;
    private BluetoothAdapter mBluetoothAdapter;
    ArrayList<SpannableString> dadesBT = new ArrayList<SpannableString>();
    ForegroundColorSpan fcsVermell = new ForegroundColorSpan(Color.RED);
    ForegroundColorSpan fcsBlau= new ForegroundColorSpan(Color.BLUE);
    BluetoothSocket btSocket = null;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;
    BluetoothDevice dispositivo=null;
    Handler bluetoothIn;
    private ArrayList<String> BTdispositivosEncontrados;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    int i=0;
    boolean stopThread;
    byte buffer[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        txtEnviar=findViewById(R.id.txtEnviar);
        txtMonitor=findViewById(R.id.txtMonitor);
        txtMonitor.setText("");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BTdispositivosEncontrados = new ArrayList<String>();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);


        ImageView imgBluetooth = findViewById(R.id.imgBluetooth);
        ImageView imgVolver = findViewById(R.id.imgVolver);
        Glide.with(this).load("file:///android_asset/bluetooth.png").into(imgBluetooth);
        Glide.with(this).load("file:///android_asset/volver.png").into(imgVolver);



        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        String addressA ="";
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                addressA = deviceHardwareAddress;
            }
            dispositivo = mBluetoothAdapter.getRemoteDevice(addressA);
        }
        BTconnect();
    }


    private void mostrarDades(){

        txtMonitor.setText(dadesBT.get(0));
        for(int i=1; i<dadesBT.size(); i++){
            txtMonitor.append("\n");
            txtMonitor.append(dadesBT.get(i));
        }
    }


    public void BTconnect() {
        try {
            btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
            btSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            tmpIn = btSocket.getInputStream();
            tmpOut = btSocket.getOutputStream();
            Toast.makeText(getApplicationContext(),
                    "SocketsOK", Toast.LENGTH_SHORT).show();
        } catch (IOException e) { }

    }



    public void volver(View view) {
        finish();
    }


    public void enviar(View view) {
        if(tmpOut!=null){
            try {
                tmpOut.write(txtEnviar.getText().toString().getBytes());

                SpannableString ss = new SpannableString(txtEnviar.getText().toString());
                ss.setSpan(fcsBlau,0,txtEnviar.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dadesBT.add(ss);
                mostrarDades();

            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),
                        R.string.noSend, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
