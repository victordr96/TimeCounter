package com.example.victor.timecounter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MenuActivity extends AppCompatActivity {

    private Boolean BTON=false;
    private TextView txtEstadoBT;

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
        txtEstadoBT=findViewById(R.id.txtEstadoBT);

        Glide.with(this).load("file:///android_asset/bluetooth.png").into(imgBluetooth);
        Glide.with(this).load("file:///android_asset/acceleration.png").into(imgAcceleration);
        Glide.with(this).load("file:///android_asset/skidpad.png").into(imgSkidpad);
        Glide.with(this).load("file:///android_asset/autocross.png").into(imgAutocross);
        Glide.with(this).load("file:///android_asset/endurance.png").into(imgEndurance);
        Glide.with(this).load("file:///android_asset/ajustes.png").into(imgAjustes);
        Glide.with(this).load("file:///android_asset/estadisticas.png").into(imgEstadisticas);

        if(BTON) txtEstadoBT.setText(R.string.estadoBT_conectado);

    }

    public void acceleration(View view) {
        Intent intent = new Intent(this, TempsActivity.class);
        intent.putExtra("Prova","ac");
        intent.putExtra("EstatBT", BTON);
        startActivity(intent);
    }


    public void skidpad(View view) {
        Intent intent = new Intent(this, TempsActivity.class);
        intent.putExtra("Prova","sk");
        intent.putExtra("EstatBT", BTON);
        startActivity(intent);
    }


    public void autocross(View view) {
        Intent intent = new Intent(this, TempsActivity.class);
        intent.putExtra("Prova","au");
        intent.putExtra("EstatBT", BTON);
        startActivity(intent);
    }


    public void endurance(View view) {
        Intent intent = new Intent(this, TempsActivity.class);
        intent.putExtra("Prova","en");
        intent.putExtra("EstatBT", BTON);
        startActivity(intent);
    }



    public void bluetooth(View view) {
        Intent intent = new Intent(this, BluetoothActivity.class);
        startActivityForResult(intent, 0);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                BTON=true;
                txtEstadoBT.setText(R.string.estadoBT_conectado);
            }
            else {
                BTON=false;
            }
        }
    }


    public void estadistiques(View view) {
        Intent intent = new Intent(this, EstadistiquesActivity.class);
        startActivity(intent);
    }
}
