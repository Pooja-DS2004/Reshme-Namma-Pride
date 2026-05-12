package com.reshmenamma.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.reshmenamma.app.data.entities.InstarStage
import com.reshmenamma.app.logic.ClimateAdvice
import com.reshmenamma.app.logic.ClimateStatus
import com.reshmenamma.app.logic.SericultureEngine
import com.reshmenamma.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdviceScreen(
    advice: ClimateAdvice,
    currentInstar: Int,
    temperature: Double,
    humidity: Double,
    onNavigateBack: () -> Unit,
    onTakeAnotherReading: () -> Unit,
    onViewBatch: () -> Unit
) {
    val instarInfo = InstarStage.fromInstar(currentInstar)
    val idealRange = SericultureEngine.getIdealRange(instarInfo)
    val scrollState = rememberScrollState()

    // Determine status colors and emojis
    val statusColor = when (advice.status) {
        ClimateStatus.SAFE -> SuccessGreen
        ClimateStatus.CAUTION -> WarningOrange
        ClimateStatus.DANGER -> DangerRed
    }

    val statusEmoji = when (advice.status) {
        ClimateStatus.SAFE -> "✅"
        ClimateStatus.CAUTION -> "⚠️"
        ClimateStatus.DANGER -> "🚨"
    }

    val statusTitle = when (advice.status) {
        ClimateStatus.SAFE -> "Conditions Are Optimal!"
        ClimateStatus.CAUTION -> "Conditions Need Attention"
        ClimateStatus.DANGER -> "URGENT: Critical Conditions!"
    }

    val statusGradient = when (advice.status) {
        ClimateStatus.SAFE -> listOf(SuccessGreen, Color(0xFF00A844))
        ClimateStatus.CAUTION -> listOf(WarningOrange, Color(0xFFE65100))
        ClimateStatus.DANGER -> listOf(DangerRed, Color(0xFF8B0000))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Smart Advice", color = SilkWhite)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = SilkWhite)
                    }
                },
                actions = {
                    IconButton(onClick = onViewBatch) {
                        Icon(Icons.Default.Dashboard, "View Batch", tint = SilkWhite)
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
            // Status Header Card with Gradient
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.shadow(12.dp, RoundedCornerShape(24.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(colors = statusGradient)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Animated status icon
                        Text(
                            statusEmoji,
                            fontSize = 56.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            statusTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Instar $currentInstar: ${instarInfo.stageName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Climate Reading Summary
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "📊 Your Reading",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Temperature reading vs ideal
                        ReadingComparisonCard(
                            label = "Temperature",
                            value = "${temperature}°C",
                            idealRange = "${idealRange.minTemp.toInt()}-${idealRange.maxTemp.toInt()}°C",
                            icon = "🌡️",
                            isOptimal = temperature in idealRange.minTemp..idealRange.maxTemp
                        )

                        // Humidity reading vs ideal
                        ReadingComparisonCard(
                            label = "Humidity",
                            value = "${humidity}%",
                            idealRange = "${idealRange.minHumidity.toInt()}-${idealRange.maxHumidity.toInt()}%",
                            icon = "💧",
                            isOptimal = humidity in idealRange.minHumidity..idealRange.maxHumidity
                        )
                    }
                }
            }

            // Detailed Analysis
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "📋 Detailed Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        advice.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )
                }
            }

            // Action Items Card
            if (advice.actions.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = statusColor.copy(alpha = 0.05f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = statusColor.copy(alpha = 0.2f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.TaskAlt,
                                        contentDescription = null,
                                        tint = statusColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "🔧 Recommended Actions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        advice.actions.forEachIndexed { index, action ->
                            ActionItem(
                                number = index + 1,
                                action = action,
                                statusColor = statusColor,
                                isLast = index == advice.actions.lastIndex
                            )
                        }
                    }
                }
            }

            // Prevention Tips
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SilkGoldLight.copy(alpha = 0.3f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💡", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Prevention Tips for Instar $currentInstar",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SilkGoldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val preventionTips = getPreventionTips(currentInstar)
                    preventionTips.forEach { tip ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                "★",
                                color = SilkGold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                tip,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Emergency Contacts (for Danger status)
            if (advice.status == ClimateStatus.DANGER) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DangerRed.copy(alpha = 0.08f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.border(
                        width = 2.dp,
                        color = DangerRed.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(20.dp)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "🆘 Emergency Contacts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DangerRed
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        EmergencyContactRow(
                            title = "Sericulture Helpline",
                            number = "1800-XXX-XXXX",
                            available = "24/7"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        EmergencyContactRow(
                            title = "Nearest Research Center",
                            number = "080-XXXX-XXXX",
                            available = "9 AM - 5 PM"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        EmergencyContactRow(
                            title = "District Sericulture Officer",
                            number = "Mobile: XXXXX-XXXXX",
                            available = "Office Hours"
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Mulberry)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = Mulberry,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Back", color = Mulberry)
                }

                Button(
                    onClick = onTakeAnotherReading,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Mulberry),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Reading")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ReadingComparisonCard(
    label: String,
    value: String,
    idealRange: String,
    icon: String,
    isOptimal: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isOptimal) SuccessGreen.copy(alpha = 0.05f)
            else DangerRed.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isOptimal) SuccessGreen else DangerRed
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "Ideal: $idealRange",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )

            if (!isOptimal) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DangerRed.copy(alpha = 0.1f)
                ) {
                    Text(
                        if (label == "Temperature")
                            if (value.replace("°C","").toDoubleOrNull() ?: 0.0 > idealRange.split("-")[1].replace("°C","").toDoubleOrNull() ?: 0.0)
                                "Too High" else "Too Low"
                        else
                            if (value.replace("%","").toDoubleOrNull() ?: 0.0 > idealRange.split("-")[1].replace("%","").toDoubleOrNull() ?: 0.0)
                                "Too High" else "Too Low",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = DangerRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ActionItem(
    number: Int,
    action: String,
    statusColor: Color,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Number circle
        Surface(
            shape = CircleShape,
            color = statusColor.copy(alpha = 0.15f),
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "$number",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                action,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                lineHeight = 20.sp
            )

            if (!isLast) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(
                    color = statusColor.copy(alpha = 0.1f),
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
fun EmergencyContactRow(
    title: String,
    number: String,
    available: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Phone,
            contentDescription = null,
            tint = DangerRed,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                number,
                style = MaterialTheme.typography.bodySmall,
                color = DangerRed,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Available: $available",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

// Prevention tips based on instar stage
fun getPreventionTips(instar: Int): List<String> {
    return when (instar) {
        1 -> listOf(
            "Maintain high humidity (80-90%) for young larvae",
            "Avoid direct air currents on hatchlings",
            "Feed tender mulberry leaves only",
            "Keep rearing trays covered with paraffin paper",
            "Disinfect rearing house before starting new batch"
        )
        2 -> listOf(
            "Begin gentle ventilation during warm hours",
            "Remove uneaten leaves promptly to prevent mold",
            "Maintain uniform temperature across all trays",
            "Check for signs of disease daily",
            "Ensure proper spacing between larvae"
        )
        3 -> listOf(
            "Increase leaf quantity as larvae grow rapidly",
            "Clean rearing beds every 2-3 days",
            "Monitor for Flacherie disease (lethargic worms)",
            "Maintain good air circulation without drafts",
            "Use lime powder near trays to control humidity"
        )
        4 -> listOf(
            "Maximum feeding stage - provide ample leaves",
            "Ensure good ventilation at all times",
            "Watch for overcrowding - separate if needed",
            "Maintain hygiene - remove dead/diseased worms",
            "Begin reducing humidity gradually"
        )
        5 -> listOf(
            "Prepare spinning trays (chandrike) in advance",
            "Stop feeding when worms stop eating",
            "Maintain slightly lower humidity for cocooning",
            "Provide adequate space for spinning",
            "Harvest cocoons after 5-7 days of spinning"
        )
        else -> listOf(
            "Follow standard sericulture practices",
            "Consult local sericulture extension officer",
            "Maintain rearing house hygiene"
        )
    }
}
