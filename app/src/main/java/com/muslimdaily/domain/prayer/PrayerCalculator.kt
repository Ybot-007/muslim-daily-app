package com.muslimdaily.domain.prayer

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

private const val KAABA_LAT = 21.422487
private const val KAABA_LON = 39.826206

data class PrayerCalculationMethod(
    val key: String,
    val title: String,
    val fajrAngle: Double,
    val ishaAngle: Double
)

data class PrayerTimes(
    val fajr: LocalTime,
    val dhuhr: LocalTime,
    val asr: LocalTime,
    val maghrib: LocalTime,
    val isha: LocalTime
) {
    fun asList(): List<Pair<String, LocalTime>> = listOf(
        "Fajr" to fajr,
        "Dhuhr" to dhuhr,
        "Asr" to asr,
        "Maghrib" to maghrib,
        "Isha" to isha
    )
}

data class NextPrayer(val name: String, val time: LocalDateTime)

object PrayerMethods {
    val all = listOf(
        PrayerCalculationMethod("mwl", "Muslim World League", 18.0, 17.0),
        PrayerCalculationMethod("egyptian", "Egyptian General Authority", 19.5, 17.5),
        PrayerCalculationMethod("karachi", "University of Islamic Sciences, Karachi", 18.0, 18.0),
        PrayerCalculationMethod("umm_al_qura", "Umm al-Qura, Makkah", 18.5, 18.5),
        PrayerCalculationMethod("dubai", "Dubai", 18.2, 18.2),
        PrayerCalculationMethod("moonsighting", "Moonsighting Committee", 18.0, 18.0)
    )

    fun byKey(key: String): PrayerCalculationMethod = all.firstOrNull { it.key == key } ?: all.first()
}

object PrayerCalculator {
    fun calculate(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        zoneId: ZoneId,
        method: PrayerCalculationMethod,
        asrShadowFactor: Double = 1.0
    ): PrayerTimes {
        val day = date.dayOfYear.toDouble()
        val gamma = 2.0 * PI / 365.0 * (day - 1.0)
        val equationOfTime = 229.18 * (
            0.000075 + 0.001868 * cos(gamma) - 0.032077 * sin(gamma) -
                0.014615 * cos(2.0 * gamma) - 0.040849 * sin(2.0 * gamma)
            )
        val declination = 0.006918 - 0.399912 * cos(gamma) + 0.070257 * sin(gamma) -
            0.006758 * cos(2.0 * gamma) + 0.000907 * sin(2.0 * gamma) -
            0.002697 * cos(3.0 * gamma) + 0.00148 * sin(3.0 * gamma)
        val offsetHours = zoneId.rules.getOffset(date.atStartOfDay()).totalSeconds / 3600.0
        val noon = 12.0 + offsetHours - longitude / 15.0 - equationOfTime / 60.0
        val latRad = latitude.toRadians()

        fun hourAngleForAltitude(altitudeDegrees: Double): Double {
            val altitude = altitudeDegrees.toRadians()
            val value = ((sin(altitude) - sin(latRad) * sin(declination)) / (cos(latRad) * cos(declination))).coerceIn(-1.0, 1.0)
            return acos(value).toDegrees() / 15.0
        }

        val sunriseAngle = hourAngleForAltitude(-0.833)
        val fajrAngle = hourAngleForAltitude(-method.fajrAngle)
        val ishaAngle = hourAngleForAltitude(-method.ishaAngle)
        val asrAltitude = -atan(1.0 / (asrShadowFactor + tan(abs(latRad - declination)))).toDegrees()
        val asrAngle = hourAngleForAltitude(asrAltitude)

        return PrayerTimes(
            fajr = decimalToTime(noon - fajrAngle),
            dhuhr = decimalToTime(noon + 2.0 / 60.0),
            asr = decimalToTime(noon + asrAngle),
            maghrib = decimalToTime(noon + sunriseAngle),
            isha = decimalToTime(noon + ishaAngle)
        )
    }

    fun nextPrayer(now: LocalDateTime, today: PrayerTimes, tomorrow: PrayerTimes): NextPrayer {
        today.asList().forEach { (name, time) ->
            val candidate = LocalDateTime.of(now.toLocalDate(), time)
            if (candidate.isAfter(now)) return NextPrayer(name, candidate)
        }
        return NextPrayer("Fajr", LocalDateTime.of(now.toLocalDate().plusDays(1), tomorrow.fajr))
    }

    private fun decimalToTime(hours: Double): LocalTime {
        val wrapped = ((hours % 24.0) + 24.0) % 24.0
        val hour = floor(wrapped).toInt()
        val minuteFloat = (wrapped - hour) * 60.0
        val minute = floor(minuteFloat).toInt()
        val second = (((minuteFloat - minute) * 60.0).toInt()).coerceIn(0, 59)
        return LocalTime.of(hour.coerceIn(0, 23), minute.coerceIn(0, 59), second)
    }
}

fun qiblaBearing(latitude: Double, longitude: Double): Double {
    val lat1 = latitude.toRadians()
    val lat2 = KAABA_LAT.toRadians()
    val deltaLon = (KAABA_LON - longitude).toRadians()
    val y = sin(deltaLon)
    val x = cos(lat1) * tan(lat2) - sin(lat1) * cos(deltaLon)
    return (atan2(y, x).toDegrees() + 360.0) % 360.0
}

private fun Double.toRadians() = this * PI / 180.0
private fun Double.toDegrees() = this * 180.0 / PI
