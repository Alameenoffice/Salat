package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class CompassReading(
    val azimuthDegrees: Float,
    val accuracy: Int,
    val hasHardwareSensor: Boolean
)

class QiblaCompassManager(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    fun observeCompass(): Flow<CompassReading> = callbackFlow {
        val mgr = sensorManager
        if (mgr == null) {
            trySend(CompassReading(azimuthDegrees = 193.6f, accuracy = 0, hasHardwareSensor = false))
            awaitClose {}
            return@callbackFlow
        }

        val rotationVectorSensor = mgr.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelerometer = mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = mgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (rotationVectorSensor == null && (accelerometer == null || magnetometer == null)) {
            trySend(CompassReading(azimuthDegrees = 193.6f, accuracy = 0, hasHardwareSensor = false))
            awaitClose {}
            return@callbackFlow
        }

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var hasGravity = false
        var hasGeomagnetic = false
        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)
        var smoothedAzimuth = 0f

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ROTATION_VECTOR -> {
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                        SensorManager.getOrientation(rotationMatrix, orientation)
                        val rawDeg = ((Math.toDegrees(orientation[0].toDouble()) + 360.0) % 360.0).toFloat()
                        smoothedAzimuth = lowPassFilterAngle(smoothedAzimuth, rawDeg)
                        trySend(
                            CompassReading(
                                azimuthDegrees = smoothedAzimuth,
                                accuracy = event.accuracy,
                                hasHardwareSensor = true
                            )
                        )
                    }
                    Sensor.TYPE_ACCELEROMETER -> {
                        val alpha = 0.85f
                        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]
                        hasGravity = true
                        if (hasGeomagnetic) emitFromAccelMag(event.accuracy)
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        val alpha = 0.85f
                        geomagnetic[0] = alpha * geomagnetic[0] + (1 - alpha) * event.values[0]
                        geomagnetic[1] = alpha * geomagnetic[1] + (1 - alpha) * event.values[1]
                        geomagnetic[2] = alpha * geomagnetic[2] + (1 - alpha) * event.values[2]
                        hasGeomagnetic = true
                        if (hasGravity) emitFromAccelMag(event.accuracy)
                    }
                }
            }

            private fun emitFromAccelMag(accuracy: Int) {
                if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val rawDeg = ((Math.toDegrees(orientation[0].toDouble()) + 360.0) % 360.0).toFloat()
                    smoothedAzimuth = lowPassFilterAngle(smoothedAzimuth, rawDeg)
                    trySend(
                        CompassReading(
                            azimuthDegrees = smoothedAzimuth,
                            accuracy = accuracy,
                            hasHardwareSensor = true
                        )
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        // Send initial state immediately so UI renders without waiting for sensor tick
        trySend(CompassReading(azimuthDegrees = 0f, accuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH, hasHardwareSensor = true))

        if (rotationVectorSensor != null) {
            mgr.registerListener(listener, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            mgr.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
            mgr.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        }

        awaitClose {
            mgr.unregisterListener(listener)
        }
    }

    private fun lowPassFilterAngle(current: Float, target: Float): Float {
        var diff = target - current
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f
        return (current + diff * 0.25f + 360f) % 360f
    }
}
