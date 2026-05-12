@file:OptIn(ExperimentalMaterial3Api::class)
package com.reshmenamma.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reshmenamma.app.data.entities.InstarStage
import com.reshmenamma.app.logic.ClimateAdvice
import com.reshmenamma.app.logic.ClimateStatus
import com.reshmenamma.app.logic.SericultureEngine
import com.reshmenamma.app.ui.theme.*
import com.reshmenamma.app.viewmodel.BatchViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BatchTrackerScreen(
    batchId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToClimateEntry: () -> Unit,
    onNavigateToAdvice: () -> Unit,
    viewModel: BatchViewModel = viewModel()
) {
    val allBatches by viewModel.allBatches.collectAsState(initial = emptyList())
    val batch = allBatches.find { it.id == batchId }
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    LaunchedEffect(batchId) {
        viewModel.loadClimateEntries(batchId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(batch?.batchName ?: "Batch Details", color = SilkWhite)
                        Text(
                            "Instar ${batch?.currentInstar ?: "?"} • ${batch?.breed ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SilkGoldLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = SilkWhite)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToClimateEntry) {
                        Icon(Icons.Default.AddChart, "Add Climate Entry", tint = SilkWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Mulberry
                )
            )
        },
        containerColor = SilkWhite
    ) { padding ->
        if (batch == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Mulberry)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Climate Dial Card
            ClimateDialCard(batch.currentInstar)

            // Instar Stage Selector
            InstarSelectorCard(
                currentStage = batch.currentInstar,
                onStageSelected = { stage ->
                    viewModel.updateInstar(batchId, stage)
                }
            )

            // Climate Entry Form
            ClimateEntryForm(
                batchId = batchId,
                viewModel = viewModel
            )

            // Smart Advice
            viewModel.currentAdvice?.let { advice ->
                Box(modifier = Modifier.clickable { onNavigateToAdvice() }) {
                    PremiumAdviceCard(advice = advice)
                }
            }

            // Harvest Timer
            batch.expectedHarvestDate?.let { harvestDate ->
                PremiumHarvestCard(harvestDate = harvestDate)
            }

            // Batch Info
            BatchInfoCard(batch = batch, dateFormat = dateFormat)
        }
    }
}

