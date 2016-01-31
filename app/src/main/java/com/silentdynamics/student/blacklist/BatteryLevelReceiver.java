package com.silentdynamics.student.blacklist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by kuhfi on 28.01.2016.
 */
public class BatteryLevelReceiver extends BroadcastReceiver {

    public static final String PREFS_NAME = "MyPrefsFile";

    boolean batteryLow = false;
    boolean isConnected = false;
    boolean batterySaferMode = false;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("Battery", "onReceive");

        if(intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
            Log.d("Battery", "Battery low");
            Toast.makeText(context, "Battery low",Toast.LENGTH_SHORT).show();
            batteryLow = true;
            getBatteryStatus(context);
        }

        if(intent.getAction().equals(Intent.ACTION_BATTERY_OKAY)) {
            Log.d("Battery", "Battery okay");
            batteryLow = false;
            getBatteryStatus(context);
        }

        if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            Log.d("Battery", "Battery power connected");
            Toast.makeText(context, "Battery power conntected",Toast.LENGTH_SHORT).show();
            isConnected = true;
            getBatteryStatus(context);
        }

        if(intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            Log.d("Battery", "Battery power disconnected");
            isConnected = false;
            getBatteryStatus(context);
        }

        if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            Log.d("Battery", "Battery changed");
        }
    }

    public boolean getBatterySaferMode(){
        return batterySaferMode;
    }

    void getBatteryStatus(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        // if battery is okay or connected
        if(/* Für Vorführungszwecke auskommentiert: batteryLow == false || */isConnected) {
            batterySaferMode = false;

            editor.putBoolean("BatterySafe", batterySaferMode);

            // Commit the edits!
            editor.commit();
            return;
        }
        // if battery is low and not connected
        batterySaferMode = true;

        editor.putBoolean("BatterySafe", batterySaferMode);

        // Commit the edits!
        editor.commit();
        return;
    }
}
