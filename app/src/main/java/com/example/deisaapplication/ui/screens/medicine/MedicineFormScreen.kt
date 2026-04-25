package com.example.deisaapplication.ui.screens.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*

@Composable
fun MedicineFormScreen(
    id: Int?,
    viewModel: MedicineViewModel,
    onBack: () -> Unit,
) {
    val medicine by viewModel.selectedMedicine.collectAsState()
    val state by viewModel.state.collectAsState()

    // Form states
    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(id) {
        if (id != null) {
            viewModel.loadDetail(id)
        }
    }

    LaunchedEffect(medicine) {
        medicine?.let { m ->
            name = m.name
            unit = m.unit
            stock = m.stock.toString()
            minStock = m.minimumStock.toString()
            expiryDate = m.expiryDate ?: ""
            description = m.description ?: ""
        }
    }

    Scaffold(
        topBar = { DeisaTopBar(title = if (id == null) "Tambah Obat" else "Ubah Obat", onBack = onBack) },
        containerColor = AppBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val data = mapOf(
                        "name" to name,
                        "unit" to unit,
                        "stock" to (stock.toIntOrNull() ?: 0),
                        "minimum_stock" to (minStock.toIntOrNull() ?: 0),
                        "expiry_date" to expiryDate.takeIf { it.isNotBlank() },
                        "description" to description.takeIf { it.isNotBlank() },
                    )
                    viewModel.save(id, data) { success -> if (success) onBack() }
                },
                containerColor = Primary,
                contentColor = AppBackground
            ) {
                Icon(Icons.Filled.Save, null)
            }
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nama Obat") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            OutlinedTextField(
                value = unit, onValueChange = { unit = it },
                label = { Text("Satuan (Tablet/Botol/Pcs)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = stock, onValueChange = { stock = it },
                    label = { Text("Stok") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                )
                OutlinedTextField(
                    value = minStock, onValueChange = { minStock = it },
                    label = { Text("Stok Minimum") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                )
            }

            OutlinedTextField(
                value = expiryDate, onValueChange = { expiryDate = it },
                label = { Text("Tanggal Kadaluarsa (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )

            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
            )
            
            Spacer(Modifier.height(80.dp))
        }
        
        if (state.isLoading) {
            LoadingBox()
        }
    }
}
