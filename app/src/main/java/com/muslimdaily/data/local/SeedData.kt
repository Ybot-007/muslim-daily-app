package com.muslimdaily.data.local

import android.content.Context
import org.json.JSONArray

object SeedData {
    suspend fun seedIfNeeded(context: Context, database: AppDatabase) {
        if (database.quranDao().surahCount() == 0) {
            database.quranDao().insertSurahs(surahs)
            database.quranDao().insertAyahs(loadAyahs(context))
        }
        if (database.azkarDao().count() == 0) {
            database.azkarDao().insertAll(loadAzkaar(context))
        }
        if (database.allahNameDao().count() == 0) {
            database.allahNameDao().insertAll(loadNames(context))
        }
        if (database.hadithDao().count() == 0) {
            database.hadithDao().insertAll(loadHadiths(context))
        }
    }

    private fun loadAzkaar(context: Context): List<AzkarEntity> {
        val english = JSONArray(context.assets.open("azkaar.json").bufferedReader().use { it.readText() })
        val persian = localizedMap(context, "azkaar_fa.json", "id")
        return List(english.length()) { index ->
            val item = english.getJSONObject(index)
            val id = item.getString("id")
            val fa = persian[id]
            AzkarEntity(
                id = id,
                category = item.getString("category"),
                arabic = item.getString("arabic"),
                transliteration = item.optString("transliteration"),
                englishTranslation = item.getString("translation"),
                persianTranslation = fa?.optString("translation")?.takeIf { it.isNotBlank() } ?: item.getString("translation"),
                repeatCount = item.optInt("repeatCount", 1)
            )
        }
    }

    private fun loadAyahs(context: Context): List<AyahEntity> {
        val english = JSONArray(context.assets.open("quran_sample.json").bufferedReader().use { it.readText() })
        val persian = localizedMap(context, "quran_fa.json", "id")
        return List(english.length()) { index ->
            val item = english.getJSONObject(index)
            val surah = item.getInt("surahNumber")
            val ayah = item.getInt("ayahNumber")
            val id = "$surah:$ayah"
            val fa = persian[id]
            AyahEntity(
                id = id,
                surahNumber = surah,
                ayahNumber = ayah,
                arabicText = item.getString("arabicText"),
                englishTranslation = item.getString("englishTranslation"),
                persianTranslation = fa?.optString("translation")?.takeIf { it.isNotBlank() } ?: item.getString("englishTranslation")
            )
        }
    }

    private fun loadNames(context: Context): List<AllahNameEntity> {
        val english = JSONArray(context.assets.open("names_of_allah.json").bufferedReader().use { it.readText() })
        val persian = localizedMap(context, "names_of_allah_fa.json", "id")
        return List(english.length()) { index ->
            val item = english.getJSONObject(index)
            val id = item.getInt("id")
            val fa = persian[id.toString()]
            AllahNameEntity(
                id = id,
                arabic = item.getString("arabic"),
                transliteration = item.getString("transliteration"),
                englishMeaning = item.getString("meaning"),
                englishExplanation = item.getString("explanation"),
                persianMeaning = fa?.optString("meaning")?.takeIf { it.isNotBlank() } ?: item.getString("meaning"),
                persianExplanation = fa?.optString("explanation")?.takeIf { it.isNotBlank() } ?: item.getString("explanation")
            )
        }
    }

