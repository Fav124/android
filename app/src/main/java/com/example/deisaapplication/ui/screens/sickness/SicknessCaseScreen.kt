package com.example.deisaapplication.ui.screens.sickness

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.SicknessCase
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SicknessCaseScreen(
    viewModel: SicknessCaseViewModel,
    canManageData: Boolean,
    onOpenDrawer: () -> Unit,
    onAddNew: () -> Unit,
    onViewDetail: (Int) -> Unit,
) {
    val state by viewModel.listState.collectAsState()
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var search by remember { mutableStateOf("") }

    val statusFilters = listOf(
        null to "Semua", "observed" to "Observasi",
        "handled" to "Ditangani", "recovered" to "Sembuh", "referred" to "Dirujuk",
    )

    LaunchedEffect(state.toast) {
        if (state.toast != null) {
            kotlinx.coroutines.delay(3000L)
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            DeisaTopBar(
                title = "Santri Sakit",
                onOpenDrawer = onOpenDrawer,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNew, containerColor = Primary, contentColor = AppBackground) {
                Icon(Icons.Filled.Add, "Tambah")
            }
        },
        containerColor = AppBackground,
        snackbarHost = {
            if (state.toast != null) {
                Snackbar(modifier = Modifier.padding(16.dp), containerColor = AppSurface, contentColor = OnAppBackground) {
                    Text(state.toast!!)
                }
            }
        },
    ) { pv ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.loadList(selectedStatus, search.takeIf { it.isNotBlank() }) },
            modifier = Modifier.fillMaxSize().padding(pv),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(Modifier.fillMaxSize()) {
                // Search
                OutlinedTextField(
                    value = search, onValueChange = { search = it },
                    placeholder = { Text("Cari nama santri...", color = MutedText) },
                    leadingIcon = { Icon(Icons.Filled.Search, null, tint = MutedText) },
                    trailingIcon = {
                        if (search.isNotBlank()) {
                            IconButton(onClick = { search = ""; viewModel.loadList(selectedStatus) }) {
                                Icon(Icons.Filled.Clear, null, tint = MutedText)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Primary,
                        unfocusedBorderColor = AppSurfaceVariant,
                        focusedTextColor     = OnAppBackground,
                        unfocusedTextColor   = OnAppBackground,
                    ),
                )

                // Status filter chips
                LazyRow(Modifier.padding(horizontal = 12.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(statusFilters) { (status, label) ->
                        val selected = selectedStatus == status
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selectedStatus = status
                                viewModel.loadList(status, search.takeIf { it.isNotBlank() })
                            },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor     = AppBackground,
                                containerColor         = AppSurfaceVariant,
                                labelColor             = OnAppBackground,
                            ),
                        )
                    }
                }

                when {
                    state.isLoading && state.cases.isEmpty() -> LoadingBox()
                    state.error != null && state.cases.isEmpty() -> ErrorBox(
                        state.error!!,
                        { viewModel.loadList(selectedStatus, search.takeIf { it.isNotBlank() }) }
                    )
                    else -> {
                        LazyColumn(
                            Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            item { Spacer(Modifier.height(4.dp)) }
                            if (state.cases.isEmpty()) {
                                item {
                                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Tidak ada data ditemukan.", color = MutedText)
                                    }
                                }
                            }
                            items(state.cases, key = { it.id }) { case ->
                                SicknessCaseItem(
                                    case = case,
                                    onViewDetail = { onViewDetail(case.id) },
                                    onMarkRecovered = { viewModel.markRecovered(case.id) {} },
                                    onNotify = { viewModel.notifyGuardian(case.id) },
                                    canManageData = canManageData,
                                )
                            }
                            item { Spacer(Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SicknessCaseItem(
    case: SicknessCase,
    onViewDetail: () -> Unit,
    onMarkRecovered: () -> Unit,
    onNotify: () -> Unit,
    canManageData: Boolean,
) {
    DeisaCard(modifier = Modifier.clickable(onClick = onViewDetail)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Box(
                Modifier.size(42.dp).background(Primary.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    (case.santri?.name?.take(1) ?: "?").uppercase(),
                    fontWeight = FontWeight.Bold, color = Primary, fontSize = 16.sp,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(case.santri?.name ?: "—", fontWeight = FontWeight.SemiBold, color = OnAppBackground, fontSize = 14.sp)
                    StatusBadge(case.status, case.statusLabel ?: "Observasi")
                }
                Spacer(Modifier.height(4.dp))
                Text(case.complaint?.take(60) ?: "Keluhan tidak ada", fontSize = 12.sp, color = MutedText)
                if (case.diagnosis != null) {
                    Text("Diagnosa: ${case.diagnosis}", fontSize = 12.sp, color = MutedText)
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(case.visitDate ?: "", fontSize = 11.sp, color = MutedText)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SmallActionButton(Icons.Filled.Message, "WA", Primary, onNotify)
                        if (case.status != "recovered") {
                            SmallActionButton(Icons.Filled.CheckCircle, "Sembuh", Primary, onMarkRecovered)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmallActionButton(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        modifier = Modifier.height(30.dp),
    ) {
        Row(
            Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
            Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}
