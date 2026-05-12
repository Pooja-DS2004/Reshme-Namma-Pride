package com.reshmenamma.app.data

import androidx.room.*
import com.reshmenamma.app.data.entities.Batch
import kotlinx.coroutines.flow.Flow

@Dao
interface BatchDao {
    @Query("SELECT * FROM batches WHERE isActive = 1 ORDER BY startDate DESC")
    fun getActiveBatches(): Flow<List<Batch>>

    @Query("SELECT * FROM batches ORDER BY startDate DESC")
    fun getAllBatches(): Flow<List<Batch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: Batch): Long

    @Update
    suspend fun updateBatch(batch: Batch)

    @Delete
    suspend fun deleteBatch(batch: Batch)

    @Query("UPDATE batches SET currentInstar = :instar WHERE id = :batchId")
    suspend fun updateInstar(batchId: Int, instar: Int)

    @Query("UPDATE batches SET isActive = 0 WHERE id = :batchId")
    suspend fun deactivateBatch(batchId: Int)
}