    private fun loadHadiths(context: Context): List<HadithEntity> {
        val persian = localizedMap(context, "hadith_fa.json", "id")
        return listOf("hadith_bukhari.json", "hadith_muslim.json").flatMap { fileName ->
            val array = JSONArray(context.assets.open(fileName).bufferedReader().use { it.readText() })
            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                val id = item.getString("id")
                val fa = persian[id]
                HadithEntity(
                    id = id,
                    collection = item.getString("collection"),
                    bookNumber = item.getInt("bookNumber"),
                    bookTitle = item.getString("bookTitle"),
                    chapterNumber = item.getInt("chapterNumber"),
                    chapterTitle = item.getString("chapterTitle"),
                    hadithNumber = item.getString("hadithNumber"),
                    arabicText = item.getString("arabicText"),
                    englishTranslation = item.getString("englishTranslation"),
                    persianTranslation = fa?.optString("translation")?.takeIf { it.isNotBlank() } ?: item.getString("englishTranslation")
                )
            }
        }
    }

    private fun localizedMap(context: Context, fileName: String, key: String): Map<String, org.json.JSONObject> {
        val array = JSONArray(context.assets.open(fileName).bufferedReader().use { it.readText() })
        return List(array.length()) { index -> array.getJSONObject(index) }
            .associateBy { it.get(key).toString() }
    }

    val surahs = listOf(
        SurahEntity(1, "Al-Fatihah", "الفاتحة", 7, "Makkah"),
        SurahEntity(2, "Al-Baqarah", "البقرة", 286, "Madinah"),
        SurahEntity(3, "Ali 'Imran", "آل عمران", 200, "Madinah"),
        SurahEntity(4, "An-Nisa", "النساء", 176, "Madinah"),
        SurahEntity(5, "Al-Ma'idah", "المائدة", 120, "Madinah"),
        SurahEntity(6, "Al-An'am", "الأنعام", 165, "Makkah"),
        SurahEntity(7, "Al-A'raf", "الأعراف", 206, "Makkah"),
        SurahEntity(8, "Al-Anfal", "الأنفال", 75, "Madinah"),
        SurahEntity(9, "At-Tawbah", "التوبة", 129, "Madinah"),
        SurahEntity(10, "Yunus", "يونس", 109, "Makkah"),
        SurahEntity(11, "Hud", "هود", 123, "Makkah"),
        SurahEntity(12, "Yusuf", "يوسف", 111, "Makkah"),
        SurahEntity(13, "Ar-Ra'd", "الرعد", 43, "Madinah"),
        SurahEntity(14, "Ibrahim", "إبراهيم", 52, "Makkah"),
        SurahEntity(15, "Al-Hijr", "الحجر", 99, "Makkah"),
        SurahEntity(16, "An-Nahl", "النحل", 128, "Makkah"),
        SurahEntity(17, "Al-Isra", "الإسراء", 111, "Makkah"),
        SurahEntity(18, "Al-Kahf", "الكهف", 110, "Makkah"),
        SurahEntity(19, "Maryam", "مريم", 98, "Makkah"),
        SurahEntity(20, "Taha", "طه", 135, "Makkah"),
        SurahEntity(21, "Al-Anbya", "الأنبياء", 112, "Makkah"),
        SurahEntity(22, "Al-Hajj", "الحج", 78, "Madinah"),
        SurahEntity(23, "Al-Mu'minun", "المؤمنون", 118, "Makkah"),
        SurahEntity(24, "An-Nur", "النور", 64, "Madinah"),
        SurahEntity(25, "Al-Furqan", "الفرقان", 77, "Makkah"),
        SurahEntity(26, "Ash-Shu'ara", "الشعراء", 227, "Makkah"),
        SurahEntity(27, "An-Naml", "النمل", 93, "Makkah"),
        SurahEntity(28, "Al-Qasas", "القصص", 88, "Makkah"),
        SurahEntity(29, "Al-'Ankabut", "العنكبوت", 69, "Makkah"),
        SurahEntity(30, "Ar-Rum", "الروم", 60, "Makkah"),
        SurahEntity(31, "Luqman", "لقمان", 34, "Makkah"),
        SurahEntity(32, "As-Sajdah", "السجدة", 30, "Makkah"),
        SurahEntity(33, "Al-Ahzab", "الأحزاب", 73, "Madinah"),
        SurahEntity(34, "Saba", "سبأ", 54, "Makkah"),
        SurahEntity(35, "Fatir", "فاطر", 45, "Makkah"),
        SurahEntity(36, "Ya-Sin", "يس", 83, "Makkah"),
        SurahEntity(37, "As-Saffat", "الصافات", 182, "Makkah"),
        SurahEntity(38, "Sad", "ص", 88, "Makkah"),
        SurahEntity(39, "Az-Zumar", "الزمر", 75, "Makkah"),
        SurahEntity(40, "Ghafir", "غافر", 85, "Makkah"),
        SurahEntity(41, "Fussilat", "فصلت", 54, "Makkah"),
        SurahEntity(42, "Ash-Shuraa", "الشورى", 53, "Makkah"),
        SurahEntity(43, "Az-Zukhruf", "الزخرف", 89, "Makkah"),
        SurahEntity(44, "Ad-Dukhan", "الدخان", 59, "Makkah"),
        SurahEntity(45, "Al-Jathiyah", "الجاثية", 37, "Makkah"),
        SurahEntity(46, "Al-Ahqaf", "الأحقاف", 35, "Makkah"),
        SurahEntity(47, "Muhammad", "محمد", 38, "Madinah"),
        SurahEntity(48, "Al-Fath", "الفتح", 29, "Madinah"),
        SurahEntity(49, "Al-Hujurat", "الحجرات", 18, "Madinah"),
        SurahEntity(50, "Qaf", "ق", 45, "Makkah"),
        SurahEntity(51, "Adh-Dhariyat", "الذاريات", 60, "Makkah"),
        SurahEntity(52, "At-Tur", "الطور", 49, "Makkah"),
        SurahEntity(53, "An-Najm", "النجم", 62, "Makkah"),
        SurahEntity(54, "Al-Qamar", "القمر", 55, "Makkah"),
        SurahEntity(55, "Ar-Rahman", "الرحمن", 78, "Madinah"),
        SurahEntity(56, "Al-Waqi'ah", "الواقعة", 96, "Makkah"),
        SurahEntity(57, "Al-Hadid", "الحديد", 29, "Madinah"),
        SurahEntity(58, "Al-Mujadilah", "المجادلة", 22, "Madinah"),
        SurahEntity(59, "Al-Hashr", "الحشر", 24, "Madinah"),
        SurahEntity(60, "Al-Mumtahanah", "الممتحنة", 13, "Madinah"),
        SurahEntity(61, "As-Saf", "الصف", 14, "Madinah"),
        SurahEntity(62, "Al-Jumu'ah", "الجمعة", 11, "Madinah"),
        SurahEntity(63, "Al-Munafiqun", "المنافقون", 11, "Madinah"),
        SurahEntity(64, "At-Taghabun", "التغابن", 18, "Madinah"),
        SurahEntity(65, "At-Talaq", "الطلاق", 12, "Madinah"),
        SurahEntity(66, "At-Tahrim", "التحريم", 12, "Madinah"),
        SurahEntity(67, "Al-Mulk", "الملك", 30, "Makkah"),
        SurahEntity(68, "Al-Qalam", "القلم", 52, "Makkah"),
        SurahEntity(69, "Al-Haqqah", "الحاقة", 52, "Makkah"),
        SurahEntity(70, "Al-Ma'arij", "المعارج", 44, "Makkah"),
        SurahEntity(71, "Nuh", "نوح", 28, "Makkah"),
        SurahEntity(72, "Al-Jinn", "الجن", 28, "Makkah"),
        SurahEntity(73, "Al-Muzzammil", "المزمل", 20, "Makkah"),
        SurahEntity(74, "Al-Muddaththir", "المدثر", 56, "Makkah"),
        SurahEntity(75, "Al-Qiyamah", "القيامة", 40, "Makkah"),
        SurahEntity(76, "Al-Insan", "الإنسان", 31, "Madinah"),
        SurahEntity(77, "Al-Mursalat", "المرسلات", 50, "Makkah"),
        SurahEntity(78, "An-Naba", "النبأ", 40, "Makkah"),
        SurahEntity(79, "An-Nazi'at", "النازعات", 46, "Makkah"),
        SurahEntity(80, "Abasa", "عبس", 42, "Makkah"),
        SurahEntity(81, "At-Takwir", "التكوير", 29, "Makkah"),
        SurahEntity(82, "Al-Infitar", "الإنفطار", 19, "Makkah"),
        SurahEntity(83, "Al-Mutaffifin", "المطففين", 36, "Makkah"),
        SurahEntity(84, "Al-Inshiqaq", "الإنشقاق", 25, "Makkah"),
        SurahEntity(85, "Al-Buruj", "البروج", 22, "Makkah"),
        SurahEntity(86, "At-Tariq", "الطارق", 17, "Makkah"),
        SurahEntity(87, "Al-A'la", "الأعلى", 19, "Makkah"),
        SurahEntity(88, "Al-Ghashiyah", "الغاشية", 26, "Makkah"),
        SurahEntity(89, "Al-Fajr", "الفجر", 30, "Makkah"),
        SurahEntity(90, "Al-Balad", "البلد", 20, "Makkah"),
        SurahEntity(91, "Ash-Shams", "الشمس", 15, "Makkah"),
        SurahEntity(92, "Al-Layl", "الليل", 21, "Makkah"),
        SurahEntity(93, "Ad-Duhaa", "الضحى", 11, "Makkah"),
        SurahEntity(94, "Ash-Sharh", "الشرح", 8, "Makkah"),
        SurahEntity(95, "At-Tin", "التين", 8, "Makkah"),
        SurahEntity(96, "Al-'Alaq", "العلق", 19, "Makkah"),
        SurahEntity(97, "Al-Qadr", "القدر", 5, "Makkah"),
        SurahEntity(98, "Al-Bayyinah", "البينة", 8, "Madinah"),
        SurahEntity(99, "Az-Zalzalah", "الزلزلة", 8, "Madinah"),
        SurahEntity(100, "Al-'Adiyat", "العاديات", 11, "Makkah"),
        SurahEntity(101, "Al-Qari'ah", "القارعة", 11, "Makkah"),
        SurahEntity(102, "At-Takathur", "التكاثر", 8, "Makkah"),
        SurahEntity(103, "Al-'Asr", "العصر", 3, "Makkah"),
        SurahEntity(104, "Al-Humazah", "الهمزة", 9, "Makkah"),
        SurahEntity(105, "Al-Fil", "الفيل", 5, "Makkah"),
        SurahEntity(106, "Quraysh", "قريش", 4, "Makkah"),
        SurahEntity(107, "Al-Ma'un", "الماعون", 7, "Makkah"),
        SurahEntity(108, "Al-Kawthar", "الكوثر", 3, "Makkah"),
        SurahEntity(109, "Al-Kafirun", "الكافرون", 6, "Makkah"),
        SurahEntity(110, "An-Nasr", "النصر", 3, "Madinah"),
        SurahEntity(111, "Al-Masad", "المسد", 5, "Makkah"),
        SurahEntity(112, "Al-Ikhlas", "الإخلاص", 4, "Makkah"),
        SurahEntity(113, "Al-Falaq", "الفلق", 5, "Makkah"),
        SurahEntity(114, "An-Nas", "الناس", 6, "Makkah")
    )
}
