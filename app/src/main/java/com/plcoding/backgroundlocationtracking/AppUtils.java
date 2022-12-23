package com.plcoding.backgroundlocationtracking;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AppUtils {
    public static final String TAG = AppUtils.class.getSimpleName();
    public static void stopRunningLocationService(Context applicationContext) {
        Log.e(TAG, "stopRunningLocationService()");
        if (applicationContext == null) {
            return;
        }
        //Stop fetching location
        if (AppUtils.isMyServiceRunning(applicationContext, LocationUpdatesServiceForeground.class)) {
            Log.e(TAG, "isMyServiceRunning(applicationContext, LocationUpdatesServiceNew.class)()");
            Intent intent1 = new Intent(applicationContext, LocationUpdatesServiceForeground.class);
            intent1.putExtra("EXTRA_STARTED_FROM_NOTIFICATION", true);
            applicationContext.startService(intent1);
        }
    }

    public static void startLocationService(Context applicationContext) {

        if (applicationContext == null) {
            Log.e(TAG, "startLocationService() - Context can't be null");
            return;
        }
        Log.e(TAG, "startLocationService()");
        //Stop fetching location
        if (!AppUtils.isMyServiceRunning(applicationContext, LocationUpdatesServiceForeground.class)) {
            Intent intent1 = new Intent(applicationContext, LocationUpdatesServiceForeground.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                applicationContext.startForegroundService(intent1);
            } else {
                applicationContext.startService(intent1);
            }
        }
    }
    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        Log.d("Harish","isMyServiceRunning ");
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equalsIgnoreCase(service.service.getClassName())) {
                Log.d("Harish","isMyServiceRunning true");
                return true;
            }
        }
        Log.d("Harish","isMyServiceRunning false");
        return false;
    }
}
