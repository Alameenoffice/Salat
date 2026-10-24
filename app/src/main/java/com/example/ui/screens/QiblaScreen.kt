package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PrayerTimeCalculator
import com.example.model.MOSUL_HISTORIC_MOSQUES
import com.example.model.MosulHistoricMosque
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.HadbaGold
import com.example.ui.theme.HadbaGoldLight
import com.example.viewmodel.PrayerUiState
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun QiblaScreen(
    uiState: PrayerUiState,
    onManualAzimuthChange: (Float?) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAzimuth = uiState.manualCompassAzimuth ?: uiState.compassReading.azimuthDegrees
    val qiblaBearing = uiState.qiblaBearingDegrees
    val animatedAzimuth by animateFloatAsState(
        targetValue = activeAzimuth,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "compass_azimuth"
    )

    // Angle difference between current device heading and Qibla bearing in Mosul (~193.64°)
    val rawDiff = abs(activeAzimuth - qiblaBearing) % 360f
    val angularError = if (rawDiff > 180f) 360f - rawDiff else rawDiff
    val isAlignedWithQibla = angularError <= 6.5f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("qibla_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "اتجاه القبلة وجوامع الموصل",
                    fontFamily = AmiriFontFamily,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${uiState.settings.selectedDistrict.nameArabic} • زاوية القبلة ${PrayerTimeCalculator.toArabicDigits(qiblaBearing.roundToInt())}° جنوب الغرب",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Interactive Qibla Compass Card
        item {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isAlignedWithQibla) 2.dp else 1.dp,
                        color = if (isAlignedWithQibla) EmeraldBright else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(26.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Sensor / Alignment Status Badge
                    Surface(
                        color = if (isAlignedWithQibla) {
                            EmeraldBright.copy(alpha = 0.2f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isAlignedWithQibla) Icons.Default.CheckCircle else Icons.Default.Explore,
                                contentDescription = null,
                                tint = if (isAlignedWithQibla) EmeraldBright else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAlignedWithQibla) {
                                    "أنت تتجه الآن نحو الكعبة المشرفة مباشرة"
                                } else {
                                    "أدر الهاتف حتى ينطبق السهم الذهبي مع المؤشر العلوي"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isAlignedWithQibla) EmeraldBright else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Compass Dial Canvas
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(250.dp)
                    ) {
                        val dialBorderColor = if (isAlignedWithQibla) EmeraldBright else HadbaGold
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val radius = size.minDimension / 2f - 12.dp.toPx()

                            // Outer stationary alignment marker at top (12 o'clock)
                            drawCircle(
                                color = dialBorderColor,
                                radius = 5.dp.toPx(),
                                center = Offset(center.x, center.y - radius - 8.dp.toPx())
                            )

                            // Outer ring
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF103B31),
                                        EmeraldDeep
                                    ),
                                    center = center,
                                    radius = radius
                                ),
                                radius = radius,
                                center = center
                            )
                            drawCircle(
                                color = dialBorderColor.copy(alpha = 0.7f),
                                radius = radius,
                                center = center,
                                style = Stroke(width = 3.dp.toPx())
                            )

                            // Rotate dial opposite to device azimuth so North points to magnetic North
                            rotate(degrees = -animatedAzimuth, pivot = center) {
                                // Degree ticks every 15 degrees
                                for (deg in 0 until 360 step 15) {
                                    val rad = Math.toRadians((deg - 90).toDouble())
                                    val isCardinal = deg % 90 == 0
                                    val innerR = if (isCardinal) radius - 18.dp.toPx() else radius - 10.dp.toPx()
                                    val start = Offset(
                                        x = center.x + (innerR * cos(rad)).toFloat(),
                                        y = center.y + (innerR * sin(rad)).toFloat()
                                    )
                                    val end = Offset(
                                        x = center.x + (radius * cos(rad)).toFloat(),
                                        y = center.y + (radius * sin(rad)).toFloat()
                                    )
                                    drawLine(
                                        color = if (deg == 0) Color(0xFFE07A5F) else HadbaGoldLight.copy(alpha = if (isCardinal) 0.85f else 0.35f),
                                        start = start,
                                        end = end,
                                        strokeWidth = if (isCardinal) 2.5.dp.toPx() else 1.2.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }

                                // Rotate Qibla Golden Pointer to exact Mosul Qibla Bearing (~193.64°)
                                rotate(degrees = qiblaBearing, pivot = center) {
                                    // Golden Needle pointing toward Kaaba
                                    val needlePath = Path().apply {
                                        moveTo(center.x, center.y - radius + 22.dp.toPx())
                                        lineTo(center.x - 11.dp.toPx(), center.y + 18.dp.toPx())
                                        lineTo(center.x, center.y + 6.dp.toPx())
                                        lineTo(center.x + 11.dp.toPx(), center.y + 18.dp.toPx())
                                        close()
                                    }
                                    drawPath(
                                        path = needlePath,
                                        brush = Brush.verticalGradient(
                                            colors = listOf(HadbaGoldLight, HadbaGold)
                                        )
                                    )

                                    // Kaaba emblem at needle tip
                                    drawCircle(
                                        color = HadbaGoldLight,
                                        radius = 11.dp.toPx(),
                                        center = Offset(center.x, center.y - radius + 20.dp.toPx())
                                    )
                                    drawCircle(
                                        color = Color(0xFF121212),
                                        radius = 7.dp.toPx(),
                                        center = Offset(center.x, center.y - radius + 20.dp.toPx())
                                    )
                                }
                            }

                            // Center pivot jewel
                            drawCircle(
                                color = HadbaGold,
                                radius = 8.dp.toPx(),
                                center = center
                            )
                            drawCircle(
                                color = EmeraldDeep,
                                radius = 3.5.dp.toPx(),
                                center = center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Telemetry metrics row: Qibla Bearing, Current Heading, Distance to Makkah
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CompassMetricColumn(
                            label = "زاوية القبلة بالموصل",
                            value = "${PrayerTimeCalculator.toArabicDigits(qiblaBearing.roundToInt())}°"
                        )
                        CompassMetricColumn(
                            label = "اتجاه الهاتف الحالي",
                            value = "${PrayerTimeCalculator.toArabicDigits(activeAzimuth.roundToInt())}°"
                        )
                        CompassMetricColumn(
                            label = "المسافة للكعبة",
                            value = "${PrayerTimeCalculator.toArabicDigits(uiState.distanceToKaabaKm)} كم"
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Manual Turntable Control (helpful when testing on devices without magnetometer or on table)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (uiState.compassReading.hasHardwareSensor && uiState.manualCompassAzimuth == null) {
                                    "البوصلة المغناطيسية نشطة (يمكنك التدوير اليدوي للمعاينة)"
                                } else {
                                    "وضع التدوير اليدوي للبوصلة"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (uiState.manualCompassAzimuth != null) {
                                TextButton(onClick = { onManualAzimuthChange(null) }) {
                                    Text("تفعيل الحساس التلقائي")
                                }
                            }
                        }
                        Slider(
                            value = activeAzimuth,
                            onValueChange = { onManualAzimuthChange(it) },
                            valueRange = 0f..359f,
                            modifier = Modifier.testTag("compass_manual_slider")
                        )
                    }
                }
            }
        }

        // Section Header: Mosul Historic Mosques Directory
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mosque,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "دليل جوامع الموصل التاريخية ومعالمها",
                    fontFamily = AmiriFontFamily,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        items(MOSUL_HISTORIC_MOSQUES, key = { it.id }) { mosque ->
            MosulMosqueCard(mosque = mosque)
        }
    }
}

@Composable
private fun CompassMetricColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun MosulMosqueCard(mosque: MosulHistoricMosque) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mosque.nameArabic,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = mosque.distanceFromCenterKm,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${mosque.locationArabic} • ${mosque.foundedEra}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mosque.architecturalFeature,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = mosque.qiblaNote,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
