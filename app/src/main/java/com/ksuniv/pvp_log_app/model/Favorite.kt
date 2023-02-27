package com.ksuniv.pvp_log_app.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Favorite(
    @PrimaryKey val id: String,
    @ColumnInfo val name: String,
)