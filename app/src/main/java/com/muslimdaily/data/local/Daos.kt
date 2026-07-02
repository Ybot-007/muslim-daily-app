package com.muslimdaily.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AzkarDao {
    @Query("SELECT * FROM azkaar ORDER BY category, id")
    fun observeAll(): Flow<List<AzkarEntity>>

    @Query("SELECT * FROM azkaar WHERE category = :category ORDER BY id")
    fun observeByCategory(category: String): Flow<List<AzkarEntity>>

    @Query("SELECT * FROM azkaar WHERE isFavorite = 1 ORDER BY category, id")
    fun observeFavorites(): Flow<List<AzkarEntity>>

    @Query("SELECT COUNT(*) FROM azkaar")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AzkarEntity>)

    @Query("UPDATE azkaar SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: String, favorite: Boolean)
}

@Dao
interface QuranDao {
    @Query("SELECT * FROM surahs ORDER BY number")
    fun observeSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumber")
    fun observeAyahs(surahNumber: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE isFavorite = 1 ORDER BY surahNumber, ayahNumber")
    fun observeFavoriteAyahs(): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE isBookmarked = 1 LIMIT 1")
    fun observeBookmark(): Flow<AyahEntity?>

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun surahCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(items: List<SurahEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyahs(items: List<AyahEntity>)

    @Query("UPDATE ayahs SET isFavorite = :favorite WHERE id = :id")
    suspend fun setAyahFavorite(id: String, favorite: Boolean)

    @Query("UPDATE ayahs SET isBookmarked = 0")
    suspend fun clearBookmark()

    @Query("UPDATE ayahs SET isBookmarked = 1 WHERE id = :id")
    suspend fun setBookmark(id: String)
}

@Dao
interface AllahNameDao {
    @Query("SELECT * FROM allah_names ORDER BY id")
    fun observeAll(): Flow<List<AllahNameEntity>>

    @Query("SELECT * FROM allah_names WHERE isFavorite = 1 ORDER BY id")
    fun observeFavorites(): Flow<List<AllahNameEntity>>

    @Query("SELECT COUNT(*) FROM allah_names")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AllahNameEntity>)

    @Query("UPDATE allah_names SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Int, favorite: Boolean)
}


@Dao
interface HadithDao {
    @Query("SELECT * FROM hadiths ORDER BY collection, bookNumber, chapterNumber, hadithNumber")
    fun observeAll(): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE isFavorite = 1 ORDER BY collection, bookNumber, chapterNumber, hadithNumber")
    fun observeFavorites(): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE isLastRead = 1 LIMIT 1")
    fun observeLastRead(): Flow<HadithEntity?>

    @Query("SELECT COUNT(*) FROM hadiths")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<HadithEntity>)

    @Query("UPDATE hadiths SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: String, favorite: Boolean)

    @Query("UPDATE hadiths SET isLastRead = 0")
    suspend fun clearLastRead()

    @Query("UPDATE hadiths SET isLastRead = 1 WHERE id = :id")
    suspend fun setLastRead(id: String)
}
