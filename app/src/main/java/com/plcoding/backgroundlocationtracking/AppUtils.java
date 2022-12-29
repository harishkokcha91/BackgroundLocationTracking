package com.plcoding.backgroundlocationtracking;

import static com.plcoding.backgroundlocationtracking.SeriveStateKt.getServiceState;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class AppUtils {
    public static final String TAG = AppUtils.class.getSimpleName();


    public static void actionOnLocationService(Context applicationContext, Actions action) {
        Log.d("Harish","Starting getServiceState(this) : " + getServiceState(applicationContext) + " action.name() : " + action.name());
        if (getServiceState(applicationContext) == ServiceState.STOPPED && action == Actions.STOP) {
            return;
        }
        Intent intent = new Intent(applicationContext, LocationEndlessService.class);
        intent.setAction(action.name());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("Harish","Starting the service in >=26 Mode");
            applicationContext.startForegroundService(intent);
            return;
        }
        Log.d("Harish","Starting the service in < 26 Mode");
        applicationContext.startService(intent);
    }

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
        Log.d("Harish", "isMyServiceRunning ");
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equalsIgnoreCase(service.service.getClassName())) {
                Log.d("Harish", "isMyServiceRunning true");
                return true;
            }
        }
        Log.d("Harish", "isMyServiceRunning false");
        return false;
    }

    public static boolean hasPermission(Context context, String permission) {

        int res = context.checkCallingOrSelfPermission(permission);

        Log.v(TAG, "permission: " + permission.toString() + " = \t\t" +
                (res == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));

        return res == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Determines if the context calling has the required permissions
     *
     * @param context     - the IPC context
     * @param permissions - The permissions to check
     * @return true if the IPC has the granted permission
     */
    public static boolean hasPermissions(Context context, String[] permissions) {
        Log.d("Harish", "permissions " + permissions);
        boolean hasAllPermissions = true;

//        for (String permission : permissions) {
        for(String permission : permissions) {
            //you can return false instead of assigning, but by assigning you can log all permission values
            if (!hasPermission(context, permission)) {
                hasAllPermissions = false;
            }
        }

        return hasAllPermissions;

    }
}
