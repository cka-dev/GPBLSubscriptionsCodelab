package com.sample.basicscodelab.database

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sample.basicscodelab.data.SubscriptionStatus

@Database(entities = [SubscriptionStatus::class], version = 1)
abstract class SubscriptionDatabase : RoomDatabase() {
    abstract fun subscriptionStatusDao(): SubscriptionStatusDao

    companion object {

        @Volatile
        private var INSTANCE: SubscriptionDatabase? = null

        @VisibleForTesting
        private val DATABASE_NAME = "subscriptions-db"

        fun getInstance(context: Context): SubscriptionDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext).also {
                    INSTANCE = it
                }
            }

        /**
         * Set up the database configuration.
         * The SQLite database is only created when it's accessed for the first time.
         */
        private fun buildDatabase(appContext: Context): SubscriptionDatabase {
            return Room.databaseBuilder(appContext, SubscriptionDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