@Composable
fun ClimateDialCard(currentInstar: Int) {
    val idealRange = SericultureEngine.getIdealRange(InstarStage.fromInstar(currentInstar))

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "🎯 Ideal Climate Range",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "Instar $currentInstar: ${InstarStage.fromInstar(currentInstar).stageName}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Temperature Dial
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = DangerRed.copy(alpha = 0.1f),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("🌡️", fontSize = 24.sp)
                            Text(
                                "${idealRange.minTemp.toInt()}-${idealRange.maxTemp.toInt()}°C",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DangerRed
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Temperature", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }

                // Humidity Dial
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF2196F3).copy(alpha = 0.1f),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("💧", fontSize = 24.sp)
                            Text(
                                "${idealRange.minHumidity.toInt()}-${idealRange.maxHumidity.toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Humidity", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun InstarSelectorCard(
    currentStage: Int,
    onStageSelected: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "🐛 Silkworm Growth Stage",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..5).forEach { stage ->
                    val stageInfo = InstarStage.fromInstar(stage)
                    val isSelected = stage == currentStage

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onStageSelected(stage) }
                            .background(
                                if (isSelected) Mulberry.copy(alpha = 0.1f)
                                else Color.Transparent
                            )
                            .padding(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) Mulberry else DividerColor,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "I$stage",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) SilkWhite else TextSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            stageInfo.stageName.replace(" Instar", ""),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Mulberry else TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClimateEntryForm(
    batchId: Int,
    viewModel: BatchViewModel
) {
    var temperature by remember { mutableStateOf("") }
    var humidity by remember { mutableStateOf("") }
    var timeOfDay by remember { mutableStateOf("Morning") }
    var showError by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "📝 Log Climate Reading",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = temperature,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null) {
                            temperature = it
                        }
                    },
                    label = { Text("Temp °C") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Mulberry,
                        focusedLabelColor = Mulberry
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = humidity,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null) {
                            humidity = it
                        }
                    },
                    label = { Text("Humidity %") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Mulberry,
                        focusedLabelColor = Mulberry
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (showError.isNotEmpty()) {
                Text(
                    showError,
                    color = DangerRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Time of Day", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Morning", "Afternoon", "Evening").forEach { time ->
                    FilterChip(
                        selected = timeOfDay == time,
                        onClick = { timeOfDay = time },
                        label = { Text(time) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Mulberry,
                            selectedLabelColor = SilkWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val temp = temperature.toDoubleOrNull()
                    val hum = humidity.toDoubleOrNull()

                    when {
                        temp == null || hum == null -> showError = "Enter valid numbers"
                        temp !in 10.0..45.0 -> showError = "Temp: 10°C to 45°C only"
                        hum !in 20.0..100.0 -> showError = "Humidity: 20% to 100% only"
                        else -> {
                            viewModel.addClimateEntry(batchId, temp, hum, timeOfDay)
                            temperature = ""
                            humidity = ""
                            showError = ""
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Mulberry),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Reading", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PremiumAdviceCard(advice: ClimateAdvice) {
    val backgroundColor = when (advice.status) {
        ClimateStatus.SAFE -> SuccessGreen.copy(alpha = 0.08f)
        ClimateStatus.CAUTION -> WarningOrange.copy(alpha = 0.08f)
        ClimateStatus.DANGER -> DangerRed.copy(alpha = 0.08f)
    }

    val accentColor = when (advice.status) {
        ClimateStatus.SAFE -> SuccessGreen
        ClimateStatus.CAUTION -> WarningOrange
        ClimateStatus.DANGER -> DangerRed
    }

    val emoji = when (advice.status) {
        ClimateStatus.SAFE -> "✅"
        ClimateStatus.CAUTION -> "⚠️"
        ClimateStatus.DANGER -> "🚨"
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier.shadow(8.dp, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Smart Advice",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                advice.message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                lineHeight = 22.sp
            )

            if (advice.actions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "🔧 Recommended Actions:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                advice.actions.forEach { action ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = accentColor.copy(alpha = 0.2f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            action,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumHarvestCard(harvestDate: Date) {
    val daysUntilHarvest = maxOf(0, (harvestDate.time - Date().time) / (1000 * 60 * 60 * 24)).toInt()
    val harvestColor = when {
        daysUntilHarvest <= 0 -> SuccessGreen
        daysUntilHarvest <= 3 -> WarningOrange
        else -> Mulberry
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = harvestColor.copy(alpha = 0.08f)),
        modifier = Modifier.shadow(4.dp, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("⏰", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Coon Harvest Timer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = harvestColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "$daysUntilHarvest",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = harvestColor
            )
            Text(
                "Days Remaining",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = maxOf(0f, minOf(1f, 1f - (daysUntilHarvest / 25f))),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = harvestColor,
                trackColor = harvestColor.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                when {
                    daysUntilHarvest <= 0 -> "✅ Time to transfer to spinning trays!"
                    daysUntilHarvest <= 3 -> "⚠️ Prepare spinning trays now!"
                    else -> "Continue monitoring and care"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = harvestColor,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BatchInfoCard(batch: com.reshmenamma.app.data.entities.Batch, dateFormat: SimpleDateFormat) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "📋 Batch Info",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = DividerColor)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow("Breed", batch.breed)
            InfoRow("Started", dateFormat.format(batch.startDate))
            InfoRow("Harvest Date", batch.expectedHarvestDate?.let { dateFormat.format(it) } ?: "TBD")
            InfoRow("Status", if (batch.isActive) "🟢 Active" else "⚫ Completed")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}
