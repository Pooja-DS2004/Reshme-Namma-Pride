package com.reshmenamma.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reshmenamma.app.data.entities.Batch
import com.reshmenamma.app.data.entities.InstarStage
import com.reshmenamma.app.ui.theme.*
import com.reshmenamma.app.viewmodel.BatchViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToBatch: (Int) -> Unit,
    onNavigateToNewBatch: () -> Unit,
    onNavigateToCommunity: () -> Unit = {},
    viewModel: BatchViewModel = viewModel()
) {
    val activeBatches by viewModel.activeBatches.collectAsState(initial = emptyList())
    var showAllBatches by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            DashboardTopBar(onNavigateToCommunity = onNavigateToCommunity)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewBatch,
                containerColor = Mulberry,
                contentColor = SilkWhite,
                shape = CircleShape,
                modifier = Modifier.shadow(8.dp, CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Batch", modifier = Modifier.size(28.dp))
            }
        },
        containerColor = SilkWhite
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            StatsSummarySection(activeBatches)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = if (showAllBatches) "All Batches" else "Active Batches", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                FilterChip(
                    selected = showAllBatches,
                    onClick = { showAllBatches = !showAllBatches },
                    label = { Text(if (showAllBatches) "Show Active" else "Show All") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MulberryLight.copy(alpha = 0.3f), selectedLabelColor = Mulberry)
                )
            }
            if (activeBatches.isEmpty()) {
                EmptyStateView(onNavigateToNewBatch)
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(activeBatches, key = { it.id }) { batch ->
                        PremiumBatchCard(batch = batch, onClick = { onNavigateToBatch(batch.id) }, onComplete = { viewModel.deactivateBatch(batch.id) })
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(onNavigateToCommunity: () -> Unit = {}) {
    Surface(modifier = Modifier.fillMaxWidth(), color = Mulberry, shadowElevation = 8.dp) {
        Column(
            modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(colors = listOf(MulberryDark, Mulberry)))
                .padding(horizontal = 20.dp, vertical = 16.dp).statusBarsPadding()
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "🐛 Reshme-Namma", style = MaterialTheme.typography.headlineLarge, color = SilkWhite, fontWeight = FontWeight.Bold)
                    Text(text = "Sericulture Guard", style = MaterialTheme.typography.bodyMedium, color = SilkGoldLight)
                }
                TextButton(onClick = onNavigateToCommunity, colors = ButtonDefaults.textButtonColors(contentColor = SilkWhite)) {
                    Icon(Icons.Default.Groups, contentDescription = "Community", modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Community", color = SilkWhite)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Karnataka's Silk Pride", style = MaterialTheme.typography.labelSmall, color = SilkGoldLight.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun StatsSummarySection(batches: List<Batch>) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("Active Batches", "${batches.size}", Icons.Default.Inventory, Mulberry, Modifier.weight(1f))
        StatCard("Ready to Harvest", "${batches.count { it.currentInstar >= 5 }}", Icons.Default.Spa, LeafGreen, Modifier.weight(1f))
        StatCard("Growing", "${batches.count { it.currentInstar in 2..4 }}", Icons.Default.TrendingUp, SilkGold, Modifier.weight(1f))
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, color = color.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun PremiumBatchCard(batch: Batch, onClick: () -> Unit, onComplete: () -> Unit) {
    val instarInfo = InstarStage.fromInstar(batch.currentInstar)
    val daysSinceStart = ((Date().time - batch.startDate.time) / (1000 * 60 * 60 * 24)).toInt()
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val instarColor = when (batch.currentInstar) {
        1 -> Mulberry; 2 -> Color(0xFF9C27B0); 3 -> Color(0xFF2196F3); 4 -> SilkGold; 5 -> LeafGreen; else -> Mulberry
    }
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(instarColor.copy(alpha = 0.3f))) {
                Box(modifier = Modifier.fillMaxWidth(batch.currentInstar / 5f).height(4.dp).background(instarColor))
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = batch.batchName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = batch.breed, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                    Surface(shape = RoundedCornerShape(12.dp), color = instarColor.copy(alpha = 0.1f), modifier = Modifier.padding(start = 8.dp)) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Instar", style = MaterialTheme.typography.labelSmall, color = instarColor)
                            Text(text = "${batch.currentInstar}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = instarColor)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = instarInfo.name, style = MaterialTheme.typography.labelLarge, color = instarColor, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    for (i in 1..5) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (i <= batch.currentInstar) instarColor else instarColor.copy(alpha = 0.2f)))
                        if (i < 5) Box(modifier = Modifier.width(24.dp).height(2.dp).background(if (i < batch.currentInstar) instarColor else instarColor.copy(alpha = 0.2f)))
                    }
                }
                Divider(color = DividerColor)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = dateFormat.format(batch.startDate), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Day $daysSinceStart", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = LeafGreen.copy(alpha = 0.1f)) {
                        Text(text = "● Active", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = LeafGreen)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(onNavigateToNewBatch: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text(text = "🦋", fontSize = 72.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "No Active Batches", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Start your sericulture journey by creating a new silkworm batch", style = MaterialTheme.typography.bodyLarge, color = TextSecondary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onNavigateToNewBatch, colors = ButtonDefaults.buttonColors(containerColor = Mulberry), shape = RoundedCornerShape(16.dp), modifier = Modifier.height(52.dp)) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start New Batch", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}