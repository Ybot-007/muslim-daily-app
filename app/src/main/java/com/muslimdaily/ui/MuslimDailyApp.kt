package com.muslimdaily.ui

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.muslimdaily.data.local.AllahNameEntity
import com.muslimdaily.data.local.AyahEntity
import com.muslimdaily.data.local.AzkarEntity
import com.muslimdaily.data.local.HadithEntity
import com.muslimdaily.data.local.SurahEntity
import com.muslimdaily.data.preferences.AppLanguage
import com.muslimdaily.domain.device.UserLocation
import com.muslimdaily.domain.prayer.PrayerTimes
import com.muslimdaily.domain.prayer.qiblaBearing
import com.muslimdaily.ui.theme.MuslimDailyTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private enum class MainTab { Home, Azkaar, Prayer, Quran, More }

private data class UiText(
    val appName: String,
    val home: String,
    val azkaar: String,
    val prayer: String,
    val quran: String,
    val more: String,
    val homeSubtitle: String,
    val nextPrayer: String,
    val countdown: String,
    val todayPrayerTimes: String,
    val dailyAyah: String,
    val dailyDhikr: String,
    val sampleLoading: String,
    val openQuran: String,
    val lastBookmark: String,
    val azkaarSubtitle: String,
    val morning: String,
    val evening: String,
    val afterPrayer: String,
    val sleep: String,
    val favorites: String,
    val noSaved: String,
    val reset: String,
    val tasbeeh: String,
    val prayerSubtitle: String,
    val location: String,
    val usePhone: String,
    val city: String,
    val calculationMethod: String,
    val notifyBefore: String,
    val quranSubtitle: String,
    val searchSurah: String,
    val quranEmpty: String,
    val moreSubtitle: String,
    val qibla: String,
    val names: String,
    val language: String,
    val english: String,
    val persian: String,
    val allowLocation: String,
    val kaaba: String,
    val compass: String,
    val degree: String,
    val ayahs: String,
    val dailyHadith: String,
    val hadith: String,
    val hadithSubtitle: String,
    val searchHadith: String,
    val allHadiths: String,
    val bukhari: String,
    val muslim: String,
    val book: String,
    val chapter: String,
    val hadithNumber: String,
    val copy: String,
    val share: String,
    val lastReadHadith: String,
    val noHadiths: String
)

