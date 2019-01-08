package com.example.victor.timecounter;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {
    private static final String appName = "APP";
    private static final String TAG = "BluetoothConectionService";

    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;
    private ConnectedThread mConnectedThread;

    public BluetoothConnectionService(Context context){
        mContext=context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }


    private class AcceptThread extends  Thread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            try{
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
            } catch(IOException c){
            }

            mmServerSocket = tmp;
        }

        public void run(){
            BluetoothSocket socket = null;

            try{
                socket = mmServerSocket.accept();
            }catch (IOException c){
            }

            if(socket!=null){
                connected(socket,mmDevice);
            }
        }

        public void cancel(){
            try{
                mmServerSocket.close();
            } catch (IOException e){
            }
        }
    }

    private class ConnectThread extends Thread{
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid){
            mmDevice=device;
            deviceUUID=uuid;
        }

        public void run(){
            BluetoothSocket tmp=null;
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
            }

            mmSocket=tmp;
            mBluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException e) {
                try{
                    mmSocket.close();
                } catch (IOException c){
                }
            }

            connected(mmSocket,mmDevice);
        }


        public void cancel(){
            try{
                mmSocket.close();
            } catch (IOException e){
            }
        }

    }

    public synchronized void start(){
        if(mConnectThread!=null){
            mConnectThread.cancel();
            mConnectThread=null;
        }

        if(mInsecureAcceptThread==null){
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }


    public void startClient(BluetoothDevice device, UUID uuid){
        mProgressDialog=ProgressDialog.show(mContext, "Connectant al bluetooth", "Espera", true);
        mConnectThread=new ConnectThread(device,uuid);
        mConnectThread.start();
    }


    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            mmSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            mProgressDialog.dismiss();

            try {
                tempIn=mmSocket.getInputStream();
                tempOut=mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream=tempIn;
            mmOutStream=tempOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try {
                    bytes=mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    break; //sortir while
                }
            }
        }

        public void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel(){
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }


    public void write(byte[] out){
        ConnectedThread r;
       mConnectedThread.write(out);
    }


}
