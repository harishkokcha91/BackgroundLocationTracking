package com.plcoding.backgroundlocationtracking;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class PermissionTransparentActivity extends Activity {

    private static final String TAG = PermissionTransparentActivity.class.getSimpleName();
    private static final int LOCATION_RESPONSE_CODE = 234;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PermissionUtils.checkHasLocationPermissions(PermissionTransparentActivity.this) && Utils.isLocationServiceEnable(PermissionTransparentActivity.this)) {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.permission_transparent_activity);
        Log.i(TAG, " onCreate() ");

        TextView textView = (TextView) findViewById(R.id.permission_msg);
        Button actionBtn = (Button) findViewById(R.id.permission_action_btn);

        if (!PermissionUtils.checkHasLocationPermissions(this)) {
            textView.setText(getString(R.string.app_need_permission));
            actionBtn.setText(getString(R.string.allow));
        } else {
            textView.setText(getString(R.string.enable_location_txt_msg));
            actionBtn.setText(getString(R.string.enable));
        }
        actionBtn.setOnClickListener(view -> {

            if (!PermissionUtils.checkHasLocationPermissions(PermissionTransparentActivity.this)) {
                ActivityCompat.requestPermissions(PermissionTransparentActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PermissionUtils.REQUEST_PERMISSIONS_REQUEST_CODE);
            } else {
                if (!Utils.isLocationServiceEnable(PermissionTransparentActivity.this)) {
                    showLocationEnableDialog();
                } else {
                    finish();
                }
            }
        });

       /* if (!PermissionUtils.checkHasLocationPermissions(PermissionTransparentActivity.this)) {
            PermissionUtils.requestLocationPermissions(PermissionTransparentActivity.this);
        }*/

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == PermissionUtils.REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                Log.i(TAG, "else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) ");
                if (!Utils.isLocationServiceEnable(this)) {
                    showLocationEnableDialog();
                } else {
                    sendLocationBroadcast();
                }
            } else {
                Log.i(TAG, "User interaction was cancelled.");
                // Permission denied.
                if (ActivityCompat.shouldShowRequestPermissionRationale(PermissionTransparentActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    PermissionUtils.requestLocationPermissions(PermissionTransparentActivity.this);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PermissionTransparentActivity.this);
                    builder.setMessage(R.string.permission_denied_explanation);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.settings, (dialog, which) -> {
                        dialog.dismiss();

                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    });
                    builder.show();

                }
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void sendLocationBroadcast() {
        Log.d(TAG, "sendLocationBroadcast() ");
        Intent intent = new Intent(LocationUpdatesServiceForeground.PERMISSION_BROADCAST_INTENT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }

    private void showLocationEnableDialog() {
        /*final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.enable_location_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_RESPONSE_CODE);
                dialog.dismiss();
            }
        });
        builder.show();*/
        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_RESPONSE_CODE);
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed() ");
        if (PermissionUtils.checkHasLocationPermissions(PermissionTransparentActivity.this) && Utils.isLocationServiceEnable(PermissionTransparentActivity.this)) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult Intent data : " + data);

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_RESPONSE_CODE) {
            Log.d(TAG, "Result Value" + resultCode);
            if (Utils.isLocationServiceEnable(this)) {
                sendLocationBroadcast();
            }
        }
    }
}
