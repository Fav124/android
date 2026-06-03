package com.example.deisaapplication.ui.screens.sickness

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.*
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*
import java.time.LocalDate

@OptIn(ExperimentalAnimationApi::class)
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
    var selectedSantris by remember { mutableStateOf<List<SantriRef>>(emptyList()) }
    var selectedBed by remember { mutableStateOf<BedRef?>(null) }
    var visitDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var complaint by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var actionTaken by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("observed") }
    var selectedMedicines by remember { mutableStateOf<List<MedicineInput>>(emptyList()) }
    var selectedKeluhans by remember { mutableStateOf<List<LookupItem>>(emptyList()) }
    var selectedDiagnoses by remember { mutableStateOf<List<LookupItem>>(emptyList()) }
    var selectedTindakans by remember { mutableStateOf<List<LookupItem>>(emptyList()) }
    var notifyGuardian by remember { mutableStateOf(false) }

    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 3

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
            selectedSantris = c.santri?.let { listOf(it) } ?: emptyList() // If editing, we just map it as a single element array
            selectedBed = c.bed
            visitDate = c.visitDate ?: LocalDate.now().toString()
            complaint = c.complaint ?: ""
            diagnosis = c.diagnosis ?: ""
            actionTaken = c.actionTaken ?: ""
            notes = c.notes ?: ""
            status = c.status
            selectedMedicines = c.medicines.map { MedicineInput(it.id, it.quantity) }
            
            // To properly prepopulate tags, we'd need them in the Case Detail response, 
            // but for now this acts as an initialization.
        }
    }

    Scaffold(
        topBar = { DeisaTopBar(title = if (id == null) "Tambah Kunjungan" else "Ubah Kunjungan", onBack = onBack) },
        containerColor = AppBackground,
        bottomBar = {
            Surface(
                color = AppSurface,
                shadowElevation = 8.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.ArrowBack, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Sebelumnya")
                        }
                    } else {
                        Spacer(Modifier.width(10.dp))
                    }

                    if (currentStep < totalSteps) {
                        Button(
                            onClick = { currentStep++ },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text("Selanjutnya")
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                        }
                    } else {
                        Button(
                            onClick = {
                                if (selectedSantris.isEmpty()) return@Button
                                
                                val req = SicknessRequest(
                                    santriIds = selectedSantris.map { it.id },
                                    infirmaryBedId = selectedBed?.id,
                                    visitDate = visitDate,
                                    diagnosaIds = selectedDiagnoses.map { it.id }.takeIf { it.isNotEmpty() },
                                    keluhanIds = selectedKeluhans.map { it.id }.takeIf { it.isNotEmpty() },
                                    tindakanIds = selectedTindakans.map { it.id }.takeIf { it.isNotEmpty() },
                                    complaint = complaint.takeIf { it.isNotBlank() },
                                    diagnosis = diagnosis.takeIf { it.isNotBlank() },
                                    actionTaken = actionTaken.takeIf { it.isNotBlank() },
                                    notes = notes.takeIf { it.isNotBlank() },
                                    status = status,
                                    medicines = selectedMedicines.takeIf { it.isNotEmpty() },
                                    notifyGuardian = notifyGuardian
                                )
                                viewModel.save(id, req) { success -> if (success) onBack() }
                            },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Icon(Icons.Filled.Save, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Simpan")
                        }
                    }
                }
            }
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
        ) {
            // Step Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppSurface)
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepIndicator(step = 1, currentStep = currentStep, title = "Data Awal")
                HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = if (currentStep >= 2) Primary else AppSurfaceVariant)
                StepIndicator(step = 2, currentStep = currentStep, title = "Pemeriksaan")
                HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = if (currentStep >= 3) Primary else AppSurfaceVariant)
                StepIndicator(step = 3, currentStep = currentStep, title = "Penanganan")
            }

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
                },
                label = "FormSteps"
            ) { step ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (step) {
                        1 -> {
                            Text("Pilih Santri & Waktu Kunjungan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Primary)
                            DeisaCard {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    DeisaMultiSearchableDropdown(
                                        label = "Pilih Santri (Bisa lebih dari 1)",
                                        items = lookups.santris,
                                        selectedItems = selectedSantris,
                                        onItemsSelected = { selectedSantris = it },
                                        itemLabel = { "${it.name} (${it.nis ?: "-"})" }
                                    )
                                    
                                    if (selectedSantris.isNotEmpty()) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text("Santri yang dipilih:", fontSize = 12.sp, color = MutedText)
                                            selectedSantris.forEach { santri ->
                                                Text("• ${santri.name}", fontSize = 14.sp, color = OnAppBackground, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                    }

                                    DeisaDatePicker(
                                        value = visitDate, onValueChange = { visitDate = it },
                                        label = "Tanggal Kunjungan",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                        2 -> {
                            Text("Keluhan & Diagnosa", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Primary)
                            DeisaCard {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    TagSelector("Keluhan", lookups.keluhans, selectedKeluhans) { selectedKeluhans = it }
                                    OutlinedTextField(
                                        value = complaint, onValueChange = { complaint = it },
                                        label = { Text("Keluhan Lainnya (opsional)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                                    )

                                    DeisaDivider()

                                    TagSelector("Diagnosa", lookups.diagnoses, selectedDiagnoses) { selectedDiagnoses = it }
                                    OutlinedTextField(
                                        value = diagnosis, onValueChange = { diagnosis = it },
                                        label = { Text("Diagnosa Lainnya (opsional)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                                    )
                                }
                            }
                        }
                        3 -> {
                            Text("Tindakan & Obat", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Primary)
                            DeisaCard {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    TagSelector("Tindakan", lookups.tindakans, selectedTindakans) { selectedTindakans = it }
                                    OutlinedTextField(
                                        value = actionTaken, onValueChange = { actionTaken = it },
                                        label = { Text("Tindakan Lainnya (opsional)") },
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
                            
                            Spacer(Modifier.height(8.dp))
                            
                            DeisaCard {
                                MedicineSelector(
                                    availableMedicines = lookups.medicines,
                                    selectedMedicines = selectedMedicines,
                                    onChanged = { selectedMedicines = it }
                                )
                            }
                            
                            Spacer(Modifier.height(8.dp))
                            
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
                        }
                    }
                }
            }
        }

        if (actionLoading) {
            LoadingBox()
        }
    }
}

@Composable
fun StepIndicator(step: Int, currentStep: Int, title: String) {
    val isPast = step < currentStep
    val isCurrent = step == currentStep
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = if (isCurrent || isPast) Primary else AppSurfaceVariant,
                    shape = androidx.compose.foundation.shape.CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isPast) {
                Icon(Icons.Filled.Check, null, tint = OnPrimary, modifier = Modifier.size(16.dp))
            } else {
                Text(step.toString(), color = if (isCurrent) OnPrimary else MutedText, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(title, fontSize = 10.sp, color = if (isCurrent) Primary else MutedText, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
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
                    DeisaSearchableDropdown(
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

@Composable
fun TagSelector(
    title: String,
    availableTags: List<LookupItem>,
    selectedTags: List<LookupItem>,
    onChanged: (List<LookupItem>) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        SectionHeader(title = title, action = {
            TextButton(onClick = { showDialog = true }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                    Text("Tambah", fontSize = 12.sp)
                }
            }
        })

        if (selectedTags.isEmpty()) {
            Text("Belum ada $title dipilih.", fontSize = 12.sp, color = MutedText)
        } else {
            selectedTags.forEach { tag ->
                DeisaCard(Modifier.padding(vertical = 4.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(tag.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnAppBackground)
                        IconButton(onClick = { onChanged(selectedTags.filter { it.id != tag.id }) }) {
                            Icon(Icons.Filled.Close, null, tint = AppError, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var selectedTag by remember { mutableStateOf<LookupItem?>(null) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = AppSurface,
            title = { Text("Tambah $title", color = OnAppBackground) },
            text = {
                DeisaSearchableDropdown(
                    label = "Pilih $title",
                    items = availableTags,
                    selectedItem = selectedTag,
                    onItemSelected = { selectedTag = it },
                    itemLabel = { it.name }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (selectedTag != null && !selectedTags.contains(selectedTag)) {
                        onChanged(selectedTags + selectedTag!!)
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
