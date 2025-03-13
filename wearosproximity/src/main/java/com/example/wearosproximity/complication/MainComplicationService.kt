package com.example.wearosproximity.complication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService

/**
 * Complication service that displays real-time proximity sensor data.
 */
class MainComplicationService : SuspendingComplicationDataSourceService(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var proximity: Float = 0.0f

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        registerProximitySensor()
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        if (type != ComplicationType.SHORT_TEXT) {
            return null
        }
        return createComplicationData("0.00 cm", "Proximity Data")
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val proximityData = getProximityData()
        return createComplicationData(proximityData, "Current Proximity")
    }

    private fun createComplicationData(text: String, contentDescription: String) =
        ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text).build(),
            contentDescription = PlainComplicationText.Builder(contentDescription).build()
        ).build()

    private fun registerProximitySensor() {
        val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun getProximityData(): String {
        return "%.2f cm".format(proximity)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            proximity = it.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}