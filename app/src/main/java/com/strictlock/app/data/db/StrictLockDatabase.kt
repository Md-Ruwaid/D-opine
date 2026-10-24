package com.strictlock.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.strictlock.app.data.model.RuleEntity

@Database(
    entities = [RuleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StrictLockDatabase : RoomDatabase() {

    abstract fun ruleDao(): RuleDao

    companion object {
        @Volatile
        private var INSTANCE: StrictLockDatabase? = null

        fun getInstance(context: Context): StrictLockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StrictLockDatabase::class.java,
                    "strictlock.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
