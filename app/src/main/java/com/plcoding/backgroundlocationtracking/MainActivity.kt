package com.plcoding.backgroundlocationtracking

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.plcoding.backgroundlocationtracking.AppUtils.isMyServiceRunning
import com.plcoding.backgroundlocationtracking.ui.theme.BackgroundLocationTrackingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity","onCreate")
        isMyServiceRunning(applicationContext, LocationUpdatesServiceForeground::class.java)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            0
        )
        setContent {
            BackgroundLocationTrackingTheme {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(onClick = {
//                        Intent(applicationContext, LocationService::class.java).apply {
//                            action = LocationService.ACTION_START
//                            startService(this)
//
//                        }
                        AppUtils.startLocationService(applicationContext)
                    }) {
                        Text(text = "Start")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
//                        Intent(applicationContext, LocationService::class.java).apply {
//                            action = LocationService.ACTION_STOP
//                            startService(this)
//                        }

                        AppUtils.stopRunningLocationService(applicationContext)
                    }) {
                        Text(text = "Stop")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        Log.d("MainActivity","onDestroy")
        isMyServiceRunning(applicationContext, LocationUpdatesServiceForeground::class.java)
        super.onDestroy()
    }
}