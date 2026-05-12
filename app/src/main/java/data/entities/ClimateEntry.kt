package com.reshmenamma.app.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "climate_entries",
    foreignKeys = [
        ForeignKey(
            entity = Batch::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ClimateEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val batchId: Int,
    val temperature: Double,
    val humidity: Double,
    val timeOfDay: String, // "Morning", "Afternoon", "Evening"
    val timestamp: Date = Date(),
    val advice: String = ""
)