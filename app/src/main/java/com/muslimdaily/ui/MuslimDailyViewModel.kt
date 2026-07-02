package com.muslimdaily.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.muslimdaily.data.local.AppDatabase
import com.muslimdaily.data.local.AyahEntity
import com.muslimdaily.data.local.AzkarEntity
import com.muslimdaily.data.local.HadithEntity
import com.muslimdaily.data.local.SurahEntity
import com.muslimdaily.data.preferences.AppLanguage
import com.muslimdaily.data.preferences.LanguagePreference
import com.muslimdaily.data.repository.ContentRepository
import com.muslimdaily.domain.device.LocationProvider
import com.muslimdaily.domain.device.ManualCities
import com.muslimdaily.domain.device.UserLocation
import com.muslimdaily.domain.prayer.NextPrayer
import com.muslimdaily.domain.prayer.PrayerCalculationMethod
import com.muslimdaily.domain.prayer.PrayerCalculator
import com.muslimdaily.domain.prayer.PrayerMethods
import com.muslimdaily.domain.prayer.PrayerNotificationWorker
import com.muslimdaily.domain.prayer.PrayerTimes
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val defaultLocation = UserLocation(34.5553, 69.2075, "Kabul, Afghanistan")

data class PrayerUiState(
    val location: UserLocation = defaultLocation,
    val method: PrayerCalculationMethod = PrayerMethods.all.first(),
    val times: PrayerTimes = PrayerCalculator.calculate(LocalDate.now(), defaultLocation.latitude, defaultLocation.longitude, ZoneId.systemDefault(), PrayerMethods.all.first()),
    val nextPrayer: NextPrayer = NextPrayer("Fajr", LocalDateTime.now()),
    val countdown: String = "--:--:--"
)

@OptIn(ExperimentalCoroutinesApi::class)
class MuslimDailyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.get(application)
    private val repository = ContentRepository(application, database)
    private val languagePreference = LanguagePreference(application)
    private val locationProvider = LocationProvider(application)
    private val selectedSurah = MutableStateFlow(1)
    private val selectedLocation = MutableStateFlow(defaultLocation)
    private val selectedMethodKey = MutableStateFlow(PrayerMethods.all.first().key)
    private val now = MutableStateFlow(LocalDateTime.now())

    val language = languagePreference.language.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppLanguage.English)
    val azkaar = repository.azkaar.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val favoriteAzkaar = repository.favoriteAzkaar.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val surahs = repository.surahs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val favoriteAyahs = repository.favoriteAyahs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val bookmark = repository.bookmark.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    val allahNames = repository.names.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val favoriteNames = repository.favoriteNames.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val hadiths = repository.hadiths.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val favoriteHadiths = repository.favoriteHadiths.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val lastReadHadith = repository.lastReadHadith.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    val ayahs = selectedSurah.flatMapLatest { repository.ayahsFor(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedSurahNumber = selectedSurah.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 1)

    val prayerState = combine(selectedLocation, selectedMethodKey, now) { location, key, moment ->
        val method = PrayerMethods.byKey(key)
        val zone = ZoneId.systemDefault()
        val today = PrayerCalculator.calculate(moment.toLocalDate(), location.latitude, location.longitude, zone, method)
        val tomorrow = PrayerCalculator.calculate(moment.toLocalDate().plusDays(1), location.latitude, location.longitude, zone, method)
        val next = PrayerCalculator.nextPrayer(moment, today, tomorrow)
        PrayerUiState(
            location = location,
            method = method,
            times = today,
            nextPrayer = next,
            countdown = formatCountdown(Duration.between(moment, next.time))
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PrayerUiState())

    init {
        viewModelScope.launch { repository.seed() }
        viewModelScope.launch {
            while (true) {
                now.value = LocalDateTime.now()
                delay(1_000)
            }
        }
    }

    fun setLanguage(language: AppLanguage) = viewModelScope.launch {
        languagePreference.setLanguage(language)
    }

    fun selectSurah(number: Int) { selectedSurah.value = number }
    fun selectMethod(key: String) { selectedMethodKey.value = key }
    fun selectLocation(location: UserLocation) { selectedLocation.value = location }

    fun useDeviceLocation() = viewModelScope.launch {
        locationProvider.lastKnownLocation()?.let { selectedLocation.value = it }
    }

    fun toggleAzkarFavorite(item: AzkarEntity) = viewModelScope.launch {
        repository.setAzkarFavorite(item.id, !item.isFavorite)
    }

    fun toggleAyahFavorite(item: AyahEntity) = viewModelScope.launch {
        repository.setAyahFavorite(item.id, !item.isFavorite)
    }

    fun bookmarkAyah(item: AyahEntity) = viewModelScope.launch {
        repository.bookmarkAyah(item.id)
    }

    fun toggleNameFavorite(id: Int, favorite: Boolean) = viewModelScope.launch {
        repository.setNameFavorite(id, !favorite)
    }

    fun toggleHadithFavorite(item: HadithEntity) = viewModelScope.launch {
        repository.setHadithFavorite(item.id, !item.isFavorite)
    }

    fun markHadithLastRead(item: HadithEntity) = viewModelScope.launch {
        repository.markHadithLastRead(item.id)
    }

    fun schedulePrayerReminders(minutesBefore: Long = 10) {
        val state = prayerState.value
        val date = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        state.times.asList().forEach { (name, time) ->
            PrayerNotificationWorker.schedule(
                getApplication(),
                name,
                LocalDateTime.of(date, time).minusMinutes(minutesBefore),
                time.format(formatter)
            )
        }
    }

    private fun formatCountdown(duration: Duration): String {
        val seconds = duration.seconds.coerceAtLeast(0)
        val hours = seconds / 3600
        val minutes = seconds % 3600 / 60
        val secs = seconds % 60
        return "%02d:%02d:%02d".format(hours, minutes, secs)
    }

    companion object {
        val manualCities = ManualCities.cities
        val methods = PrayerMethods.all
    }
}
