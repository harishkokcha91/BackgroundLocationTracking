package com.plcoding.backgroundlocationtracking;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final int REQUEST_PERMISSIONS_ALL_REQUEST_CODE = 323;
    private static final String TAG = PermissionUtils.class.getSimpleName();
    private static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static boolean hasPermissions(Context context, String... permissions) {
        Log.d(TAG, "hasPermissions");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                Log.d(TAG, "has Permissions permission : " + permission);
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkHasLocationPermissions(Context activity) {
        boolean hasPermission = (PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                &&
                PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                &&
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION));

        Log.d("Harish", "hasPermission ACCESS_FINE_LOCATION " + hasPermission);
        return hasPermission;
    }

    public static void requestAllPermissions(final Activity baseActivity) {

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (checkShouldShowRequestRationale(baseActivity)) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showRequestRationaleDialog(baseActivity);

        } else {
            Log.i(TAG, "Requesting All Permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            // Request permission
            ActivityCompat.requestPermissions(baseActivity, PERMISSIONS, REQUEST_PERMISSIONS_ALL_REQUEST_CODE);
        }
    }

    public static void showRequestRationaleDialog(final Context baseActivity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
        builder.setMessage(R.string.permission_rationale);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                ActivityCompat.requestPermissions((Activity) baseActivity, PERMISSIONS, REQUEST_PERMISSIONS_ALL_REQUEST_CODE);

            }
        });
        builder.show();

    }

    public static boolean checkShouldShowRequestRationale(Activity baseActivity) {

        boolean shouldProvideRationale = false;
        if (ActivityCompat.shouldShowRequestPermissionRationale(baseActivity, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(baseActivity, Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(baseActivity, Manifest.permission.READ_PHONE_STATE) || ActivityCompat.shouldShowRequestPermissionRationale(baseActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(baseActivity, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(baseActivity, Manifest.permission.GET_ACCOUNTS)) {
            shouldProvideRationale = true;
        }

        return shouldProvideRationale;
    }

    public static void requestLocationPermissions(final Activity baseActivity) {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(baseActivity, android.Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
            builder.setMessage(R.string.permission_rationale);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Request permission
                    ActivityCompat.requestPermissions(baseActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);

                    dialog.dismiss();

                }
            });
            builder.show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(baseActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    public static void onRequestPermissionsResult(final Activity baseActivity, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult permissions : " + permissions.toString());

        for (int i = 0; i < permissions.length; i++) {
            Log.d(TAG, "onRequestPermissionsResult permissions : " + permissions[i] + " grantResults :" + grantResults[i]);

            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                if (checkShouldShowRequestRationale(baseActivity)) {
                    Log.d(TAG, "onRequestPermissionsResult checkShouldShowRequestRationale : ");
                    showRequestRationaleDialog(baseActivity);

                } else {

                    Log.i(TAG, "User interaction was cancelled.");
                    // Permission denied.
                    AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
                    builder.setMessage(R.string.permission_denied_explanation);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            // Build intent that displays the App settings screen.
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            baseActivity.startActivity(intent);


                        }
                    });
                    builder.show();

                }
                break;
            }

        }
    }

    public static boolean checkReceiveSMSPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        }
        return true;
    }

    public static boolean checkPhoneStatePermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        }
        return true;
    }
}
