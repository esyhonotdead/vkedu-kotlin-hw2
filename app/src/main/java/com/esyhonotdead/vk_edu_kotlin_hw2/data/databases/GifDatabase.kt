package com.esyhonotdead.vk_edu_kotlin_hw2.data.databases

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver

@Database(
    entities = [CachedGif::class, TrendingPageState::class],
    version = 2,
    exportSchema = false,
)
abstract class GifDatabase : RoomDatabase() {
    abstract fun gifDao(): GifDao

    companion object {
        private var instance: GifDatabase? = null

        @Synchronized
        fun getInstance(context: Context): GifDatabase {
            return instance ?: Room.databaseBuilder<GifDatabase>(
                context.applicationContext,
                "gif.db"
            )
                .setDriver(driver = AndroidSQLiteDriver())
                .build()
                .also { instance = it }
        }
    }
}
