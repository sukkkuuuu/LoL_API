package com.ksuniv.pvp_log_app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ksuniv.pvp_log_app.dao.FavoriteDao
import com.ksuniv.pvp_log_app.model.Favorite

@Database(entities = [Favorite::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun favoriteDao() : FavoriteDao

    companion object{
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase? {
            if(instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "favorite-database"
                    ).build()
                }
            }
            return instance
        }
    }
}