private fun strings(language: AppLanguage) = if (language == AppLanguage.Persian) UiText(
    appName = "مسلم دیلی",
    home = "خانه",
    azkaar = "اذکار",
    prayer = "نماز",
    quran = "قرآن",
    more = "بیشتر",
    homeSubtitle = "نگاه امروز",
    nextPrayer = "نماز بعدی",
    countdown = "شمارش معکوس",
    todayPrayerTimes = "اوقات نماز امروز",
    dailyAyah = "آیه روز",
    dailyDhikr = "ذکر روز",
    sampleLoading = "داده‌های نمونه قرآن در حال بارگذاری است",
    openQuran = "برای خواندن آیات و نشانک‌ها بخش قرآن را باز کنید.",
    lastBookmark = "آخرین نشانک",
    azkaarSubtitle = "اذکار صبح، شام، بعد از نماز و خواب",
    morning = "صبح",
    evening = "شام",
    afterPrayer = "بعد از نماز",
    sleep = "خواب",
    favorites = "پسندیده‌ها",
    noSaved = "هنوز موردی ذخیره نشده است.",
    reset = "از نو",
    tasbeeh = "تسبیح",
    prayerSubtitle = "اوقات نماز، روش محاسبه و یادآورها",
    location = "موقعیت",
    usePhone = "استفاده از گوشی",
    city = "شهر",
    calculationMethod = "روش محاسبه",
    notifyBefore = "یادآوری ۱۰ دقیقه پیش",
    quranSubtitle = "۱۱۴ سوره، متن عربی، ترجمه فارسی/انگلیسی و نشانک",
    searchSurah = "جستجوی سوره",
    quranEmpty = "این نسخه شامل آیات نمونه است. برای مطالعه کامل آفلاین، فایل quran_sample.json و quran_fa.json را با داده کامل جایگزین کنید.",
    moreSubtitle = "قبله‌نما، نام‌های الله و زبان",
    qibla = "قبله",
    names = "نام‌ها",
    language = "زبان",
    english = "English",
    persian = "فارسی",
    allowLocation = "اجازه موقعیت",
    kaaba = "کعبه",
    compass = "قطب‌نما",
    degree = "درجه",
    ayahs = "آیه",
    dailyHadith = "حدیث روز",
    hadith = "حدیث",
    hadithSubtitle = "بخاری و مسلم به صورت آفلاین، با جستجو و نشانک",
    searchHadith = "جستجوی حدیث، کتاب، باب یا شماره",
    allHadiths = "همه",
    bukhari = "بخاری",
    muslim = "مسلم",
    book = "کتاب",
    chapter = "باب",
    hadithNumber = "شماره حدیث",
    copy = "کپی",
    share = "اشتراک",
    lastReadHadith = "آخرین حدیث خوانده‌شده",
    noHadiths = "حدیثی یافت نشد."
) else UiText(
    appName = "Muslim Daily",
    home = "Home",
    azkaar = "Azkaar",
    prayer = "Prayer",
    quran = "Quran",
    more = "More",
    homeSubtitle = "Today at a glance",
    nextPrayer = "Next prayer",
    countdown = "Countdown",
    todayPrayerTimes = "Today's prayer times",
    dailyAyah = "Daily ayah",
    dailyDhikr = "Daily dhikr",
    sampleLoading = "Sample Quran data is loading",
    openQuran = "Open Quran to read bookmarked and favorite ayahs.",
    lastBookmark = "Last bookmark",
    azkaarSubtitle = "Morning, evening, after-prayer, and sleep remembrance",
    morning = "Morning",
    evening = "Evening",
    afterPrayer = "After prayer",
    sleep = "Sleep",
    favorites = "Favorites",
    noSaved = "No saved items here yet.",
    reset = "Reset",
    tasbeeh = "Tasbeeh",
    prayerSubtitle = "Location-based times, calculation methods, and reminders",
    location = "Location",
    usePhone = "Use phone",
    city = "City",
    calculationMethod = "Calculation method",
    notifyBefore = "Notify 10 minutes before",
    quranSubtitle = "All 114 surahs, Arabic text, English/Persian translation, search, and bookmarks",
    searchSurah = "Search surah",
    quranEmpty = "This scaffold includes sample ayahs. Replace quran_sample.json and quran_fa.json with complete datasets for full offline reading.",
    moreSubtitle = "Qibla compass, 99 Names of Allah, and language",
    qibla = "Qibla",
    names = "Names",
    language = "Language",
    english = "English",
    persian = "Persian",
    allowLocation = "Allow location",
    kaaba = "Kaaba",
    compass = "Compass",
    degree = "deg",
    ayahs = "ayahs",
    dailyHadith = "Daily Hadith",
    hadith = "Hadith",
    hadithSubtitle = "Offline Bukhari and Muslim with search and bookmarks",
    searchHadith = "Search hadith, book, chapter, or number",
    allHadiths = "All",
    bukhari = "Bukhari",
    muslim = "Muslim",
    book = "Book",
    chapter = "Chapter",
    hadithNumber = "Hadith no.",
    copy = "Copy",
    share = "Share",
    lastReadHadith = "Last read hadith",
    noHadiths = "No hadiths found."
)

