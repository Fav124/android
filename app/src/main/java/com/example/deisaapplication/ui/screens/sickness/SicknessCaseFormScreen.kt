package com.example.deisaapplication.ui.screens.sickness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.deisaapplication.data.model.*
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*
import java.time.LocalDate

@Composable
fun SicknessCaseFormScreen(
    id: Int?,
    viewModel: SicknessCaseViewModel,
    onBack: () -> Unit,
) {
    val case by viewModel.selectedCase.collectAsState()
    val lookups by viewModel.lookups.collectAsState()
    val actionLoading by viewModel.actionLoading.collectAsState()

    // Form states
    var selectedSantri by remember { mutableStateOf<SantriRef?>(null) }
    var selectedBed by remember { mutableStateOf<BedRef?>(null) }
    var visitDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var complaint by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var actionTaken by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("observed") }
    var selectedMedicines by remember { mutableStateOf<List<MedicineInput>>(emptyList()) }
    var notifyGuardian by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        viewModel.loadLookups()
        if (id != null) {
            viewModel.loadDetail(id)
        } else {
            viewModel.clearSelected()
        }
    }

    LaunchedEffect(case) {
        case?.let { c ->
            selectedSantri = c.santri
            selectedBed = c.bed
            visitDate = c.visitDate ?: LocalDate.now().toString()
            complaint = c.complaint ?: ""
            diagnosis = c.diagnosis ?: ""
            actionTaken = c.actionTaken ?: ""
            notes = c.notes ?: ""
            status = c.status
            selectedMedicines = c.medicines.map { MedicineInput(it.id, it.quantity) }
        }
    }

    Scaffold(
        topBar = { DeisaTopBar(title = if (id == null) "Tambah Kunjungan" else "Ubah Kunjungan", onBack = onBack) },
        containerColor = AppBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedSantri == null || complaint.isBlank()) return@FloatingActionButton
                    
                    val req = SicknessRequest(
                        santriId = selectedSantri!!.id,
                        infirmaryBedId = selectedBed?.id,
                        visitDate = visitDate,
                        complaint = complaint,
                        diagnosis = diagnosis.takeIf { it.isNotBlank() },
                        actionTaken = actionTaken.takeIf { it.isNotBlank() },
                        notes = notes.takeIf { it.isNotBlank() },
                        status = status,
                        medicines = selectedMedicines.takeIf { it.isNotEmpty() },
                        notifyGuardian = notifyGuardian
                    )
                    viewModel.save(id, req) { success -> if (success) onBack() }
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
            Text("Informasi Santri & Waktu", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Primary)
            
            DeisaCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DeisaSearchableDropdown(
                        label = "Santri",
                        items = lookups.santris,
                        selectedItem = selectedSantri,
                        onItemSelected = { selectedSantri = it },
                        itemLabel = { "${it.name} (${it.nis ?: "-"})" }
                    )

                    OutlinedTextField(
                        value = visitDate, onValueChange = { visitDate = it },
                        label = { Text("Tanggal Kunjungan (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                    )
                }
            }

            Text("Pemeriksaan & Diagnosa", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Primary)
            
            DeisaCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = complaint, onValueChange = { complaint = it },
                        label = { Text("Keluhan Utama") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                    )

                    OutlinedTextField(
                        value = diagnosis, onValueChange = { diagnosis = it },
                        label = { Text("Diagnosa Sementara") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                    )

                    OutlinedTextField(
                        value = actionTaken, onValueChange = { actionTaken = it },
                        label = { Text("Tindakan yang Diberikan") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                    )
                    
                    DeisaRadioGroup(
                        label = "Tindak Lanjut / Status",
                        options = listOf(
                            "observed" to "Observasi",
                            "handled" to "Ditangani",
                            "recovered" to "Sembuh",
                            "referred" to "Dirujuk",
                            "rawat_inap" to "Rawat Inap"
                        ),
                        selectedOption = status,
                        onOptionSelected = { status = it }
                    )

                    if (status == "observed" || status == "rawat_inap") {
                        DeisaDropdown(
                            label = "Pilih Kasur UKS",
                            items = lookups.beds,
                            selectedItem = selectedBed,
                            onItemSelected = { selectedBed = it },
                            itemLabel = { it.code }
                        )
                    }
                }
            }

            Text("Obat yang Diberikan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Primary)
            
            DeisaCard {
                MedicineSelector(
                    availableMedicines = lookups.medicines,
                    selectedMedicines = selectedMedicines,
                    onChanged = { selectedMedicines = it }
                )
            }

            Text("Keterangan & Notifikasi", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Primary)
            
            DeisaCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = notes, onValueChange = { notes = it },
                        label = { Text("Catatan Tambahan") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = notifyGuardian,
                            onCheckedChange = { notifyGuardian = it },
                            colors = CheckboxDefaults.colors(checkedColor = Primary, uncheckedColor = MutedText)
                        )
                        Text("Kirim Notifikasi WhatsApp ke Wali", color = OnAppBackground, fontSize = 14.sp)
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }

        if (actionLoading) {
            LoadingBox()
        }
    }
}

@Composable
fun MedicineSelector(
    availableMedicines: List<MedicineLookupRef>,
    selectedMedicines: List<MedicineInput>,
    onChanged: (List<MedicineInput>) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        SectionHeader(title = "Obat-obatan", action = {
            TextButton(onClick = { showDialog = true }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                    Text("Tambah", fontSize = 12.sp)
                }
            }
        })

        if (selectedMedicines.isEmpty()) {
            Text("Belum ada obat dipilih.", fontSize = 12.sp, color = MutedText)
        } else {
            selectedMedicines.forEach { input ->
                val med = availableMedicines.find { it.id == input.id }
                DeisaCard(Modifier.padding(vertical = 4.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(med?.name ?: "Obat #${input.id}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnAppBackground)
                            Text("Jumlah: ${input.quantity} ${med?.unit ?: ""}", fontSize = 12.sp, color = MutedText)
                        }
                        IconButton(onClick = { onChanged(selectedMedicines.filter { it.id != input.id }) }) {
                            Icon(Icons.Filled.Close, null, tint = AppError, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var selectedMed by remember { mutableStateOf<MedicineLookupRef?>(null) }
        var quantity by remember { mutableStateOf("1") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = AppSurface,
            title = { Text("Tambah Obat", color = OnAppBackground) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DeisaDropdown(
                        label = "Pilih Obat",
                        items = availableMedicines,
                        selectedItem = selectedMed,
                        onItemSelected = { selectedMed = it },
                        itemLabel = { "${it.name} (Stok: ${it.stock})" }
                    )
                    OutlinedTextField(
                        value = quantity, onValueChange = { quantity = it },
                        label = { Text("Jumlah") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val q = quantity.toIntOrNull() ?: 1
                    if (selectedMed != null) {
                        onChanged(selectedMedicines + MedicineInput(selectedMed!!.id, q))
                    }
                    showDialog = false
                }) { Text("Tambah", color = Primary) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Batal", color = MutedText) }
            }
        )
    }
}
