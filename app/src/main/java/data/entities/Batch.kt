package com.reshmenamma.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "batches")
data class Batch(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val batchName: String,
    val breed: String,
    val startDate: Date,
    val currentInstar: Int = 1, // 1-5 stages
    val isActive: Boolean = true,
    val expectedHarvestDate: Date? = null
)

enum class InstarStage(val stage: Int, val stageName: String) {
    STAGE_1(1, "First Instar"),
    STAGE_2(2, "Second Instar"),
    STAGE_3(3, "Third Instar"),
    STAGE_4(4, "Fourth Instar"),
    STAGE_5(5, "Fifth Instar");

    companion object {
        fun fromInstar(stage: Int): InstarStage {
            return entries.find { it.stage == stage } ?: STAGE_1
        }
    }
}
