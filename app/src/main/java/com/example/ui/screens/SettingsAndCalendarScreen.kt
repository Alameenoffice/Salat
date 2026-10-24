package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.PrayerTimeCalculator
import com.example.model.AsrJuristicMethod
import com.example.model.CalculationMethodOption
import com.example.model.MOSUL_DISTRICTS
import com.example.model.MosulDistrict
import com.example.model.PrayerType
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.HadbaGold
import com.example.viewmodel.MonthlyDayRow
import com.example.viewmodel.PrayerUiState

@Composable
fun SettingsAndCalendarScreen(
    uiState: PrayerUiState,
    onSelectDistrict: (MosulDistrict) -> Unit,
    onSelectCalculationMethod: (CalculationMethodOption) -> Unit,
    onSelectAsrMethod: (AsrJuristicMethod) -> Unit,
    onAdjustPrayerOffset: (PrayerType, Int) -> Unit,
    onResetOffsets: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 0 = الإعدادات وضبط الدقائق, 1 = الإمساكية والجدول الشهري للموصل
    var selectedTab by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_calendar_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "التقويم الشهري وإعدادات الموصل",
                    fontFamily = AmiriFontFamily,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "إمساكية الشهر الكامل لمدينة الموصل وضبط الحساب الفلكي والمذهب الفقهي",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Top Switcher: Settings vs Monthly Timetable
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null)
                    },
                    label = { Text("إعدادات المواقيت والأحياء") },
                    modifier = Modifier.testTag("tab_settings_chip")
                )
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
                    },
                    label = { Text("جدول الشهر الكامل") },
                    modifier = Modifier.testTag("tab_monthly_calendar_chip")
                )
            }
        }

        if (selectedTab == 0) {
            // 1. Mosul Neighborhood Selector
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "حيّك أو منطقتك في الموصل ونينوى",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "يضبط خطوط الطول والعرض وارتفاع الأفق (${PrayerTimeCalculator.toArabicDigits(uiState.settings.selectedDistrict.elevationMeters.toInt())} متر)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        MOSUL_DISTRICTS.forEach { district ->
                            val selected = district.id == uiState.settings.selectedDistrict.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectDistrict(district) }
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = { onSelectDistrict(district) }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = district.nameArabic,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${district.sideLabel} • ${district.landmarkArabic}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Calculation Method Selector
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "طريقة الحساب الفلكي",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        CalculationMethodOption.entries.forEach { method ->
                            val selected = method == uiState.settings.calculationMethod
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectCalculationMethod(method) }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = { onSelectCalculationMethod(method) }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = method.titleArabic,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = method.descriptionArabic,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Asr Juristic Method Selector
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "حساب وقت صلاة العصر",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        AsrJuristicMethod.entries.forEach { asr ->
                            val selected = asr == uiState.settings.asrMethod
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectAsrMethod(asr) }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = { onSelectAsrMethod(asr) }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = asr.titleArabic,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = asr.descriptionArabic,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Individual Prayer Minute Fine-Tuning (ضبط الدقائق لمطابقة مؤذن المسجد القريب)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تعديل الدقائق الفردي (احتياط المسجد)",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "تقديم أو تأخير كل وقت بالدقائق لمطابقة أذان مسجد حيّك",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TextButton(onClick = onResetOffsets) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("صفّر الكل")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        PrayerType.entries.forEach { type ->
                            val offset = uiState.settings.prayerOffsets[type] ?: 0
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = type.arabicName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { onAdjustPrayerOffset(type, -1) }) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "إنقاص دقيقة"
                                        )
                                    }
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.width(68.dp)
                                    ) {
                                        val sign = if (offset > 0) "+" else ""
                                        Text(
                                            text = "$sign${PrayerTimeCalculator.toArabicDigits(offset)} د",
                                            style = MaterialTheme.typography.labelLarge,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                    }
                                    IconButton(onClick = { onAdjustPrayerOffset(type, 1) }) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "إضافة دقيقة"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Monthly Timetable Header
            item {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("اليوم", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1.3f))
                        Text("الفجر", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("الشروق", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("الظهر", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("العصر", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("المغرب", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("العشاء", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }
                }
            }

            items(uiState.monthlyRows, key = { it.dayOfMonth }) { row ->
                MonthlyTimetableRowCard(row = row)
            }
        }
    }
}

@Composable
private fun MonthlyTimetableRowCard(row: MonthlyDayRow) {
    Surface(
        color = if (row.isToday) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (row.isToday) {
                    Modifier.border(1.5.dp, HadbaGold, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            )
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1.3f)) {
                    Text(
                        text = "${PrayerTimeCalculator.toArabicDigits(row.dayOfMonth)} • ${row.dayNameArabic}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = row.hijriDayFormatted,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(row.fajr, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(row.sunrise, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(row.dhuhr, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(row.asr, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(row.maghrib, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(row.isha, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}
