package com.example.deisaapplication.ui.screens.referral

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.data.model.HospitalReferral
import com.example.deisaapplication.ui.components.*
import com.example.deisaapplication.ui.screens.sickness.SmallActionButton
import com.example.deisaapplication.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalReferralScreen(
    viewModel: HospitalReferralViewModel,
    canManageData: Boolean,
    onOpenDrawer: () -> Unit,
    onAddNew: () -> Unit,
    onViewDetail: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var search by remember { mutableStateOf("") }

    val statusFilters = listOf(
        null to "Semua", "referred" to "Dirujuk",
        "treated" to "Dalam Perawatan", "returned" to "Dipulangkan",
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
                title = "Rujukan RS",
                onOpenDrawer = onOpenDrawer,
                actions = {
                    IconButton(onClick = { viewModel.loadList(selectedStatus, search.takeIf { it.isNotBlank() }) }) {
                        Icon(Icons.Filled.Refresh, "Refresh", tint = Primary)
                    }
                }
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
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = AppSurface,
                    contentColor = OnAppBackground,
                ) { Text(state.toast!!) }
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
                OutlinedTextField(
                    value = search, onValueChange = { search = it },
                    placeholder = { Text("Cari nama santri...", color = MutedText) },
                    leadingIcon = { Icon(Icons.Filled.Search, null, tint = MutedText) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Primary,
                        unfocusedBorderColor = AppSurfaceVariant,
                        focusedTextColor     = OnAppBackground,
                        unfocusedTextColor   = OnAppBackground,
                    ),
                )

                LazyRow(Modifier.padding(horizontal = 12.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(statusFilters) { (status, label) ->
                        FilterChip(
                            selected = selectedStatus == status,
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
                    state.isLoading -> LoadingBox()
                    state.error != null -> ErrorBox(state.error!!, { viewModel.loadList() })
                    else -> LazyColumn(
                        Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item { Spacer(Modifier.height(4.dp)) }
                        if (state.referrals.isEmpty()) {
                            item {
                                Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Tidak ada data.", color = MutedText)
                                }
                            }
                        }
                        items(state.referrals, key = { it.id }) { ref ->
                            ReferralItem(
                                ref = ref,
                                canManageData = canManageData,
                                onClick = { onViewDetail(ref.id) },
                                onNotify = { viewModel.notifyGuardian(ref.id) },
                                onDelete = { viewModel.delete(ref.id) },
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReferralItem(
    ref: HospitalReferral,
    canManageData: Boolean,
    onClick: () -> Unit,
    onNotify: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DeisaCard(modifier = Modifier.clickable(onClick = onClick)) {
        Column(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(ref.santri?.name ?: "—", fontWeight = FontWeight.SemiBold, color = OnAppBackground, fontSize = 14.sp)
                    Text(ref.hospitalName, fontSize = 12.sp, color = Primary, fontWeight = FontWeight.Medium)
                }
                StatusBadge(ref.status, ref.statusLabel)
            }
            Spacer(Modifier.height(6.dp))
            Text(ref.complaint.take(60), fontSize = 12.sp, color = MutedText)
            if (ref.diagnosis != null) Text("Diagnosa: ${ref.diagnosis}", fontSize = 12.sp, color = MutedText)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(ref.referralDate ?: "", fontSize = 11.sp, color = MutedText)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SmallActionButton(Icons.Filled.Message, "WA", Primary, onNotify)
                    if (canManageData) {
                        SmallActionButton(Icons.Filled.DeleteOutline, "Hapus", AppError) { showDeleteDialog = true }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = AppSurface,
            title = { Text("Hapus Rujukan", color = OnAppBackground) },
            text = { Text("Hapus rujukan ${ref.santri?.name} ke ${ref.hospitalName}?", color = MutedText) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Hapus", color = AppError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = MutedText)
                }
            }
        )
    }
}
