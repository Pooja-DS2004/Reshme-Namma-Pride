package com.reshmenamma.app.data

import androidx.room.*
import com.reshmenamma.app.data.entities.ClimateEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface ClimateEntryDao {
    @Query("SELECT * FROM climate_entries WHERE batchId = :batchId ORDER BY timestamp DESC")
    fun getEntriesForBatch(batchId: Int): Flow<List<ClimateEntry>>

    @Query("SELECT * FROM climate_entries WHERE batchId = :batchId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestEntry(batchId: Int): Flow<ClimateEntry?>

    @Insert
    suspend fun insertEntry(entry: ClimateEntry)

    @Query("SELECT * FROM climate_entries WHERE batchId = :batchId AND date(timestamp/1000, 'unixepoch') = date('now')")
    fun getTodayEntries(batchId: Int): Flow<List<ClimateEntry>>
}