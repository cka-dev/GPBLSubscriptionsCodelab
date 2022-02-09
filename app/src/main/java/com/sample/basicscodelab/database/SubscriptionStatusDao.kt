package com.sample.basicscodelab.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sample.basicscodelab.data.SubscriptionStatus

@Dao
interface SubscriptionStatusDao {

    @Query("SELECT * FROM subscriptions")
    fun getAll():LiveData<List<SubscriptionStatus>>

    @Insert()
    fun insertAll(comments: List<SubscriptionStatus>)

    @Query("DELETE FROM subscriptions")
    fun deleteAll()
}