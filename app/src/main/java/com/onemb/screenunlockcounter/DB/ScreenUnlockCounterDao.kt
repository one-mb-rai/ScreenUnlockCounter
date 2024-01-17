package com.onemb.screenunlockcounter.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScreenUnlockCounterDao {
    @Insert
    fun insert(screenUnlockCounter: ScreenUnlockCounter)

    @Query("SELECT * FROM screen_unlock_counters WHERE date = :date")
    fun getCounterByDate(date: String): ScreenUnlockCounter?

    @Query("UPDATE screen_unlock_counters SET counter = :counter WHERE date = :date")
    fun updateCounter(date: String, counter: Int): Int
}