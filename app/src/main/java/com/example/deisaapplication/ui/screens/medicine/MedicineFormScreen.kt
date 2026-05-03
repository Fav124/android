package com.example.deisaapplication.ui.screens.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var formulation by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(id) {
        if (id != null) {
            viewModel.loadDetail(id)
        } else {
            viewModel.clearSelected()
        }
    }

    LaunchedEffect(medicine) {
        medicine?.let { m ->
            code = m.code
            name = m.name
            category = m.kategori
            formulation = m.formulation
            unit = m.unit
            stock = m.stock.toString()
            minStock = m.minimumStock.toString()
            expiryDate = m.expiryDate ?: ""
            location = m.location ?: ""
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
                        "kode_obat" to code,
                        "nama_obat" to name,
                        "kategori" to category,
                        "bentuk_sediaan" to formulation,
                        "satuan" to unit,
                        "stok" to (stock.toIntOrNull() ?: 0),
                        "stok_minimum" to (minStock.toIntOrNull() ?: 0),
                        "tanggal_kadaluarsa" to expiryDate,
                        "lokasi_penyimpanan" to location,
                        "deskripsi" to description,
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section 1: Identitas
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Badge, null, tint = Primary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Identitas Obat", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = OnAppBackground)
                }
                DeisaCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = code, onValueChange = { code = it },
                            label = { Text("Kode Obat (e.g. OBT-001)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                        )
                        OutlinedTextField(
                            value = name, onValueChange = { name = it },
                            label = { Text("Nama Obat") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = category, onValueChange = { category = it },
                                label = { Text("Kategori") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                            )
                            OutlinedTextField(
                                value = formulation, onValueChange = { formulation = it },
                                label = { Text("Bentuk Sediaan") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                            )
                        }
                        OutlinedTextField(
                            value = unit, onValueChange = { unit = it },
                            label = { Text("Satuan (Tablet/Botol/Pcs)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                        )
                    }
                }
            }
            
            // Section 2: Inventori
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Inventory, null, tint = Secondary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Stok & Lokasi", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = OnAppBackground)
                }
                DeisaCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = stock, onValueChange = { stock = it },
                                label = { Text("Stok") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                            )
                            OutlinedTextField(
                                value = minStock, onValueChange = { minStock = it },
                                label = { Text("Stok Minimum") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                            )
                        }
                        OutlinedTextField(
                            value = location, onValueChange = { location = it },
                            label = { Text("Lokasi Penyimpanan") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                        )
                    }
                }
            }

            // Section 3: Kadaluarsa
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Event, null, tint = AppWarning, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Kadaluarsa & Keterangan", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = OnAppBackground)
                }
                DeisaCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DeisaDatePicker(
                            label = "Tanggal Kadaluarsa",
                            value = expiryDate,
                            onValueChange = { expiryDate = it }
                        )
                        OutlinedTextField(
                            value = description, onValueChange = { description = it },
                            label = { Text("Deskripsi") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppWarning, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(80.dp))
        }
        
        if (state.isLoading) {
            LoadingBox()
        }
    }
}
