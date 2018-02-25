package com.example.krishna.logger;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView tvAccelerometer;
    //the Sensor Manager
    private EditText etTimer;
    private EditText etFilename;
    private SensorManager sManager;
    private Button btAccelerometerStart;
    private Button btAccelerometerStop;
    private Timer timer = new Timer();
    private int counter = 0,timeCounter = 0,minutes;
    private String fileName;
    private double x=0,y=0,z=0;
    private boolean flag = false;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private static final int REQUEST_READ_PERMISSION = 786;

    private boolean dumpData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        tvAccelerometer = findViewById(R.id.tvAccelerometer);
        btAccelerometerStart = findViewById(R.id.btAccelerometerStart);
        btAccelerometerStop = findViewById(R.id.btAccelerometerStop);
        etTimer = findViewById(R.id.etTimer);
        etFilename = findViewById(R.id.etFilename);


        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        btAccelerometerStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dumpData = true;
                flag = true;
                minutes = Integer.parseInt(etTimer.getText().toString());
                fileName = etFilename.getText().toString();
                Toast.makeText(getBaseContext(), "Started dumping data", Toast.LENGTH_SHORT).show();

            }
        });
        btAccelerometerStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dumpData = false;
                Toast.makeText(getBaseContext(), "Finished dumping data", Toast.LENGTH_SHORT).show();

            }
        });

    }
    @Override
    protected void onResume()
    {
        super.onResume();
        /*register the sensor listener to listen to the gyroscope sensor, use the
        callbacks defined in this class, and gather the sensor information as quick
        as possible*/
        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
    }

    //When this Activity isn't visible anymore
    @Override
    protected void onStop()
    {
        //unregister the sensor listener
        sManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
        //Do nothing.
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        //if sensor is unreliable, return void
//        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
//        {
//            return;
//        }
        Log.i("reading", String.valueOf(event.values[0]));
        x += event.values[0];
        y += event.values[1];
        z += event.values[2];
        counter++;
        if(flag){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String entry = Double.toString(x/counter) + "," + Double.toString(y/counter) + "," + Double.toString(z/counter) + "\n";
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        //else it will output the Roll, Pitch and Yawn values
                        tvAccelerometer.setText("Orientation X (Roll) :" + Double.toString(x/counter) + "\n" +
                        "Orientation Y (Pitch) :" + Double.toString(y/counter) + "\n" +
                        "Orientation Z (Yaw) :" + Double.toString(z/counter) + "\n" + "Reading Count : " + (timeCounter+1));                        // update UI here
                    }
                });


                if (dumpData) {
                    try {

                        File sdCard = Environment.getExternalStorageDirectory();
                        File dir = new File(sdCard.getAbsolutePath());
                        Boolean dirsMade = dir.mkdir();
                        //System.out.println(dirsMade);
                        Log.v("Accel", dirsMade.toString());

                        File file = new File(dir, fileName + ".csv");
                        FileOutputStream f = new FileOutputStream(file, true);
//            f = new FileOutputStream(file);


                        try {
                            f.write(entry.getBytes());
                            f.flush();
                            f.close();
//                Toast.makeText(getBaseContext(), sdCard.getAbsolutePath(), Toast.LENGTH_LONG).show();
                            Log.i("entry", entry);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                x = 0;
                y = 0;
                z = 0;
                counter = 0;
                flag = false;
                timeCounter++;
                if(timeCounter == minutes * 60){
                    dumpData = false;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getBaseContext(), "Finished dumping", Toast.LENGTH_SHORT).show();
                        }
                    });
                    timer.cancel();
                    timer.purge();
                }
            }
        },0,1000
        );

    }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
        } else {
//            openFilePicker();
        }
    }
}

