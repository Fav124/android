package com.example.deisaapplication.ui.screens.sickness

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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

    LaunchedEffect(id) {
        viewModel.loadDetail(id)
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
                }
            )
        },
        containerColor = AppBackground,
    ) { pv ->
        if (case == null) {
            LoadingBox(Modifier.padding(pv))
        } else {
            SicknessCaseDetailContent(
                case = case!!, 
                modifier = Modifier.padding(pv),
                onMarkRecovered = { viewModel.markRecovered(id) { /* onDone */ } },
                onNotifyGuardian = { viewModel.notifyGuardian(id) }
            )
        }
    }
}

@Composable
private fun SicknessCaseDetailContent(
    case: SicknessCase, 
    modifier: Modifier = Modifier,
    onMarkRecovered: () -> Unit,
    onNotifyGuardian: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Section
        DeisaCard {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(case.santri?.name ?: "Tanpa Nama", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = OnAppBackground)
                    Text("NIS: ${case.santri?.nis ?: "-"}", fontSize = 13.sp, color = MutedText)
                    Text("Kelas: ${case.santri?.schoolClass ?: "-"}", fontSize = 13.sp, color = MutedText)
                }
                StatusBadge(case.status, case.statusLabel ?: "Observasi")
            }
        }

        // Action Buttons
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (case.status != "recovered") {
                Button(
                    onClick = onMarkRecovered,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.CheckCircle, null, Modifier.size(18.dp), tint = OnPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("Sembuh", fontWeight = FontWeight.Bold, color = OnPrimary)
                }
            }
            OutlinedButton(
                onClick = onNotifyGuardian,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                border = BorderStroke(1.dp, Primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Message, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("WhatsApp", fontWeight = FontWeight.Bold)
            }
        }

        // Medical Details
        SectionHeader("Diagnosis & Keluhan")
        DeisaCard {
            DetailItem(Icons.Filled.Sick, "Keluhan", case.complaint ?: "-")
            DeisaDivider()
            DetailItem(Icons.Filled.FactCheck, "Diagnosa", case.diagnosis ?: "Belum ada diagnosa")
            DeisaDivider()
            DetailItem(Icons.Filled.HealthAndSafety, "Tindakan", case.actionTaken ?: "Menunggu penanganan")
        }

        // Treatment Details
        SectionHeader("Penanganan")
        DeisaCard {
            DetailItem(Icons.Filled.Bed, "Kasur UKS", case.bed?.code ?: "Rawat Jalan")
            DeisaDivider()
            DetailItem(Icons.Filled.Person, "Petugas", case.handledBy ?: "-")
            DeisaDivider()
            DetailItem(Icons.Filled.Event, "Waktu Masuk", case.visitDate ?: "-")
            if (case.returnDate != null) {
                DeisaDivider()
                DetailItem(Icons.Filled.EventAvailable, "Waktu Keluar", case.returnDate ?: "-")
            }
        }

        // Medicines
        if (case.medicines.isNotEmpty()) {
            SectionHeader("Obat Diberikan")
            case.medicines.forEach { med ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    border = BorderStroke(1.dp, Primary.copy(0.2f))
                ) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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

        // Notes
        if (!case.notes.isNullOrEmpty()) {
            SectionHeader("Catatan")
            DeisaCard {
                Text(case.notes ?: "", fontSize = 14.sp, color = OnAppBackground, lineHeight = 20.sp)
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
