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

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("Battery", "onReceive");

        if(intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
            Log.d("Battery", "Battery low");
            Toast.makeText(context, "Battery low",Toast.LENGTH_SHORT).show();
        }

        if(intent.getAction().equals(Intent.ACTION_BATTERY_OKAY)) {
            Log.d("Battery", "Battery okay");
        }

        if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            Log.d("Battery", "Battery power connected");
            Toast.makeText(context, "Battery power conntected",Toast.LENGTH_SHORT).show();
        }

        if(intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            Log.d("Battery", "Battery power disconnected");
        }

        if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            Log.d("Battery", "Battery changed");
        }
    }
}
