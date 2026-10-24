package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PrayerTimeCalculator
import com.example.model.MOSUL_DISTRICTS
import com.example.model.MosulDistrict
import com.example.model.PrayerTimeEntry
import com.example.model.PrayerType
import com.example.ui.components.MosulHeroBanner
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.HadbaGold
import com.example.ui.theme.HadbaGoldLight
import com.example.viewmodel.PrayerUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerHomeScreen(
    uiState: PrayerUiState,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit,
    onResetToday: () -> Unit,
    onToggleNotification: (PrayerType) -> Unit,
    onSelectPrayerDetail: (PrayerType?) -> Unit,
    onSelectDistrict: (MosulDistrict) -> Unit,
    onSyncOnline: () -> Unit,
    onToggleTheme: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val schedule = uiState.schedule ?: return
    var showDistrictSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("prayer_home_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // App Header with Title, Sync Button, and Dark/Light Mode Toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "مواقيت الموصل",
                        fontFamily = AmiriFontFamily,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "أم الربيعين • توقيت المساجد المحلي",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSyncOnline,
                        modifier = Modifier.testTag("sync_online_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "تحديث عبر الإنترنت",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { onToggleTheme(!uiState.settings.darkTheme) },
                        modifier = Modifier.testTag("toggle_theme_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.settings.darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "تبديل المظهر",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Optional sync feedback banner
        if (uiState.syncStatusMessage != null) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.syncStatusMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }

        // Hero Banner with Al-Hadba Minaret & Live Countdown
        item {
            MosulHeroBanner(
                schedule = schedule,
                onDistrictClick = { showDistrictSheet = true }
            )
        }

        // Sunnah Night/Fasting Times Strip (الإمساك، منتصف الليل، الثلث الأخير)
        item {
            AuxiliaryTimesStrip(
                imsak = schedule.imsakFormatted,
                midnight = schedule.midnightFormatted,
                lastThird = schedule.lastThirdFormatted
            )
        }

        // Date Navigation Bar (اليوم السابق / اليوم / اليوم التالي)
        item {
            DateNavigationBar(
                dayOffset = uiState.dayOffset,
                dayOfWeek = schedule.dayOfWeekArabic,
                gregorianDate = schedule.gregorianDateFormatted,
                onPrevDay = onPrevDay,
                onNextDay = onNextDay,
                onResetToday = onResetToday
            )
        }

        // Daily Prayer Cards (الفجر، الشروق، الظهر، العصر، المغرب، العشاء)
        items(schedule.prayers, key = { it.type.name }) { entry ->
            PrayerTimeCard(
                entry = entry,
                isExpanded = uiState.selectedPrayerForDetail == entry.type,
                onCardClick = {
                    val next = if (uiState.selectedPrayerForDetail == entry.type) null else entry.type
                    onSelectPrayerDetail(next)
                },
                onToggleNotification = { onToggleNotification(entry.type) }
            )
        }

        // Footer Note on Calculation Method
        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = uiState.settings.calculationMethod.titleArabic,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${uiState.settings.asrMethod.titleArabic} • الارتفاع ${PrayerTimeCalculator.toArabicDigits(schedule.district.elevationMeters.toInt())}م فوق سطح البحر",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Mosul District Picker Bottom Sheet
    if (showDistrictSheet) {
        ModalBottomSheet(
            onDismissRequest = { showDistrictSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "اختر منطقتك في الموصل ونينوى",
                    fontFamily = AmiriFontFamily,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "تُحسب المواقيت وزاوية الأفق بدقة فلكية حسب إحداثيات وارتفاع كل حي في الساحلين الأيمن والأيسر",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                MOSUL_DISTRICTS.forEach { district ->
                    val isSelected = district.id == schedule.district.id
                    Surface(
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onSelectDistrict(district)
                                showDistrictSheet = false
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = district.nameArabic,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${district.sideLabel} • ${district.landmarkArabic}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Text(
                                    text = "مُختار",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AuxiliaryTimesStrip(
    imsak: String,
    midnight: String,
    lastThird: String
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AuxiliaryTimeItem(label = "الإمساك", time = imsak)
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            )
            AuxiliaryTimeItem(label = "منتصف الليل", time = midnight)
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            )
            AuxiliaryTimeItem(label = "الثلث الأخير (القيام)", time = lastThird)
        }
    }
}

@Composable
private fun AuxiliaryTimeItem(label: String, time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun DateNavigationBar(
    dayOffset: Int,
    dayOfWeek: String,
    gregorianDate: String,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit,
    onResetToday: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPrevDay,
            modifier = Modifier.testTag("prev_day_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "اليوم السابق",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (dayOffset == 0) "مواقيت اليوم ($dayOfWeek)" else "$dayOfWeek • $gregorianDate",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (dayOffset != 0) {
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onResetToday,
                    modifier = Modifier.testTag("reset_today_button")
                ) {
                    Text("العودة لليوم")
                }
            }
        }

        IconButton(
            onClick = onNextDay,
            modifier = Modifier.testTag("next_day_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "اليوم التالي",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PrayerTimeCard(
    entry: PrayerTimeEntry,
    isExpanded: Boolean,
    onCardClick: () -> Unit,
    onToggleNotification: () -> Unit
) {
    val isNext = entry.isNextPrayer
    val cardBorder = if (isNext) {
        Modifier.border(
            width = 1.8.dp,
            brush = Brush.horizontalGradient(listOf(HadbaGold, HadbaGoldLight)),
            shape = RoundedCornerShape(20.dp)
        )
    } else {
        Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
            shape = RoundedCornerShape(20.dp)
        )
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isNext) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .then(cardBorder)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onCardClick() }
            .testTag("prayer_card_${entry.type.name.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Right side (in RTL): Icon + Prayer Name + Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isNext) HadbaGold.copy(alpha = 0.22f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    ) {
                        Icon(
                            imageVector = iconForPrayer(entry.type),
                            contentDescription = entry.type.arabicName,
                            tint = if (isNext) HadbaGold else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = entry.type.arabicName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isNext) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            if (isNext) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = HadbaGold,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "الصلاة القادمة",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (entry.type.iqamaDefaultMinutes > 0) {
                                "الإقامة بعد ${PrayerTimeCalculator.toArabicDigits(entry.type.iqamaDefaultMinutes)} دقيقة • السنن: ${entry.type.sunnahBefore}"
                            } else {
                                entry.type.subtitle
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Left side (in RTL): Formatted Time + Notification Bell
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = entry.formatted12h,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = entry.periodLabel,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                        if (entry.offsetMinutes != 0) {
                            val sign = if (entry.offsetMinutes > 0) "+" else ""
                            Text(
                                text = "تعديل $sign${PrayerTimeCalculator.toArabicDigits(entry.offsetMinutes)} د",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onToggleNotification,
                        modifier = Modifier.testTag("notif_toggle_${entry.type.name.lowercase()}")
                    ) {
                        Icon(
                            imageVector = if (entry.notificationEnabled) {
                                Icons.Default.NotificationsActive
                            } else {
                                Icons.Default.NotificationsOff
                            },
                            contentDescription = "تنبيه الأذان",
                            tint = if (entry.notificationEnabled) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            }
                        )
                    }
                }
            }

            // Expandable detail section with Sunnah rakaat and Mosul tradition note
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "السنة القبلية: ${entry.type.sunnahBefore}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "السنة البعدية: ${entry.type.sunnahAfter}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = entry.type.mosulHeritageNote,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun iconForPrayer(type: PrayerType): ImageVector = when (type) {
    PrayerType.FAJR -> Icons.Outlined.WbTwilight
    PrayerType.SUNRISE -> Icons.Default.WbSunny
    PrayerType.DHUHR -> Icons.Default.WbSunny
    PrayerType.ASR -> Icons.Default.AccessTime
    PrayerType.MAGHRIB -> Icons.Outlined.WbTwilight
    PrayerType.ISHA -> Icons.Outlined.NightsStay
}
