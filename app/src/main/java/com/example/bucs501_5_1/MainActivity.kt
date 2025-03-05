package com.example.bucs501_5_1

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bucs501_5_1.ui.theme.BUCS501_5_1Theme
import kotlin.math.pow

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var barometer: Sensor? = null

    private var _p by  mutableStateOf(1000.0)

    private var _accuracy by mutableStateOf("Unknown")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Sensor Manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        setContent {
            BUCS501_5_1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SensorScreen(_p, accuracy = _accuracy)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        barometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            _p = 44330*(1- (it.values[0] / 1013.25).pow((1/5.255)))

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _accuracy = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "High"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Medium"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "Low"
            SensorManager.SENSOR_STATUS_UNRELIABLE -> "Unreliable"
            else -> "Unknown"
        }
    }
}

@Composable
fun SensorScreen(_p: Double, accuracy: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = getBackgroundColorForAltitude(_p))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Accelerometer Data", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        SensorValue(label = "Pressure", value = _p)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Sensor Accuracy: $accuracy", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Red)
    }
}

@Composable
fun SensorValue(label: String, value: Double) {
    Text(
        text = "$label: ${"%.2f".format(value)}",
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = Color.DarkGray
    )
}

fun getBackgroundColorForAltitude(altitude: Double): Color {
    return when {
        altitude < 500 -> Color(0xFF00FF00)
        altitude in 500.0..2000.0 -> Color(0xFFFFFF00)
        altitude in 2000.0..5000.0 -> Color(0xFFFFA500)
        else -> Color(0xFFFF0000)
    }
}