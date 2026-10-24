package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PrayerTimeCalculator
import com.example.data.UserPreferencesRepository
import com.example.data.UserSettings
import com.example.model.AsrJuristicMethod
import com.example.model.AthkarItem
import com.example.model.CalculationMethodOption
import com.example.model.DEFAULT_ATHKAR_LIST
import com.example.model.DailyPrayerSchedule
import com.example.model.MosulDistrict
import com.example.model.PRAYER_MOTIVATORS
import com.example.model.PrayerType
import com.example.model.WEEKLY_RENEWED_WIRD
import com.example.sensor.AdhanAudioSynthesizer
import com.example.sensor.CompassReading
import com.example.sensor.QiblaCompassManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AppDestination {
    PRAYERS,
    QIBLA,
    ATHKAR,
    CALENDAR_SETTINGS
}

data class MonthlyDayRow(
    val dayOfMonth: Int,
    val dayNameArabic: String,
    val hijriDayFormatted: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val isToday: Boolean
)

data class TasbihPhrase(
    val arabic: String,
    val meaning: String,
    val target: Int
)

val TASBIH_PHRASES = listOf(
    TasbihPhrase("سُبْحَانَ اللهِ وَبِحَمْدِهِ", "كلمتان خفيفتان على اللسان ثقيلتان في الميزان", 33),
    TasbihPhrase("الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "تملأ الميزان حمداً وثناءً", 33),
    TasbihPhrase("اللهُ أَكْبَرُ كَبِيراً", "تكبير وتعظيم لله جل جلاله", 33),
    TasbihPhrase("أَسْتَغْفِرُ اللهَ وَأَتُوبُ إِلَيْهِ", "استغفار وتوبة ومغفرة للذنوب", 100),
    TasbihPhrase("اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ", "من صلى عليّ صلاة صلى الله عليه بها عشراً", 33),
    TasbihPhrase("لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ", "دعوة نبي الله يونس عليه السلام في نينوى", 33)
)

data class PrayerUiState(
    val currentDestination: AppDestination = AppDestination.PRAYERS,
    val settings: UserSettings = UserSettings(),
    val dayOffset: Int = 0,
    val schedule: DailyPrayerSchedule? = null,
    val selectedPrayerForDetail: PrayerType? = null,
    val qiblaBearingDegrees: Float = 191.8f,
    val distanceToKaabaKm: Int = 1667,
    val compassReading: CompassReading = CompassReading(191.8f, 3, false),
    val manualCompassAzimuth: Float? = null,
    val selectedAthkarCategory: String = "ورد اليوم المتجدد",
    val selectedWeekdayWirdIndex: Int = (Calendar.getInstance(PrayerTimeCalculator.BAGHDAD_TIME_ZONE).get(Calendar.DAY_OF_WEEK) - 1).coerceIn(0, 6),
    val athkarCounts: Map<String, Int> = emptyMap(),
    val selectedTasbihIndex: Int = 0,
    val currentTasbihSessionCount: Int = 0,
    val motivatorIndex: Int = 0,
    val completedDailyPrayers: Set<PrayerType> = emptySet(),
    val completedRawatibIds: Set<String> = emptySet(),
    val activeAdhanAlertPrayer: PrayerType? = null,
    val isSyncingOnline: Boolean = false,
    val syncStatusMessage: String? = null,
    val monthlyRows: List<MonthlyDayRow> = emptyList()
)

class PrayerViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = UserPreferencesRepository(application.applicationContext)
    private val compassManager = QiblaCompassManager(application.applicationContext)

    private val _currentDestination = MutableStateFlow(AppDestination.PRAYERS)
    private val _dayOffset = MutableStateFlow(0)
    private val _selectedPrayerForDetail = MutableStateFlow<PrayerType?>(null)
    private val _manualCompassAzimuth = MutableStateFlow<Float?>(null)
    private val _selectedAthkarCategory = MutableStateFlow("ورد اليوم المتجدد")
    private val _athkarCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val _selectedTasbihIndex = MutableStateFlow(0)
    private val _currentTasbihSessionCount = MutableStateFlow(0)
    private val _isSyncingOnline = MutableStateFlow(false)
    private val _syncStatusMessage = MutableStateFlow<String?>(null)
    private val _onlineTimingsCache = MutableStateFlow<Map<String, Map<PrayerType, Pair<Int, Int>>>>(emptyMap())
    private val _tickerSeconds = MutableStateFlow(0L)
    private var lastAutoTriggeredKey: String? = null

    private val settingsFlow = prefsRepo.userSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings()
    )

    private val compassFlow = compassManager.observeCompass().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CompassReading(azimuthDegrees = 191.8f, accuracy = 3, hasHardwareSensor = false)
    )

    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                _tickerSeconds.value = System.currentTimeMillis() / 1000L
                delay(1000L)
            }
        }

        viewModelScope.launch {
            combine(
                settingsFlow,
                _dayOffset,
                _tickerSeconds,
                _onlineTimingsCache,
                compassFlow
            ) { settings, offset, _, onlineCache, compass ->
                val targetCal = Calendar.getInstance(PrayerTimeCalculator.BAGHDAD_TIME_ZONE).apply {
                    add(Calendar.DAY_OF_YEAR, offset)
                }
                val cacheKey = "${settings.selectedDistrict.id}_${settings.calculationMethod.id}_${settings.asrMethod.id}_" +
                    "${targetCal.get(Calendar.YEAR)}_${targetCal.get(Calendar.MONTH)}_${targetCal.get(Calendar.DAY_OF_MONTH)}"

                val schedule = PrayerTimeCalculator.calculateScheduleForDate(
                    targetCalendar = targetCal,
                    district = settings.selectedDistrict,
                    method = settings.calculationMethod,
                    asrMethod = settings.asrMethod,
                    offsets = settings.prayerOffsets,
                    notifications = settings.prayerNotifications,
                    onlineTimingsOverride = onlineCache[cacheKey]
                )

                // Check automatic Adhan alert trigger at exact prayer minute
                val nowBaghdad = Calendar.getInstance(PrayerTimeCalculator.BAGHDAD_TIME_ZONE)
                if (offset == 0 && nowBaghdad.get(Calendar.SECOND) in 0..2) {
                    val nowMin = nowBaghdad.get(Calendar.HOUR_OF_DAY) * 60 + nowBaghdad.get(Calendar.MINUTE)
                    schedule.prayers.forEach { entry ->
                        if (entry.type != PrayerType.SUNRISE && entry.notificationEnabled && entry.totalMinutesFromMidnight == nowMin) {
                            val key = "${nowBaghdad.get(Calendar.DAY_OF_YEAR)}_${entry.type.name}"
                            if (lastAutoTriggeredKey != key) {
                                lastAutoTriggeredKey = key
                                triggerAdhanAlert(entry.type)
                            }
                        }
                    }
                }

                val qiblaBearing = PrayerTimeCalculator.calculateQiblaBearing(
                    settings.selectedDistrict.latitude,
                    settings.selectedDistrict.longitude
                )
                val distanceKm = PrayerTimeCalculator.calculateDistanceToKaabaKm(
                    settings.selectedDistrict.latitude,
                    settings.selectedDistrict.longitude
                )

                Triple(settings, schedule, Triple(qiblaBearing, distanceKm, compass))
            }.collect { (settings, schedule, qiblaData) ->
                val monthly = buildMonthlyTimetable(settings)
                _uiState.update { current ->
                    current.copy(
                        settings = settings,
                        schedule = schedule,
                        qiblaBearingDegrees = qiblaData.first,
                        distanceToKaabaKm = qiblaData.second,
                        compassReading = qiblaData.third,
                        monthlyRows = monthly
                    )
                }
            }
        }
    }

    fun triggerAdhanAlert(prayerType: PrayerType) {
        _uiState.update { it.copy(activeAdhanAlertPrayer = prayerType) }
        AdhanAudioSynthesizer.startAdhanMelody(viewModelScope)
    }

    fun dismissAdhanAlert() {
        AdhanAudioSynthesizer.stopAdhanMelody()
        _uiState.update { it.copy(activeAdhanAlertPrayer = null) }
    }

    fun nextMotivatorQuote() {
        _uiState.update { it.copy(motivatorIndex = (it.motivatorIndex + 1) % PRAYER_MOTIVATORS.size) }
    }

    fun toggleDailyPrayerCompleted(type: PrayerType) {
        _uiState.update { state ->
            val next = if (type in state.completedDailyPrayers) state.completedDailyPrayers - type else state.completedDailyPrayers + type
            state.copy(completedDailyPrayers = next)
        }
    }

    fun toggleRawatibCompleted(id: String) {
        _uiState.update { state ->
            val next = if (id in state.completedRawatibIds) state.completedRawatibIds - id else state.completedRawatibIds + id
            state.copy(completedRawatibIds = next)
        }
    }

    fun selectWeekdayWird(dayIndex: Int) {
        _uiState.update { it.copy(selectedWeekdayWirdIndex = dayIndex.coerceIn(0, 6)) }
    }

    fun navigateTo(destination: AppDestination) {
        _currentDestination.value = destination
        _uiState.update { it.copy(currentDestination = destination) }
    }

    fun changeDayOffset(delta: Int) {
        val next = (_dayOffset.value + delta).coerceIn(-60, 60)
        _dayOffset.value = next
        _uiState.update { it.copy(dayOffset = next) }
    }

    fun resetToToday() {
        _dayOffset.value = 0
        _uiState.update { it.copy(dayOffset = 0) }
    }

    fun selectPrayerForDetail(type: PrayerType?) {
        _selectedPrayerForDetail.value = type
        _uiState.update { it.copy(selectedPrayerForDetail = type) }
    }

    fun selectDistrict(district: MosulDistrict) {
        viewModelScope.launch { prefsRepo.selectDistrict(district) }
    }

    fun selectCalculationMethod(method: CalculationMethodOption) {
        viewModelScope.launch { prefsRepo.selectCalculationMethod(method) }
    }

    fun selectAsrMethod(asrMethod: AsrJuristicMethod) {
        viewModelScope.launch { prefsRepo.selectAsrMethod(asrMethod) }
    }

    fun adjustPrayerOffset(type: PrayerType, deltaMinutes: Int) {
        viewModelScope.launch {
            val current = _uiState.value.settings.prayerOffsets[type] ?: 0
            prefsRepo.updatePrayerOffset(type, current + deltaMinutes)
        }
    }

    fun resetAllPrayerOffsets() {
        viewModelScope.launch { prefsRepo.resetAllOffsets() }
    }

    fun toggleNotification(type: PrayerType) {
        viewModelScope.launch { prefsRepo.togglePrayerNotification(type) }
    }

    fun toggleDarkTheme(isDark: Boolean) {
        viewModelScope.launch { prefsRepo.toggleDarkTheme(isDark) }
    }

    fun setManualCompassAzimuth(azimuth: Float?) {
        _manualCompassAzimuth.value = azimuth
        _uiState.update { it.copy(manualCompassAzimuth = azimuth) }
    }

    fun selectAthkarCategory(category: String) {
        _selectedAthkarCategory.value = category
        _uiState.update { it.copy(selectedAthkarCategory = category) }
    }

    fun incrementAthkar(item: AthkarItem) {
        val current = _athkarCounts.value[item.id] ?: 0
        if (current < item.targetCount) {
            val updated = _athkarCounts.value.toMutableMap().apply { put(item.id, current + 1) }
            _athkarCounts.value = updated
            _uiState.update { it.copy(athkarCounts = updated) }
        }
    }

    fun resetAthkarCategory(category: String) {
        val idsInCategory = if (category == "ورد اليوم المتجدد") {
            WEEKLY_RENEWED_WIRD.flatMap { it.items }.map { it.id }.toSet()
        } else {
            DEFAULT_ATHKAR_LIST.filter { it.category == category }.map { it.id }.toSet()
        }
        val updated = _athkarCounts.value.filterKeys { it !in idsInCategory }
        _athkarCounts.value = updated
        _uiState.update { it.copy(athkarCounts = updated) }
    }

    fun selectTasbihPhrase(index: Int) {
        val safeIndex = index.coerceIn(0, TASBIH_PHRASES.lastIndex)
        _selectedTasbihIndex.value = safeIndex
        _currentTasbihSessionCount.value = 0
        _uiState.update { it.copy(selectedTasbihIndex = safeIndex, currentTasbihSessionCount = 0) }
    }

    fun incrementTasbih() {
        val next = _currentTasbihSessionCount.value + 1
        _currentTasbihSessionCount.value = next
        _uiState.update { it.copy(currentTasbihSessionCount = next) }
        viewModelScope.launch { prefsRepo.incrementTotalTasbih() }
    }

    fun resetTasbihSession() {
        _currentTasbihSessionCount.value = 0
        _uiState.update { it.copy(currentTasbihSessionCount = 0) }
    }

    fun syncOnlineWithAladhan() {
        if (_isSyncingOnline.value) return
        viewModelScope.launch {
            _isSyncingOnline.value = true
            _uiState.update { it.copy(isSyncingOnline = true, syncStatusMessage = null) }

            val settings = _uiState.value.settings
            val targetCal = Calendar.getInstance(PrayerTimeCalculator.BAGHDAD_TIME_ZONE).apply {
                add(Calendar.DAY_OF_YEAR, _dayOffset.value)
            }
            val year = targetCal.get(Calendar.YEAR)
            val month = targetCal.get(Calendar.MONTH) + 1
            val day = targetCal.get(Calendar.DAY_OF_MONTH)

            val result = PrayerTimeCalculator.fetchOnlineTimingsForMosul(
                year = year,
                month = month,
                day = day,
                district = settings.selectedDistrict,
                method = settings.calculationMethod,
                asrMethod = settings.asrMethod
            )

            val cacheKey = "${settings.selectedDistrict.id}_${settings.calculationMethod.id}_${settings.asrMethod.id}_" +
                "${targetCal.get(Calendar.YEAR)}_${targetCal.get(Calendar.MONTH)}_${targetCal.get(Calendar.DAY_OF_MONTH)}"

            if (result != null) {
                _onlineTimingsCache.update { it + (cacheKey to result) }
                _uiState.update {
                    it.copy(
                        isSyncingOnline = false,
                        syncStatusMessage = "تمت مطابقة المواقيت بنجاح مع الخادم الفلكي لمدينة الموصل"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isSyncingOnline = false,
                        syncStatusMessage = "يعمل التطبيق بالحساب الفلكي المحلي الدقيق لمدينة الموصل (بدون إنترنت)"
                    )
                }
            }
        }
    }

    private fun buildMonthlyTimetable(settings: UserSettings): List<MonthlyDayRow> {
        val nowCal = Calendar.getInstance(PrayerTimeCalculator.BAGHDAD_TIME_ZONE)
        val todayDay = nowCal.get(Calendar.DAY_OF_MONTH)
        val maxDay = nowCal.getActualMaximum(Calendar.DAY_OF_MONTH)

        return (1..maxDay).map { dayNum ->
            val cal = Calendar.getInstance(PrayerTimeCalculator.BAGHDAD_TIME_ZONE).apply {
                set(Calendar.DAY_OF_MONTH, dayNum)
            }
            val daily = PrayerTimeCalculator.calculateScheduleForDate(
                targetCalendar = cal,
                district = settings.selectedDistrict,
                method = settings.calculationMethod,
                asrMethod = settings.asrMethod,
                offsets = settings.prayerOffsets,
                notifications = settings.prayerNotifications
            )
            fun timeOf(type: PrayerType): String = daily.prayers.first { it.type == type }.formatted12h
            MonthlyDayRow(
                dayOfMonth = dayNum,
                dayNameArabic = daily.dayOfWeekArabic,
                hijriDayFormatted = "${PrayerTimeCalculator.toArabicDigits(daily.hijriDay)} ${daily.hijriMonthName}",
                fajr = timeOf(PrayerType.FAJR),
                sunrise = timeOf(PrayerType.SUNRISE),
                dhuhr = timeOf(PrayerType.DHUHR),
                asr = timeOf(PrayerType.ASR),
                maghrib = timeOf(PrayerType.MAGHRIB),
                isha = timeOf(PrayerType.ISHA),
                isToday = dayNum == todayDay
            )
        }
    }
}
