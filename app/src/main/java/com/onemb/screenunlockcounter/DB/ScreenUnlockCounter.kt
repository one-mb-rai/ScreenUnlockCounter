package com.onemb.screenunlockcounter.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screen_unlock_counters")
data class ScreenUnlockCounter(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val counter: Int
)