@Composable
fun MuslimDailyApp(
    requestPermissions: () -> Unit,
    viewModel: MuslimDailyViewModel = viewModel()
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val azkaar by viewModel.azkaar.collectAsStateWithLifecycle()
    val surahs by viewModel.surahs.collectAsStateWithLifecycle()
    val ayahs by viewModel.ayahs.collectAsStateWithLifecycle()
    val selectedSurah by viewModel.selectedSurahNumber.collectAsStateWithLifecycle()
    val names by viewModel.allahNames.collectAsStateWithLifecycle()
    val hadiths by viewModel.hadiths.collectAsStateWithLifecycle()
    val lastReadHadith by viewModel.lastReadHadith.collectAsStateWithLifecycle()
    val prayer by viewModel.prayerState.collectAsStateWithLifecycle()
    val bookmark by viewModel.bookmark.collectAsStateWithLifecycle()
    val text = strings(language)
    var tab by rememberSaveable { mutableStateOf(MainTab.Home) }

    MuslimDailyTheme {
        CompositionLocalProvider(LocalLayoutDirection provides if (language == AppLanguage.Persian) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        MainTab.entries.forEach { item ->
                            NavigationBarItem(
                                selected = tab == item,
                                onClick = { tab = item },
                                icon = { Icon(tabIcon(item), contentDescription = item.label(text)) },
                                label = { Text(item.label(text), maxLines = 1) }
                            )
                        }
                    }
                }
            ) { padding ->
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when (tab) {
                        MainTab.Home -> HomeScreen(padding, text, language, prayer, azkaar.firstOrNull(), ayahs.firstOrNull(), hadiths.firstOrNull(), bookmark)
                        MainTab.Azkaar -> AzkaarScreen(padding, text, language, azkaar, viewModel::toggleAzkarFavorite)
                        MainTab.Prayer -> PrayerScreen(padding, text, language, prayer, requestPermissions, viewModel::useDeviceLocation, viewModel::selectLocation, viewModel::selectMethod, viewModel::schedulePrayerReminders)
                        MainTab.Quran -> QuranScreen(padding, text, language, surahs, ayahs, selectedSurah, viewModel::selectSurah, viewModel::toggleAyahFavorite, viewModel::bookmarkAyah)
                        MainTab.More -> MoreScreen(padding, text, language, prayer.location, names, hadiths, lastReadHadith, requestPermissions, viewModel::setLanguage, viewModel::toggleNameFavorite, viewModel::toggleHadithFavorite, viewModel::markHadithLastRead)
                    }
                }
            }
        }
    }
}

private fun MainTab.label(text: UiText) = when (this) {
    MainTab.Home -> text.home
    MainTab.Azkaar -> text.azkaar
    MainTab.Prayer -> text.prayer
    MainTab.Quran -> text.quran
    MainTab.More -> text.more
}

private fun tabIcon(tab: MainTab) = when (tab) {
    MainTab.Home -> Icons.Default.Home
    MainTab.Azkaar -> Icons.Default.WbTwilight
    MainTab.Prayer -> Icons.Default.AccessTime
    MainTab.Quran -> Icons.Default.MenuBook
    MainTab.More -> Icons.Default.Settings
}

@Composable
private fun ScreenHeader(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LanguageSelector(text: UiText, language: AppLanguage, setLanguage: (AppLanguage) -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(8.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, contentDescription = text.language)
                Spacer(Modifier.width(8.dp))
                Text(text.language, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = language == AppLanguage.English, onClick = { setLanguage(AppLanguage.English) }, label = { Text(text.english) })
                FilterChip(selected = language == AppLanguage.Persian, onClick = { setLanguage(AppLanguage.Persian) }, label = { Text(text.persian) })
            }
        }
    }
}

