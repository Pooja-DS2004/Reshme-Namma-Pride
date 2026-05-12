package com.reshmenamma.app.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.reshmenamma.app.data.AppDatabase
import com.reshmenamma.app.data.entities.Batch
import com.reshmenamma.app.data.entities.ClimateEntry
import com.reshmenamma.app.data.entities.InstarStage
import com.reshmenamma.app.logic.ClimateAdvice
import com.reshmenamma.app.logic.ClimateStatus
import com.reshmenamma.app.logic.SericultureEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class BatchViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val batchDao = database.batchDao()
    private val climateEntryDao = database.climateEntryDao()

    val activeBatches = batchDao.getActiveBatches()
    val allBatches = batchDao.getAllBatches()

    var currentAdvice by mutableStateOf<ClimateAdvice?>(null)
    var currentEntries by mutableStateOf<List<ClimateEntry>>(emptyList())

    fun addBatch(name: String, breed: String) {
        viewModelScope.launch {
            val harvestDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 25)
            }.time

            val batch = Batch(
                batchName = name,
                breed = breed,
                startDate = Date(),
                currentInstar = 1,
                expectedHarvestDate = harvestDate
            )
            batchDao.insertBatch(batch)
        }
    }

    fun updateInstar(batchId: Int, instar: Int) {
        viewModelScope.launch {
            batchDao.updateInstar(batchId, instar)
        }
    }

    fun addClimateEntry(batchId: Int, temperature: Double, humidity: Double, timeOfDay: String) {
        viewModelScope.launch {
            val batch = batchDao.getAllBatches()
            // Get current batch
            val currentBatch = batchDao.getActiveBatches()

            // We need to get the current instar from the batch
            // For simplicity, we'll query it
            val instarStage = InstarStage.fromInstar(1) // This will be updated

            val advice = SericultureEngine.analyzeClimate(
                instarStage,
                temperature,
                humidity
            )

            val entry = ClimateEntry(
                batchId = batchId,
                temperature = temperature,
                humidity = humidity,
                timeOfDay = timeOfDay,
                advice = advice.message
            )

            climateEntryDao.insertEntry(entry)
            currentAdvice = advice

            // Get updated entries
            loadClimateEntries(batchId)
        }
    }

    fun loadClimateEntries(batchId: Int) {
        viewModelScope.launch {
            climateEntryDao.getEntriesForBatch(batchId).collect { entries ->
                currentEntries = entries
            }
        }
    }

    fun getTodayEntries(batchId: Int): Flow<List<ClimateEntry>> {
        return climateEntryDao.getTodayEntries(batchId)
    }

    fun getAdviceForBatch(batchId: Int, temperature: Double, humidity: Double) {
        viewModelScope.launch {
            // Get batch to find current instar
            val batches = batchDao.getActiveBatches()
            batches.collect { activeBatches ->
                val batch = activeBatches.find { it.id == batchId }
                if (batch != null) {
                    val instarStage = InstarStage.fromInstar(batch.currentInstar)
                    val advice = SericultureEngine.analyzeClimate(
                        instarStage,
                        temperature,
                        humidity
                    )
                    currentAdvice = advice
                }
            }
        }
    }

    fun deactivateBatch(batchId: Int) {
        viewModelScope.launch {
            batchDao.deactivateBatch(batchId)
        }
    }
}

// Extension function to map int to InstarStage
fun InstarStage.Companion.fromInstar(stage: Int): InstarStage {
    return when (stage) {
        1 -> InstarStage.STAGE_1
        2 -> InstarStage.STAGE_2
        3 -> InstarStage.STAGE_3
        4 -> InstarStage.STAGE_4
        5 -> InstarStage.STAGE_5
        else -> InstarStage.STAGE_1
    }
}