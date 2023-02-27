package com.ksuniv.pvp_log_app.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ksuniv.pvp_log_app.model.Favorite

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite ORDER BY name")
    fun getAll(): List<Favorite>

    @Query("SELECT name FROM favorite WHERE name = :nickname")
    fun getUser(nickname: String): String

    @Insert
    fun insertFavorite(favorite: Favorite)

    @Delete
    fun deleteFavorite(favorite: Favorite)
}