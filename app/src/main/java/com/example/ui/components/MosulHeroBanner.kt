package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PrayerTimeCalculator
import com.example.model.DailyPrayerSchedule
import com.example.model.PrayerType
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.HadbaGold
import com.example.ui.theme.HadbaGoldLight

@Composable
fun MosulHeroBanner(
    schedule: DailyPrayerSchedule,
    onDistrictClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mosul_sky_glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lantern_pulse"
    )

    // Dynamic sky gradient inspired by the upcoming prayer in Mosul
    val skyColors = when (schedule.nextPrayer) {
        PrayerType.FAJR, PrayerType.SUNRISE -> listOf(
            Color(0xFF0A1D2E),
            Color(0xFF183B4E),
            Color(0xFF3E3B5B)
        )
        PrayerType.DHUHR, PrayerType.ASR -> listOf(
            Color(0xFF082D24),
            Color(0xFF135040),
            Color(0xFF1F6E58)
        )
        PrayerType.MAGHRIB -> listOf(
            Color(0xFF1B1934),
            Color(0xFF42273B),
            Color(0xFF7B3E3B)
        )
        PrayerType.ISHA -> listOf(
            Color(0xFF051814),
            Color(0xFF0B2922),
            Color(0xFF123F35)
        )
    }

    val nextPrayerEntry = schedule.prayers.firstOrNull { it.type == schedule.nextPrayer }
        ?: schedule.prayers.first()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(skyColors))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(HadbaGold.copy(alpha = 0.65f), HadbaGold.copy(alpha = 0.15f))
                ),
                shape = RoundedCornerShape(28.dp)
            )
    ) {
        // Custom Canvas Illustration: Al-Hadba Minaret, Great Mosque of Al-Nuri Dome, Crescent & Tigris Shimmer
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(265.dp)
        ) {
            val w = size.width
            val h = size.height

            // Subtle Islamic Geometric Star Rosette in top-left sky
            val starCenterX = w * 0.18f
            val starCenterY = h * 0.25f
            drawCircle(
                color = HadbaGold.copy(alpha = 0.08f * glowPulse),
                radius = 68.dp.toPx(),
                center = Offset(starCenterX, starCenterY)
            )
            drawCircle(
                color = HadbaGold.copy(alpha = 0.14f * glowPulse),
                radius = 44.dp.toPx(),
                center = Offset(starCenterX, starCenterY)
            )

            // Glowing Crescent Moon
            drawCircle(
                color = HadbaGoldLight.copy(alpha = 0.9f * glowPulse),
                radius = 18.dp.toPx(),
                center = Offset(starCenterX, starCenterY)
            )
            drawCircle(
                color = skyColors[0],
                radius = 15.dp.toPx(),
                center = Offset(starCenterX - 7.dp.toPx(), starCenterY - 4.dp.toPx())
            )

            // Twinkling stars across Mosul sky
            val starOffsets = listOf(
                Offset(w * 0.08f, h * 0.15f),
                Offset(w * 0.32f, h * 0.12f),
                Offset(w * 0.48f, h * 0.18f),
                Offset(w * 0.72f, h * 0.14f),
                Offset(w * 0.88f, h * 0.22f)
            )
            starOffsets.forEachIndexed { idx, pos ->
                val radius = if (idx % 2 == 0) 2.2.dp.toPx() else 1.5.dp.toPx()
                drawCircle(
                    color = HadbaGoldLight.copy(alpha = 0.45f * glowPulse),
                    radius = radius,
                    center = pos
                )
            }

            // Distant Nineveh horizon silhouette
            val horizonY = h * 0.82f
            val domeCenterX = w * 0.26f

            // Al-Nuri Mosque Main Dome Silhouette
            val domePath = Path().apply {
                moveTo(domeCenterX - 42.dp.toPx(), horizonY)
                cubicTo(
                    domeCenterX - 40.dp.toPx(), horizonY - 38.dp.toPx(),
                    domeCenterX - 18.dp.toPx(), horizonY - 56.dp.toPx(),
                    domeCenterX, horizonY - 64.dp.toPx()
                )
                cubicTo(
                    domeCenterX + 18.dp.toPx(), horizonY - 56.dp.toPx(),
                    domeCenterX + 40.dp.toPx(), horizonY - 38.dp.toPx(),
                    domeCenterX + 42.dp.toPx(), horizonY
                )
                close()
            }
            drawPath(
                path = domePath,
                color = Color(0xFF061B16).copy(alpha = 0.65f)
            )
            drawPath(
                path = domePath,
                color = HadbaGold.copy(alpha = 0.28f),
                style = Stroke(width = 1.2.dp.toPx())
            )

            // Iconic Leaning Al-Hadba Minaret (منارة الحدباء المائلة)
            val minaretBaseX = w * 0.11f
            val minaretWidth = 15.dp.toPx()
            val minaretHeight = 105.dp.toPx()

            // Slight authentic 3.5-degree lean representing Al-Hadba
            withTransform({
                rotate(degrees = 3.5f, pivot = Offset(minaretBaseX, horizonY))
            }) {
                // Square stone base (قاعدة المنارة)
                drawRoundRect(
                    color = Color(0xFF07221B).copy(alpha = 0.82f),
                    topLeft = Offset(minaretBaseX - minaretWidth * 0.65f, horizonY - 26.dp.toPx()),
                    size = Size(minaretWidth * 1.3f, 26.dp.toPx()),
                    cornerRadius = CornerRadius(3.dp.toPx())
                )
                // Cylindrical patterned shaft
                drawRoundRect(
                    color = HadbaGold.copy(alpha = 0.26f),
                    topLeft = Offset(minaretBaseX - minaretWidth * 0.45f, horizonY - minaretHeight),
                    size = Size(minaretWidth * 0.9f, minaretHeight - 24.dp.toPx()),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
                // Seven decorative brickwork bands (الأحزمة السبعة المزخرفة بالآجر الموصلي)
                for (band in 1..6) {
                    val bandY = horizonY - 28.dp.toPx() - band * 11.dp.toPx()
                    drawLine(
                        color = HadbaGoldLight.copy(alpha = 0.5f),
                        start = Offset(minaretBaseX - minaretWidth * 0.42f, bandY),
                        end = Offset(minaretBaseX + minaretWidth * 0.42f, bandY),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
                // Minaret balcony & small cupola at top
                drawCircle(
                    color = HadbaGoldLight.copy(alpha = 0.7f),
                    radius = 5.5.dp.toPx(),
                    center = Offset(minaretBaseX, horizonY - minaretHeight - 4.dp.toPx())
                )
            }

            // Subtle Tigris River reflection lines at bottom
            for (i in 0..3) {
                val lineY = horizonY + 8.dp.toPx() + i * 6.dp.toPx()
                drawLine(
                    color = HadbaGold.copy(alpha = (0.20f - i * 0.04f) * glowPulse),
                    start = Offset(w * 0.05f + i * 14.dp.toPx(), lineY),
                    end = Offset(w * 0.42f - i * 10.dp.toPx(), lineY),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Foreground Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Top bar: District selector chip & Hijri/Gregorian badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.32f),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        HadbaGold.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onDistrictClick() }
                        .testTag("select_mosul_district_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "منطقة الموصل",
                            tint = HadbaGoldLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = schedule.district.nameArabic,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    color = HadbaGold.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = schedule.district.sideLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = HadbaGoldLight,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Hero Row: Next prayer info + Circular Countdown Progress Ring
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.WbTwilight,
                            contentDescription = null,
                            tint = HadbaGoldLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "الصلاة القادمة في الموصل",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "صلاة ${schedule.nextPrayer.arabicName}",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 34.sp,
                            color = HadbaGoldLight
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${nextPrayerEntry.formatted12h} ${nextPrayerEntry.periodLabel}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = schedule.nextPrayer.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.78f)
                    )
                }

                // Live Countdown Ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(106.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White.copy(alpha = 0.15f),
                        strokeWidth = 6.dp
                    )
                    CircularProgressIndicator(
                        progress = { schedule.progressToNextPrayer },
                        modifier = Modifier.fillMaxSize(),
                        color = HadbaGoldLight,
                        strokeWidth = 6.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "المتبقي",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        Text(
                            text = PrayerTimeCalculator.formatCountdownArabic(schedule.secondsUntilNextPrayer),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.testTag("countdown_timer_text")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Date & Hijri Strip
            Surface(
                color = EmeraldDeep.copy(alpha = 0.55f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${schedule.dayOfWeekArabic} • ${schedule.hijriDateFormatted}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = HadbaGoldLight
                        )
                        Text(
                            text = schedule.gregorianDateFormatted,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip( CircleShape)
                            .background(HadbaGold.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = HadbaGoldLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "الإقامة ${PrayerTimeCalculator.toArabicDigits(schedule.nextPrayer.iqamaDefaultMinutes)} د",
                            style = MaterialTheme.typography.labelMedium,
                            color = HadbaGoldLight
                        )
                    }
                }
            }
        }
    }
}
