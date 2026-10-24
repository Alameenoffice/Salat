package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PrayerTimeCalculator
import com.example.model.AthkarItem
import com.example.model.DEFAULT_ATHKAR_LIST
import com.example.model.WEEKLY_RENEWED_WIRD
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.HadbaGold
import com.example.ui.theme.HadbaGoldLight
import com.example.viewmodel.PrayerUiState
import com.example.viewmodel.TASBIH_PHRASES

@Composable
fun AthkarAndTasbihScreen(
    uiState: PrayerUiState,
    onSelectCategory: (String) -> Unit,
    onSelectWeekdayWird: (Int) -> Unit,
    onIncrementAthkar: (AthkarItem) -> Unit,
    onResetCategory: (String) -> Unit,
    onSelectTasbihPhrase: (Int) -> Unit,
    onIncrementTasbih: () -> Unit,
    onResetTasbih: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val categories = listOf("ورد اليوم المتجدد", "أذكار بعد الصلاة", "أذكار الصباح", "أذكار المساء", "أدعية الموصل")
    val currentWeekdayPackage = WEEKLY_RENEWED_WIRD.getOrElse(uiState.selectedWeekdayWirdIndex) { WEEKLY_RENEWED_WIRD.first() }
    val filteredAthkar = if (uiState.selectedAthkarCategory == "ورد اليوم المتجدد") {
        currentWeekdayPackage.items
    } else {
        DEFAULT_ATHKAR_LIST.filter { it.category == uiState.selectedAthkarCategory }
    }
    val currentPhrase = TASBIH_PHRASES.getOrElse(uiState.selectedTasbihIndex) { TASBIH_PHRASES.first() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("athkar_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "الأذكار اليومية المتجددة والسبحة",
                    fontFamily = AmiriFontFamily,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "ورد يومي متجدد تلقائياً لأيام الأسبوع (الأحد - السبت) دون تكرار مع السبحة الموصلية",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Category Filter Row for Athkar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = category == uiState.selectedAthkarCategory,
                            onClick = { onSelectCategory(category) },
                            label = { Text(category) }
                        )
                    }
                }
                IconButton(onClick = { onResetCategory(uiState.selectedAthkarCategory) }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "إعادة تعيين الأذكار",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 3. قسم الأذكار اليومية المتجددة بحسب أيام الأسبوع (من الأحد إلى السبت)
        if (uiState.selectedAthkarCategory == "ورد اليوم المتجدد") {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, HadbaGold, RoundedCornerShape(22.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(WEEKLY_RENEWED_WIRD) { pkg ->
                                FilterChip(
                                    selected = pkg.dayIndex == uiState.selectedWeekdayWirdIndex,
                                    onClick = { onSelectWeekdayWird(pkg.dayIndex) },
                                    label = { Text(pkg.dayNameArabic) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentWeekdayPackage.dayTitle,
                            fontFamily = AmiriFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = currentWeekdayPackage.themeNote,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Athkar Cards
        items(filteredAthkar, key = { it.id }) { item ->
            val currentCount = uiState.athkarCounts[item.id] ?: 0
            val isCompleted = currentCount >= item.targetCount

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompleted) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onIncrementAthkar(item)
                    }
                    .testTag("athkar_card_${item.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Surface(
                            color = if (isCompleted) EmeraldBright.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = EmeraldBright,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = "${PrayerTimeCalculator.toArabicDigits(currentCount)} / ${PrayerTimeCalculator.toArabicDigits(item.targetCount)}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isCompleted) EmeraldBright else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = item.arabicText,
                        fontFamily = AmiriFontFamily,
                        fontSize = 21.sp,
                        lineHeight = 34.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = item.virtue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (currentCount.toFloat() / item.targetCount.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50)),
                        color = if (isCompleted) EmeraldBright else HadbaGold,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // Interactive Digital Masbaha (السبحة الإلكترونية)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.2.dp,
                        brush = Brush.horizontalGradient(listOf(HadbaGold, HadbaGoldLight)),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "السبحة الموصلية الإلكترونية",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "إجمالي تسبيحاتك المحفوظة: ${PrayerTimeCalculator.toArabicDigits(uiState.settings.totalTasbihCount)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onResetTasbih,
                            modifier = Modifier.testTag("reset_tasbih_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "تصفير العداد",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(TASBIH_PHRASES) { index, phrase ->
                            FilterChip(
                                selected = index == uiState.selectedTasbihIndex,
                                onClick = { onSelectTasbihPhrase(index) },
                                label = { Text(text = phrase.arabic, style = MaterialTheme.typography.labelLarge) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val progress = (uiState.currentTasbihSessionCount % currentPhrase.target).toFloat() /
                        currentPhrase.target.toFloat()

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(164.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onIncrementTasbih()
                            }
                            .testTag("tasbih_tap_button")
                    ) {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                            strokeWidth = 8.dp
                        )
                        CircularProgressIndicator(
                            progress = { if (uiState.currentTasbihSessionCount > 0 && progress == 0f) 1f else progress },
                            modifier = Modifier.fillMaxSize(),
                            color = HadbaGold,
                            strokeWidth = 8.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = PrayerTimeCalculator.toArabicDigits(uiState.currentTasbihSessionCount),
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 40.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "الهدف ${PrayerTimeCalculator.toArabicDigits(currentPhrase.target)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TouchApp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "اضغط للتسبيح",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentPhrase.arabic,
                        fontFamily = AmiriFontFamily,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = currentPhrase.meaning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
