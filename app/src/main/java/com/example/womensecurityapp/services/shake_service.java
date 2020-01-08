package com.example.womensecurityapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class shake_service extends Service implements SensorEventListener {

    public final int MIN_TIME_BETWEEN_SHAKES = 3000;
    SensorManager sensorManager = null;

    Vibrator vibrator = null;

    private long lastShakeTime = 0;

    private Float shakeThreshold = 30.0f;

    public shake_service(){}

    @Override
    public void onCreate()
    {
        Log.e("kj","uin");
        super.onCreate();
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        if(sensorManager!=null)
        {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Log.e("kijj","uin");

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            long curTime = System.currentTimeMillis();

            if((curTime - lastShakeTime)>MIN_TIME_BETWEEN_SHAKES)
            {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                double acceleration = Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2))-SensorManager.GRAVITY_EARTH;

                if (acceleration > shakeThreshold) {
                    lastShakeTime = curTime;

                        Toast.makeText(getApplicationContext(),"ok",Toast.LENGTH_LONG).show();


                    //    Toast.makeText(getApplicationContext(),"no",Toast.LENGTH_LONG).show();

                }
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