@Composable
private fun HomeScreen(
    padding: PaddingValues,
    text: UiText,
    language: AppLanguage,
    prayer: PrayerUiState,
    dailyDhikr: AzkarEntity?,
    dailyAyah: AyahEntity?,
    dailyHadith: HadithEntity?,
    bookmark: AyahEntity?
) {
    LazyColumn(contentPadding = padding, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { ScreenHeader(text.appName, text.homeSubtitle) }
        item {
            HeroCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text.nextPrayer, color = Color.White.copy(alpha = 0.8f))
                        Text(prayerName(prayer.nextPrayer.name, language), style = MaterialTheme.typography.headlineLarge, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(prayer.nextPrayer.time.toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a")), color = Color.White.copy(alpha = 0.86f))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text.countdown, color = Color.White.copy(alpha = 0.8f))
                        Text(prayer.countdown, style = MaterialTheme.typography.headlineSmall, color = Color(0xFFD6B25E), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        item { PrayerTimesCard(text, language, prayer.times) }
        item {
            InfoCard(text.dailyAyah, dailyAyah?.arabicText ?: text.sampleLoading) {
                Text(dailyAyah?.translation(language) ?: text.openQuran)
                bookmark?.let { Text("${text.lastBookmark}: ${it.surahNumber}:${it.ayahNumber}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold) }
            }
        }
        item {
            InfoCard(text.dailyDhikr, dailyDhikr?.arabic ?: text.azkaar) {
                Text(dailyDhikr?.translation(language) ?: text.azkaarSubtitle)
            }
        }
        item {
            InfoCard(text.dailyHadith, dailyHadith?.arabicText ?: text.hadith) {
                dailyHadith?.let {
                    Text("${it.collection} • ${text.hadithNumber} ${it.hadithNumber}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    Text(it.translation(language))
                } ?: Text(text.hadithSubtitle)
            }
        }
    }
}

@Composable
private fun HeroCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) { Box(Modifier.fillMaxWidth().padding(20.dp)) { content() } }
}

@Composable
private fun InfoCard(title: String, arabic: String, body: @Composable () -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(8.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(arabic, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            body()
        }
    }
}

@Composable
private fun PrayerTimesCard(text: UiText, language: AppLanguage, times: PrayerTimes) {
    ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(8.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text.todayPrayerTimes, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            times.asList().forEach { (name, time) -> PrayerTimeRow(prayerName(name, language), time) }
        }
    }
}

@Composable
private fun PrayerTimeRow(name: String, time: LocalTime) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(name, fontWeight = FontWeight.Medium)
        Text(time.format(DateTimeFormatter.ofPattern("h:mm a")), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AzkaarScreen(padding: PaddingValues, text: UiText, language: AppLanguage, azkaar: List<AzkarEntity>, toggleFavorite: (AzkarEntity) -> Unit) {
    val categories = listOf("Morning", "Evening", "After prayer", "Sleep", "Favorites")
    var selected by rememberSaveable { mutableStateOf("Morning") }
    val filtered = if (selected == "Favorites") azkaar.filter { it.isFavorite } else azkaar.filter { it.category == selected }
    LazyColumn(contentPadding = padding, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { ScreenHeader(text.azkaar, text.azkaarSubtitle) }
        item {
            FlowRow(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { category -> FilterChip(selected = selected == category, onClick = { selected = category }, label = { Text(categoryLabel(category, text)) }) }
            }
        }
        items(filtered, key = { it.id }) { item -> AzkarCard(text, language, item, toggleFavorite) }
        if (filtered.isEmpty()) item { EmptyText(text.noSaved) }
    }
}

@Composable
private fun AzkarCard(text: UiText, language: AppLanguage, item: AzkarEntity, toggleFavorite: (AzkarEntity) -> Unit) {
    var count by rememberSaveable(item.id) { mutableIntStateOf(0) }
    ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(8.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AssistChip(onClick = {}, label = { Text("${item.repeatCount}x") })
                IconButton(onClick = { toggleFavorite(item) }) { Icon(if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = text.favorites) }
            }
            Text(item.arabic, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            if (item.transliteration.isNotBlank()) Text(item.transliteration, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(item.translation(language))
            Button(onClick = { count = (count + 1).coerceAtMost(item.repeatCount) }, modifier = Modifier.fillMaxWidth()) {
                Text("${text.tasbeeh} $count / ${item.repeatCount}")
            }
            if (count >= item.repeatCount) OutlinedButton(onClick = { count = 0 }, modifier = Modifier.fillMaxWidth()) { Text(text.reset) }
        }
    }
}

@Composable
private fun PrayerScreen(
    padding: PaddingValues,
    text: UiText,
    language: AppLanguage,
    prayer: PrayerUiState,
    requestPermissions: () -> Unit,
    useDeviceLocation: () -> Unit,
    selectLocation: (UserLocation) -> Unit,
    selectMethod: (String) -> Unit,
    scheduleReminders: () -> Unit
) {
    LazyColumn(contentPadding = padding, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { ScreenHeader(text.prayer, text.prayerSubtitle) }
        item { PrayerTimesCard(text, language, prayer.times) }
        item {
            ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(8.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text.location, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(prayer.location.cityLabel, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { requestPermissions(); useDeviceLocation() }) { Icon(Icons.Default.LocationOn, null); Spacer(Modifier.width(8.dp)); Text(text.usePhone) }
                        CityDropdown(text, selectLocation)
                    }
                    Divider()
                    Text(text.calculationMethod, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    MethodDropdown(prayer.method.key, selectMethod)
                    Button(onClick = scheduleReminders, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Notifications, null); Spacer(Modifier.width(8.dp)); Text(text.notifyBefore) }
                }
            }
        }
    }
}

@Composable
private fun CityDropdown(text: UiText, selectLocation: (UserLocation) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) { Text(text.city) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            MuslimDailyViewModel.manualCities.forEach { city ->
                DropdownMenuItem(text = { Text(city.cityLabel) }, onClick = { selectLocation(city); expanded = false })
            }
        }
    }
}

