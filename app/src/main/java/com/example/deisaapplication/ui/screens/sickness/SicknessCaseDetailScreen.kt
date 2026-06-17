package com.example.deisaapplication.ui.screens.sickness

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.Guardian
import com.example.deisaapplication.data.model.SicknessCase
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.screens.santri.DetailItem
import com.example.deisaapplication.ui.theme.*

@Composable
fun SicknessCaseDetailScreen(
    id: Int,
    viewModel: SicknessCaseViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
) {
    val case by viewModel.selectedCase.collectAsState()
    val lookups by viewModel.lookups.collectAsState()
    val actionLoading by viewModel.actionLoading.collectAsState()

    var showDischargeDialog by remember { mutableStateOf(false) }
    var showReferDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        viewModel.loadDetail(id)
        viewModel.loadLookups()
    }

    Scaffold(
        topBar = {
            DeisaTopBar(
                title = "Detail Kunjungan",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onEdit(id) }) {
                        Icon(Icons.Filled.Edit, "Ubah", tint = Primary)
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Filled.DeleteOutline, "Hapus", tint = AppError)
                    }
                }
            )
        },
        containerColor = AppBackground,
    ) { pv ->
        if (case == null) {
            LoadingBox(Modifier.padding(pv))
        } else {
            val c = case!!
            Column(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Hero Card ────────────────────────────────────────────────
                DeisaCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Column(Modifier.weight(1f)) {
                            Text(c.santri?.name ?: "-", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = OnAppBackground)
                            Text("NIS: ${c.santri?.nis ?: "-"}", fontSize = 13.sp, color = MutedText)
                            Text("Kelas: ${c.santri?.schoolClass ?: "-"}", fontSize = 13.sp, color = MutedText)
                        }
                        StatusBadge(c.status, c.statusLabel ?: c.status)
                    }
                }

                // ── Status-contextual Action Buttons ─────────────────────────
                when (c.status) {
                    "observed" -> {
                        SectionHeader("Tindak Lanjut")
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ActionBtn("Rujuk RS", Icons.Filled.LocalHospital, Secondary, Modifier.weight(1f)) { showReferDialog = true }
                            ActionBtn("Sembuh", Icons.Filled.CheckCircle, AppSuccess, Modifier.weight(1f)) { showDischargeDialog = true }
                        }
                    }
                    "rawat_inap" -> {
                        SectionHeader("Tindak Lanjut")
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ActionBtn("Rujuk RS", Icons.Filled.LocalHospital, Secondary, Modifier.weight(1f)) { showReferDialog = true }
                            ActionBtn("Pulangkan", Icons.Filled.ExitToApp, AppSuccess, Modifier.weight(1f)) { showDischargeDialog = true }
                        }
                    }
                    "referred" -> {
                        SectionHeader("Tindak Lanjut")
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ActionBtn("Pulang dari RS", Icons.Filled.ExitToApp, AppSuccess, Modifier.weight(1f)) { showDischargeDialog = true }
                        }
                    }
                    "handled" -> {
                        SectionHeader("Tindak Lanjut")
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ActionBtn("Rujuk RS", Icons.Filled.LocalHospital, Secondary, Modifier.weight(1f)) { showReferDialog = true }
                            ActionBtn("Sembuh", Icons.Filled.CheckCircle, AppSuccess, Modifier.weight(1f)) { showDischargeDialog = true }
                        }
                    }
                }

                // ── Notify Guardian Button ────────────────────────────────────
                OutlinedButton(
                    onClick = { viewModel.notifyGuardian(id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                    border = BorderStroke(1.dp, Primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Message, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Kirim Notifikasi WhatsApp ke Wali", fontWeight = FontWeight.Bold)
                }

                // ── Medical Details ───────────────────────────────────────────
                SectionHeader("Diagnosis & Keluhan")
                DeisaCard {
                    DetailItem(Icons.Filled.Sick, "Keluhan", c.complaint ?: "-")
                    DeisaDivider()
                    DetailItem(Icons.Filled.FactCheck, "Diagnosa", c.diagnosis ?: "Belum ada diagnosa")
                    DeisaDivider()
                    DetailItem(Icons.Filled.HealthAndSafety, "Tindakan", c.actionTaken ?: "Menunggu penanganan")
                    DeisaDivider()
                    DetailItem(Icons.Filled.Person, "Petugas", c.handledBy ?: "-")
                    DeisaDivider()
                    DetailItem(Icons.Filled.Event, "Tanggal Masuk", c.visitDate ?: "-")
                    if (c.returnDate != null) {
                        DeisaDivider()
                        DetailItem(Icons.Filled.EventAvailable, "Tanggal Keluar", c.returnDate)
                    }
                }

                // ── Hospital Referral Info ────────────────────────────────────
                if (c.status == "referred" || !c.hospitalName.isNullOrBlank()) {
                    SectionHeader("Rujukan Rumah Sakit")
                    DeisaCard {
                        DetailItem(Icons.Filled.LocalHospital, "Rumah Sakit", c.hospitalName ?: "-")
                        if (!c.transport.isNullOrBlank()) {
                            DeisaDivider()
                            DetailItem(Icons.Filled.DirectionsCar, "Transportasi", c.transport)
                        }
                        if (!c.companionName.isNullOrBlank()) {
                            DeisaDivider()
                            DetailItem(Icons.Filled.Person, "Pendamping", c.companionName)
                        }
                    }
                }

                // ── Discharge / Pickup Info ───────────────────────────────────
                if (!c.pickedUpBy.isNullOrBlank()) {
                    SectionHeader("Informasi Kepulangan")
                    DeisaCard {
                        DetailItem(Icons.Filled.DirectionsWalk, "Dijemput oleh", c.pickedUpBy)
                        if (!c.pickedUpAt.isNullOrBlank()) {
                            DeisaDivider()
                            DetailItem(Icons.Filled.AccessTime, "Waktu Jemput", c.pickedUpAt)
                        }
                        if (!c.dischargeNotes.isNullOrBlank()) {
                            DeisaDivider()
                            DetailItem(Icons.Filled.Notes, "Catatan", c.dischargeNotes)
                        }
                    }
                }

                // ── Wali / Guardian ───────────────────────────────────────────
                val guardians = c.santri?.guardians ?: emptyList()
                if (guardians.isNotEmpty()) {
                    SectionHeader("Daftar Wali Santri")
                    guardians.forEach { guardian ->
                        GuardianCard(guardian = guardian, santriId = c.santri!!.id)
                    }
                } else if (!c.santri?.guardianName.isNullOrBlank()) {
                    SectionHeader("Wali Santri")
                    DeisaCard {
                        DetailItem(Icons.Filled.Person, "Nama Wali", c.santri?.guardianName ?: "-")
                        if (!c.santri?.guardianPhone.isNullOrBlank()) {
                            DeisaDivider()
                            DetailItem(Icons.Filled.Phone, "Telepon", c.santri?.guardianPhone ?: "-")
                        }
                    }
                }

                // ── Medicines ─────────────────────────────────────────────────
                if (c.medicines.isNotEmpty()) {
                    SectionHeader("Obat Diberikan")
                    c.medicines.forEach { med ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = AppSurface),
                            border = BorderStroke(1.dp, Primary.copy(0.2f))
                        ) {
                            Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Medication, null, tint = Primary)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(med.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = OnAppBackground)
                                    Text("Dosis: ${med.quantity} ${med.unit ?: ""}", fontSize = 12.sp, color = MutedText)
                                }
                            }
                        }
                    }
                }

                // ── Notes ─────────────────────────────────────────────────────
                if (!c.notes.isNullOrBlank()) {
                    SectionHeader("Catatan")
                    DeisaCard {
                        Text(c.notes ?: "", fontSize = 14.sp, color = OnAppBackground, lineHeight = 20.sp)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }

        if (actionLoading) LoadingBox()
    }

    // ── Dialogs ───────────────────────────────────────────────────────────────
    val currentCase = case
    if (showDischargeDialog && currentCase != null) {
        DischargeDialog(
            guardians = currentCase.santri?.guardians ?: emptyList(),
            onDismiss = { showDischargeDialog = false },
            onSubmit = { pickedUpBy, guardianId, notes ->
                viewModel.discharge(id, pickedUpBy, guardianId, notes) { ok ->
                    if (ok) showDischargeDialog = false
                }
            }
        )
    }

    if (showReferDialog) {
        ReferHospitalDialog(
            onDismiss = { showReferDialog = false },
            onSubmit = { hospital, transport, companion, notes ->
                viewModel.referToHospital(id, hospital, transport, companion, notes) { ok ->
                    if (ok) showReferDialog = false
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = AppSurface,
            title = { Text("Hapus Kunjungan", color = OnAppBackground, fontWeight = FontWeight.Bold) },
            text = { Text("Data kunjungan ini akan dihapus secara permanen.", color = MutedText) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(id) { onBack() }
                    showDeleteConfirm = false
                }) { Text("Hapus", color = AppError, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Batal", color = MutedText) }
            }
        )
    }
}

// ── Helper Composables ────────────────────────────────────────────────────────

@Composable
private fun ActionBtn(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
            Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, lineHeight = 13.sp, maxLines = 2)
        }
    }
}

