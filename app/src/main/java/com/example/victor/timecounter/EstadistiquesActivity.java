package com.example.victor.timecounter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class EstadistiquesActivity extends AppCompatActivity {

    ArrayList<String> temps = new ArrayList<String>();
    ArrayList<Integer> tempsGrafic = new ArrayList<Integer>();
    RecyclerView recycler;
    Adaptador adaptador;
    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> average;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadistiques);


        ImageView imgBluetooth = findViewById(R.id.imgBluetooth);
        ImageView imgVolver = findViewById(R.id.imgVolver);

        Glide.with(this).load("file:///android_asset/bluetooth.png").into(imgBluetooth);
        Glide.with(this).load("file:///android_asset/volver.png").into(imgVolver);

        graphInit();

        Spinner spinner = findViewById(R.id.spinner);
        String spinnerData[] = {getResources().getString(R.string.acceleration),
                getResources().getString(R.string.skidpad),
                getResources().getString(R.string.autocross),
                getResources().getString(R.string.endurance)

        };
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, spinnerData);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        recycler=findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


        llegirSD();
    }

    public void volver(View view) {
        finish();
    }






    private void llegirSD(){
        if(comprobarSD() && comprovarPermisEscriureSD()){
            StringBuilder sb = new StringBuilder();
            try{
                File directorio = new File(Environment.getExternalStorageDirectory()+"/TimeCounterData");
                File[] listaArchivos = directorio.listFiles();
                File textFile = new File(Environment.getExternalStorageDirectory()+"/TimeCounterData", listaArchivos[1].getName());
                FileInputStream fis = new FileInputStream(textFile);

                if(fis != null){
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader buff = new BufferedReader(isr);

                    String line = null;
                    int i=1;
                    while((line = buff.readLine()) != null){
                        line=line.replace("&", ""); //eliminar final de linia
                        temps.add((i+" -   "+line));
                        int Ss,Sd, Sm;

                        Ss = Integer.parseInt(line.substring(0, line.indexOf(':')));
                        line=line.substring(line.indexOf(':')+1, line.length());
                        Sd = Integer.parseInt(line.substring(0, line.indexOf(':')));
                        line=line.substring(line.indexOf(':')+1, line.length());
                        Sm = Integer.parseInt(line);
                        tempsGrafic.add(Ss+Sd/60+Sm/600);

                        i++;
                    }
                    fis.close();
                    adaptador = new Adaptador(temps);
                    recycler.setAdapter(adaptador);
                    actualitzarGrafic();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Cannot Read from External Storage.", Toast.LENGTH_SHORT).show();
        }
    }


    private void actualitzarGrafic(){
        int graphPos=1;
        double tempsTtotal=0;


        DataPoint[] serieTemps = new DataPoint[tempsGrafic.size()];

        for(int i=0; i<tempsGrafic.size(); i++){
            serieTemps[i] = new DataPoint(i+1, tempsGrafic.get(i));
            tempsTtotal=tempsTtotal+tempsGrafic.get(i);
        }

        series.resetData(serieTemps);


        DataPoint[] serieMedia = new DataPoint[2];
        serieMedia[0] =  new DataPoint(1, tempsTtotal/tempsGrafic.size());
        serieMedia[1] =  new DataPoint(tempsGrafic.size()-1, tempsTtotal/tempsGrafic.size());
        average.resetData(serieMedia);
    }


    void graphInit(){
        GraphView graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>(new DataPoint[] {
        });
        series.setTitle(getResources().getString(R.string.time));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        graph.addSeries(series);

        average= new LineGraphSeries<>(new DataPoint[] {});
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


    private boolean comprobarSD(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            return true;
        }else{
            return false;
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
}
