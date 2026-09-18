package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.*

@Database(
    entities = [
        Job::class,
        CandidateProfile::class,
        JobApplication::class,
        GovtJob::class,
        ResumeVersion::class,
        JobAlert::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
    abstract fun candidateProfileDao(): CandidateProfileDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun govtJobDao(): GovtJobDao
    abstract fun resumeDao(): ResumeDao
    abstract fun jobAlertDao(): JobAlertDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jobsetu_ai_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