@Composable
private fun GuardianCard(guardian: Guardian, santriId: Int) {
    val context = LocalContext.current
    DeisaCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(42.dp).background(
                    if (guardian.isPrimary) Primary.copy(0.12f) else AppSurfaceVariant,
                    CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, null, tint = if (guardian.isPrimary) Primary else MutedText, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(guardian.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = OnAppBackground)
                    if (guardian.isPrimary) {
                        Spacer(Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = Primary.copy(0.15f)) {
                            Text("Utama", fontSize = 9.sp, color = Primary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp))
                        }
                    }
                }
                Text(guardian.relationship, fontSize = 12.sp, color = MutedText)
                Text(guardian.phone, fontSize = 12.sp, color = Primary)
            }
            // WA Button
            if (guardian.phone.isNotBlank()) {
                IconButton(onClick = {
                    val phone = guardian.phone.replace(Regex("[^0-9+]"), "").let {
                        if (it.startsWith("0")) "62" + it.drop(1) else it
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phone"))
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Filled.Message, "WhatsApp ${guardian.name}", tint = AppSuccess, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

// ── Dialogs ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DischargeDialog(
    guardians: List<Guardian>,
    onDismiss: () -> Unit,
    onSubmit: (pickedUpBy: String, guardianId: Int?, notes: String?) -> Unit,
) {
    var pickedUpBy by remember { mutableStateOf("") }
    var selectedGuardianId by remember { mutableStateOf<Int?>(null) }
    var notes by remember { mutableStateOf("") }
    var expandedGuardian by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppSurface,
        title = { Text("Pulangkan Santri", fontWeight = FontWeight.Bold, color = OnAppBackground) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Konfirmasi penjemput dan wali yang hadir:", color = MutedText, fontSize = 12.sp)

                if (guardians.isNotEmpty()) {
                    ExposedDropdownMenuBox(expanded = expandedGuardian, onExpandedChange = { expandedGuardian = !expandedGuardian }) {
                        OutlinedTextField(
                            value = guardians.find { it.id == selectedGuardianId }?.let { "${it.name} (${it.relationship})" } ?: "Pilih Wali Penjemput",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Wali yang Menjemput") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGuardian) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                        )
                        ExposedDropdownMenu(expanded = expandedGuardian, onDismissRequest = { expandedGuardian = false }) {
                            DropdownMenuItem(text = { Text("Pilih wali lain / Bukan dari daftar") }, onClick = { selectedGuardianId = null; expandedGuardian = false })
                            guardians.forEach { g ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(g.name, fontWeight = FontWeight.Medium)
                                            Text("${g.relationship} – ${g.phone}", fontSize = 11.sp, color = MutedText)
                                        }
                                    },
                                    onClick = { selectedGuardianId = g.id; pickedUpBy = g.name; expandedGuardian = false }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = pickedUpBy, onValueChange = { pickedUpBy = it },
                    label = { Text("Nama Penjemput (wajib)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                )
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Catatan Kepulangan") },
                    modifier = Modifier.fillMaxWidth(), minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (pickedUpBy.isNotBlank()) onSubmit(pickedUpBy, selectedGuardianId, notes.takeIf { it.isNotBlank() }) },
                colors = ButtonDefaults.buttonColors(containerColor = AppSuccess)
            ) { Text("Pulangkan", color = Color.White, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = MutedText) } }
    )
}

@Composable
fun ReferHospitalDialog(
    onDismiss: () -> Unit,
    onSubmit: (hospital: String, transport: String?, companion: String?, notes: String?) -> Unit,
) {
    var hospital by remember { mutableStateOf("") }
    var transport by remember { mutableStateOf("") }
    var companion by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppSurface,
        title = { Text("Rujuk ke Rumah Sakit", fontWeight = FontWeight.Bold, color = OnAppBackground) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = hospital, onValueChange = { hospital = it },
                    label = { Text("Nama Rumah Sakit (wajib)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                )
                OutlinedTextField(
                    value = transport, onValueChange = { transport = it },
                    label = { Text("Transportasi (Ambulans, Mobil, dll)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                )
                OutlinedTextField(
                    value = companion, onValueChange = { companion = it },
                    label = { Text("Nama Pendamping") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                )
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Catatan") }, minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = AppSurfaceVariant, focusedTextColor = OnAppBackground, unfocusedTextColor = OnAppBackground)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (hospital.isNotBlank()) onSubmit(hospital, transport.takeIf { it.isNotBlank() }, companion.takeIf { it.isNotBlank() }, notes.takeIf { it.isNotBlank() }) },
                colors = ButtonDefaults.buttonColors(containerColor = Secondary)
            ) { Text("Rujuk", color = Color.White, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = MutedText) } }
    )
}
