package com.example

import com.example.data.PrayerTimeCalculator
import com.example.model.AsrJuristicMethod
import com.example.model.CalculationMethodOption
import com.example.model.MOSUL_DISTRICTS
import com.example.model.PrayerType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {
  @Test
  fun mosulPrayerScheduleAndQibla_areAccurate() {
    val oldMosul = MOSUL_DISTRICTS.first()
    val cal = Calendar.getInstance(PrayerTimeCalculator.BAGHDAD_TIME_ZONE).apply {
      set(2026, Calendar.OCTOBER, 24, 10, 0, 0)
    }

    val schedule = PrayerTimeCalculator.calculateScheduleForDate(
      targetCalendar = cal,
      district = oldMosul,
      method = CalculationMethodOption.IRAQI_SUNNI_ENDOWMENT,
      asrMethod = AsrJuristicMethod.SHAFII,
      offsets = emptyMap(),
      notifications = emptyMap()
    )

    assertEquals(6, schedule.prayers.size)
    val mins = schedule.prayers.associate { it.type to it.totalMinutesFromMidnight }
    assertTrue(mins.getValue(PrayerType.FAJR) < mins.getValue(PrayerType.SUNRISE))
    assertTrue(mins.getValue(PrayerType.SUNRISE) < mins.getValue(PrayerType.DHUHR))
    assertTrue(mins.getValue(PrayerType.DHUHR) < mins.getValue(PrayerType.ASR))
    assertTrue(mins.getValue(PrayerType.ASR) < mins.getValue(PrayerType.MAGHRIB))
    assertTrue(mins.getValue(PrayerType.MAGHRIB) < mins.getValue(PrayerType.ISHA))

    val qiblaBearing = PrayerTimeCalculator.calculateQiblaBearing(oldMosul.latitude, oldMosul.longitude)
    assertTrue("Mosul Qibla bearing should be ~191.8 deg (got $qiblaBearing)", qiblaBearing in 191.0f..193.0f)
  }
}
