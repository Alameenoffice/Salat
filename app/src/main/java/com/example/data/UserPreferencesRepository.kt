package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.AsrJuristicMethod
import com.example.model.CalculationMethodOption
import com.example.model.MOSUL_DISTRICTS
import com.example.model.MosulDistrict
import com.example.model.PrayerType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "mosul_prayer_preferences")

data class UserSettings(
    val selectedDistrict: MosulDistrict = MOSUL_DISTRICTS.first(),
    val calculationMethod: CalculationMethodOption = CalculationMethodOption.IRAQI_SUNNI_ENDOWMENT,
    val asrMethod: AsrJuristicMethod = AsrJuristicMethod.SHAFII,
    val prayerOffsets: Map<PrayerType, Int> = PrayerType.entries.associateWith { 0 },
    val prayerNotifications: Map<PrayerType, Boolean> = PrayerType.entries.associateWith { true },
    val darkTheme: Boolean = true,
    val totalTasbihCount: Int = 0
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val DISTRICT_ID = stringPreferencesKey("district_id")
        val CALC_METHOD_ID = intPreferencesKey("calc_method_id")
        val ASR_METHOD_ID = intPreferencesKey("asr_method_id")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val TOTAL_TASBIH = intPreferencesKey("total_tasbih")

        fun offsetKey(type: PrayerType) = intPreferencesKey("offset_${type.name}")
        fun notifKey(type: PrayerType) = booleanPreferencesKey("notif_${type.name}")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        val districtId = prefs[Keys.DISTRICT_ID] ?: MOSUL_DISTRICTS.first().id
        val district = MOSUL_DISTRICTS.find { it.id == districtId } ?: MOSUL_DISTRICTS.first()

        val methodId = prefs[Keys.CALC_METHOD_ID] ?: CalculationMethodOption.IRAQI_SUNNI_ENDOWMENT.id
        val calcMethod = CalculationMethodOption.entries.find { it.id == methodId }
            ?: CalculationMethodOption.IRAQI_SUNNI_ENDOWMENT

        val asrId = prefs[Keys.ASR_METHOD_ID] ?: AsrJuristicMethod.SHAFII.id
        val asrMethod = AsrJuristicMethod.entries.find { it.id == asrId } ?: AsrJuristicMethod.SHAFII

        val offsets = PrayerType.entries.associateWith { type ->
            prefs[Keys.offsetKey(type)] ?: 0
        }

        val notifs = PrayerType.entries.associateWith { type ->
            prefs[Keys.notifKey(type)] ?: (type != PrayerType.SUNRISE)
        }

        val darkTheme = prefs[Keys.DARK_THEME] ?: true
        val totalTasbih = prefs[Keys.TOTAL_TASBIH] ?: 0

        UserSettings(
            selectedDistrict = district,
            calculationMethod = calcMethod,
            asrMethod = asrMethod,
            prayerOffsets = offsets,
            prayerNotifications = notifs,
            darkTheme = darkTheme,
            totalTasbihCount = totalTasbih
        )
    }

    suspend fun selectDistrict(district: MosulDistrict) {
        context.dataStore.edit { it[Keys.DISTRICT_ID] = district.id }
    }

    suspend fun selectCalculationMethod(method: CalculationMethodOption) {
        context.dataStore.edit { it[Keys.CALC_METHOD_ID] = method.id }
    }

    suspend fun selectAsrMethod(asrMethod: AsrJuristicMethod) {
        context.dataStore.edit { it[Keys.ASR_METHOD_ID] = asrMethod.id }
    }

    suspend fun updatePrayerOffset(type: PrayerType, minutes: Int) {
        context.dataStore.edit { it[Keys.offsetKey(type)] = minutes.coerceIn(-30, 30) }
    }

    suspend fun togglePrayerNotification(type: PrayerType) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.notifKey(type)] ?: (type != PrayerType.SUNRISE)
            prefs[Keys.notifKey(type)] = !current
        }
    }

    suspend fun toggleDarkTheme(isDark: Boolean) {
        context.dataStore.edit { it[Keys.DARK_THEME] = isDark }
    }

    suspend fun incrementTotalTasbih() {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.TOTAL_TASBIH] ?: 0
            prefs[Keys.TOTAL_TASBIH] = current + 1
        }
    }

    suspend fun resetAllOffsets() {
        context.dataStore.edit { prefs ->
            PrayerType.entries.forEach { type ->
                prefs[Keys.offsetKey(type)] = 0
            }
        }
    }
}
