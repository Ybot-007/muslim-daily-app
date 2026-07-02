package com.muslimdaily.data.repository

import android.content.Context
import com.muslimdaily.data.local.AppDatabase
import com.muslimdaily.data.local.SeedData

class ContentRepository(
    private val context: Context,
    private val database: AppDatabase
) {
    val azkaar = database.azkarDao().observeAll()
    val favoriteAzkaar = database.azkarDao().observeFavorites()
    val surahs = database.quranDao().observeSurahs()
    val favoriteAyahs = database.quranDao().observeFavoriteAyahs()
    val bookmark = database.quranDao().observeBookmark()
    val names = database.allahNameDao().observeAll()
    val favoriteNames = database.allahNameDao().observeFavorites()
    val hadiths = database.hadithDao().observeAll()
    val favoriteHadiths = database.hadithDao().observeFavorites()
    val lastReadHadith = database.hadithDao().observeLastRead()

    suspend fun seed() = SeedData.seedIfNeeded(context, database)
    fun ayahsFor(surahNumber: Int) = database.quranDao().observeAyahs(surahNumber)
    suspend fun setAzkarFavorite(id: String, favorite: Boolean) = database.azkarDao().setFavorite(id, favorite)
    suspend fun setNameFavorite(id: Int, favorite: Boolean) = database.allahNameDao().setFavorite(id, favorite)
    suspend fun setAyahFavorite(id: String, favorite: Boolean) = database.quranDao().setAyahFavorite(id, favorite)
    suspend fun bookmarkAyah(id: String) {
        database.quranDao().clearBookmark()
        database.quranDao().setBookmark(id)
    }

    suspend fun setHadithFavorite(id: String, favorite: Boolean) = database.hadithDao().setFavorite(id, favorite)

    suspend fun markHadithLastRead(id: String) {
        database.hadithDao().clearLastRead()
        database.hadithDao().setLastRead(id)
    }
}
