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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
    ArrayList<Double> tempsGrafic = new ArrayList<Double>();
    RecyclerView recycler;
    Adaptador adaptador;
    String prova="";
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> average;
    int min=0;
    int max=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadistiques);


        ImageView imgBluetooth = findViewById(R.id.imgBluetooth);
        ImageView imgVolver = findViewById(R.id.imgVolver);

        Glide.with(this).load("file:///android_asset/bluetooth.png").into(imgBluetooth);
        Glide.with(this).load("file:///android_asset/volver.png").into(imgVolver);

        recycler=findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) prova="ac";
                else if (position == 1) prova="sk";
                else if (position == 2) prova="au";
                else if (position == 3) prova="en";

                llegirSD();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void volver(View view) {
        finish();
    }


    private void llegirSD(){
        if(comprobarSD() && comprovarPermisEscriureSD()){
            StringBuilder sb = new StringBuilder();
            try{
                Boolean algunArxiu=false;
                Boolean primerValor=true;

                File directorio = new File(Environment.getExternalStorageDirectory()+"/TimeCounterData");
                File[] listaArchivos = directorio.listFiles();

                temps.clear();
                tempsGrafic.clear();

                String borrar="";
                int pos=1;

                for(int i=0; i<listaArchivos.length; i++){

                    if(listaArchivos[i].getName().contains(prova)){ //buscar tots els arxius de la serie
                        algunArxiu=true;

                        File textFile = new File(Environment.getExternalStorageDirectory()+"/TimeCounterData", listaArchivos[i].getName());
                        FileInputStream fis = new FileInputStream(textFile);

                        if(fis != null){
                            InputStreamReader isr = new InputStreamReader(fis);
                            BufferedReader buff = new BufferedReader(isr);

                            String line = null;
                            while((line = buff.readLine()) != null && line.contains(":")){
                                line=line.replace("&", ""); //eliminar final de linia
                                temps.add((pos+" -   "+line));
                                int Ss,Sd, Sm;

                                Ss = Integer.parseInt(line.substring(0, line.indexOf(':')));
                                line=line.substring(line.indexOf(':')+1, line.length());
                                Sd = Integer.parseInt(line.substring(0, line.indexOf(':')));
                                line=line.substring(line.indexOf(':')+1, line.length());
                                Sm = Integer.parseInt(line);
                                double t=Ss/1.0+Sd/60.0+Sm/60000.0;
                                Log.d("De", "T="+t);
                                tempsGrafic.add(t);


                                if(primerValor){
                                    min=(int)t;
                                    max=(int)t;
                                    primerValor=false;
                                }
                                if(t<min) min=(int)t;
                                if(t>max) max=(int)t;

                                pos++;
                            }
                            fis.close();
                        }

                    }
                }

                if(algunArxiu){
                    adaptador = new Adaptador(this, temps);
                    recycler.setAdapter(adaptador);
                    actualitzarGrafic();
                } else {
                    Toast.makeText(this, R.string.noArxius, Toast.LENGTH_SHORT).show();
                }

                Log.d("De", Integer.toString(max));

            }catch (IOException e){
                e.printStackTrace();
            }

        }else{
            Toast.makeText(this, R.string.noLlegir, Toast.LENGTH_SHORT).show();
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
        if(tempsGrafic.size()>1) serieMedia[1] =  new DataPoint(tempsGrafic.size()-1, tempsTtotal/tempsGrafic.size());
        else serieMedia[1] =  new DataPoint(tempsGrafic.size(), tempsTtotal/tempsGrafic.size());
        average.resetData(serieMedia);


        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(tempsGrafic.size());
    }


    void graphInit(){
        graph = (GraphView) findViewById(R.id.graph);
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
