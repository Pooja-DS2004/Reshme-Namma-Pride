package com.reshmenamma.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reshmenamma.app.data.entities.ClimateEntry
import com.reshmenamma.app.data.entities.InstarStage
import com.reshmenamma.app.logic.ClimateAdvice
import com.reshmenamma.app.logic.ClimateStatus
import com.reshmenamma.app.logic.SericultureEngine
import com.reshmenamma.app.ui.theme.*
import com.reshmenamma.app.viewmodel.BatchViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClimateEntryScreen(
    batchId: Int,
    currentInstar: Int,
    onNavigateBack: () -> Unit,
    onAdviceGenerated: (ClimateAdvice) -> Unit,
    viewModel: BatchViewModel = viewModel()
) {
    var temperature by remember { mutableStateOf("") }
    var humidity by remember { mutableStateOf("") }
    var timeOfDay by remember { mutableStateOf("Morning") }
    var showError by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }
    var showGuidelines by remember { mutableStateOf(false) }

    val instarInfo = InstarStage.fromInstar(currentInstar)
    val idealRange = SericultureEngine.getIdealRange(instarInfo)
    val scrollState = rememberScrollState()

    // Get today's entries
    val todayEntries by viewModel.getTodayEntries(batchId).collectAsState(initial = emptyList<ClimateEntry>())
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Climate Entry", color = SilkWhite)
                        Text(
                            "Instar $currentInstar: ${instarInfo.name}",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Mulberry
                )
            )
        },
        containerColor = SilkWhite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Today's Previous Entries
            if (todayEntries.isNotEmpty()) {
                TodayEntriesCard(
                    entries = todayEntries,
                    dateFormat = dateFormat,
                    idealRange = idealRange
                )
            }

            // Entry Form Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "📝 New Climate Reading",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        // Guidelines toggle
                        IconButton(onClick = { showGuidelines = !showGuidelines }) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Guidelines",
                                tint = if (showGuidelines) Mulberry else TextSecondary
                            )
                        }
                    }

                    // Guidelines expandable section
                    AnimatedVisibility(visible = showGuidelines) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Mulberry.copy(alpha = 0.05f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "📋 Reading Guidelines",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Mulberry
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                GuidelineRow("Take readings 3 times daily")
                                GuidelineRow("Morning: 6-8 AM before feeding")
                                GuidelineRow("Afternoon: 12-2 PM peak heat")
                                GuidelineRow("Evening: 5-7 PM before sunset")
                                GuidelineRow("Place thermometer at tray level")
                                GuidelineRow("Wait 5 mins for accurate reading")
                            }
                        }
                    }

                    // Temperature Input with Visual Dial
                    Text(
                        "🌡️ Temperature",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Temperature Quick Select Chips
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = temperature,
                                onValueChange = {
                                    if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                        temperature = it
                                        showError = null
                                    }
                                },
                                label = { Text("Enter °C") },
                                placeholder = { Text("e.g., 26.5") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Thermostat,
                                        contentDescription = null,
                                        tint = DangerRed
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Mulberry,
                                    focusedLabelColor = Mulberry,
                                    cursorColor = Mulberry
                                ),
                                singleLine = true,
                                isError = showError?.contains("Temperature") == true
                            )

                            // Quick temperature buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("20", "23", "25", "27", "30").forEach { temp ->
                                    FilterChip(
                                        selected = temperature == temp,
                                        onClick = { temperature = temp },
                                        label = {
                                            Text(
                                                "$temp°",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Mulberry.copy(alpha = 0.2f),
                                            selectedLabelColor = Mulberry
                                        )
                                    )
                                }
                            }
                        }

                        // Visual Temperature Gauge
                        TemperatureGauge(
                            temperature = temperature.toDoubleOrNull() ?: 25.0,
                            idealMin = idealRange.minTemp,
                            idealMax = idealRange.maxTemp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Humidity Input with Visual Dial
                    Text(
                        "💧 Humidity",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = humidity,
                                onValueChange = {
                                    if (it.isEmpty() || it.toDoubleOrNull() != null) {
                                        humidity = it
                                        showError = null
                                    }
                                },
                                label = { Text("Enter %") },
                                placeholder = { Text("e.g., 80") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = Color(0xFF2196F3)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Mulberry,
                                    focusedLabelColor = Mulberry,
                                    cursorColor = Mulberry
                                ),
                                singleLine = true,
                                isError = showError?.contains("Humidity") == true
                            )

                            // Quick humidity buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("60", "70", "80", "85", "90").forEach { hum ->
                                    FilterChip(
                                        selected = humidity == hum,
                                        onClick = { humidity = hum },
                                        label = {
                                            Text(
                                                "$hum%",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF2196F3).copy(alpha = 0.2f),
                                            selectedLabelColor = Color(0xFF2196F3)
                                        )
                                    )
                                }
                            }
                        }

                        // Visual Humidity Gauge
                        HumidityGauge(
                            humidity = humidity.toDoubleOrNull() ?: 80.0,
                            idealMin = idealRange.minHumidity,
                            idealMax = idealRange.maxHumidity
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Time of Day Selector
                    Text(
                        "🕐 Time of Reading",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        data class TimeSlot(
                            val value: String,
                            val icon: String,
                            val label: String,
                            val timeRange: String
                        )

                        listOf(
                            TimeSlot("Morning", "🌅", "Morning", "6-8 AM"),
                            TimeSlot("Afternoon", "☀️", "Afternoon", "12-2 PM"),
                            TimeSlot("Evening", "🌆", "Evening", "5-7 PM")
                        ).forEach { slot ->
                            val isSelected = timeOfDay == slot.value

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .then(
                                        if (isSelected) Modifier.background(
                                            Brush.verticalGradient(
                                                colors = listOf(Mulberry, MulberryDark)
                                            ),
                                            RoundedCornerShape(12.dp)
                                        )
                                        else Modifier
                                    ),
                                onClick = { timeOfDay = slot.value },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Mulberry
                                    else SurfaceLight
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(slot.icon, fontSize = 24.sp)
                                    Text(
                                        slot.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) SilkWhite else TextPrimary
                                    )
                                    Text(
                                        slot.timeRange,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) SilkGoldLight else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Already logged indicator
                    val alreadyLogged = todayEntries.any { it.timeOfDay == timeOfDay }
                    if (alreadyLogged) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = WarningOrange.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = WarningOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "You've already logged a $timeOfDay reading today. You can update it.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarningOrange
                                )
                            }
                        }
                    }

                    // Error display
                    showError?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = DangerRed.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = DangerRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    error,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DangerRed
                                )
                            }
                        }
                    }

                    // Success animation
                    AnimatedVisibility(visible = showSuccess) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = SuccessGreen.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "✅ Climate reading logged successfully!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            val temp = temperature.toDoubleOrNull()
                            val hum = humidity.toDoubleOrNull()

                            when {
                                temp == null || hum == null -> {
                                    showError = "Please enter valid numbers for both fields"
                                }
                                temp < 10.0 -> {
                                    showError = "Temperature too low! Minimum is 10°C"
                                }
                                temp > 45.0 -> {
                                    showError = "Temperature too high! Maximum is 45°C"
                                }
                                hum < 20.0 -> {
                                    showError = "Humidity too low! Minimum is 20%"
                                }
                                hum > 100.0 -> {
                                    showError = "Humidity too high! Maximum is 100%"
                                }
                                else -> {
                                    // Generate advice
                                    val advice = SericultureEngine.analyzeClimate(
                                        instarInfo,
                                        temp,
                                        hum
                                    )

                                    // Save to database
                                    viewModel.addClimateEntry(batchId, temp, hum, timeOfDay)

                                    // Show success
                                    showSuccess = true
                                    showError = null

                                    // Navigate to advice after delay
                                    onAdviceGenerated(advice)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Mulberry),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Log Climate Reading",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Ideal Range Reference Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Mulberry.copy(alpha = 0.05f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "🎯 Target Range for Instar $currentInstar",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Mulberry
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${idealRange.minTemp.toInt()}°C - ${idealRange.maxTemp.toInt()}°C",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DangerRed
                            )
                            Text(
                                "Temperature",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${idealRange.minHumidity.toInt()}% - ${idealRange.maxHumidity.toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                            Text(
                                "Humidity",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TemperatureGauge(
    temperature: Double,
    idealMin: Double,
    idealMax: Double
) {
    val gaugeColor = when {
        temperature in idealMin..idealMax -> SuccessGreen
        temperature in (idealMin - 3)..(idealMin - 0.1) -> WarningOrange
        temperature in (idealMax + 0.1)..(idealMax + 3) -> WarningOrange
        else -> DangerRed
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        // Thermometer visual
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Fill level
            val fillPercent = (((temperature - 10) / 35.0).coerceIn(0.0, 1.0)).toFloat()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fillPercent)
                    .background(gaugeColor)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Current temperature
        Text(
            "${temperature.toInt()}°C",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = gaugeColor
        )
    }
}

@Composable
fun HumidityGauge(
    humidity: Double,
    idealMin: Double,
    idealMax: Double
) {
    val gaugeColor = when {
        humidity in idealMin..idealMax -> SuccessGreen
        humidity in (idealMin - 10)..(idealMin - 0.1) -> WarningOrange
        humidity in (idealMax + 0.1)..(idealMax + 10) -> WarningOrange
        else -> DangerRed
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        // Humidity bar visual
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.BottomCenter
        ) {
            val fillPercent = ((humidity / 100.0).coerceIn(0.0, 1.0)).toFloat()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fillPercent)
                    .background(gaugeColor)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Current humidity
        Text(
            "${humidity.toInt()}%",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = gaugeColor
        )
    }
}

@Composable
fun TodayEntriesCard(
    entries: List<ClimateEntry>,
    dateFormat: SimpleDateFormat,
    idealRange: com.reshmenamma.app.logic.ClimateRange
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = LeafGreen.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    tint = LeafGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Today's Readings",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = LeafGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            entries.forEach { entry ->
                val tempStatus = when {
                    entry.temperature in idealRange.minTemp..idealRange.maxTemp -> SuccessGreen
                    entry.temperature in (idealRange.minTemp - 3)..(idealRange.maxTemp + 3) -> WarningOrange
                    else -> DangerRed
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = tempStatus.copy(alpha = 0.2f),
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            entry.timeOfDay,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        "${entry.temperature}°C / ${entry.humidity}%",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        dateFormat.format(entry.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun GuidelineRow(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("•", color = Mulberry, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}