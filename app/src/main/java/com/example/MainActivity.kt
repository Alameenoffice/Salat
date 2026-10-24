package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AthkarAndTasbihScreen
import com.example.ui.screens.PrayerHomeScreen
import com.example.ui.screens.QiblaScreen
import com.example.ui.screens.SettingsAndCalendarScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppDestination
import com.example.viewmodel.PrayerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prayerViewModel: PrayerViewModel = viewModel()
            val uiState by prayerViewModel.uiState.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = uiState.settings.darkTheme) {
                MosulPrayerApp(
                    viewModel = prayerViewModel
                )
            }
        }
    }
}

@Composable
fun MosulPrayerApp(
    viewModel: PrayerViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.currentDestination != AppDestination.PRAYERS) {
        BackHandler {
            viewModel.navigateTo(AppDestination.PRAYERS)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationBarItem(
                    selected = uiState.currentDestination == AppDestination.PRAYERS,
                    onClick = { viewModel.navigateTo(AppDestination.PRAYERS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentDestination == AppDestination.PRAYERS) {
                                Icons.Filled.AccessTimeFilled
                            } else {
                                Icons.Outlined.AccessTime
                            },
                            contentDescription = "المواقيت"
                        )
                    },
                    label = { Text("المواقيت") },
                    modifier = Modifier.testTag("nav_tab_prayers")
                )

                NavigationBarItem(
                    selected = uiState.currentDestination == AppDestination.QIBLA,
                    onClick = { viewModel.navigateTo(AppDestination.QIBLA) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentDestination == AppDestination.QIBLA) {
                                Icons.Filled.Explore
                            } else {
                                Icons.Outlined.Explore
                            },
                            contentDescription = "القبلة والمساجد"
                        )
                    },
                    label = { Text("القبلة والمساجد") },
                    modifier = Modifier.testTag("nav_tab_qibla")
                )

                NavigationBarItem(
                    selected = uiState.currentDestination == AppDestination.ATHKAR,
                    onClick = { viewModel.navigateTo(AppDestination.ATHKAR) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentDestination == AppDestination.ATHKAR) {
                                Icons.Filled.AutoAwesome
                            } else {
                                Icons.Outlined.AutoAwesome
                            },
                            contentDescription = "الأذكار والورد"
                        )
                    },
                    label = { Text("الأذكار والورد") },
                    modifier = Modifier.testTag("nav_tab_athkar")
                )

                NavigationBarItem(
                    selected = uiState.currentDestination == AppDestination.CALENDAR_SETTINGS,
                    onClick = { viewModel.navigateTo(AppDestination.CALENDAR_SETTINGS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentDestination == AppDestination.CALENDAR_SETTINGS) {
                                Icons.Filled.CalendarMonth
                            } else {
                                Icons.Outlined.CalendarMonth
                            },
                            contentDescription = "التقويم والإعدادات"
                        )
                    },
                    label = { Text("التقويم والإعدادات") },
                    modifier = Modifier.testTag("nav_tab_settings")
                )
            }
        }
    ) { innerPadding ->
        when (uiState.currentDestination) {
            AppDestination.PRAYERS -> {
                PrayerHomeScreen(
                    uiState = uiState,
                    onPrevDay = { viewModel.changeDayOffset(-1) },
                    onNextDay = { viewModel.changeDayOffset(1) },
                    onResetToday = { viewModel.resetToToday() },
                    onToggleNotification = { viewModel.toggleNotification(it) },
                    onSelectPrayerDetail = { viewModel.selectPrayerForDetail(it) },
                    onSelectDistrict = { viewModel.selectDistrict(it) },
                    onSyncOnline = { viewModel.syncOnlineWithAladhan() },
                    onToggleTheme = { viewModel.toggleDarkTheme(it) },
                    onTriggerAdhanAlert = { viewModel.triggerAdhanAlert(it) },
                    onDismissAdhanAlert = { viewModel.dismissAdhanAlert() },
                    onNextMotivatorQuote = { viewModel.nextMotivatorQuote() },
                    onToggleDailyPrayerDone = { viewModel.toggleDailyPrayerCompleted(it) },
                    onToggleRawatibDone = { viewModel.toggleRawatibCompleted(it) },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestination.QIBLA -> {
                QiblaScreen(
                    uiState = uiState,
                    onManualAzimuthChange = { viewModel.setManualCompassAzimuth(it) },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestination.ATHKAR -> {
                AthkarAndTasbihScreen(
                    uiState = uiState,
                    onSelectCategory = { viewModel.selectAthkarCategory(it) },
                    onSelectWeekdayWird = { viewModel.selectWeekdayWird(it) },
                    onIncrementAthkar = { viewModel.incrementAthkar(it) },
                    onResetCategory = { viewModel.resetAthkarCategory(it) },
                    onSelectTasbihPhrase = { viewModel.selectTasbihPhrase(it) },
                    onIncrementTasbih = { viewModel.incrementTasbih() },
                    onResetTasbih = { viewModel.resetTasbihSession() },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestination.CALENDAR_SETTINGS -> {
                SettingsAndCalendarScreen(
                    uiState = uiState,
                    onSelectDistrict = { viewModel.selectDistrict(it) },
                    onSelectCalculationMethod = { viewModel.selectCalculationMethod(it) },
                    onSelectAsrMethod = { viewModel.selectAsrMethod(it) },
                    onAdjustPrayerOffset = { type, delta -> viewModel.adjustPrayerOffset(type, delta) },
                    onResetOffsets = { viewModel.resetAllPrayerOffsets() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
