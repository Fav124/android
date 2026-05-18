package com.example.deisaapplication.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.DashboardData
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    userName: String,
    onOpenDrawer: () -> Unit,
    onNavigateSicknessCase: () -> Unit,
    onNavigateMedicine: () -> Unit,
    onNavigateReferral: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DEIHealth", color = NavyBlue, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = null, tint = NavyBlue)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = NavyBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppSurface)
            )
        },
        containerColor = AppBackground,
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> DeisaLoadingBar()
                is DashboardUiState.Error   -> ErrorBox(state.message, { viewModel.load() })
                is DashboardUiState.Success -> DashboardContent(
                    data = state.data,
                    userName = userName,
                    onNavigateSicknessCase = onNavigateSicknessCase
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    data: DashboardData,
    userName: String,
    onNavigateSicknessCase: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Selamat datang,",
                    fontSize = 16.sp,
                    color = MutedText
                )
                Text(
                    text = userName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )
            }
        }

        // Status Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusSummaryCard(
                    title = "Sakit",
                    value = data.stats.santriSakitAktif.toString(),
                    icon = Icons.Default.Sick,
                    color = Color(0xFFEF4444),
                    modifier = Modifier.weight(1f)
                )
                StatusSummaryCard(
                    title = "Kunjungan",
                    value = data.stats.santriTotal.toString(),
                    icon = Icons.Default.EventNote,
                    color = LightBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Santri Sakit Terakhir",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnAppBackground
                )
                TextButton(onClick = onNavigateSicknessCase) {
                    Text("Lihat Semua", color = LightBlue)
                }
            }
        }

        // Santri List
        if (data.recentCases.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Tidak ada data santri sakit.", color = MutedText)
                }
            }
        } else {
            items(data.recentCases) { case ->
                SantriCardComponent(
                    case = case,
                    onClick = { /* Detail navigation can be added here */ }
                )
            }
        }
        
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun StatusSummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OnAppBackground
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = MutedText
            )
        }
    }
}
