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
        //TODO: Get actual Battery Level
        Log.d("Battery", "Battery Low");
    }
}
