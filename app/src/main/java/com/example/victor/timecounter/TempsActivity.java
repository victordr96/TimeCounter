package com.example.victor.timecounter;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TempsActivity extends AppCompatActivity {

    Boolean TEST=true; //si true, genera dades aleatories sense bluetooth per provar
    ArrayList<String> temps = new ArrayList<String>();
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> average;
    private CountDownTimer mCountDownTimer;
    TextView txtLast1;
    TextView txtLast2;
    TextView txtLast3;
    TextView txtLast4;
    TextView txtBest;
    double sumaTiempos=0;
    int graphPos=4;
    int bestS=99;
    int bestD=99;
    int bestM=999;
    String best;
    String prova="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temps);

        Intent intent = getIntent();
        if(intent!=null){
            prova = intent.getStringExtra("Prova");
        }

        txtLast1 = findViewById(R.id.txtLast1);
        txtLast2 = findViewById(R.id.txtLast2);
        txtLast3 = findViewById(R.id.txtLast3);
        txtLast4 = findViewById(R.id.txtLast4);
        txtBest = findViewById(R.id.txtBest);

        ImageView imgBluetooth = findViewById(R.id.imgBluetooth);
        ImageView imgVolver = findViewById(R.id.imgVolver);
        ImageView imgStart = findViewById(R.id.imgStart);
        ImageView imgPause = findViewById(R.id.imgPause);
        ImageView imgRestart = findViewById(R.id.imgRestart);

        Glide.with(this).load("file:///android_asset/bluetooth.png").into(imgBluetooth);
        Glide.with(this).load("file:///android_asset/volver.png").into(imgVolver);
        Glide.with(this).load("file:///android_asset/start.png").into(imgStart);
        Glide.with(this).load("file:///android_asset/pause.png").into(imgPause);
        Glide.with(this).load("file:///android_asset/replay.png").into(imgRestart);

        graphInit();
        txtLast1.setText("--:--:---");
        txtLast2.setText("--:--:---");
        txtLast3.setText("--:--:---");
        txtLast4.setText("--:--:---");
        txtBest.setText("--:--:---");

        //anadirTiempo(1, 50, 123);
        //anadirTiempo(2, 51, 124);
        //anadirTiempo(3, 52, 125);
        //anadirTiempo(4, 53, 126);
        //anadirTiempo(1, 49, 127);
        //anadirTiempo(1, 49, 124);
        //anadirTiempo(7, 56, 129);


        if(TEST) iniciarTimer();

    }

    public void volver(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialogoGuardar).setTitle(R.string.exit);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                escriureArxiu();
                finish();

            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.show();
    }


    private void anadirTiempo(int s, int d, int m){

        String Ss, Sd, Sm;
        Ss=Integer.toString(s);
        Sd=Integer.toString(d);
        Sm=Integer.toString(m);
        if(s<10) Ss="0"+s;
        if(d<10) Sd="0"+d;
        if(m>=10 && m<100) Sm="0"+m;
        else if(m<10) Sm="00"+m;
        temps.add(Ss+":"+Sd+":"+Sm);

        Boolean a=false;

        if(s<bestS) a=true;
        else if (s==bestS && d<bestD) a=true;
        else if (s==bestS && d==bestD && m<bestM) a=true;

        if(a){
            bestS=s;
            bestM=m;
            bestD=d;
            best = Ss+":"+Sd+":"+Sm;
        }

        txtLast1.setText(temps.get(temps.size()-1));
        if(temps.size()>1) txtLast2.setText(temps.get(temps.size()-2));
        if(temps.size()>2)  txtLast3.setText(temps.get(temps.size()-3));
        if(temps.size()>3) txtLast4.setText(temps.get(temps.size()-4));

        txtBest.setText(best);

        double tempsAux=s+d/60+m/600;
        sumaTiempos=sumaTiempos+tempsAux;
        actualizarGraficos(tempsAux);




    }

    private void actualizarGraficos(double tempsAux) {
        graphPos++;
        series.appendData(new DataPoint(graphPos, tempsAux), true, 40);

        DataPoint[] valoresMedia = new DataPoint[2];
        valoresMedia[0] =  new DataPoint(0, sumaTiempos/graphPos);
        valoresMedia[1] =  new DataPoint(graphPos, sumaTiempos/graphPos);
        average.resetData(valoresMedia);


        if(graphPos>20) graph.getViewport().setMinX(graphPos-20);
        else graph.getViewport().setMinX(1);

        graph.getViewport().setMaxX(graphPos);
    }


    void graphInit(){
        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>(new DataPoint[] {
        });
        series.setTitle(getResources().getString(R.string.time));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        graph.addSeries(series);

        average= new LineGraphSeries<>(new DataPoint[] {

        });
        average.setTitle(getResources().getString(R.string.average));
        average.setColor(Color.rgb(0,102,0));
        graph.addSeries(average);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setBackgroundColor(Color.WHITE);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
    }



    private void iniciarTimer() {
        mCountDownTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Random r = new Random();
                anadirTiempo(r.nextInt(11),r.nextInt(60),r.nextInt(1000));
                iniciarTimer();
            }
        }.start();
    }







    private boolean comprobarSD(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            return true;
        }else{
            return false;
        }
    }

    public void escriureArxiu(){

        boolean guardat=false;
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String data = df.format(c.getTime());


        String texto = crearTextArxiu();

        if(comprobarSD() && comprovarPermisEscriureSD()){

            File carpeta = new File(Environment.getExternalStorageDirectory(), "TimeCounterData");
            if(!carpeta.exists()) {
                carpeta.mkdirs();
                carpeta = new File(Environment.getExternalStorageDirectory(), "TimeCounterData");
            }

            if(carpeta.exists()){

                File textFile = new File(Environment.getExternalStorageDirectory()+"/TimeCounterData", data + prova + ".txt");
                try{
                    FileOutputStream fos = new FileOutputStream(textFile);
                    fos.write(texto.getBytes());
                    fos.close();

                    Toast.makeText(this, R.string.ArxiuGuardat, Toast.LENGTH_SHORT).show();
                    guardat=true;
                }catch (IOException e){
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(this, R.string.noCrearCarpeta, Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, R.string.NoEscriureSD, Toast.LENGTH_SHORT).show();
        }

        if(!guardat){
            errorGuardar();
        }
    }

    public  boolean comprovarPermisEscriureSD() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else { //Si marshmallow o superior ha d'acceptar permis
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //sdk<23 té permís pel manifest
            return true;
        }
    }


    private String crearTextArxiu(){
        String s="";
        for(int i=0; i<temps.size(); i++){
            s=s+temps.get(i)+"&\n";
        }
        return s;

    }



    public void onBackPressed() {
        //super.onBackPressed();
        volver(null);
    }


    public void errorGuardar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.errorGuardar).setTitle(R.string.exit);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                escriureArxiu();

            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }




}
