package com.example.deisaapplication.ui.screens.referral

import androidx.compose.foundation.BorderStroke
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
import com.example.deisaapplication.data.model.HospitalReferral
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.screens.santri.DetailItem
import com.example.deisaapplication.ui.theme.*

@Composable
fun HospitalReferralDetailScreen(
    id: Int,
    viewModel: HospitalReferralViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
) {
    val referral by viewModel.selectedReferral.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadDetail(id)
    }

    Scaffold(
        topBar = {
            DeisaTopBar(
                title = "Detail Rujukan",
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
        if (referral == null) {
            LoadingBox(Modifier.padding(pv))
        } else {
            HospitalReferralDetailContent(
                referral = referral!!, 
                modifier = Modifier.padding(pv),
                onNotifyGuardian = { viewModel.notifyGuardian(id) }
            )
        }
    }
}

@Composable
private fun HospitalReferralDetailContent(
    referral: HospitalReferral, 
    modifier: Modifier = Modifier,
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
                    Text(referral.santri?.name ?: "Tanpa Nama", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = OnAppBackground)
                    Text(referral.hospitalName ?: "-", fontSize = 16.sp, color = Primary, fontWeight = FontWeight.Bold)
                }
                StatusBadge(referral.status, referral.statusLabel ?: "Dirujuk")
            }
        }

        // WhatsApp Notification
        Button(
            onClick = onNotifyGuardian,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.Message, null, Modifier.size(18.dp), tint = OnPrimary)
            Spacer(Modifier.width(8.dp))
            Text("Kirim Notifikasi WhatsApp", fontWeight = FontWeight.Bold, color = OnPrimary)
        }

        // Clinical Details
        SectionHeader("Informasi Medis")
        DeisaCard {
            DetailItem(Icons.Filled.Sick, "Keluhan", referral.complaint ?: "-")
            DeisaDivider()
            DetailItem(Icons.Filled.FactCheck, "Diagnosa Sementara", referral.diagnosis ?: "Belum ada diagnosa")
        }

        // Logistic Info
        SectionHeader("Logistik")
        DeisaCard {
            DetailItem(Icons.Filled.CalendarToday, "Tanggal Rujukan", referral.referralDate ?: "-")
            DeisaDivider()
            DetailItem(Icons.Filled.DirectionsCar, "Transportasi", referral.transport ?: "-")
            DeisaDivider()
            DetailItem(Icons.Filled.Person, "Pendamping", referral.companionName ?: "-")
            DeisaDivider()
            DetailItem(Icons.Filled.AdminPanelSettings, "Dirujuk Oleh", referral.referredBy ?: "-")
        }

        // Notes
        if (!referral.notes.isNullOrEmpty()) {
            SectionHeader("Catatan")
            DeisaCard {
                Text(referral.notes ?: "", fontSize = 14.sp, color = OnAppBackground, lineHeight = 20.sp)
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
