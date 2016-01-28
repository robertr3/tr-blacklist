package com.silentdynamics.student.blacklist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by kuhfi on 28.01.2016.
 */
public class BatteryLevelReceiver extends BroadcastReceiver {

    boolean batteryLow = false;
    boolean isConnected = false;
    boolean batteryOkay = true;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("Battery", "onReceive");

        if(intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
            Log.d("Battery", "Battery low");
            Toast.makeText(context, "Battery low",Toast.LENGTH_SHORT).show();
            batteryLow = true;
            getBatteryStatus();
        }

        if(intent.getAction().equals(Intent.ACTION_BATTERY_OKAY)) {
            Log.d("Battery", "Battery okay");
            batteryLow = false;
            getBatteryStatus();
        }

        if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            Log.d("Battery", "Battery power connected");
            Toast.makeText(context, "Battery power conntected",Toast.LENGTH_SHORT).show();
            isConnected = true;
            getBatteryStatus();
        }

        if(intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            Log.d("Battery", "Battery power disconnected");
            isConnected = false;
            getBatteryStatus();
        }

        if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            Log.d("Battery", "Battery changed");
        }
    }

    public boolean getBatteryOkay (){
        return batteryOkay;
    }

    void getBatteryStatus() {
        // if battery is okay or connected
        if(batteryLow == false || isConnected) {
            batteryOkay = true;
            return;
        }
        // if battery is low and not connected
        batteryOkay = false;
    }
}