@Composable
private fun MethodDropdown(currentKey: String, selectMethod: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val current = MuslimDailyViewModel.methods.first { it.key == currentKey }
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(current.title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            MuslimDailyViewModel.methods.forEach { method ->
                DropdownMenuItem(text = { Text(method.title) }, onClick = { selectMethod(method.key); expanded = false })
            }
        }
    }
}

@Composable
private fun QuranScreen(
    padding: PaddingValues,
    text: UiText,
    language: AppLanguage,
    surahs: List<SurahEntity>,
    ayahs: List<AyahEntity>,
    selectedSurah: Int,
    selectSurah: (Int) -> Unit,
    toggleFavorite: (AyahEntity) -> Unit,
    bookmarkAyah: (AyahEntity) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filtered = surahs.filter { it.englishName.contains(query, true) || it.arabicName.contains(query) || it.number.toString() == query }
    LazyColumn(contentPadding = padding, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { ScreenHeader(text.quran, text.quranSubtitle) }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                leadingIcon = { Icon(Icons.Default.Search, null) },
                label = { Text(text.searchSurah) },
                singleLine = true
            )
        }
        item {
            LazyColumn(
                modifier = Modifier.height(210.dp).padding(horizontal = 20.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surface),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(filtered, key = { it.number }) { surah ->
                    Row(
                        Modifier.fillMaxWidth().clickable { selectSurah(surah.number) }.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("${surah.number}. ${surah.englishName}", fontWeight = if (selectedSurah == surah.number) FontWeight.Bold else FontWeight.Normal)
                            Text("${surah.revelationPlace} - ${surah.ayahCount} ${text.ayahs}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(surah.arabicName, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.End)
                    }
                }
            }
        }
        items(ayahs, key = { it.id }) { ayah -> AyahCard(text, language, ayah, toggleFavorite, bookmarkAyah) }
        if (ayahs.isEmpty()) item { EmptyText(text.quranEmpty) }
    }
}

@Composable
private fun AyahCard(text: UiText, language: AppLanguage, item: AyahEntity, toggleFavorite: (AyahEntity) -> Unit, bookmarkAyah: (AyahEntity) -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(8.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${item.surahNumber}:${item.ayahNumber}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Row {
                    IconButton(onClick = { bookmarkAyah(item) }) { Icon(if (item.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, contentDescription = text.lastBookmark) }
                    IconButton(onClick = { toggleFavorite(item) }) { Icon(if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = text.favorites) }
                }
            }
            Text(item.arabicText, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            Text(item.translation(language))
        }
    }
}

