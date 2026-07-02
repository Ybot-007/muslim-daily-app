package com.muslimdaily.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "azkaar")
data class AzkarEntity(
    @PrimaryKey val id: String,
    val category: String,
    val arabic: String,
    val transliteration: String,
    val englishTranslation: String,
    val persianTranslation: String,
    val repeatCount: Int,
    val isFavorite: Boolean = false
)

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val number: Int,
    val englishName: String,
    val arabicName: String,
    val ayahCount: Int,
    val revelationPlace: String
)

@Entity(tableName = "ayahs")
data class AyahEntity(
    @PrimaryKey val id: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val arabicText: String,
    val englishTranslation: String,
    val persianTranslation: String,
    val isFavorite: Boolean = false,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "allah_names")
data class AllahNameEntity(
    @PrimaryKey val id: Int,
    val arabic: String,
    val transliteration: String,
    val englishMeaning: String,
    val englishExplanation: String,
    val persianMeaning: String,
    val persianExplanation: String,
    val isFavorite: Boolean = false
)


@Entity(tableName = "hadiths")
data class HadithEntity(
    @PrimaryKey val id: String,
    val collection: String,
    val bookNumber: Int,
    val bookTitle: String,
    val chapterNumber: Int,
    val chapterTitle: String,
    val hadithNumber: String,
    val arabicText: String,
    val englishTranslation: String,
    val persianTranslation: String,
    val isFavorite: Boolean = false,
    val isLastRead: Boolean = false
)
