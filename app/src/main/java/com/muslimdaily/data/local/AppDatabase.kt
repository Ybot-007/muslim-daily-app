package com.muslimdaily.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AzkarEntity::class, SurahEntity::class, AyahEntity::class, AllahNameEntity::class, HadithEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun azkarDao(): AzkarDao
    abstract fun quranDao(): QuranDao
    abstract fun allahNameDao(): AllahNameDao
    abstract fun hadithDao(): HadithDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "muslim_daily.db"
            ).fallbackToDestructiveMigration().build().also { instance = it }
        }
    }
}
