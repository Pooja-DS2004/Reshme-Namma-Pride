package com.reshmenamma.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.reshmenamma.app.data.entities.Batch
import com.reshmenamma.app.data.entities.ClimateEntry
import com.reshmenamma.app.data.entities.CommunityPost
import com.reshmenamma.app.data.entities.ForumComment

@Database(
    entities = [
        Batch::class,
        ClimateEntry::class,
        CommunityPost::class,
        ForumComment::class
    ],
    version = 2, // Updated version
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun batchDao(): BatchDao
    abstract fun climateEntryDao(): ClimateEntryDao
    abstract fun communityPostDao(): CommunityPostDao
    abstract fun forumCommentDao(): ForumCommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reshme_namma_database"
                )
                    .fallbackToDestructiveMigration() // Add this for version changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}