package com.example.victor.timecounter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);




        ImageView imgBluetooth = findViewById(R.id.imgBluetooth);
        ImageView imgAcceleration = findViewById(R.id.imgAcceleration);
        ImageView imgSkidpad = findViewById(R.id.imgSkidpad);
        ImageView imgAutocross = findViewById(R.id.imgAutocross);
        ImageView imgEndurance = findViewById(R.id.imgEndurance);
        ImageView imgAjustes = findViewById(R.id.imgAjustes);
        ImageView imgEstadisticas = findViewById(R.id.imgEstadisticas);

        Glide.with(this).load("file:///android_asset/bluetooth.png").into(imgBluetooth);
        Glide.with(this).load("file:///android_asset/acceleration.png").into(imgAcceleration);
        Glide.with(this).load("file:///android_asset/skidpad.png").into(imgSkidpad);
        Glide.with(this).load("file:///android_asset/autocross.png").into(imgAutocross);
        Glide.with(this).load("file:///android_asset/endurance.png").into(imgEndurance);
        Glide.with(this).load("file:///android_asset/ajustes.png").into(imgAjustes);
        Glide.with(this).load("file:///android_asset/estadisticas.png").into(imgEstadisticas);


    }

    public void temps(View view) {
        Intent intent = new Intent(this, TempsActivity.class);
        startActivity(intent);
    }

    public void bluetooth(View view) {
        Intent intent = new Intent(this, BluetoothActivity.class);
        startActivity(intent);
    }
}