@Composable
private fun MoreScreen(
    padding: PaddingValues,
    text: UiText,
    language: AppLanguage,
    location: UserLocation,
    names: List<AllahNameEntity>,
    hadiths: List<HadithEntity>,
    lastReadHadith: HadithEntity?,
    requestPermissions: () -> Unit,
    setLanguage: (AppLanguage) -> Unit,
    toggleNameFavorite: (Int, Boolean) -> Unit,
    toggleHadithFavorite: (HadithEntity) -> Unit,
    markHadithLastRead: (HadithEntity) -> Unit
) {
    var page by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize().padding(padding)) {
        ScreenHeader(text.more, text.moreSubtitle)
        LanguageSelector(text, language, setLanguage)
        Spacer(Modifier.height(12.dp))
        TabRow(selectedTabIndex = page) {
            Tab(selected = page == 0, onClick = { page = 0 }, text = { Text(text.qibla) }, icon = { Icon(Icons.Default.Explore, null) })
            Tab(selected = page == 1, onClick = { page = 1 }, text = { Text(text.names) }, icon = { Icon(Icons.Default.Star, null) })
            Tab(selected = page == 2, onClick = { page = 2 }, text = { Text(text.hadith) }, icon = { Icon(Icons.Default.MenuBook, null) })
        }
        when (page) {
            0 -> QiblaCompass(text, location, requestPermissions)
            1 -> NamesScreen(text, language, names, toggleNameFavorite)
            else -> HadithScreen(text, language, hadiths, lastReadHadith, toggleHadithFavorite, markHadithLastRead)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HadithScreen(
    text: UiText,
    language: AppLanguage,
    hadiths: List<HadithEntity>,
    lastReadHadith: HadithEntity?,
    toggleFavorite: (HadithEntity) -> Unit,
    markLastRead: (HadithEntity) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var collection by rememberSaveable { mutableStateOf("All") }
    val filtered = hadiths.filter { item ->
        val matchesCollection = when (collection) {
            "Bukhari" -> item.collection == "Sahih al-Bukhari"
            "Muslim" -> item.collection == "Sahih Muslim"
            "Favorites" -> item.isFavorite
            else -> true
        }
        val q = query.trim()
        val searchable = item.searchableText()
        matchesCollection && (q.isBlank() || searchable.contains(q, ignoreCase = true))
    }

    LazyColumn(contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { ScreenHeader(text.hadith, text.hadithSubtitle) }
        lastReadHadith?.let { last ->
            item {
                ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(8.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text.lastReadHadith, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("${last.collection} - ${text.hadithNumber} ${last.hadithNumber}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        Text(last.arabicText, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                        Text(last.translation(language), maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                leadingIcon = { Icon(Icons.Default.Search, null) },
                label = { Text(text.searchHadith) },
                singleLine = true
            )
        }
        item {
            FlowRow(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = collection == "All", onClick = { collection = "All" }, label = { Text(text.allHadiths) })
                FilterChip(selected = collection == "Bukhari", onClick = { collection = "Bukhari" }, label = { Text(text.bukhari) })
                FilterChip(selected = collection == "Muslim", onClick = { collection = "Muslim" }, label = { Text(text.muslim) })
                FilterChip(selected = collection == "Favorites", onClick = { collection = "Favorites" }, label = { Text(text.favorites) })
            }
        }
        items(filtered, key = { it.id }) { item ->
            HadithCard(text, language, item, toggleFavorite, markLastRead)
        }
        if (filtered.isEmpty()) item { EmptyText(text.noHadiths) }
    }
}

@Composable
private fun HadithCard(
    text: UiText,
    language: AppLanguage,
    item: HadithEntity,
    toggleFavorite: (HadithEntity) -> Unit,
    markLastRead: (HadithEntity) -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val shareText = item.shareText(language)
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).clickable { markLastRead(item) },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(item.collection, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("${text.book} ${item.bookNumber}: ${item.bookTitle}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${text.chapter} ${item.chapterNumber}: ${item.chapterTitle}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { toggleFavorite(item) }) {
                    Icon(if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = text.favorites)
                }
            }
            AssistChip(onClick = {}, label = { Text("${text.hadithNumber} ${item.hadithNumber}") })
            Text(item.arabicText, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            Text(item.translation(language))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { clipboard.setText(AnnotatedString(shareText)) }) {
                    Icon(Icons.Default.ContentCopy, null)
                    Spacer(Modifier.width(8.dp))
                    Text(text.copy)
                }
                Button(onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, text.share))
                }) {
                    Icon(Icons.Default.Share, null)
                    Spacer(Modifier.width(8.dp))
                    Text(text.share)
                }
            }
        }
    }
}

