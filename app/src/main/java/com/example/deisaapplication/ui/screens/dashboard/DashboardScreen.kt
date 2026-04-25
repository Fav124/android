package com.example.deisaapplication.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.DashboardData
import com.example.deisaapplication.data.model.SicknessCase
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*
import com.example.deisaapplication.ui.screens.report.SicknessTrendChart
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateSicknessCase: () -> Unit,
    onNavigateMedicine: () -> Unit,
    onNavigateReferral: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            DeisaTopBar(
                title = "DEIHealth",
                onOpenDrawer = onOpenDrawer,
                actions = {
                    IconButton(onClick = { viewModel.load() }) {
                        Icon(Icons.Filled.Refresh, "Refresh", tint = Primary)
                    }
                }
            )
        },
        containerColor = AppBackground,
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState is DashboardUiState.Loading,
            onRefresh = { viewModel.load() },
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> LoadingBox()
                is DashboardUiState.Error   -> ErrorBox(state.message, { viewModel.load() })
                is DashboardUiState.Success -> DashboardContent(
                    data = state.data,
                    onNavigateSicknessCase = onNavigateSicknessCase,
                    onNavigateMedicine = onNavigateMedicine,
                    onNavigateReferral = onNavigateReferral,
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    data: DashboardData,
    onNavigateSicknessCase: () -> Unit,
    onNavigateMedicine: () -> Unit,
    onNavigateReferral: () -> Unit,
) {
    val stats = data.stats
    val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    val currentDate = sdf.format(Date())

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(Modifier.padding(top = 8.dp)) {
                Text("Halo, Pengurus!", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = OnAppBackground)
                Text(currentDate, fontSize = 14.sp, color = MutedText)
            }
        }

        // Quick Actions Grid
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                QuickActionButton("Sakit Baru", Icons.Filled.AddModerator, Primary, onNavigateSicknessCase)
                QuickActionButton("Data Santri", Icons.Filled.PersonAdd, Secondary, { /* Nav to Santri Form */ })
                QuickActionButton("Input Obat", Icons.Filled.AddBox, AppWarning, onNavigateMedicine)
                QuickActionButton("Rujukan", Icons.Filled.ExitToApp, AppError, onNavigateReferral)
            }
        }

        // Expired Alert
        if (stats.obatKadaluarsa > 0) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppError.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, AppError.copy(alpha = 0.3f))
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Warning, null, tint = AppError)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Peringatan Obat", fontWeight = FontWeight.Bold, color = AppError, fontSize = 14.sp)
                            Text("${stats.obatKadaluarsa} jenis obat telah kadaluarsa.", color = AppError.copy(0.8f), fontSize = 12.sp)
                        }
                        TextButton(onClick = onNavigateMedicine) { Text("Detail", color = AppError) }
                    }
                }
            }
        }

        // Stats Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ModernStatCard("Total Santri", stats.santriTotal.toString(), Icons.Filled.Group, Primary, Modifier.weight(1f))
                    ModernStatCard("Sakit Aktif", stats.santriSakitAktif.toString(), Icons.Filled.Sick, AppError, Modifier.weight(1f))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ModernStatCard("Rujukan RS", stats.rujukan.toString(), Icons.Filled.LocalHospital, Secondary, Modifier.weight(1f))
                    ModernStatCard("Kasur UKS", "${stats.kasurTersedia}/${stats.kasurTotal}", Icons.Filled.Bed, AppWarning, Modifier.weight(1f))
                }
            }
        }

        // Integrated Chart
        if (data.sicknessTrends.isNotEmpty()) {
            item {
                DeisaCard {
                    Text("Tren Penyakit (14 Hari)", fontWeight = FontWeight.Bold, color = OnAppBackground, fontSize = 16.sp)
                    Spacer(Modifier.height(16.dp))
                    SicknessTrendChart(data.sicknessTrends, Modifier.height(160.dp).fillMaxWidth().padding(horizontal = 8.dp))
                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        // Recent Visits
        item {
            SectionHeader("Kunjungan Terakhir", "Lihat Semua", onNavigateSicknessCase)
        }
        
        if (data.recentCases.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Belum ada data kunjungan.", color = MutedText, fontSize = 14.sp)
                }
            }
        } else {
            items(data.recentCases) { case ->
                RecentCaseItem(case, onNavigateSicknessCase)
            }
        }

        // Low Stock Medicines
        if (data.lowStockMedicines.isNotEmpty()) {
            item { SectionHeader("Stok Obat Menipis", "Cek Inventori", onNavigateMedicine) }
            items(data.lowStockMedicines) { med ->
                DeisaCard {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(40.dp).background(AppWarning.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Medication, null, tint = AppWarning, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(med.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = OnAppBackground)
                            Text("Sisa Stok: ${med.stock} ${med.unit}", fontSize = 12.sp, color = MutedText)
                        }
                        StatusBadge("stok_kritis", "Kritis")
                    }
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun RecentCaseItem(case: SicknessCase, onClick: () -> Unit) {
    DeisaCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).background(Primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    (case.santri?.name?.take(1) ?: "?").uppercase(),
                    fontWeight = FontWeight.ExtraBold, color = Primary, fontSize = 18.sp,
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(case.santri?.name ?: "Tanpa Nama", fontWeight = FontWeight.Bold, color = OnAppBackground, fontSize = 15.sp)
                Text(
                    case.complaint.take(35) + if (case.complaint.length > 35) "…" else "",
                    fontSize = 12.sp, color = MutedText,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                StatusBadge(case.status, case.statusLabel)
                Spacer(Modifier.height(4.dp))
                Text(case.visitDate ?: "", fontSize = 10.sp, color = MutedText)
            }
        }
    }
}
