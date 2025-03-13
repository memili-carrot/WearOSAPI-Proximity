package com.example.wearosproximity.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var proximity by mutableStateOf("측정중...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SensorManager 초기화
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_UI)
            Log.d("ProximitySensor", "Proximity sensor detected and registered!") // ✅ 센서 등록 확인 로그
        } else {
            proximity = "센서 없음"
            Log.e("ProximitySensor", "Proximity sensor NOT found!") // ✅ 센서 없음 로그
        }

        setContent {
            ProximityWearOSApp(proximity)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val proximityValue = it.values[0]
            proximity = "%.2f cm".format(proximityValue) // ✅ UI 업데이트

            Log.d("ProximitySensor", "Proximity Value Updated: $proximityValue") // ✅ 로그로 값 확인
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}

@Composable
fun ProximityWearOSApp(proximity: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Proximity Sensor", style = MaterialTheme.typography.body1, modifier = Modifier.padding(16.dp))
        Text("Distance: $proximity", modifier = Modifier.padding(8.dp))
    }
}