@Composable
private fun NamesScreen(text: UiText, language: AppLanguage, names: List<AllahNameEntity>, toggleNameFavorite: (Int, Boolean) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(names, key = { it.id }) { name ->
            ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(8.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Text(name.id.toString().padStart(2, '0'), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column { Text(name.transliteration, fontWeight = FontWeight.Bold); Text(name.meaning(language), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            Text(name.arabic, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.End)
                        }
                        Text(name.explanation(language))
                    }
                    IconButton(onClick = { toggleNameFavorite(name.id, name.isFavorite) }) { Icon(if (name.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = text.favorites) }
                }
            }
        }
    }
}

@Composable
private fun QiblaCompass(text: UiText, location: UserLocation, requestPermissions: () -> Unit) {
    val context = LocalContext.current
    var heading by remember { mutableFloatStateOf(0f) }
    val qibla = remember(location) { qiblaBearing(location.latitude, location.longitude).toFloat() }
    val rotation = (qibla - heading + 360f) % 360f

    DisposableEffect(context) {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetic = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) System.arraycopy(event.values, 0, gravity, 0, 3)
                if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                val rotationMatrix = FloatArray(9)
                if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    heading = ((Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        accelerometer?.let { manager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        magnetic?.let { manager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        onDispose { manager.unregisterListener(listener) }
    }

    Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(location.cityLabel, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        Box(Modifier.fillMaxWidth().aspectRatio(1f), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize().padding(20.dp)) {
                val stroke = Stroke(width = 10f, cap = StrokeCap.Round)
                drawCircle(color = Color(0xFFD6B25E), style = stroke)
                drawCircle(color = Color(0x330E4D3A), radius = size.minDimension * 0.34f, style = Stroke(width = 3f))
            }
            Icon(
                Icons.Default.Navigation,
                contentDescription = text.qibla,
                modifier = Modifier.size(112.dp).graphicsLayer { rotationZ = rotation },
                tint = MaterialTheme.colorScheme.primary
            )
            Text(text.kaaba, modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp), fontWeight = FontWeight.Bold)
        }
        Text("${text.qibla} ${qibla.roundToInt()} ${text.degree} - ${text.compass} ${heading.roundToInt()} ${text.degree}", style = MaterialTheme.typography.titleMedium)
        Button(onClick = requestPermissions) { Icon(Icons.Default.LocationOn, null); Spacer(Modifier.width(8.dp)); Text(text.allowLocation) }
    }
}

@Composable
private fun EmptyText(value: String) {
    Text(value, modifier = Modifier.fillMaxWidth().padding(24.dp), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

private fun AyahEntity.translation(language: AppLanguage) = if (language == AppLanguage.Persian) persianTranslation else englishTranslation
private fun HadithEntity.translation(language: AppLanguage) = if (language == AppLanguage.Persian) persianTranslation else englishTranslation
private fun AzkarEntity.translation(language: AppLanguage) = if (language == AppLanguage.Persian) persianTranslation else englishTranslation
private fun AllahNameEntity.meaning(language: AppLanguage) = if (language == AppLanguage.Persian) persianMeaning else englishMeaning
private fun AllahNameEntity.explanation(language: AppLanguage) = if (language == AppLanguage.Persian) persianExplanation else englishExplanation

private fun HadithEntity.searchableText(): String = listOf(collection, bookTitle, chapterTitle, hadithNumber, arabicText, englishTranslation, persianTranslation).joinToString(" ")

private fun HadithEntity.shareText(language: AppLanguage): String = buildString {
    appendLine(collection)
    appendLine("Book $bookNumber: $bookTitle")
    appendLine("Chapter $chapterNumber: $chapterTitle")
    appendLine("Hadith $hadithNumber")
    appendLine(arabicText)
    appendLine(translation(language))
}

private fun categoryLabel(category: String, text: UiText) = when (category) {
    "Morning" -> text.morning
    "Evening" -> text.evening
    "After prayer" -> text.afterPrayer
    "Sleep" -> text.sleep
    "Favorites" -> text.favorites
    else -> category
}

private fun prayerName(name: String, language: AppLanguage) = if (language == AppLanguage.Persian) when (name) {
    "Fajr" -> "فجر"
    "Dhuhr" -> "ظهر"
    "Asr" -> "عصر"
    "Maghrib" -> "مغرب"
    "Isha" -> "عشاء"
    else -> name
} else name
