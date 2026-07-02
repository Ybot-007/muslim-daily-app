# Muslim Daily

Muslim Daily is a Kotlin + Jetpack Compose Android app scaffold with offline Islamic content, prayer times, Qibla compass support, favorites, bookmarks, dark mode, and WorkManager-based prayer reminders.

## Main Features

- Home: today's prayer times, next prayer countdown, daily ayah, and daily dhikr.
- Azkaar: Morning, Evening, After prayer, and Sleep categories with tasbeeh counter and favorites.
- Prayer: Fajr, Dhuhr, Asr, Maghrib, Isha, manual city selection, device location support, calculation methods, and reminder scheduling.
- Quran: all 114 surahs listed offline, Arabic sample ayahs, English sample translation, surah search, ayah favorites, and last-read bookmark.
- More: Qibla compass using phone sensors and location, all 99 Names of Allah with favorites, offline Hadith, and an English/Persian language selector.
- Hadith: Sahih al-Bukhari and Sahih Muslim stored locally, organized by book/chapter, searchable, favoriteable, last-read aware, copyable, and shareable.
- Language: DataStore saves English or Persian after restart; Persian switches the Compose UI to RTL while Arabic Quran/Azkaar text remains unchanged.

## Project Structure

~~~text
app/src/main/java/com/muslimdaily/
  MainActivity.kt
  MuslimDailyApplication.kt
  data/local/
    AppDatabase.kt
    Daos.kt
    Entities.kt
    SeedData.kt
  data/preferences/
    LanguagePreference.kt
  data/repository/
    ContentRepository.kt
  domain/device/
    LocationProvider.kt
  domain/prayer/
    PrayerCalculator.kt
    PrayerNotificationWorker.kt
  ui/
    MuslimDailyApp.kt
    MuslimDailyViewModel.kt
    theme/Theme.kt
app/src/main/assets/
  azkaar.json
  quran_sample.json
  quran_fa.json
  names_of_allah.json
  names_of_allah_fa.json
  azkaar_fa.json
  hadith_bukhari.json
  hadith_muslim.json
  hadith_fa.json
gradle/libs.versions.toml
~~~

## Setup

1. Open this folder in Android Studio.
2. Let Gradle sync the project.
3. Use JDK 17 or newer.
4. Run the app configuration on an emulator or Android phone.
5. Grant location permission for Qibla/prayer location and notification permission for prayer reminders.

The Gradle setup uses Kotlin, Jetpack Compose, Room, DataStore Preferences, WorkManager, Lifecycle, Navigation Compose, Material 3, and AndroidX Splash Screen. No paid APIs are used.

## Offline Data

- azkaar.json contains sample azkaar for all requested categories in English.
- azkaar_fa.json contains Persian meanings for the azkaar, keyed by azkar id.
- names_of_allah.json contains all 99 Names with Arabic, transliteration, English meaning, and English explanation.
- names_of_allah_fa.json contains Persian meaning and explanation for all 99 Names, keyed by id.
- quran_sample.json contains sample Arabic ayahs and English translations.
- quran_fa.json contains Persian Quran translations, keyed by surah:ayah id. The app already lists all 114 surahs; replace or expand both Quran JSON files to enable full offline Quran reading.
- hadith_bukhari.json and hadith_muslim.json contain Arabic and English Hadith records organized by collection, book, chapter, and hadith number.
- hadith_fa.json contains Persian Hadith translations keyed by hadith id.

Expected English Quran item format:

~~~json
{
  "surahNumber": 1,
  "ayahNumber": 1,
  "arabicText": "...",
  "englishTranslation": "..."
}
~~~

Expected Persian Quran item format:

~~~json
{
  "id": "1:1",
  "translation": "..."
}
~~~

## App Icon Idea

The included launcher icon uses a deep Islamic green background with a gold crescent, white star, and simple book lines to suggest Quran and daily worship. For production, refine it in a vector design tool and export adaptive foreground/background layers.

## Notes

Prayer calculations are local approximations based on solar geometry and selectable calculation-angle presets. For production release, compare output against trusted local mosque calendars for each supported region and add high-latitude adjustments if your audience needs them.


Expected Hadith item format:

~~~json
{
  "id": "bukhari-1-1-1",
  "collection": "Sahih al-Bukhari",
  "bookNumber": 1,
  "bookTitle": "Revelation",
  "chapterNumber": 1,
  "chapterTitle": "How revelation began",
  "hadithNumber": "1",
  "arabicText": "...",
  "englishTranslation": "..."
}
~~~

Expected Persian Hadith item format:

~~~json
{
  "id": "bukhari-1-1-1",
  "translation": "..."
}
~~~
