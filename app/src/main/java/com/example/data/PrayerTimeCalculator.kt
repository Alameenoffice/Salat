package com.example.data

import com.example.model.AsrJuristicMethod
import com.example.model.CalculationMethodOption
import com.example.model.DailyPrayerSchedule
import com.example.model.MosulDistrict
import com.example.model.PrayerTimeEntry
import com.example.model.PrayerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Astronomical & Live-Sync Prayer Time Calculator calibrated for Mosul, Iraq (Asia/Baghdad UTC+3).
 * Uses high-precision solar declination & equation-of-time formulas with horizon dip correction
 * for Mosul's elevation (~223m-410m), plus optional Aladhan API live sync.
 */
object PrayerTimeCalculator {

    val BAGHDAD_TIME_ZONE: TimeZone = TimeZone.getTimeZone("Asia/Baghdad")
    private const val KAABA_LAT = 21.422487
    private const val KAABA_LNG = 39.826206

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .build()
    }

    private val ARABIC_DAYS = arrayOf(
        "", "الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت"
    )

    private val ARABIC_GREGORIAN_MONTHS = arrayOf(
        "كانون الثاني (يناير)", "شباط (فبراير)", "آذار (مارس)", "نيسان (أبريل)",
        "أيار (مايو)", "حزيران (يونيو)", "تموز (يوليو)", "آب (أغسطس)",
        "أيلول (سبتمبر)", "تشرين الأول (أكتوبر)", "تشرين الثاني (نوفمبر)", "كانون الأول (ديسمبر)"
    )

    private val HIJRI_MONTHS = arrayOf(
        "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
    )

    fun calculateScheduleForDate(
        targetCalendar: Calendar,
        district: MosulDistrict,
        method: CalculationMethodOption,
        asrMethod: AsrJuristicMethod,
        offsets: Map<PrayerType, Int>,
        notifications: Map<PrayerType, Boolean>,
        onlineTimingsOverride: Map<PrayerType, Pair<Int, Int>>? = null
    ): DailyPrayerSchedule {
        val year = targetCalendar.get(Calendar.YEAR)
        val month = targetCalendar.get(Calendar.MONTH) + 1 // 1..12
        val day = targetCalendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = targetCalendar.get(Calendar.DAY_OF_WEEK)

        val rawMinutesMap = if (onlineTimingsOverride != null && onlineTimingsOverride.size == 6) {
            onlineTimingsOverride.mapValues { (_, pair) -> pair.first * 60 + pair.second }
        } else {
            computeAstronomicalMinutes(
                year = year,
                month = month,
                day = day,
                latitude = district.latitude,
                longitude = district.longitude,
                elevation = district.elevationMeters,
                method = method,
                asrMethod = asrMethod
            )
        }

        // Current Mosul local time for countdown & active prayer highlighting
        val nowBaghdad = Calendar.getInstance(BAGHDAD_TIME_ZONE)
        val nowMinutes = nowBaghdad.get(Calendar.HOUR_OF_DAY) * 60 + nowBaghdad.get(Calendar.MINUTE)
        val nowSecondsOfDay = nowMinutes * 60L + nowBaghdad.get(Calendar.SECOND)

        val adjustedEntries = PrayerType.entries.map { type ->
            val baseMin = rawMinutesMap[type] ?: 0
            val offset = offsets[type] ?: 0
            val finalMin = (baseMin + offset + 1440) % 1440
            val h24 = finalMin / 60
            val m = finalMin % 60
            val (formatted12, period) = formatTime12Hour(h24, m)
            PrayerTimeEntry(
                type = type,
                hour24 = h24,
                minute = m,
                formatted12h = formatted12,
                periodLabel = period,
                totalMinutesFromMidnight = finalMin,
                offsetMinutes = offset,
                notificationEnabled = notifications[type] ?: true
            )
        }

        // Determine next prayer and remaining seconds
        val nextPrayerEntry = adjustedEntries.firstOrNull { entry ->
            entry.totalMinutesFromMidnight * 60L > nowSecondsOfDay
        } ?: adjustedEntries.first()

        val previousPrayerEntry = adjustedEntries.lastOrNull { entry ->
            entry.totalMinutesFromMidnight * 60L <= nowSecondsOfDay
        } ?: adjustedEntries.last()

        val nextPrayerSeconds = nextPrayerEntry.totalMinutesFromMidnight * 60L
        val secondsUntilNext = if (nextPrayerSeconds > nowSecondsOfDay) {
            nextPrayerSeconds - nowSecondsOfDay
        } else {
            (86400L - nowSecondsOfDay) + nextPrayerSeconds
        }

        val prevPrayerSeconds = previousPrayerEntry.totalMinutesFromMidnight * 60L
        val totalIntervalSeconds = if (nextPrayerSeconds > prevPrayerSeconds) {
            (nextPrayerSeconds - prevPrayerSeconds).coerceAtLeast(60L)
        } else {
            ((86400L - prevPrayerSeconds) + nextPrayerSeconds).coerceAtLeast(60L)
        }
        val elapsedSincePrev = (totalIntervalSeconds - secondsUntilNext).coerceAtLeast(0L)
        val progress = (elapsedSincePrev.toFloat() / totalIntervalSeconds.toFloat()).coerceIn(0f, 1f)

        val finalEntriesWithStatus = adjustedEntries.map { entry ->
            entry.copy(
                isNextPrayer = entry.type == nextPrayerEntry.type,
                isPassed = entry.totalMinutesFromMidnight * 60L <= nowSecondsOfDay
            )
        }

        // Compute Imsak (10 minutes before Fajr), Midnight, and Last Third of Night
        val fajrMin = finalEntriesWithStatus.first { it.type == PrayerType.FAJR }.totalMinutesFromMidnight
        val maghribMin = finalEntriesWithStatus.first { it.type == PrayerType.MAGHRIB }.totalMinutesFromMidnight
        val imsakMin = (fajrMin - 10 + 1440) % 1440
        val nightDurationMin = ((fajrMin + 1440) - maghribMin) % 1440
        val midnightMin = (maghribMin + nightDurationMin / 2) % 1440
        val lastThirdMin = (maghribMin + (nightDurationMin * 2) / 3) % 1440

        val (imsakStr, imsakP) = formatTime12Hour(imsakMin / 60, imsakMin % 60)
        val (midnightStr, midP) = formatTime12Hour(midnightMin / 60, midnightMin % 60)
        val (lastThirdStr, thirdP) = formatTime12Hour(lastThirdMin / 60, lastThirdMin % 60)

        val (hDay, hMonthIndex, hYear) = gregorianToHijri(year, month, day)
        val hijriMonthName = HIJRI_MONTHS[hMonthIndex.coerceIn(0, 11)]
        val hijriFormatted = "${toArabicDigits(hDay)} $hijriMonthName ${toArabicDigits(hYear)} هـ"
        val gregorianFormatted = "${toArabicDigits(day)} ${ARABIC_GREGORIAN_MONTHS[(month - 1).coerceIn(0, 11)]} ${toArabicDigits(year)} م"
        val dayName = ARABIC_DAYS.getOrElse(dayOfWeek) { "اليوم" }

        return DailyPrayerSchedule(
            gregorianDateFormatted = gregorianFormatted,
            hijriDateFormatted = hijriFormatted,
            hijriMonthName = hijriMonthName,
            hijriDay = hDay,
            hijriYear = hYear,
            dayOfWeekArabic = dayName,
            district = district,
            prayers = finalEntriesWithStatus,
            imsakFormatted = "$imsakStr $imsakP",
            midnightFormatted = "$midnightStr $midP",
            lastThirdFormatted = "$lastThirdStr $thirdP",
            nextPrayer = nextPrayerEntry.type,
            secondsUntilNextPrayer = secondsUntilNext,
            progressToNextPrayer = progress,
            isSyncedOnline = onlineTimingsOverride != null
        )
    }

    /**
     * Fetches official timings from Aladhan API for Mosul coordinates if network is available.
     */
    suspend fun fetchOnlineTimingsForMosul(
        year: Int,
        month: Int,
        day: Int,
        district: MosulDistrict,
        method: CalculationMethodOption,
        asrMethod: AsrJuristicMethod
    ): Map<PrayerType, Pair<Int, Int>>? = withContext(Dispatchers.IO) {
        try {
            val dateStr = String.format("%02d-%02d-%04d", day, month, year)
            val apiMethod = if (method == CalculationMethodOption.IRAQI_SUNNI_ENDOWMENT) 3 else method.id
            val url = "https://api.aladhan.com/v1/timings/$dateStr" +
                "?latitude=${district.latitude}&longitude=${district.longitude}" +
                "&method=$apiMethod&school=${asrMethod.id}&timezonestring=Asia/Baghdad"

            val request = Request.Builder().url(url).get().build()
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body?.string() ?: return@withContext null
                val root = JSONObject(body)
                val timings = root.optJSONObject("data")?.optJSONObject("timings") ?: return@withContext null

                fun parseHHMM(key: String): Pair<Int, Int>? {
                    val raw = timings.optString(key, "").substringBefore(" ").trim()
                    val parts = raw.split(":")
                    if (parts.size < 2) return null
                    val h = parts[0].toIntOrNull() ?: return null
                    val m = parts[1].toIntOrNull() ?: return null
                    return h to m
                }

                val fajr = parseHHMM("Fajr") ?: return@withContext null
                val sunrise = parseHHMM("Sunrise") ?: return@withContext null
                val dhuhr = parseHHMM("Dhuhr") ?: return@withContext null
                val asr = parseHHMM("Asr") ?: return@withContext null
                val maghrib = parseHHMM("Maghrib") ?: return@withContext null
                val isha = parseHHMM("Isha") ?: return@withContext null

                mapOf(
                    PrayerType.FAJR to fajr,
                    PrayerType.SUNRISE to sunrise,
                    PrayerType.DHUHR to dhuhr,
                    PrayerType.ASR to asr,
                    PrayerType.MAGHRIB to maghrib,
                    PrayerType.ISHA to isha
                )
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Computes exact Qibla bearing in degrees clockwise from True North (for Mosul ~193.64°).
     */
    fun calculateQiblaBearing(latitude: Double, longitude: Double): Float {
        val latRad = Math.toRadians(latitude)
        val lngRad = Math.toRadians(longitude)
        val kaabaLatRad = Math.toRadians(KAABA_LAT)
        val kaabaLngRad = Math.toRadians(KAABA_LNG)

        val dLng = kaabaLngRad - lngRad
        val y = sin(dLng)
        val x = cos(latRad) * tan(kaabaLatRad) - sin(latRad) * cos(dLng)
        val bearingDeg = (Math.toDegrees(atan2(y, x)) + 360.0) % 360.0
        return bearingDeg.toFloat()
    }

    /**
     * Computes great-circle distance in kilometers from Mosul district to Makkah (~1,667 km).
     */
    fun calculateDistanceToKaabaKm(latitude: Double, longitude: Double): Int {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(KAABA_LAT - latitude)
        val dLng = Math.toRadians(KAABA_LNG - longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(latitude)) * cos(Math.toRadians(KAABA_LAT)) *
            sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadiusKm * c).roundToInt()
    }

    private fun computeAstronomicalMinutes(
        year: Int,
        month: Int,
        day: Int,
        latitude: Double,
        longitude: Double,
        elevation: Double,
        method: CalculationMethodOption,
        asrMethod: AsrJuristicMethod
    ): Map<PrayerType, Int> {
        val tzHours = 3.0 // Iraq Standard Time UTC+3 (no DST in Iraq)
        val jd = gregorianToJulianDay(year, month, day) - longitude / (15.0 * 24.0)
        val d = jd - 2451545.0

        // Solar coordinates
        val g = normalizeAngle(357.529 + 0.98560028 * d)
        val q = normalizeAngle(280.459 + 0.98564736 * d)
        val l = normalizeAngle(q + 1.915 * dsin(g) + 0.020 * dsin(2 * g))
        val e = 23.439 - 0.00000036 * d
        val ra = datan2(dcos(e) * dsin(l), dcos(l)) / 15.0
        val raNormalized = (ra + 24.0) % 24.0
        val decl = dasin(dsin(e) * dsin(l))
        val eqt = q / 15.0 - raNormalized
        val eqTime = when {
            eqt > 12.0 -> eqt - 24.0
            eqt < -12.0 -> eqt + 24.0
            else -> eqt
        }

        // Solar Noon (Dhuhr) with 2-minute precaution after meridian transit (احتياط الزوال في الموصل)
        val solarNoonHours = 12.0 + tzHours - (longitude / 15.0) - eqTime
        val dhuhrHours = solarNoonHours + (2.0 / 60.0)

        // Horizon altitude angle for Sunrise/Sunset accounting for atmospheric refraction (0.833°) and Mosul elevation
        val horizonAltitude = 0.833 + 0.0347 * sqrt(elevation.coerceAtLeast(0.0))

        fun hourAngleForZenith(angleBelowHorizon: Double): Double {
            val numerator = -dsin(angleBelowHorizon) - dsin(latitude) * dsin(decl)
            val denominator = dcos(latitude) * dcos(decl)
            val cosH = (numerator / denominator).coerceIn(-1.0, 1.0)
            return dacos(cosH) / 15.0
        }

        val sunriseDiff = hourAngleForZenith(horizonAltitude)
        val sunriseHours = solarNoonHours - sunriseDiff
        val maghribHours = solarNoonHours + sunriseDiff + (2.0 / 60.0) // 2 min precaution for sunset

        val fajrDiff = hourAngleForZenith(method.fajrAngle)
        val fajrHours = solarNoonHours - fajrDiff

        // Asr shadow angle
        val shadowRatio = asrMethod.shadowFactor.toDouble() + dtan(abs(latitude - decl))
        val asrAltitude = datan(1.0 / shadowRatio)
        val asrNum = dsin(asrAltitude) - dsin(latitude) * dsin(decl)
        val asrDen = dcos(latitude) * dcos(decl)
        val asrDiff = dacos((asrNum / asrDen).coerceIn(-1.0, 1.0)) / 15.0
        val asrHours = solarNoonHours + asrDiff

        val ishaHours = if (method.ishaFixedMinutes > 0) {
            maghribHours + (method.ishaFixedMinutes / 60.0)
        } else {
            val ishaDiff = hourAngleForZenith(method.ishaAngle)
            solarNoonHours + ishaDiff
        }

        fun toMinutes(hours: Double): Int {
            val normalized = ((hours % 24.0) + 24.0) % 24.0
            return (normalized * 60.0).roundToInt() % 1440
        }

        return mapOf(
            PrayerType.FAJR to toMinutes(fajrHours),
            PrayerType.SUNRISE to toMinutes(sunriseHours),
            PrayerType.DHUHR to toMinutes(dhuhrHours),
            PrayerType.ASR to toMinutes(asrHours),
            PrayerType.MAGHRIB to toMinutes(maghribHours),
            PrayerType.ISHA to toMinutes(ishaHours)
        )
    }

    private fun gregorianToJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    /**
     * Converts Gregorian date to Hijri (Day, MonthIndex 0..11, Year).
     */
    fun gregorianToHijri(year: Int, month: Int, day: Int): Triple<Int, Int, Int> {
        val jd = gregorianToJulianDay(year, month, day) + 0.5
        val lInit = jd.toLong() - 1948440L + 10632L
        val n = (lInit - 1L) / 10631L
        val l1 = lInit - 10631L * n + 354L
        val j = ((10985L - l1) / 5316L) * ((50L * l1) / 17719L) + (l1 / 5670L) * ((43L * l1) / 15238L)
        val l2 = l1 - ((30L - j) / 15L) * ((17719L * j) / 50L) - (j / 16L) * ((15238L * j) / 43L) + 29L
        val hMonth = ((24L * l2) / 709L).toInt()
        val hDay = (l2 - (709L * hMonth) / 24L).toInt()
        val hYear = (30L * n + j - 30L).toInt()
        return Triple(hDay.coerceIn(1, 30), (hMonth - 1).coerceIn(0, 11), hYear)
    }

    fun formatTime12Hour(hour24: Int, minute: Int): Pair<String, String> {
        val period = if (hour24 < 12) "ص" else "م"
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val timeStr = String.format("%02d:%02d", hour12, minute)
        return toArabicDigits(timeStr) to period
    }

    fun formatCountdownArabic(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return toArabicDigits(String.format("%02d:%02d:%02d", h, m, s))
    }

    fun toArabicDigits(input: Any): String {
        val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return input.toString().map { ch ->
            if (ch in '0'..'9') arabicDigits[ch - '0'] else ch
        }.joinToString("")
    }

    private fun normalizeAngle(angle: Double): Double {
        val mod = angle % 360.0
        return if (mod < 0) mod + 360.0 else mod
    }

    private fun dsin(deg: Double) = sin(Math.toRadians(deg))
    private fun dcos(deg: Double) = cos(Math.toRadians(deg))
    private fun dtan(deg: Double) = tan(Math.toRadians(deg))
    private fun dasin(x: Double) = Math.toDegrees(asin(x))
    private fun dacos(x: Double) = Math.toDegrees(acos(x))
    private fun datan(x: Double) = Math.toDegrees(atan(x))
    private fun datan2(y: Double, x: Double) = Math.toDegrees(atan2(y, x))
}
