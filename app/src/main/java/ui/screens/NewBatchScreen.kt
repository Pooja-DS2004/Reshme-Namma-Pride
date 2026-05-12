package com.reshmenamma.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reshmenamma.app.viewmodel.BatchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBatchScreen(
    onNavigateBack: () -> Unit,
    viewModel: BatchViewModel = viewModel()
) {
    var batchName by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Batch") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = batchName,
                onValueChange = { batchName = it },
                label = { Text("Batch Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Silkworm Breed") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("e.g., Bivoltine Hybrid, Multivoltine") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (batchName.isBlank() || breed.isBlank()) {
                        showError = "Please fill all fields"
                    } else {
                        viewModel.addBatch(batchName, breed)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start New Batch")
            }

            if (showError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    showError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}